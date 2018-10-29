package com.dma.web;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class ContextListener
 *
 */
@WebListener
public class ContextListener implements ServletContextListener {

	InitialContext ic;

    /**
     * Default constructor. 
     */
    public ContextListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    @SuppressWarnings("rawtypes")
	public void contextInitialized(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
       	try {

			System.out.println("Start context initialization...");
       		
			ic = new InitialContext();
			ServletContext sc = arg0.getServletContext();
			sc.setAttribute("ic", ic);
			sc.setAttribute("resources", Tools.getResources());
			
    		String cognosFolder = (String) ic.lookup("CognosFolder");
    		sc.setAttribute("cognosFolder", cognosFolder);
    		String cognosModelsPath = (String) ic.lookup("CognosModelsPath"); 
    		sc.setAttribute("cognosModelsPath", cognosModelsPath);
    		String cognosDispatcher= (String) ic.lookup("CognosDispatcher");
    		sc.setAttribute("cognosDispatcher", cognosDispatcher);
    		String cognosLogin = (String) ic.lookup("CognosLogin");
    		sc.setAttribute("cognosLogin", cognosLogin);
    		String cognosPassword = (String) ic.lookup("CognosPassword");
    		sc.setAttribute("cognosPassword", cognosPassword);
    		String cognosNamespace = (String) ic.lookup("CognosNamespace");
    		sc.setAttribute("cognosNamespace", cognosNamespace);
    		String cognosDefaultLocale = (String) ic.lookup("CognosDefaultLocale"); 
    		sc.setAttribute("cognosDefaultLocale", cognosDefaultLocale);
    		String cognosLocales = (String) ic.lookup("CognosLocales");
    		sc.setAttribute("cognosLocales", cognosLocales);
			
    		boolean withRecCount = (Boolean) ic.lookup("WithRecCount");
    		sc.setAttribute("withRecCount", withRecCount);
    		
    		Enumeration<?> e = sc.getAttributeNames();
    		while (e.hasMoreElements())
    		{
    		    String name = (String) e.nextElement();

    		    // Get the value of the attribute
    		    Object value = sc.getAttribute(name);

    		    if (value instanceof Map) {
    		        for (Map.Entry<?, ?> entry : ((Map<?, ?>)value).entrySet()) {
    		            System.out.println(entry.getKey() + "=" + entry.getValue());
    		        }
    		    } else if (value instanceof List) {
    		        for (Object element : (List)value) {
    		            System.out.println(element);
    		        }
    		    }
    		    else if (value instanceof String) {
    		    	System.out.println(name + "=" + value);
    		    }
    		}    		

			System.out.println("Context has been initialized...");
    			
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    	arg0.getServletContext().removeAttribute("ic");
		System.out.println("Context has been destroyed...");    	
    }
	
}
