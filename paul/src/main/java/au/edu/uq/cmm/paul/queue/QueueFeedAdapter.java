package au.edu.uq.cmm.paul.queue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Text.Type;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.log4j.Logger;

import au.edu.uq.cmm.paul.PaulConfiguration;
import au.edu.uq.cmm.paul.grabber.AdminMetadata;

public class QueueFeedAdapter extends AbstractEntityCollectionAdapter<AdminMetadata> {
    private static final Logger LOG = Logger.getLogger(QueueFeedAdapter.class);
    private static final String ID_PREFIX = "urn:uuid:";

    private EntityManagerFactory entityManagerFactory;
    private PaulConfiguration configuration;
    
    public QueueFeedAdapter(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.configuration = PaulConfiguration.load(entityManagerFactory);
    }
    
    @Override
    public String getTitle(RequestContext request) {
        return configuration.getFeedTitle();
    }

    @Override
    public void deleteEntry(String resourceName, RequestContext request)
            throws ResponseContextException {
        throw new ResponseContextException(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    public Object getContent(AdminMetadata record, RequestContext request)
            throws ResponseContextException {
        return null;
    }

    @Override
    public Iterable<AdminMetadata> getEntries(RequestContext request)
            throws ResponseContextException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            TypedQuery<AdminMetadata> query;
            Long id;
            try {
                String from = request.getParameter("from");
                id = from == null ? null : Long.valueOf(from);
            } catch (NumberFormatException ex) {
                throw new ResponseContextException(HttpServletResponse.SC_BAD_REQUEST);
            }
            if (id != null) {
                LOG.debug("Fetching from id " + id);
                query = entityManager.createQuery(
                        "from AdminMetadata a where a.id <= :id order by a.id desc", 
                        AdminMetadata.class).setParameter("id", id);
            } else {
                LOG.debug("Fetching from start of queue");
                query = entityManager.createQuery(
                        "from AdminMetadata a order by a.id desc", 
                        AdminMetadata.class);
            }
            query.setMaxResults(configuration.getFeedPageSize() + 1);
            List<AdminMetadata> res = new ArrayList<AdminMetadata>(query.getResultList());
            LOG.debug("Max page size " + configuration.getFeedPageSize() +
                      ", fetched " + res.size());
            return res;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public AdminMetadata getEntry(String resourceName, RequestContext request)
            throws ResponseContextException {
        String[] parts = resourceName.split("-");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            AdminMetadata record = 
                    entityManager.createQuery("from AdminMetadata a where a.id = :id", 
                    AdminMetadata.class).setParameter("id", parts[0]).getSingleResult();
            if (record == null) {
                throw new ResponseContextException(HttpServletResponse.SC_NOT_FOUND);
            } else {
                return record;
            }
        } finally {
            entityManager.close();
        }
    }

    @Override
    public String getId(AdminMetadata record) throws ResponseContextException {
        return ID_PREFIX + record.getRecordUuid();
    }

    @Override
    public String getName(AdminMetadata record) throws ResponseContextException {
        return record.getId() + "-" + record.getRecordUuid();
    }

    @Override
    public String getTitle(AdminMetadata record) throws ResponseContextException {
        return record.getSourceFilePathname();
    }

    @Override
    public Date getUpdated(AdminMetadata record) throws ResponseContextException {
        return record.getFileWriteTimestamp();
    }

    @Override
    public AdminMetadata postEntry(String title, IRI id, String summary, Date updated,
            List<Person> authors, Content content, RequestContext rc)
            throws ResponseContextException {
        throw new ResponseContextException(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    public void putEntry(AdminMetadata record, String title, Date updated, 
            List<Person> authors, String summary, Content content, RequestContext rc)
            throws ResponseContextException {
        throw new ResponseContextException(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    public String getAuthor(RequestContext rc)
            throws ResponseContextException {
        return configuration.getFeedAuthor();
    }
    
    @Override
    public List<Person> getAuthors(AdminMetadata record, RequestContext request)
            throws ResponseContextException {
        Person author = request.getAbdera().getFactory().newAuthor();
        author.setName(record.getUserName());
        if (record.getEmailAddress() != null) {
            author.setEmail(record.getEmailAddress());
        }
        return Arrays.asList(author);
    }
    
    @Override
    protected String addEntryDetails(RequestContext request, Entry entry,
            IRI feedIri, AdminMetadata record)
            throws ResponseContextException {
        String res = super.addEntryDetails(request, entry, feedIri, record);
        entry.addLink(configuration.getBaseFileUrl() + 
                new File(record.getCapturedFilePathname()).getName(),
                "enclosure");
        entry.addLink(configuration.getBaseFileUrl() + 
                new File(record.getMetadataFilePathname()).getName(),
                "enclosure");
        return res;
    }

    @Override
    public String getId(RequestContext rc) {
        return configuration.getFeedId();
    }
    
    /**
     * Create the base feed for the requested collection.  This override allows
     * us to add the author email and so forth.
     */
    @Override
    protected Feed createFeedBase(RequestContext request) 
            throws ResponseContextException {
        Factory factory = request.getAbdera().getFactory();
        Feed feed = factory.newFeed();
        feed.setId(getId(request));
        feed.setTitle(getTitle(request));
        feed.addLink(request.getUri().toASCIIString(), "self");
        Person author = factory.newAuthor();
        author.setName(getAuthor(request));
        String email = configuration.getFeedAuthorEmail();
        if (email != null && !email.isEmpty()) {
            author.setEmail(email);
        }
        feed.addAuthor(author);
        feed.setUpdated(new Date());
        return feed;
    }
    
    /**
     * Adds the selected entries to the Feed document.  It also sets 
     * the feed's atom:updated element to the current date and time,
     * and adds a link to the next "page" of the feed.
     */
    @Override
    protected void addFeedDetails(Feed feed, RequestContext request) 
            throws ResponseContextException {
        feed.setUpdated(new Date());
        Iterable<AdminMetadata> entries = getEntries(request);
        if (entries != null) {
            int count = 0;
            for (AdminMetadata record : entries) {
                LOG.debug("count = " + count + ", entry id = " + record.getId());
                if (++count > configuration.getFeedPageSize()) {
                    String nextPageUrl = configuration.getFeedUrl() +
                            "?from=" + record.getId();
                    LOG.debug("Adding 'next' link - " + nextPageUrl);
                    feed.addLink(nextPageUrl, "next");
                    break;
                }
                Entry entry = feed.addEntry();

                IRI feedIri = new IRI(getFeedIriForEntry(record, request));
                addEntryDetails(request, entry, feedIri, record);

                if (isMediaEntry(record)) {
                    addMediaContent(feedIri, entry, record, request);
                } else {
                    addContent(entry, record, request);
                }

                if (record.getSessionId() != -1) {
                    String sessionTitle = "Session of " + record.getUserName() + "/" +
                            record.getAccountName() + " started on " +
                            record.getSessionStartTimestamp();
                    entry.addCategory(
                            "http://mytardis.org/schemas/atom-import#experiment-ExperimentID",
                            record.getSessionUuid(), "experiment");
                    entry.addCategory(
                            "http://mytardis.org/schemas/atom-import#experiment-ExperimentTitle",
                            sessionTitle, "experiment title");
                }
            }
        }
    }

    @Override
    public Text getSummary(AdminMetadata record, RequestContext request)
            throws ResponseContextException {
        Text summary = request.getAbdera().getFactory().newSummary(Type.TEXT);
        summary.setText(record.getSourceFilePathname() + " as captured at " +
                record.getCaptureTimestamp() + " (id = " + record.getId() +
                ", uuid = " + record.getRecordUuid() + ", session uuid = " +
                record.getSessionUuid() + ")");
       
        return summary;
    }
}
