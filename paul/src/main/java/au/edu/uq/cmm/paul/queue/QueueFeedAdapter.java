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
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.log4j.Logger;

import au.edu.uq.cmm.paul.PaulConfiguration;
import au.edu.uq.cmm.paul.grabber.DatafileMetadata;
import au.edu.uq.cmm.paul.grabber.DatasetMetadata;
import au.edu.uq.cmm.paul.status.FacilitySession;

public class QueueFeedAdapter extends AbstractEntityCollectionAdapter<DatasetMetadata> {
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
    public Object getContent(DatasetMetadata record, RequestContext request)
            throws ResponseContextException {
        return "dataset for " + record.getSourceFilePathnameBase() + 
                ", capture timestamp = " + record.getCaptureTimestamp() + 
                ", dataset uuid = " + record.getRecordUuid() + 
                ", session uuid = " + record.getSessionUuid();
    }

    @Override
    public Iterable<DatasetMetadata> getEntries(RequestContext request)
            throws ResponseContextException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            TypedQuery<DatasetMetadata> query;
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
                        "from DatasetMetadata a where a.id <= :id order by a.id desc", 
                        DatasetMetadata.class).setParameter("id", id);
            } else {
                LOG.debug("Fetching from start of queue");
                query = entityManager.createQuery(
                        "from DatasetMetadata a order by a.id desc", 
                        DatasetMetadata.class);
            }
            query.setMaxResults(configuration.getFeedPageSize() + 1);
            List<DatasetMetadata> res = new ArrayList<DatasetMetadata>(query.getResultList());
            LOG.debug("Max page size " + configuration.getFeedPageSize() +
                      ", fetched " + res.size());
            return res;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public DatasetMetadata getEntry(String resourceName, RequestContext request)
            throws ResponseContextException {
        String[] parts = resourceName.split("-");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            DatasetMetadata record = 
                    entityManager.createQuery("from DatasetMetadata a where a.id = :id", 
                    DatasetMetadata.class).setParameter("id", parts[0]).getSingleResult();
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
    public String getId(DatasetMetadata record) throws ResponseContextException {
        return ID_PREFIX + record.getRecordUuid();
    }

    @Override
    public String getName(DatasetMetadata record) throws ResponseContextException {
        return record.getId() + "-" + record.getRecordUuid();
    }

    @Override
    public String getTitle(DatasetMetadata record) throws ResponseContextException {
        return record.getSourceFilePathnameBase();
    }

    @Override
    public Date getUpdated(DatasetMetadata record) throws ResponseContextException {
        return record.getCaptureTimestamp();
    }

    @Override
    public DatasetMetadata postEntry(String title, IRI id, String summary, Date updated,
            List<Person> authors, Content content, RequestContext rc)
            throws ResponseContextException {
        throw new ResponseContextException(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    public void putEntry(DatasetMetadata record, String title, Date updated, 
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
    public List<Person> getAuthors(DatasetMetadata record, RequestContext request)
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
            IRI feedIri, DatasetMetadata record)
            throws ResponseContextException {
        String res = super.addEntryDetails(request, entry, feedIri, record);
        for (DatafileMetadata datafile : record.getDatafiles()) {
            entry.addLink(configuration.getBaseFileUrl() + 
                    new File(datafile.getCapturedFilePathname()).getName(),
                    "enclosure");
        }
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
        Iterable<DatasetMetadata> entries = getEntries(request);
        if (entries != null) {
            int count = 0;
            for (DatasetMetadata record : entries) {
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

                if (!record.getUserName().equals(FacilitySession.UNKNOWN)) {
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
}
