package org.aitools.programd.processor.lsa;

import java.util.Arrays;
import java.util.Random;

import org.aitools.programd.Core;
import org.aitools.programd.parser.TemplateParser;
import org.aitools.programd.processor.ProcessorException;
import org.aitools.programd.processor.aiml.AIMLProcessor;
import org.aitools.programd.server.core.lsa.LsaSupport;
import org.w3c.dom.Element;

public class LsaRandomRelated extends AIMLProcessor {
	
	public static String label = "lsarandomrelated";
	private Random random = new Random(System.currentTimeMillis());
	
	public LsaRandomRelated(Core coreToUse) {
		super(coreToUse);
	}
	
	
	@Override
	public String process(Element element, TemplateParser parser)
			throws ProcessorException {
		
		if (element.getChildNodes() != null) {
			
			String request = parser.evaluate(element.getChildNodes()).trim(); 
			
			LsaSupport lsaSupport = (LsaSupport)core.getPluginSupportMap().get(LsaSupport.class);
			
			if(lsaSupport!=null){
				String[] related = lsaSupport.getRelated(request);

				System.out.println(Arrays.toString(related));
				if(related!=null && related.length>1){
					int i = random.nextInt(related.length);
					if(i==0) i=1;
					return splitConstant(related[i]);
				}				
			}			
			return "";
		} 
		else{
			throw new ProcessorException(
					"<lsarandomrelated></lsarandomrelated> must have content!", null);
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
