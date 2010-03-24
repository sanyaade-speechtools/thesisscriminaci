/*
 * CyctermProcessor.java
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
 * This class process a &lt;cycterm</code>&gt; element.
 * 
 * @author Andrea Pergolizzi
 * 
 */
public class CycTermProcessor extends AIMLProcessor {
	public static final String label = "cycterm";

	/** Creates a new instance of CyctermProcessor */
	public CycTermProcessor(Core coreToUse) {
		super(coreToUse);
	}
	@Override
	public String process(Element element, TemplateParser parser)
			throws ProcessorException {
		if (element.getChildNodes() != null) {
			String cycterm = parser.evaluate(element.getChildNodes()).trim();
			CycSupport cs = (CycSupport)core.getPluginSupportMap().get(CycSupport.class);
			return cs.findFirstMatch(cycterm);
		} else {
			throw new ProcessorException(
					"<cycterm></cycterm> must have content!", null);
		}
	}
}
