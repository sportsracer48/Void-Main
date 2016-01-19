package org.python.compiler;

import org.python.core.Py;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PySet;
import org.python.core.PyString;
import org.python.core.PyType;

public class ClassInstance extends PyObject
{	
	private static final long serialVersionUID = 1L;
	
	public PyObject __doc__;
	public PyObject __call__ = this;
	public PyObject __bases__;
	
	protected Scope staticMembers;
	protected CompiledCode internals;
	protected PyObject[] bases;
	protected Klass type;
	
	public ClassInstance(Klass type, PyObject[] bases)
	{
		this(type,null,bases);
	}
	
	public ClassInstance(Klass type, Scope closure,  PyObject[] bases)
	{
		this.type = type;
		this.internals = type.internals;
		this.__doc__ = new PyString(internals.doc);
		
		staticMembers = new Scope();
		staticMembers.setClosure(closure);
		
		this.bases = bases;
		this.__bases__ = new PyList(bases);
	}
	
	protected Scope call()
	{
		return staticMembers;
	}
	protected ObjectInstance construct()
	{
		ObjectInstance result = new ObjectInstance(this);
		return result;
	}
	
	public String toString()
	{
		return "<class '"+type.name+"' at "+Py.idstr(this)+">";
	}
	
	public PyObject __dir__()
	{
		PyList toReturn = new PyList();
		toReturn.extend(super.__dir__());
		for(PyObject base:bases)
		{
			toReturn.extend(base.__dir__());
		}
		for(String key:staticMembers.scope.keySet())
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
		if(attr.equals("__class__"))
		{
			return PyType.TYPE;
		}
		if(staticMembers.containsReadable(attr))
		{
			return staticMembers.get(attr);
		}
		for(PyObject base:bases)
		{
			PyObject result = base.__findattr__(attr);
			if(result!=null)
			{
				return result;
			}
		}
		if(super.__findattr_ex__(attr) != null)
		{
			return super.__findattr_ex__(attr);
		}
		return Py.None;
	}
	public void __setattr__(String attr, PyObject val)
	{
		staticMembers.set(attr, val);
	}
	public void __delattr__(String attr)
	{
		staticMembers.delete(attr);
	}
}
