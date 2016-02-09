package org.python.compiler;

import org.python.core.PyList;
import org.python.core.PyObject;

public class NewList extends PyObject
{
	private static final long serialVersionUID = -1177661888664086347L;

	public PyObject __call__(PyObject[] args, String[] keywords)
	{
		return new PyList(args);
	}
}
