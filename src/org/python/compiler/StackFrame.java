package org.python.compiler;

import java.io.Serializable;
import java.util.ArrayList;

import org.python.core.PyObject;

public class StackFrame implements Serializable
{
	private static final long serialVersionUID = 1338920319479492915L;
	
	CompiledCode returnTo;
	int returnLine;
	
	ArrayList<PyObject> stack = new ArrayList<>();
	ArrayList<Scope> scopeStack = new ArrayList<>();
	ArrayList<StackFrame> callStack = new ArrayList<>();
	ArrayList<StackFrame> continueStack = new ArrayList<>();
	ArrayList<StackFrame> breakStack = new ArrayList<>();
	ArrayList<ExceptionHandler> exceptionStack = new ArrayList<>();
	ArrayList<FinallyFrame> finallyStack = new ArrayList<>();
	
	public StackFrame(CompiledCode returnTo, int returnLine,
			ArrayList<PyObject> stack,ArrayList<Scope> scopeStack,ArrayList<StackFrame> callStack,
			ArrayList<StackFrame> continueStack,ArrayList<StackFrame> breakStack,ArrayList<ExceptionHandler> exceptionStack,ArrayList<FinallyFrame> finallyStack)
	{
		this.returnTo = returnTo;
		this.returnLine = returnLine;
		
		this.stack.addAll(stack);
		this.scopeStack.addAll(scopeStack);
		this.callStack.addAll(callStack);
		this.continueStack.addAll(continueStack);
		this.breakStack.addAll(breakStack);
		this.exceptionStack.addAll(exceptionStack);
		this.finallyStack.addAll(finallyStack);
	}
	
	public String toString()
	{
		return new String(""
				+ "stack: "+stack+"\n"
				+ "scopes: "+scopeStack+"\n"
				+ "calls: "+callStack+"\n"
				+ "continue: "+continueStack+"\n"
				+ "break: "+breakStack+"\n"
				+ "pc: "+returnLine+"\n");
	}
}
