package au.edu.uq.cmm.paul.queue;

import javax.persistence.EntityManagerFactory;

import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class QueueFeedServlet extends AbderaServlet {

    private static final long serialVersionUID = 1L;

    protected Provider createProvider() {
        QueueFeedAdapter ca = new QueueFeedAdapter(createEntityManagerFactory());
        ca.setHref("queue");

        SimpleWorkspaceInfo wi = new SimpleWorkspaceInfo();
        wi.setTitle("Ingestion Queue Workspace");
        wi.addCollection(ca);
        DefaultProvider provider = new DefaultProvider("/atom/");
        provider.addWorkspace(wi);
        provider.init(getAbdera(), null);
        return provider;
    }
    
    private EntityManagerFactory createEntityManagerFactory() {
        WebApplicationContext ctx = 
                WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        return ctx.getBean("entityManagerFactory", EntityManagerFactory.class);
    }
}
