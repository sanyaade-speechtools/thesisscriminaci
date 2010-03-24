package org.aitools.programd.server.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aitools.programd.services.CoreServices;
import org.jabsorb.JSONRPCBridge;


public class ServicesFilter implements Filter{
	
	private static boolean reloadFilter=false;
	private static Map<String, Class<?>> pluginsServices;

	public static Map<String, Class<?>> getPluginsServices() {
		return pluginsServices;
	}

	public static synchronized boolean isReloadFilter() {
		return reloadFilter;
	}

	public static void setPluginsServices(Map<String, Class<?>> pluginsServices) {
		ServicesFilter.pluginsServices = pluginsServices;
	}

	public static synchronized void setReloadFilter(boolean reloadFilter) {
		ServicesFilter.reloadFilter = reloadFilter;
	}
	   
	public void destroy() {
		System.out.println("ServicesFilter distrutto");
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
	throws IOException, ServletException {
		try{
			HttpSession session = ((HttpServletRequest)request).getSession();
			JSONRPCBridge json_bridge = (JSONRPCBridge) session.getAttribute("JSONRPCBridge");

			if(json_bridge == null || reloadFilter){
				json_bridge = new JSONRPCBridge();
				session.setAttribute("JSONRPCBridge", json_bridge);
				json_bridge.registerObject("coreServices", new CoreServices());
				
				if(getPluginsServices()!=null && getPluginsServices().size()>0){
					for(String name:getPluginsServices().keySet()){
						json_bridge.registerObject(name, getPluginsServices().get(name).newInstance());
					}	
				}
				
				setReloadFilter(false);
			}				

		}catch(Exception e){
			e.printStackTrace();
		}	

		chain.doFilter(request, response);   

	}

	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("ServicesFilter inizializzato");
	}	
}
