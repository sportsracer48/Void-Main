package org.python.compiler;

import org.python.core.Py;
import org.python.core.PyObject;

public class AttrFunctionInstance extends FunctionInstance
{
	private static final long serialVersionUID = -763343636161697573L;
	
	PyObject self;
	FunctionInstance staticInstance;
	public String toString()
	{
		return "<bound function '"+type.name+"' of "+self.__repr__()+" at "+Py.idstr(this)+">";
	}
	public AttrFunctionInstance(FunctionInstance staticInstance, PyObject self)
	{
		super(staticInstance.type,staticInstance.__defaults__,staticInstance.closure);
		this.self = self;
		this.staticInstance = staticInstance;
	}
	protected Scope call(PyObject[] givenArgs)
	{
		PyObject[] staticArgs = new PyObject[givenArgs.length+1];
		System.arraycopy(givenArgs, 0, staticArgs, 1, givenArgs.length);
		staticArgs[0] = self;
		return staticInstance.call(staticArgs);
	}
}
