/*
 * CycdimProcessor.java
 */
package org.aitools.programd.processor.cyc;

import org.aitools.programd.Core;
import org.aitools.programd.parser.TemplateParser;
import org.aitools.programd.processor.ProcessorException;
import org.aitools.programd.processor.aiml.AIMLProcessor;
import org.aitools.programd.server.core.cyc.CycStore;
import org.w3c.dom.Element;

/**
 * This class implements the functioning of the <code>&lt;cycdim&gt;</code>
 * tag.
 * 
 * @author Andrea Pergolizzi
 * 
 */
public class CycDimProcessor extends AIMLProcessor {
	public static final String label = "cycdim";

	public CycDimProcessor(Core coreToUse) {
		super(coreToUse);
	}
	@Override
	public String process(Element element, TemplateParser parser)
			throws ProcessorException {
		if (element.getChildNodes() == null) {
			return ("" + CycStore.size());
		} else {
			throw new ProcessorException("<cycdim/> cannot have content!", null);
		}
	}
}