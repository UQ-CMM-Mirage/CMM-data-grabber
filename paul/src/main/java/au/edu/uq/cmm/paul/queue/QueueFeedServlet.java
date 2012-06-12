/*
* Copyright 2012, CMM, University of Queensland.
*
* This file is part of Paul.
*
* Paul is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Paul is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Paul. If not, see <http://www.gnu.org/licenses/>.
*/

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
        // Grab the JPA EntityManagerFactory for the QueueAdapter from the services object.
        WebApplicationContext ctx = 
                WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        EntityManagerFactory emf = ctx.getBean("entityManagerFactory", EntityManagerFactory.class);
        
        // Initialize the Abdera infrastructure ... 
        QueueFeedAdapter ca = new QueueFeedAdapter(emf);
        ca.setHref("queue");
        SimpleWorkspaceInfo wi = new SimpleWorkspaceInfo();
        wi.setTitle("Ingestion Queue Workspace");
        wi.addCollection(ca);
        DefaultProvider provider = new DefaultProvider("/atom/");
        provider.addWorkspace(wi);
        provider.init(getAbdera(), null);

        // This frobbit allows the atom feed can be turned on / off via the web UI.
        FeedSwitch fs = ctx.getBean("feedSwitch", FeedSwitch.class);
        ca.setFeedSwitch(fs);
        return provider;
    }
}
