/*
 * CycaddProcessor.java
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
 * 
 * This class implements the functioning of a &lt;cycadd&gt; tag.
 * 
 * @author Andrea Pergolizzi
 * 
 */
public class CycAddProcessor extends AIMLProcessor {
	public static String label = "cycadd";

	public CycAddProcessor(Core coreToUse) {
		super(coreToUse);
	}
	@Override
	public String process(Element element, TemplateParser parser)
			throws ProcessorException {
		if (element.getChildNodes() != null) {
			
			CycSupport cs = (CycSupport)core.getPluginSupportMap().get(CycSupport.class);
			
			cs.storeNextFormula();
			return parser.evaluate(element.getChildNodes());
		} else {
			throw new ProcessorException(
					"<cycadd></cycadd> must have content!", null);
		}
	}
}