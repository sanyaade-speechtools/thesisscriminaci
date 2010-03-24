package org.aitools.programd.server.core.cyc;


/**
 *
 * Thrown by {@link CycStore} when its size is lesser than the index given.
 */

public class StoreOutOfBoundException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7475526130323767539L;


	public StoreOutOfBoundException(String message)
    {
        super(message);
    }


    public StoreOutOfBoundException(Exception e)
    {
        super(e.getMessage());
    }
}