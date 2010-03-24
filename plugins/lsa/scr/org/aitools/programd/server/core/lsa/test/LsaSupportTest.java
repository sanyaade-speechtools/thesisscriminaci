package org.aitools.programd.server.core.lsa.test;

import java.util.Arrays;

import org.aitools.programd.server.core.lsa.LsaSupport;
import org.junit.Test;

public class LsaSupportTest {
	
	private LsaSupport lsaSupport = new LsaSupport("plugins/lsa/plugin.properties");
	private String request = "I want to do a design thesis";
	
	@Test
	public void getQueryRelatedTest(){
		boolean control = false;
		System.out.println("Imposto lo spazio: "+(control=lsaSupport.setSpace("DinfoSpace")));
		
		if(control){
			System.out.println(Arrays.toString(lsaSupport.getQueryRelated(request)));
		}		
	}
	
	@Test
	public void getRelatedTest(){
		boolean control = false;
		System.out.println("Imposto lo spazio: "+(control=lsaSupport.setSpace("DinfoSpace")));
		lsaSupport.setCompetence(0.1);
		if(control){
			System.out.println(Arrays.toString(lsaSupport.getRelated("WirelessSensorNetwork")));
		}		
	}
		

}
