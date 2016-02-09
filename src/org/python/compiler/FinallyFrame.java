package org.python.compiler;

import java.util.ArrayList;

import org.python.core.PyObject;

public class FinallyFrame extends StackFrame
{
	private static final long serialVersionUID = 8991797749942000828L;
	
	public static final int BREAK = 0;
	public static final int CONTINUE = 1;
	public static final int RETURN = 2;
	public static final int EXCEPT = 3;
	public static final int EXCEPT_UNCAUGHT = 3;
	
	int startLine,endLine;
	CompiledCode code;
	
	boolean tripped = false;
	StackFrame returnTo;
	int returnMode = -1;
	PyObject savedValue;
	
	public FinallyFrame(CompiledCode finallyCode,int startLine, int finallyLine,
			ArrayList<PyObject> stack, ArrayList<Scope> scopeStack,
			ArrayList<StackFrame> callStack,
			ArrayList<StackFrame> continueStack,
			ArrayList<StackFrame> breakStack,
			ArrayList<ExceptionHandler> exceptionStack,
			ArrayList<FinallyFrame> finallyStack)
	{
		super(finallyCode, finallyLine, stack, scopeStack, callStack, continueStack,
				breakStack, exceptionStack,finallyStack);
		this.startLine = startLine;
		this.endLine = finallyLine;
		this.code = finallyCode;
	}
	public boolean triggeredBy(CompiledCode code,int pc)
	{
		if(tripped)
		{
			//how can this trigger the finally
			//WHEN IT'S ALREADY HERE????
			return false;
		}
		return this.code != code || pc<startLine || pc>endLine;
	}
	
	public boolean triggeredFrom(CompiledCode code,int pc)
	{
		if(tripped)
		{
			//how can this trigger the finally
			//WHEN IT'S ALREADY HERE????
			return false;
		}
		return code == this.code && pc>=startLine && pc <= endLine;
	}
	
	public void setReturn(StackFrame returnTo, int returnMode)
	{
		this.returnTo = returnTo;
		this.returnMode = returnMode;
		tripped = true;
	}
	public void setReturnValue(PyObject returnValue)
	{
		this.savedValue = returnValue;
		tripped = true;
	}
}
