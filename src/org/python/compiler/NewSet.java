package org.python.compiler;

import org.python.core.PyObject;
import org.python.core.PySet;

public class NewSet extends PyObject
{
	private static final long serialVersionUID = -3475791180320451745L;

	public PyObject __call__(PyObject[] args, String[] keywords)
	{
		return new PySet(args);
	}
}
