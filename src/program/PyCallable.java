package program;

import org.python.core.PyObject;

public class PyCallable extends PyObject
{
	private static final long serialVersionUID = 1L;
	
	private PythonFunction func;
	public PyCallable(PythonFunction func)
	{
		this.func = func;
	}
	public PyObject __call__(PyObject[] args, String[] keys)
	{
		return func.accept(args, keys);
	}
}
