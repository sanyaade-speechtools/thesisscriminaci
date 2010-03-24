package org.aitools.programd.processor.cyc;

import org.aitools.programd.Core;
import org.aitools.programd.parser.TemplateParser;
import org.aitools.programd.processor.ProcessorException;
import org.aitools.programd.processor.aiml.AIMLProcessor;
import org.aitools.programd.server.core.cyc.CycStore;
import org.aitools.programd.server.core.cyc.StoreOutOfBoundException;
import org.w3c.dom.Element;

/**
 * 
 * This class handles a <code>cycget</code> tag.
 * 
 * @author Andrea Pergolizzi
 * 
 */
public class CycGetProcessor extends AIMLProcessor {
	public static final String label = "cycget";
	private static final String TYPE = "type";
	private static final String FORMULA = "formula";
	private static final String RESULT = "result";
	private static final String NUMBER = "n";

	public CycGetProcessor(Core coreToUse) {
		super(coreToUse);
		// TODO Auto-generated constructor stub
	}
	@Override
	public String process(Element element, TemplateParser parser)
			throws ProcessorException {
		if (element.getChildNodes() == null) {
			String type = element.getAttribute(TYPE);
			String number = element.getAttribute(NUMBER);
			try {
				if (type.equals(EMPTY_STRING) && number.equals(EMPTY_STRING)) {
					// Must recover the last CycPair
					try {
						return CycStore.getLast().toString();
					} catch (StoreOutOfBoundException se) {
						return se.getMessage();
					}
				} else if (type.equalsIgnoreCase(FORMULA)) {
					if (number.equals(EMPTY_STRING)) {
						// Must recover the last formula
						return CycStore.getLastFormula();
					} else {
						// Must recover the nth formula
						try {
							return CycStore.getFormulaAt(Integer
									.parseInt(number));
						} catch (NumberFormatException ne) {
							throw new ProcessorException(
									"number format of attribute \"n\" incorrect!",
									ne);
						}
					}
				} else if (type.equalsIgnoreCase(RESULT)) {
					if (number.equals(EMPTY_STRING)) {
						// Must recover the last result
						return CycStore.getLastResult();
					} else {
						// Must recover the nth formula
						try {
							return CycStore.getResultAt(Integer
									.parseInt(number));
						} catch (NumberFormatException ne) {
							throw new ProcessorException(
									"number format of attribute \"n\" incorrect!",
									ne);
						}
					}
				}
				// type is empty
				else if (!number.equals(EMPTY_STRING)) {
					// Must recover the nth CycPair
					try {
						return CycStore.getStoreAt(Integer.parseInt(number))
								.toString();
					} catch (NumberFormatException ne) {
						throw new ProcessorException(
								"number format of attribute \"n\" incorrect!",
								ne);
					}
				}
				return EMPTY_STRING;
			} catch (StoreOutOfBoundException se) {
				return se.getMessage();
			} catch (Exception e) {
				return EMPTY_STRING;
			}
		} else {
			throw new ProcessorException("<cycget/> cannot have content!", null);
		}
	}
}