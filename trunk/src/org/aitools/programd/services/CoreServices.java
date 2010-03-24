package org.aitools.programd.services;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.aitools.programd.Core;
import org.aitools.programd.services.dto.PluginAdaptor;
import org.aitools.programd.util.FileManager;
import org.aitools.programd.util.GlobalProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class CoreServices implements Serializable{

	private static final long serialVersionUID = 1042781342317508827L;
	private ServletContext context = null;
	
	public List<PluginAdaptor> getPlugins(HttpServletRequest request) throws Throwable{
		List<PluginAdaptor> ret = new ArrayList<PluginAdaptor>();
		
		List<String> plAv = getAvaiblePlugins(request);
		List<String> plAc = getActivePlugins(request);
		
		for(String pl:plAv){
			PluginAdaptor plugin = new PluginAdaptor();
			plugin.setName(pl);
			boolean control = false;
			for(String pl2:plAc){
				if(pl2.trim().equalsIgnoreCase(pl.trim())){
					control = true;
				}
			}
			plugin.setActive(control);
	
			ret.add(plugin);
		}	
		
		return ret;
	}
	
	public List<String> getAvaiblePlugins(HttpServletRequest request) throws Throwable{
		
		List<String> ret = new ArrayList<String>();
		
		String pluginPath = GlobalProperties.pluginDirectory;
		System.out.println("GlobalProperties.pluginDirectory: "+GlobalProperties.pluginDirectory);
		
		File file = FileManager.getExistingDirectory(pluginPath);
		
		for(String plugin:file.list()){
			ret.add(plugin);
		}
		
		return ret;
	}
	
	public List<String> getActivePlugins(HttpServletRequest request) throws Throwable{
		List<String> ret = new ArrayList<String>();
		
		String pluginPath = GlobalProperties.pluginXmlLocation;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(FileManager.getAbsolutePath(pluginPath));
		
		NodeList list = doc.getChildNodes();
		
		if(list.getLength()==1){
			Node plugins = list.item(0);					
			
			list = plugins.getChildNodes();
			for(int i=0; i<list.getLength(); i++){
				if(list.item(i).getNodeType()!=Node.TEXT_NODE){
					Node plugin = list.item(i);
					
					NodeList properties = plugin.getChildNodes();
					String name = null;
					String directory = null;
					for(int j=0; j<properties.getLength(); j++){
						if(properties.item(j).getNodeType()!=Node.TEXT_NODE){
							if(properties.item(j).getNodeName().equalsIgnoreCase("name")){
								name = properties.item(j).getTextContent();
							}else if(properties.item(j).getNodeName().equalsIgnoreCase("directory")){
								directory = properties.item(j).getTextContent();
							}
						}
					}
					if(name!=null && directory!=null){
						File dir = new File(FileManager.getAbsolutePath(directory));
						if(dir.exists()){
							ret.add(name);
						}
					}					
				}
			}
		}
		
		return ret;
	}
	
	public Boolean deactivePlugin(HttpServletRequest request, String pluginName) throws Throwable{
		
		boolean ret = false;
		
		File dir = FileManager.getExistingDirectory(GlobalProperties.pluginDirectory+File.separator+pluginName);
		if(dir.exists() && dir.isDirectory()){

			String pluginPath = GlobalProperties.pluginXmlLocation;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(FileManager.getAbsolutePath(pluginPath));

			NodeList list = doc.getChildNodes();
			if(list.getLength()==1){
				Element plugins = (Element)list.item(0);

				list = plugins.getChildNodes();
				for(int i=0; i<list.getLength(); i++){
					if(list.item(i).getNodeType()!=Node.TEXT_NODE){
						Node plugin = list.item(i);
						
						NodeList properties = plugin.getChildNodes();
						String name = null;
						for(int j=0; j<properties.getLength(); j++){
							if(properties.item(j).getNodeType()!=Node.TEXT_NODE){
								if(properties.item(j).getNodeName().equalsIgnoreCase("name")){
									name = properties.item(j).getTextContent();
									if(name.equalsIgnoreCase(pluginName)){
										Node oldNode = plugins.removeChild(plugin);
										if(oldNode!=null){
											XMLSerializer serializer = new XMLSerializer();
										    serializer.setOutputCharStream(new FileWriter(FileManager.getAbsolutePath(pluginPath)));
										    serializer.serialize(doc);
											ret = true;
										}										
									}
								}
							}
						}
					}
				}
			}	
		}
		
		return ret;
	}
	
	public Boolean activePlugin(HttpServletRequest request, String pluginName) throws Throwable{
		
		boolean ret = false;
		File dir = FileManager.getExistingDirectory(GlobalProperties.pluginDirectory+File.separator+pluginName);
		if(dir.exists() && dir.isDirectory()){
			
			String pluginPath = GlobalProperties.pluginXmlLocation;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(FileManager.getAbsolutePath(pluginPath));

			NodeList list = doc.getChildNodes();
			if(list.getLength()==1){
				Element plugins = (Element)list.item(0);

				//remove the node
				Element name = doc.createElement("name");
				name.setTextContent(pluginName);
				Element directory = doc.createElement("directory");
				directory.setTextContent(GlobalProperties.pluginDirectory+File.separator+pluginName);

				Element plugin = doc.createElement("plugin");

				plugin.appendChild(name);
				plugin.appendChild(directory);

				plugins.appendChild(plugin);

				XMLSerializer serializer = new XMLSerializer();
				serializer.setOutputCharStream(new FileWriter(FileManager.getAbsolutePath(pluginPath)));
				serializer.serialize(doc);
				ret = true;
			}	
		}
		
		return ret;
	}
	
	public Boolean reloadCore(HttpServletRequest request){
		
		boolean ret = false;
		
		this.context = request.getSession().getServletContext();
		
		Object obj = context.getAttribute(GlobalProperties.sessionKeyCore);
		if(obj!=null && obj instanceof Core){
	        
	        System.out.println("Core initialization...");
	        Core core = ((Core)obj);
	        core.restart();
	        
	        ret = true;
		}
		
		return ret;
	}

}
