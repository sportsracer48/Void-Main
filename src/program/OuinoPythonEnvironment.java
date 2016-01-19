package program;

import org.python.compiler.NewDict;
import org.python.compiler.NewList;
import org.python.compiler.NewSet;
import org.python.compiler.NewTuple;
import org.python.core.Py;
import org.python.core.PyInteger;
import org.python.core.PyObject;

import java.util.Hashtable;

public class OuinoPythonEnvironment
{
	public static void exec(PyObject locals, OuinoEnvironment env)
	{
		locals.__setitem__("HIGH", new PyInteger(OuinoEnvironment.HIGH));
		locals.__setitem__("LOW", new PyInteger(OuinoEnvironment.LOW));
		locals.__setitem__("OUTPUT", new PyInteger(OuinoEnvironment.OUTPUT));
		locals.__setitem__("INPUT", new PyInteger(OuinoEnvironment.INPUT));
		locals.__setitem__("INPUT_PULLUP", new PyInteger(OuinoEnvironment.INPUT_PULLUP));
		locals.__setitem__("NO_CONNECTION", new PyInteger(OuinoEnvironment.NO_CONNECTION));
		locals.__setitem__("CONSTANT", new PyInteger(OuinoEnvironment.CONSTANT));
		
		
		locals.__setitem__("pinMode", new PyCallable((args,keys)->{
			env.pinMode(
					getArg(0,"pin",args,keys).asInt(),
					getArg(1,"mode",args,keys).asInt()
					);
			return null;
		}));
		
		locals.__setitem__("digitalWrite", new PyCallable((args,keys)->{
			env.digitalWrite(
					getArg(0,"pin",args,keys).asInt(),
					getArg(1,"val",args,keys).asInt()
					);
			return null;
		}));
		
		locals.__setitem__("delay", new PyCallable((args,keys)->{
			env.delay(
					getArg(0,"ms",args,keys).asInt()
					);
			return null;
		}));
	}
	public static Hashtable<String,PyObject> getGlobals(OuinoEnvironment env)
	{
		Hashtable<String,PyObject> locals = new Hashtable<>();
		
		locals.put("HIGH", new PyInteger(OuinoEnvironment.HIGH));
		locals.put("LOW", new PyInteger(OuinoEnvironment.LOW));
		locals.put("OUTPUT", new PyInteger(OuinoEnvironment.OUTPUT));
		locals.put("INPUT", new PyInteger(OuinoEnvironment.INPUT));
		locals.put("INPUT_PULLUP", new PyInteger(OuinoEnvironment.INPUT_PULLUP));
		locals.put("NO_CONNECTION", new PyInteger(OuinoEnvironment.NO_CONNECTION));
		locals.put("CONSTANT", new PyInteger(OuinoEnvironment.CONSTANT));
		
		
		locals.put("True", Py.True);
		locals.put("False", Py.False);
		locals.put("$newList", new NewList());
		locals.put("$newDict", new NewDict());
		locals.put("$newSet", new NewSet());
		locals.put("$newTuple", new NewTuple());
		locals.put("None", Py.None);
		locals.put("Ellipsis", Py.Ellipsis);
		
		PyObject builtins = Py.getSystemState().modules.__finditem__("__builtin__");
		
		locals.put("enumerate", builtins.__getattr__("enumerate"));
		locals.put("dir", builtins.__getattr__("dir"));
		locals.put("range", builtins.__getattr__("range"));
		
		locals.put("Exception", Py.Exception);
		
		locals.put("RuntimeError", Py.RuntimeError);
		
		locals.put("pinMode", new PyCallable((args,keys)->{
			env.pinMode(
					getArg(0,"pin",args,keys).asInt(),
					getArg(1,"mode",args,keys).asInt()
					);
			return null;
		}));
		
		locals.put("digitalWrite", new PyCallable((args,keys)->{
			throw new RuntimeException("lakjsdlkajsdkljalskjd");
//			env.digitalWrite(
//					getArg(0,"pin",args,keys).asInt(),
//					getArg(1,"val",args,keys).asInt()
//					);
//			return null;
		}));
		
		locals.put("delay", new PyCallable((args,keys)->{
			env.delay(
					getArg(0,"ms",args,keys).asInt()
					);
			return null;
		}));
		
		return locals;
	}
	
	public static void printArgs(Object... args)
	{
		for(Object o:args)
		{
			System.out.println(o);
		}
		System.out.println();
	}
	
	public static PyObject getArg(int position, String name, PyObject[] args, String[] keys)
	{
		for(int i = 0; i<keys.length; i++)
		{
			if(keys[i].equals(name))
			{
				return args[args.length-keys.length+i];
			}
		}
		return args[position];
	}
}
