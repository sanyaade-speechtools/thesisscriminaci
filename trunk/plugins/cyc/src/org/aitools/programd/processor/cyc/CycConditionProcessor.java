/*
 * CycconditionProcessor.java
 *
 */
package org.aitools.programd.processor.cyc;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.aitools.programd.Core;
import org.aitools.programd.parser.TemplateParser;
import org.aitools.programd.processor.ProcessorException;
import org.aitools.programd.processor.aiml.AIMLProcessor;
import org.aitools.programd.server.core.cyc.CycSupport;
import org.aitools.programd.server.core.cyc.CycSupportError;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * This class process a &lt;cyccondition&gt; tag.
 * 
 * @author Andrea Pergolizzi
 * 
 */
public class CycConditionProcessor extends AIMLProcessor {
	public static final String label = "cyccondition";
	private static final String QUERY = "query";
	private static final String LI = "li";
	
	private static CycSystemProcessor cycSystemProcessor;

	/** Creates a new instance of CycconditionProcessor */
	public CycConditionProcessor(Core coreToUse) {
		super(coreToUse);
		cycSystemProcessor = new CycSystemProcessor(coreToUse);
	}
	@Override
	public String process(Element element, TemplateParser parser)
			throws ProcessorException {
		
		if (element.getChildNodes() != null) {
			String actQuery = element.getAttribute(QUERY);		
			
			if (actQuery.equals(EMPTY_STRING)) {
				NodeList nList = element.getElementsByTagName(LI);
				if (nList.getLength() == 0)
					return EMPTY_STRING;
				for (int index = 0; index < nList.getLength(); index++) {
					Node node = nList.item(index);
					if (node instanceof Element) {
						Element e = (Element) node;
						actQuery = e.getAttribute(QUERY);
						if (!(actQuery.equals(EMPTY_STRING))) {
							String result = null;
							
							try {
								result = getCycResponse(actQuery, parser);
							} catch (Exception ex) {
								throw new ProcessorException(
										"parser exception!", ex);
							}
							boolean truthValue = (result
									.equals(CycSupport.FALSE) ? false : true);
							if (truthValue)
								return parser.evaluate(node.getChildNodes());
						}
					}
				}
				return EMPTY_STRING;
			} else {
				try {
					String result = getCycResponse(actQuery, parser);
					try {
						// Retrieve the query "treated"s
						boolean truthValue = (result.equals(CycSupport.FALSE) ? false
								: true);
						if (truthValue)
							return parser.evaluate(element.getChildNodes());
						else
							return EMPTY_STRING;
					} catch (CycSupportError cse) {
						cse.printStackTrace();
						return cse.getMessage();
					}
				} catch (Exception e) {
					e.printStackTrace();
					return EMPTY_STRING;
				}
			}
		} else {
			throw new ProcessorException(
					"<cyccondition></cyccondition> must have content!", null);
		}
	}
	
	private String getCycResponse(String actQuery, TemplateParser parser) throws ProcessorException{
		String res = "";
		String fragment="<cycsystem xmlns=\"http://alicebot.org/2001/AIML-1.0.1\">"+actQuery+"</cycsystem>";
		
		Element n;
		try {
			n = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.parse(new ByteArrayInputStream(fragment.getBytes()))
				.getDocumentElement();			
			
			res = cycSystemProcessor.process(n, parser);
			
			System.out.println("risposta "+res);
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		
		return res;
	}
}
