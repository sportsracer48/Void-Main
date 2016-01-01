package program;

import org.python.core.Py;
import org.python.core.PyObject;

public class SafeGetAttr extends PyObject
{
	private static final long serialVersionUID = 1L;
	
	private PyObject getattr;
	public SafeGetAttr(PyObject getattr)
	{
		this.getattr = getattr;
	}
	
	public PyObject __call__(PyObject[] args, String[] keys)
	{
		String name = OuinoPythonEnvironment.getArg(1, "name", args, keys).asString();
		if(name.startsWith("_")) //not getting any private members this way.
		{
			return Py.None;
		}
		return getattr.__call__(args,keys);
	}
}
