package program;

import org.python.compiler.NewDict;
import org.python.compiler.NewList;
import org.python.compiler.NewSet;
import org.python.compiler.NewTuple;
import org.python.core.Py;
import org.python.core.PyInteger;
import org.python.core.PyObject;

import game.map.UnitController;

import java.util.Hashtable;

public class OuinoPythonEnvironment
{
	public static Hashtable<String,PyObject> getGlobals(OuinoEnvironment env)
	{
		Hashtable<String,PyObject> locals = new Hashtable<>();
		
		locals.put("HIGH", new PyInteger(OuinoEnvironment.HIGH));
		locals.put("LOW", new PyInteger(OuinoEnvironment.LOW));
		locals.put("OUTPUT", new PyInteger(OuinoEnvironment.OUTPUT));
		locals.put("INPUT", new PyInteger(OuinoEnvironment.INPUT));
		locals.put("INPUT_PULLUP", new PyInteger(OuinoEnvironment.INPUT_PULLUP));
		locals.put("SERIAL_INPUT", new PyInteger(OuinoEnvironment.SERIAL_INPUT));
		locals.put("SERIAL_CLOCK", new PyInteger(OuinoEnvironment.SERIAL_CLOCK));
		
		
		locals.put("True", Py.True);
		locals.put("False", Py.False);
		locals.put("$newList", new NewList());
		locals.put("$newDict", new NewDict());
		locals.put("$newSet", new NewSet());
		locals.put("$newTuple", new NewTuple());
		locals.put("None", Py.None);
		locals.put("Ellipsis", Py.Ellipsis);
		locals.put("AssertionError", Py.AssertionError);
		locals.put("__debug__", Py.True);
		
		PyObject builtins = Py.getSystemState().modules.__finditem__("__builtin__");
		
		locals.put("enumerate", builtins.__getattr__("enumerate"));
		locals.put("dir", builtins.__getattr__("dir"));
		locals.put("range", builtins.__getattr__("range"));
		locals.put("__import__", builtins.__getattr__("__import__"));
		
		locals.put("Exception", Py.Exception);
		
		locals.put("RuntimeError", Py.RuntimeError);
		
		if(env != null)
		{
			locals.put("pinMode", new PyCallable((args,keys)->{
				env.pinMode(
						getArg(0,"pin",args,keys).asInt(),
						getArg(1,"mode",args,keys).asInt()
						);
				return null;
			}));
			
			locals.put("serialMode", new PyCallable((args,keys)->{
				env.serialMode(
						getArg(0,"clockPin",args,keys).asInt(),
						getArg(1,"signalPin",args,keys).asInt()
						);
				return null;
			}));
			
			locals.put("digitalWrite", new PyCallable((args,keys)->{
				env.digitalWrite(
						getArg(0,"pin",args,keys).asInt(),
						getArg(1,"val",args,keys).asInt()
						);
				return null;
			}));
			
			locals.put("delay", new PyCallable((args,keys)->{
				env.delay(
						getArg(0,"ms",args,keys).asInt()
						);
				return null;
			}));
			
			locals.put("digitalRead", new PyCallable((args,keys)->{
				return new PyInteger(
						env.digitalRead(
								getArg(0,"pin",args,keys).asInt()
								)
						);
			}));
			
			locals.put("serialRead", new PyCallable((args,keys)->{
				return new PyInteger(
						env.serialRead(
								getArg(0,"pin",args,keys).asInt()
								)
						);
			}));
			
			locals.put("tick", new PyCallable((args,keys)->{
				UnitController controller = env.getUnitController();
				if(controller != null)
				{
					controller.tick();
				}
				return null;
			}));
		}
		
		
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
