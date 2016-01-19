package org.python.compiler;

import java.util.ArrayList;
import java.util.List;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyString;

public class FunctionInstance extends PyObject
{
	private static final long serialVersionUID = 1L;
	
	public PyObject __defaults__;
	public PyObject __doc__;
	public PyObject __call__;
	
	protected CompiledCode internals;
	protected List<String> args = new ArrayList<>();
	protected int numDefaults;
	protected Function type;
	
	protected Scope closure;
	
	public FunctionInstance(Function type, PyObject defaults, Scope closure)
	{
		this(type,defaults);
		this.closure = closure;
	}
	
	public FunctionInstance(Function type, PyObject defaults)
	{
		this.type = type;
		this.args = type.args;
		this.numDefaults = type.defaults.size();
		this.internals = type.internals;
		this.__defaults__ = defaults;
		this.__doc__ = new PyString(type.doc);
		this.__call__ = this;
	}
	public String toString()
	{
		return "<function '"+type.name+"' at "+Py.idstr(this)+">";
	}
	
	public PyObject __findattr_ex__(String attr)
	{
		if(attr.equals("__closure__"))
		{
			if(closure != null)
			{
				return closure.getPretendClosure();//changing this closure will have no effect, but it's fun to pretend
			}
			else
			{
				return null;
			}
		}
		return super.__findattr_ex__(attr);
	}
	
	protected Scope call(PyObject[] givenArgs)
	{
		Scope localScope = new Scope();
		localScope.setClosure(closure);
		int requiredArgs = args.size()-numDefaults;
		for(int i = 0; i<args.size(); i++)
		{
			if(i<givenArgs.length)
			{
				localScope.set(args.get(i), givenArgs[i]);
			}
			else
			{
				localScope.set(args.get(i),__defaults__.__getitem__(i-requiredArgs));
			}
		}
		return localScope;
	}
}
