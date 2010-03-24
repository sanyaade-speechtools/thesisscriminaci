/*
 * CycrandomProcessor.java
 *
 */
package org.aitools.programd.processor.cyc;

import org.aitools.programd.Core;
import org.aitools.programd.parser.TemplateParser;
import org.aitools.programd.processor.ProcessorException;
import org.aitools.programd.processor.aiml.AIMLProcessor;
import org.aitools.programd.server.core.cyc.CycSupport;
import org.w3c.dom.Element;

/**
 * This class processes a &lt;cycrandom&gt; tag.
 * 
 * @author Andrea Pergolizzi
 * 
 */
public class CycRandomProcessor extends AIMLProcessor {
	public static final String label = "cycrandom";

	/** Creates a new instance of CycrandomProcessor */
	public CycRandomProcessor(Core coreToUse) {
		super(coreToUse);
	}
	@Override
	public String process(Element element, TemplateParser parser)
			throws ProcessorException {
		if (element.getChildNodes() != null) {
			String result = parser.evaluate(element.getChildNodes()).trim();
			CycSupport cs = (CycSupport)core.getPluginSupportMap().get(CycSupport.class);
			return splitConstant(cs.executeRandomCycL(result));
		} else {
			throw new ProcessorException(
					"<cycrandom></cycrandom> must have content!", null);
		}
	}
	
	private String splitConstant(String constant){
		
		String result = "";
		
		for(int i=0; i<constant.length(); i++){
			char c = constant.charAt(i);
			if(Character.isLowerCase(c)){
				result+=c;
			}else{
				result+=" "+c;
			}
			
		}
		
		return result;
	}
}
