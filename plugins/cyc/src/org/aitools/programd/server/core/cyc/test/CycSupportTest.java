package org.aitools.programd.server.core.cyc.test;

import org.aitools.programd.server.core.cyc.CycSupport;
import org.junit.Test;

public class CycSupportTest {
	
	
	private CycSupport cycSupport = new CycSupport("node4:3600");
	
	@Test
	public void getOntologyTest(){
		System.out.println(cycSupport.getOntology("#$BiologyMt"));
	}
	
	@Test
	public void executeRandomCycLTest(){
		String statement = "(cyc-query '(#$isa #$Dog ?WHAT ) #$EverythingPSC )";
		
		System.out.println(cycSupport.executeRandomCycL(statement));
		
	}
	

}
