package org.python.compiler;

import org.python.core.Py;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PySet;

public class ObjectInstance extends PyObject
{
	private static final long serialVersionUID = -1619018145777009108L;

	public PyObject __class__;
	
	protected ClassInstance type;
	protected Scope attributes;
	
	public ObjectInstance(ClassInstance type)
	{
		this.type = type;
		this.__class__ = type;
		attributes = new Scope();
		
		for(String key:type.staticMembers.scope.keySet())
		{
			PyObject val = type.staticMembers.get(key);
			if(val instanceof FunctionInstance && ((FunctionInstance) val).args.size()>0 && ((FunctionInstance) val).args.get(0).equals("self"))
				//boy that is a dumb way to differentiate static functions from instance functions. oh well
			{
				attributes.set(key, new AttrFunctionInstance((FunctionInstance)val,this));
			}
			else
			{
				attributes.set(key, val);
			}
		}
	}
	
	
	public String toString()
	{
		return "<"+type.toString()+" object at "+Py.idstr(this)+">";
	}
	public PyObject __dir__()
	{
		PyList toReturn = new PyList();
		toReturn.extend(super.__dir__());
		toReturn.extend(type.__dir__());
		for(String key:attributes.scope.keySet())
		{
			if(!key.contains("$"))
			{
				toReturn.add(key);
			}
		}
		PySet dupes = new PySet(toReturn);
		return new PyList((PyObject)dupes);
	}
	public PyObject __findattr_ex__(String attr)
	{
		if(attr.equals("__class__"))//fuck your shitty rules anyway.
		{
			return __class__;
		}
		if(attributes.containsReadable(attr))
		{
			return attributes.get(attr);
		}
		else
		{
			PyObject val = type.__getattr__(attr);
			//boy that is a dumb way to differentiate static functions from instance functions. oh well
			if(val instanceof FunctionInstance && ((FunctionInstance) val).args.size()>0 && ((FunctionInstance) val).args.get(0).equals("self"))
			{
				PyObject result = new AttrFunctionInstance((FunctionInstance)val,this);
				attributes.set(attr, result);
				return result;
			}
			else
			{
				return val;
			}
		}
	}

	public void __setattr__(String attr, PyObject val)
	{
		attributes.set(attr, val);
	}
	public void __delattr__(String attr)
	{
		attributes.delete(attr);
	}
	
}
