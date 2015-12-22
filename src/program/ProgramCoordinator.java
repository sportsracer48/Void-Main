package program;

import java.util.ArrayList;
import java.util.List;

import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

public class ProgramCoordinator
{
	List<Environment> environments = new ArrayList<>();
	
	public ProgramCoordinator()
	{
		PythonInterpreter temp = new PythonInterpreter();
		temp.exec("pass");
		sanitize(temp);
		temp.close();
	}
	
	public void addEnvironment(Environment e)
	{
		environments.add(e);
	}
	
	public void act(int dt)
	{
		for(Environment e:environments)
		{
			e.act(dt);
		}
	}
	
	private static PyObject unsafeImport;
	public void sanitize(PythonInterpreter interpreter)
	{
		try
		{
			PyObject locals = interpreter.getLocals();
			PyObject builtins = locals.__getitem__(new PyString("__builtins__"));
			builtins.__delattr__("exit");
			builtins.__delattr__("eval");
			builtins.__delattr__("execfile");
			builtins.__delattr__("compile");
			unsafeImport = builtins.__getattr__(new PyString("__import__"));
			builtins.__setattr__("__import__", new SafeImport(unsafeImport));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
