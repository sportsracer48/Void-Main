package program;

import org.python.core.PyInteger;
import org.python.core.PyObject;

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
