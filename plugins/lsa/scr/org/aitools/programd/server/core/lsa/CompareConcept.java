package org.aitools.programd.server.core.lsa;

import java.util.List;

import org.aitools.programd.server.core.lsa.util.StringDouble;

public interface CompareConcept {
	
	public List<StringDouble> getQueryRelated(String request);
	public List<StringDouble> getQueryRelated(String request, double competence);
	public List<StringDouble> getRelated(String concept);
	public List<StringDouble> getRelated(String concept, double competence);
		
	public String[] getConcepts();
}
