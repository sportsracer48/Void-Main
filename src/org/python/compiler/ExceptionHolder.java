package org.python.compiler;

import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.core.PyType;

public class ExceptionHolder extends PyObject
{
	private static final long serialVersionUID = 1L;
	
	PyException exception;
	PyObject pythonVersion;
	
	PyType pythonType;
	Class<? extends Exception> javaType;
	
	String type;
	String message;
	int line,col;
	
	public ExceptionHolder(PyException e, CodeLine line)
	{
		this(e,null,null,line.line,line.col);
		this.type = pythonType.__getattr__("__name__").asString();
		if(type.equals("str"))
		{
			type = e.toString().trim();
		}
		this.message = e.value.__repr__().asString();
		this.javaType = null;
	}
	public ExceptionHolder(PyObject val, CodeLine line)
	{
		this(null,null,null,line.line,line.col);
		this.type = val.__getattr__("__class__").__repr__().asString();
		this.message = val.__repr__().asString();
		this.exception = new PyException(val.getType(),val);
		this.pythonVersion = val;
		this.pythonType = pythonVersion.getType();
		this.javaType = null;
	}
	public ExceptionHolder(PyException exception, String type, String message, int line, int col)
	{
		if(exception != null)
		{
			this.pythonVersion = exception.value;
			this.pythonType = pythonVersion.getType();
		}
		this.exception = exception;
		this.type = type;
		this.message = message;
		this.line = line;
		this.col = col;
	}
}
