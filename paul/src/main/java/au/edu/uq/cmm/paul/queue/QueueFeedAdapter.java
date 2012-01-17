package au.edu.uq.cmm.paul.queue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.log4j.Logger;

import au.edu.uq.cmm.paul.PaulConfiguration;
import au.edu.uq.cmm.paul.grabber.AdminMetadata;

public class QueueFeedAdapter extends AbstractEntityCollectionAdapter<AdminMetadata> {
    private static final Logger LOG = Logger.getLogger(QueueFeedAdapter.class);
    private static final String ID_PREFIX = "";

    private EntityManagerFactory entityManagerFactory;
    private PaulConfiguration configuration;
    
    public QueueFeedAdapter(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.configuration = PaulConfiguration.load(entityManagerFactory);
    }
    
    @Override
    public String getTitle(RequestContext rc) {
        return configuration.getFeedTitle();
    }

    @Override
    public void deleteEntry(String resourceName, RequestContext rc)
            throws ResponseContextException {
        throw new ResponseContextException(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    public Object getContent(AdminMetadata record, RequestContext rc)
            throws ResponseContextException {
        String url = new File(record.getCapturedFilePathname()).toURI().toString();
        Content content = rc.getAbdera().getFactory().newContent(Content.Type.TEXT);
        content.setText(url);
        return content;
    }

    @Override
    public Iterable<AdminMetadata> getEntries(RequestContext rc)
            throws ResponseContextException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            List<AdminMetadata> list = entityManager.createQuery("from AdminMetadata", 
                    AdminMetadata.class).getResultList();
            return new ArrayList<AdminMetadata>(list);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public AdminMetadata getEntry(String resourceName, RequestContext rc)
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
        return ID_PREFIX + record.getUuid();
    }

    @Override
    public String getName(AdminMetadata record) throws ResponseContextException {
        return record.getId() + "-" + record.getUuid();
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
        return Arrays.asList(author);
    }
    
    @Override
    protected String addEntryDetails(RequestContext request, Entry e,
            IRI feedIri, AdminMetadata record)
            throws ResponseContextException {
        String res = super.addEntryDetails(request, e, feedIri, record);
        e.addLink(configuration.getBaseFileUrl() + 
                new File(record.getCapturedFilePathname()).getName(),
                "enclosure");
        return res;
    }

    @Override
    public String getId(RequestContext rc) {
        return configuration.getFeedId();
    }
}
