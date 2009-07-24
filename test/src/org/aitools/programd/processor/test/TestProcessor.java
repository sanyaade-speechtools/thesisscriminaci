package org.aitools.programd.processor.test;

import org.aitools.programd.Core;
import org.aitools.programd.parser.TemplateParser;
import org.aitools.programd.processor.ProcessorException;
import org.aitools.programd.processor.aiml.AIMLProcessor;
import org.aitools.programd.server.core.test.TestSupport;
import org.w3c.dom.Element;

public class TestProcessor extends AIMLProcessor {

	public static String label = "test";
	
	
	public TestProcessor(Core coreToUse) {
		super(coreToUse);
	}
	
	@Override
	public String process(Element element, TemplateParser parser)
			throws ProcessorException {
		if (element.getChildNodes() != null) {
			
			TestSupport support = (TestSupport)Core.getPluginSupportMap().get("test");
			System.err.println(Core.getPluginSupportMap().get("Test"));
			return element.getTextContent()+"; TEST! "+support.getCiao();
		} else {
			throw new ProcessorException(
					"<test></test> must have content!", null);
		}
	}

}
