package com.wiley.wws.search.plugin;

import java.util.Iterator;
import java.util.Map;

import org.apache.felix.http.api.ExtHttpService;
import org.apache.log4j.Logger;
import org.apache.velocity.tools.view.ToolInfo;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.web.servlet.DispatcherServlet;

import com.dotmarketing.filters.CMSFilter;
import com.dotmarketing.osgi.GenericBundleActivator;
import com.wiley.wws.utils.ViewToolInfoBean;


/**
 * Created by Gareth Wright
 * Date: 01/10/12
 */
public class Activator extends GenericBundleActivator {
	private static final Logger logger = Logger.getLogger(Activator.class);
    @SuppressWarnings ("unchecked")
    public void start ( BundleContext context ) throws Exception {

        //Initializing services...
        initializeServices( context );

        //Service reference to ExtHttpService that will allows to register servlets and filters
        ServiceReference sRef = context.getServiceReference( ExtHttpService.class.getName() );
        if ( sRef != null ) {

            //Publish bundle services
            publishBundleServices( context );

            ExtHttpService service = (ExtHttpService) context.getService( sRef );
            try {
                DispatcherServlet ds = new DispatcherServlet();
                ds.setContextConfigLocation( "spring/plugin-beans.xml" );
                logger.info("Registering wwsplugin...");
                service.registerServlet( "/spring99", ds, null, null );
                Map beans = ds.getWebApplicationContext().getBeansOfType(ViewToolInfoBean.class);
                for (Iterator it = beans.entrySet().iterator(); it.hasNext(); ) {
					Map.Entry entry = (Map.Entry) it.next();
	                ToolInfo sserviceToolInfo = (ToolInfo)ds.getWebApplicationContext().getBean(entry.getKey().toString());
	                logger.info("Registering viewtool with key=" + sserviceToolInfo.getKey());
	                registerViewToolService(context,sserviceToolInfo);					
				}
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            CMSFilter.addExclude( "/app/spring" );
        }

    }

    public void stop ( BundleContext context ) throws Exception {

        CMSFilter.removeExclude( "/app/spring" );

        //Unpublish bundle services
        unpublishBundleServices();
    }

}