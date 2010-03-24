/*
 * CycLogProcessor.java
 *
 */
package org.aitools.programd.processor.cyc;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.aitools.programd.Core;
import org.aitools.programd.parser.TemplateParser;
import org.aitools.programd.processor.ProcessorException;
import org.aitools.programd.processor.aiml.AIMLProcessor;
import org.aitools.programd.server.core.cyc.CycStore;
import org.w3c.dom.Element;

/**
 * 
 * This class implements the functioning of a &lt;cyclog&gt; tag.
 * 
 * @author Andrea Pergolizzi
 * 
 */
public class CycLogProcessor extends AIMLProcessor {
	public static String label = "cyclog";
	private static String PATH = "path";
	private static String CYC_SAVED = "formulas and results successfully saved!";
	private static String CYC_NOT_SAVED = "Unable to save formulas and results!";

	/** Creates a new instance of CycLogProcessor */
	public CycLogProcessor(Core coreToUse) {
		super(coreToUse);
	}
	@Override
	public String process(Element element, TemplateParser parser)
			throws ProcessorException {
		// (tag.XMLType == XMLNode.EMPTY)
		if (element.getChildNodes() == null) {
			String path = element.getAttribute(PATH);
			if (path.equals(EMPTY_STRING)) {
				throw new ProcessorException(
						"<cyclog/> must have a \"path\" attribute!", null);
			} else {
				try {
					CycStore.logCycPairs(path);
					return CYC_SAVED;
				} catch (FileNotFoundException fe) {
					return CYC_NOT_SAVED;
				} catch (IOException fe) {
					return CYC_NOT_SAVED;
				}
			}
		} else {
			throw new ProcessorException("<cyclog/> cannot have content!", null);
		}
	}
}