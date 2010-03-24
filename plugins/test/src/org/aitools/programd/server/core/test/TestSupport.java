package org.aitools.programd.server.core.test;

public class TestSupport {
	
	private String s;
	private Integer i;
	
	public TestSupport(String s, Integer i){
		this.s=s;
		this.i=i;
	}
	
	public String getCiao(){
		
		return "ciao "+s+" "+i;
	}

}
