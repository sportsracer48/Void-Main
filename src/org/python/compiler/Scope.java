package org.python.compiler;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.stream.Collectors;

import org.python.core.PyCell;
import org.python.core.PyObject;
import org.python.core.PyTuple;

public class Scope
{
	Hashtable<String,PyObject> scope;
	HashSet<String> global = new HashSet<>();
	Scope closure;
	
	public Scope(Hashtable<String, PyObject> scope)
	{
		this.scope = scope;
	}
	public Scope()
	{
		scope = new Hashtable<>();
	}
	
	public PyTuple getPretendClosure()//because we are pretending this does something
	{
		Set<PyObject> allMembers = getAllMembers();
		PyObject[] tuple = new PyObject[allMembers.size()];
		int i = 0;
		for(PyObject member:allMembers)
		{
			PyCell cell = new PyCell();
			cell.ob_ref = member;
			tuple[i]=cell;
			i++;
		}
		return new PyTuple(tuple);
	}
	public Set<PyObject> getAllMembers()
	{
		Set<PyObject> firstLevel = scope.keySet().
				stream().
				filter(s->!s.contains("$")).
				map(scope::get).
				collect(Collectors.toSet());
		Set<PyObject> toReturn = new HashSet<>();
		toReturn.addAll(firstLevel);
		if(closure!=null)
		{
			toReturn.addAll(closure.getAllMembers());
		}
		return toReturn;
	}
	
	public boolean containsReadable(String s)
	{
		if(closure!=null && closure.containsReadable(s))
		{
			return true;
		}
		return scope.containsKey(s);
	}
	public boolean containsWritable(String s)
	{
		return scope.containsKey(s);
	}
	
	public PyObject get(String s)
	{
		if(scope.containsKey(s))
		{
			return scope.get(s);
		}
		if(closure!=null && closure.containsReadable(s))
		{
			return closure.get(s);
		}
		return null;
	}
	
	public boolean isGlobal(String s)
	{
		return global.contains(s);
	}
	public void setGlobal(String s,boolean isGlobal)
	{
		if(isGlobal)
		{
			global.add(s);
		}
		else
		{
			global.remove(s);
		}
	}
	
	public void set(String s, PyObject v)
	{
		if(s.equals("$"))
		{
			return;
		}
		if(scope.containsKey(s))
		{
			scope.remove(s);
		}
		scope.put(s, v);
	}
	public String toString()
	{
		return scope.keySet().stream().map(s->s+" : "+scope.get(s)).reduce((s1,s2)->s1+"\n\t"+s2).orElse("") + (closure==null?"":("CLOSURE: "+closure.toString()));
	}
	public void delete(String name)
	{
		scope.remove(name);
	}
	public void setClosure(Scope closure)
	{
		this.closure = closure;
	}
	public Scope makeClosure(Set<String> referencedExternalVars)
	{
		Scope toReturn = new Scope();
		for(String var:referencedExternalVars)
		{
			PyObject val = this.get(var);
			if(val != null)
			{
				toReturn.set(var, this.get(var));
			}
		}
		return toReturn;
	}
}
