package org.python.compiler;

import org.python.core.Py;

public class Constructor
{	
	static Function globalConstructor;
	static FunctionInstance globalInstance;
	static
	{
		CompiledCode internals = new CompiledCode();
		//this.__init__(*args)
		internals.addLine("PUSH args");
		internals.addLine("PUSH this");
		internals.addLine("SELECTMEMBER __init__");
		internals.addLine("PUSHMEMBER");
		internals.addLine("CALL");
		internals.addLine("POP $");
		
		//return this
		internals.addLine("PUSH this");
		internals.addLine("RETURN");
		globalConstructor = new Function(internals,"this","args");
		globalInstance = new FunctionInstance(globalConstructor,Py.None);
	}
}
