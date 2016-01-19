package org.python.compiler;

import java.io.PrintStream;

public class VMException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	String type,message;
	int line;
	int col;
	
	public VMException(String type, String message, int line, int col)
	{
		this.type = type;
		this.message = message;
		this.line=line;
		this.col=col;
	}
	
	public void printStackTrace(PrintStream o)
	{
		o.println("Exception of type "+type+" in line "+line+", "+col+": "+message);
	}
}
