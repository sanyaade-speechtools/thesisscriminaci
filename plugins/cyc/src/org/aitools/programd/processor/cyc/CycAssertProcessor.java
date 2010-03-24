/*
 * CycassertProcessor.java
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
 * This class process a &lt;cycassert&gt; tag.
 * 
 * @author Andrea Pergolizzi
 * 
 */
public class CycAssertProcessor extends AIMLProcessor {
	public static final String label = "cycassert";
	private static final String EMPTY = "";

	/** Creates a new instance of CycassertProcessor */
	public CycAssertProcessor(Core coreToUse) {
		super(coreToUse);
	}
	@Override
	public String process(Element element, TemplateParser parser)
			throws ProcessorException {
		if (element.getChildNodes() != null) {
			String result = parser.evaluate(element.getChildNodes());
			System.out.println(result);
			CycSupport cs = (CycSupport)core.getPluginSupportMap().get(CycSupport.class);
			cs.executeCycL("(cyc-assert '" + result + ")");
			return EMPTY;
		} else {
			throw new ProcessorException(
					"<cycassert></cycassert> must have content!", null);
		}
	}
}
