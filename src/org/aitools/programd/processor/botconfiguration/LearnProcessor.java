/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.aitools.programd.processor.botconfiguration;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.Element;

import org.aitools.programd.Core;
import org.aitools.programd.parser.BotsConfigurationFileParser;
import org.aitools.programd.processor.ProcessorException;
import org.aitools.programd.util.FileManager;
import org.aitools.programd.util.URLTools;

/**
 * Loads AIML at load-time.
 * 
 * @since 4.1.2
 * @version 4.5
 * @author <a href="mailto:noel@aitools.org">Noel Bush</a>
 */
public class LearnProcessor extends BotConfigurationElementProcessor
{
    /** The label (as required by the registration scheme). */
    public static final String label = "learn";

    /**
     * Creates a new LearnProcessor using the given Core.
     * 
     * @param coreToUse the Core object to use
     */
    public LearnProcessor(Core coreToUse)
    {
        super(coreToUse);
    }

    /**
     * @see BotConfigurationElementProcessor#process(Element,
     *      BotsConfigurationFileParser)
     */
    @Override
    public String process(Element element, BotsConfigurationFileParser parser) throws ProcessorException
    {	
    	URL confPath = null;
    	try {
    		confPath = FileManager.getConfDirectory();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if(confPath!=null){
			FileManager.pushWorkingDirectory(confPath);
        	URL path = URLTools.contextualize(FileManager.getWorkingDirectory(), parser.evaluate(element.getChildNodes()));
        	
        	parser.getCore().load(path, parser.getCurrentBot().getID());
        	FileManager.popWorkingDirectory();
		}
		else{
        	URL path = URLTools.contextualize(FileManager.getWorkingDirectory(), parser.evaluate(element.getChildNodes()));
        	parser.getCore().load(path, parser.getCurrentBot().getID());        	
		}
        return EMPTY_STRING;
    }
}