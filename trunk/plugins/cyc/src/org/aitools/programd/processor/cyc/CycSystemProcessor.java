/*
 * CycsystemProcessor.java
 *
 */
package org.aitools.programd.processor.cyc;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.aitools.programd.Core;
import org.aitools.programd.parser.TemplateParser;
import org.aitools.programd.processor.ProcessorException;
import org.aitools.programd.processor.aiml.AIMLProcessor;
import org.aitools.programd.server.core.cyc.CycSupport;
import org.w3c.dom.Element;

/**
 * 
 * This class implements the functioning of a &lt;cycsystem&gt; tag.
 */
public class CycSystemProcessor extends AIMLProcessor {
	private static final String TRUE = CycSupport.TRUE;
	private static final String FALSE = CycSupport.FALSE;
	public static final String label = "cycsystem";

	/** Creates a new instance of CycsystemProcessor */
	public CycSystemProcessor(Core coreToUse) {
		super(coreToUse);
	}
	@Override
	public String process(Element element, TemplateParser parser)
			throws ProcessorException {
		//System.out.println("miao");
		if (element.getChildNodes() != null) {			
			String sentence = parser.evaluate(element.getChildNodes()).trim();

			CycSupport cs = (CycSupport)core.getPluginSupportMap().get(CycSupport.class);
			String result = cs.executeCycL(sentence);
			if (!(result.equals(TRUE) || result.equals(FALSE))) {
				// Vedo se si tratta di un commento, altrimenti restituisco
				// l'intero risultato
				String comment = cs.getFirstComment(result);
				if (comment.equals("Unknown"))
					return splitConstant(result);
				else
					return comment;
			} else {
				return result;
			}
		} else {
			throw new ProcessorException(
					"<cycsystem></cycsystem> must have content!", null);
		}
	}
	
	private String splitConstant(String constant){
		
		String result = "";
		
		for(int i=0; i<constant.length(); i++){
			char c = constant.charAt(i);
			if(Character.isLowerCase(c)){
				result+=c;
			}else{
				result+=" "+c;
			}
			
		}
		
		return result;
	}

}
