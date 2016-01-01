package program;

import java.util.HashMap;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyString;

public class SafeImport extends PyObject
{
	private static HashMap<String,PyObject> modules = new HashMap<>();
	private static String[] allowedImports = {"math","random"};
	
	private static final long serialVersionUID = 1L;
	public SafeImport(PyObject unsafeImport)
	{
		for(String module: allowedImports)
		{
			modules.put(module, unsafeImport.__call__(new PyString(module)));
		}
	}
	public PyObject __call__(PyObject[] args, String[] keys)
	{
		String name = OuinoPythonEnvironment.getArg(0, "name", args, keys).asString();
		
		PyObject result = modules.get(name);
		if(result == null)
		{
			return Py.None;
		}
		
		return result;
	}
	
}
