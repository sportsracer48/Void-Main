package org.python.compiler;

import org.python.core.PyDictionary;
import org.python.core.PyObject;

public class NewDict extends PyObject
{
	private static final long serialVersionUID = 1L;
	
	public PyObject __call__(PyObject[] args, String[] keywords)
	{
		for(int i = 0; i<args.length; i+=2)
		{
			PyObject temp = args[i];
			args[i] = args[i+1];
			args[i+1] = temp;
		}
		return new PyDictionary(args);
	}
}
