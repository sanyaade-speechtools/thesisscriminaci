package org.aitools.programd.processor.lsa;

import java.util.Arrays;
import java.util.Random;

import org.aitools.programd.Core;
import org.aitools.programd.parser.TemplateParser;
import org.aitools.programd.processor.ProcessorException;
import org.aitools.programd.processor.aiml.AIMLProcessor;
import org.aitools.programd.server.core.lsa.LsaSupport;
import org.w3c.dom.Element;

public class LsaRandomQuery extends AIMLProcessor {
	
	public static String label = "lsarandomquery";
	private Random random = new Random(System.currentTimeMillis());
	
	public LsaRandomQuery(Core coreToUse) {
		super(coreToUse);
	}
	
	
	@Override
	public String process(Element element, TemplateParser parser)
			throws ProcessorException {
		
		if (element.getChildNodes() != null) {
			
			String request = parser.evaluate(element.getChildNodes()).trim(); 
			
			LsaSupport lsaSupport = (LsaSupport)core.getPluginSupportMap().get(LsaSupport.class);
			
			if(lsaSupport!=null){
				String[] related = lsaSupport.getQueryRelated(request);
				System.out.println(Arrays.toString(related));
				if(related!=null && related.length>0){
					int i = random.nextInt(related.length);
				
					return splitConstant(related[i]);
				}
				
			}
			
			return "";
		} 
		else{
			throw new ProcessorException(
					"<lsarandomquery></lsarandomquery> must have content!", null);
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