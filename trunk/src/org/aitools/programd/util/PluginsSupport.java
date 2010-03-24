package org.aitools.programd.util;

import java.util.HashMap;
import java.util.Map;

public class PluginsSupport {
	
	private static Map<String, String> pluginPath = new HashMap<String, String>();
	private static Map<String, String> pluginProperties = new HashMap<String, String>();
	
	public static String getPluginPropertiesPath(String pluginName){
		return pluginProperties.get(pluginName.trim().toLowerCase());
	}
	
	public static void addPluginPropertiesPath(String pluginName, String pathProperties){
		pluginProperties.put(pluginName.trim().toLowerCase(), pathProperties);
	}

	public static void addPluginPath(String pluginName, String pathProperties) {
		pluginPath.put(pluginName.trim().toLowerCase(), pathProperties);
	}
	
	public static String getPluginPath(String pluginName){
		return pluginPath.get(pluginName.trim().toLowerCase());
	}

}
