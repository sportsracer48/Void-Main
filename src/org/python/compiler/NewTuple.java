package org.python.compiler;

import org.python.core.PyObject;
import org.python.core.PyTuple;

public class NewTuple extends PyObject
{
private static final long serialVersionUID = 1L;
	
	public PyObject __call__(PyObject[] args, String[] keywords)
	{
		return new PyTuple(args);
	}
}
