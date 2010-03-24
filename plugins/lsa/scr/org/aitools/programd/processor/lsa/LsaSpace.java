package org.aitools.programd.processor.lsa;

import org.aitools.programd.Core;
import org.aitools.programd.parser.TemplateParser;
import org.aitools.programd.processor.ProcessorException;
import org.aitools.programd.processor.aiml.AIMLProcessor;
import org.aitools.programd.server.core.lsa.LsaSupport;
import org.w3c.dom.Element;

public class LsaSpace extends AIMLProcessor {
	
	public static String label = "lsaspace";
	
	public LsaSpace(Core coreToUse) {
		super(coreToUse);
	}
	
	
	@Override
	public String process(Element element, TemplateParser parser)
			throws ProcessorException {
		
		if (element.getChildNodes() != null) {
			
			String space = parser.evaluate(element.getChildNodes()).trim();
			LsaSupport lsaSupport = (LsaSupport)core.getPluginSupportMap().get(LsaSupport.class);
			
			if(lsaSupport.containsSpace(space)){
				lsaSupport.setSpace(space);
				return "TRUE";
			}else{
				return "This space does not exist!";
			}			
		} 
		else{
			throw new ProcessorException(
					"<lsaspace></lsaspace> must have content!", null);
			}
	}

}
