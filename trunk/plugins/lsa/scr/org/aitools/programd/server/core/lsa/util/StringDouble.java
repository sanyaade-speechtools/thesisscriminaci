package org.aitools.programd.server.core.lsa.util;

public class StringDouble implements Comparable<StringDouble> {
	
	public String name;
	public Double relevance;
	public int compareTo(StringDouble o) {
		return relevance.compareTo(o.relevance);
	}
	
	public String toString(){
		return "[name="+name+",relevace="+relevance+"]";
	}
}
