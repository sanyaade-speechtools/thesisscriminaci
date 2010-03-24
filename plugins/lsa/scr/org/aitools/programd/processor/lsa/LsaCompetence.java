package org.aitools.programd.processor.lsa;

import org.aitools.programd.Core;
import org.aitools.programd.parser.TemplateParser;
import org.aitools.programd.processor.ProcessorException;
import org.aitools.programd.processor.aiml.AIMLProcessor;
import org.aitools.programd.server.core.lsa.LsaSupport;
import org.w3c.dom.Element;

public class LsaCompetence extends AIMLProcessor {
	
	public static String label = "lsacompetence";
	
	public LsaCompetence(Core coreToUse) {
		super(coreToUse);
	}
	
	
	@Override
	public String process(Element element, TemplateParser parser)
			throws ProcessorException {
		
		if (element.getChildNodes() != null) {
			
			Boolean ret = false;
			try{
				Double competence = Double.parseDouble(parser.evaluate(element.getChildNodes()).trim()); 
			
				LsaSupport lsaSupport = (LsaSupport)core.getPluginSupportMap().get("lsa");
				lsaSupport.setCompetence(competence);
				
				ret = true;
			}catch (Throwable t) {
				logger.fatal("Error during LsaCompetence.process", t);
			}
			
			return String.valueOf(ret);
		} 
		else{
			throw new ProcessorException(
					"<lsacompetence></lsacompetence> must have content!", null);
			}
	}
}
