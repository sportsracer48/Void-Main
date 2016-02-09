package org.python.compiler;

import org.python.core.PyObject;

public class Pointer extends PyObject
{
	private static final long serialVersionUID = -3290562145776076595L;
	
	String name;
	PyObject value;
	Interpreter terp;
	
	public Pointer(Interpreter terp,String name)
	{
		this.name = name;
		if(terp!=null)
		{
			this.value = terp.getVar(name);
		}
		this.terp = terp;
	}
	public void set(PyObject value)
	{
		terp.setVar(name, value);
	}
	public PyObject get()
	{
		return value;
	}
}
