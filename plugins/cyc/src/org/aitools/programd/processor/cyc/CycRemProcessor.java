/*
 * CycremProcessor.java
 */
package org.aitools.programd.processor.cyc;

import org.aitools.programd.Core;
import org.aitools.programd.parser.TemplateParser;
import org.aitools.programd.processor.ProcessorException;
import org.aitools.programd.processor.aiml.AIMLProcessor;
import org.aitools.programd.server.core.cyc.CycStore;
import org.aitools.programd.server.core.cyc.StoreOutOfBoundException;
import org.w3c.dom.Element;

/**
 * This class implements the functioning of the <code>&lt;cycrem&gt;</code>
 * tag.
 * 
 * @author Andrea Pergolizzi
 * 
 */
public class CycRemProcessor extends AIMLProcessor {
	public static final String label = "cycget";
	private static final String TYPE = "type";
	private static final String FORMULA = "formula";
	private static final String RESULT = "result";
	private static final String NUMBER = "n";

	public CycRemProcessor(Core coreToUse) {
		super(coreToUse);
	}
	@Override
	public String process(Element element, TemplateParser parser)
			throws ProcessorException {
		if (element.getChildNodes() == null) {
			String type = element.getAttribute(TYPE);
			String number = element.getAttribute(NUMBER);
			try {
				if (type.equals(EMPTY_STRING) && number.equals(EMPTY_STRING)) {
					try {
						return CycStore.removeLast().toString();
					} catch (StoreOutOfBoundException se) {
						return se.getMessage();
					}
				} else if (type.equalsIgnoreCase(FORMULA)) {
					if (number.equals(EMPTY_STRING)) {
						return CycStore.removeLastFormula();
					} else {
						try {
							return CycStore.remFormulaAt(Integer
									.parseInt(number));
						} catch (NumberFormatException ne) {
							throw new ProcessorException(
									"number format of attribute \"n\" incorrect!",
									null);
						}
					}
				} else if (type.equalsIgnoreCase(RESULT)) {
					if (number.equals(EMPTY_STRING)) {
						// Must recover the last result
						return CycStore.getLastResult();
					} else {
						// Must recover the nth formula
						try {
							return CycStore.remResultAt(Integer
									.parseInt(number));
						} catch (NumberFormatException ne) {
							throw new ProcessorException(
									"number format of attribute \"n\" incorrect!",
									null);
						}
					}
				}
				// type is empty
				else if (!number.equals(EMPTY_STRING)) {
					// Must recover the nth CycPair
					try {
						return CycStore.removeStoreAt(Integer.parseInt(number))
								.toString();
					} catch (NumberFormatException ne) {
						throw new ProcessorException(
								"number format of attribute \"n\" incorrect!",
								null);
					}
				}
				return EMPTY_STRING;
			} catch (StoreOutOfBoundException se) {
				return se.getMessage();
			} catch (Exception e) {
				return EMPTY_STRING;
			}
		} else {
			throw new ProcessorException("<cycrem/> cannot have content!", null);
		}
	}
}