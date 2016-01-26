package entry;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyString;

import program.SafeImport;

public class PythonInit
{
	public static void init()
	{
		Py.initPython();
		sanitize();
	}
	
	public static void sanitize()
	{
		try
		{
			PyObject builtins = Py.getSystemState().modules.__finditem__("__builtin__");
			
			//things that have no reason to exist and will never be seen again
			builtins.__delattr__("eval");
			builtins.__delattr__("execfile");
			builtins.__delattr__("compile");
			builtins.__delattr__("reload");
			
			//things which will one day be replaced
			builtins.__delattr__("raw_input");
			builtins.__delattr__("print");
			builtins.__delattr__("open");
			
			
			PyObject unsafeImport = builtins.__getattr__(new PyString("__import__"));
			builtins.__setattr__("__import__", new SafeImport(unsafeImport));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
