/*
 * CycretractProcessor.java
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
 * This class implements the functioning of the &lt;cycretract&gt; tag.
 * 
 * @author Andrea Pergolizzi
 * 
 */
public class CycRetractProcessor extends AIMLProcessor {
	public static final String label = "cycretract";
	private static final String EMPTY = "";

	/** Creates a new instance of CycretractProcessor */
	public CycRetractProcessor(Core coreToUse) {
		super(coreToUse);
	}
	@Override
	public String process(Element element, TemplateParser parser)
			throws ProcessorException {
		if (element.getChildNodes() != null) {
			String result = parser.evaluate(element.getChildNodes());
			CycSupport cs = (CycSupport)core.getPluginSupportMap().get(CycSupport.class);
			cs.executeCycL("(cyc-unassert '" + result + ")");
			return EMPTY;
		} else {
			throw new ProcessorException(
					"<cycretract></cycretract> must have content!", null);
		}
	}
}
