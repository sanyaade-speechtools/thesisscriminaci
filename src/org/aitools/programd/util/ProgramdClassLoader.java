package org.aitools.programd.util;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.List;

public class ProgramdClassLoader extends URLClassLoader{

	public ProgramdClassLoader(URL[] urls, ClassLoader parent,
			URLStreamHandlerFactory factory) {
		super(urls, parent, factory);
	}

	public ProgramdClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public ProgramdClassLoader(URL[] urls) {
		super(urls);
	}
	
	public void addUrl(URL url){
		addURL(url);
	}
	
	public void addUrls(List<URL> urls){
		for(URL url:urls){
			addUrl(url);
		}
	}
}
