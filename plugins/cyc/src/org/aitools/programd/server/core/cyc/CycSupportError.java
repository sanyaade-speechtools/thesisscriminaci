/*
 * CycSupportError.java
 *
 */

package org.aitools.programd.server.core.cyc;


/**
 *	<p>
 * 	This error is thrown by CycSupport when it cannot be communicate with OpenCyc.
 *	</p>
 *
 *	@author Andrea Pergolizzi
 * 
 */

public class CycSupportError extends Error
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 3174698594099860427L;
	private Exception e;

    public CycSupportError(String message)
    {
        super(message);
    }

    public CycSupportError(String message, Exception e)
    {
        super(message);
        this.e = e;
    }


    public Exception getException()
    {
        return this.e;
    }
}