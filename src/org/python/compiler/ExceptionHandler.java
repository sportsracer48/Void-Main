package org.python.compiler;

import java.util.ArrayList;

import org.python.core.PyObject;
import org.python.core.PyType;

public class ExceptionHandler extends StackFrame
{
	private static final long serialVersionUID = -1581904180503080118L;
	
	String type;
	
	public ExceptionHandler(CompiledCode suite, int line, String type,
			ArrayList<PyObject> stack,ArrayList<Scope> scopeStack,ArrayList<StackFrame> callStack,
			ArrayList<StackFrame> continueStack,ArrayList<StackFrame> breakStack,ArrayList<ExceptionHandler> exceptionStack, ArrayList<FinallyFrame> finallyStack)
	{
		super(suite,line,stack,scopeStack,callStack,continueStack,breakStack,exceptionStack,finallyStack);
		this.type = type;
	}

	public boolean matches(ExceptionHolder e)
	{
		if(type.equals("All$"))
		{
			return true;
		}
		if(e.type.equals(type))
		{
			return true;
		}
		PyType pyType = e.pythonType;
		if(pyType != null)
		{
			PyObject bases = pyType.getBases();
			for(int i = 0; i<bases.__len__(); i++)
			{
				PyObject superType = bases.__getitem__(i);
				String superTypeName = superType.__getattr__("__name__").asString();
				if(superTypeName.equals(type))
				{
					return true;
				}
			}
		}
		Class<?> javaType = e.javaType;
		while(javaType != null)
		{
			if(javaType.getSimpleName().equals(type))
			{
				return true;
			}
			javaType = javaType.getSuperclass();
		}
		return false;
	}
	public String toString()
	{
		return type;
	}
}
