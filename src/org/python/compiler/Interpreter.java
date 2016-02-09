package org.python.compiler;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;

import program.SafeImport;

public class Interpreter implements Serializable
{
	private static final long serialVersionUID = -5414910241151988330L;
	
	ArrayList<PyObject> stack = new ArrayList<>();
	ArrayList<Scope> scopeStack = new ArrayList<>();
	ArrayList<StackFrame> callStack = new ArrayList<>();
	ArrayList<StackFrame> continueStack = new ArrayList<>();
	ArrayList<StackFrame> breakStack = new ArrayList<>();
	ArrayList<ExceptionHandler> exceptionStack = new ArrayList<>();
	ArrayList<FinallyFrame> finallyStack = new ArrayList<>();
	
	Hashtable<String,Integer> labelLocations = new Hashtable<>();
	String member = "";
	ItemSelectArgs itemArgs = null;
	
	Scope global = new Scope();
	CompiledCode root;
	
	private CompiledCode pSuite;
	private int pc = 0;
	boolean running;
	SafeImport safeImport;
	
	transient PrintStream stdout;
	
	public Interpreter(CompiledCode root,Hashtable<String,PyObject> globals,PrintStream stdout)
	{
		this(globals,stdout);
		this.root = root;
		this.setpSuite(root);
	}
	public Interpreter(Hashtable<String,PyObject> globals,PrintStream stdout)
	{
		this.stdout = stdout;
		if(stdout == null)
		{
			stdout = System.out;
		}
		scopeStack.add(global);
		for(String s:globals.keySet())
		{
			global.set(s, globals.get(s));
		}
		this.safeImport = (SafeImport) global.get("__import__");
	}
	
	public void setStdOut(PrintStream stdOut)
	{
		this.stdout = stdOut;
	}
	
	
	/**
	 * 
	 * @param count the maximum number of cycles to execute
	 * @return the number of cycles executed
	 * @throw any uncaught exceptions in the code
	 */
	public int execute(int count)
	{
		running = true;
		int executed = 0;
		for(int i = 0; (i<count || count<0) && running; i++)
		{
			CodeLine line = null;
			if(pc<getpSuite().code.size())
			{
				line = getpSuite().getLine(pc);
				try
				{
					evaluate(line);
					executed++;
				}
				catch(Exception e)
				{
					if(e instanceof PyException)
					{
						PyException pe = (PyException)e;
						this.loadExceptionHandlerState(new ExceptionHolder(pe, line));
					}
					else
					{
						this.loadExceptionHandlerState(new ExceptionHolder(e, line));
					}
				}
			}
			else
			{
				if(callStack.isEmpty())
				{
					running = false;
					break;
				}
				else
				{
					revertFrame();
					pushObject(Py.None);
				}
			}
		}
		return executed;
	}
	public void executeAll()
	{
		execute(-1);
	}
	public PyObject eval(String source)
	{
		pSuite = PythonCompiler.compile(source);
		pc = 0;
		if(pSuite.code.get(pSuite.code.size()-1).op.equals("POP $"))
		{
			pSuite.code.remove(pSuite.code.size()-1);
		}
		executeAll();
		if(stack.isEmpty())
		{
			return Py.None;
		}
		return popObject();
	}
	public void beginEval(String source)
	{
		pSuite = PythonCompiler.compile(source);
		pc = 0;
		if(pSuite.code.get(pSuite.code.size()-1).op.equals("POP $"))
		{
			pSuite.code.remove(pSuite.code.size()-1);
		}
		running = true;
	}
	public boolean isRunning()
	{
		return running;
	}
	public PyObject evalValue()
	{
		if(stack.isEmpty())
		{
			return Py.None;
		}
		else
		{
			return popObject();
		}
	}
	
	public void setpSuiteAndPc(CompiledCode pSuite, int pc)
	{
		this.pSuite = pSuite;
		this.pc = pc;
	}
	CompiledCode getpSuite()
	{
		return pSuite;
	}
	void setpSuite(CompiledCode pSuite)
	{
		setpSuiteAndPc(pSuite,pc);
	}
	public FinallyFrame getFinallyFrame()
	{
		if(finallyStack.isEmpty())
		{
			return null;
		}
		return finallyStack.get(0);
	}
	public void pushFinally(FinallyFrame f)
	{
		finallyStack.add(0, f);
	}
	public FinallyFrame popFinallyFrame()
	{
		return finallyStack.remove(0);
	}
	public FinallyFrame makeFinallyFrame(String finallyLabel)
	{
		return new FinallyFrame(getpSuite(), pc, getLabelLocation(finallyLabel), stack, scopeStack, callStack, breakStack, continueStack, exceptionStack, finallyStack);
	}
	public void dump()
	{
		System.out.println(this.getReturnFrame());
		System.out.println();
	}
	
	public ExceptionHandler getMatchingHandler(ExceptionHolder e)
	{
		for(ExceptionHandler handler:exceptionStack)
		{
			if(handler.matches(e))
			{
				return handler;
			}
		}
		return null;
	}
	
	public PyObject getVar(String name)
	{
		if(currentScope().isGlobal(name))
		{
			return global.get(name);
		}
		if(currentScope().containsReadable(name))
		{
			return currentScope().get(name);
		}
		else if(global.containsReadable(name))
		{
			return global.get(name);
		}
		throw new PyException(Py.NameError.getType(),Py.NameError.__call__(new PyString("Name "+name+" is not defined in this scope")));
	}
	
	public boolean hasReadable(String name)
	{
		return global.containsReadable(name) || currentScope().containsReadable(name);
	}
	
	public void setVar(String name, PyObject value)
	{
		if(currentScope().isGlobal(name))
		{
			global.set(name, value);
			return;
		}
		currentScope().set(name, value);
	}
	
	public void delVar(String name)
	{
		if(currentScope().isGlobal(name))
		{
			global.delete(name);
			return;
		}
		currentScope().delete(name);
	}
	
	public Scope currentScope()
	{
		return scopeStack.get(0);
	}
	public StackFrame getReturnFrame()
	{
		return new StackFrame(getpSuite(),pc+1,stack,scopeStack,callStack,continueStack,breakStack,exceptionStack,finallyStack);
	}
	
	private StackFrame getReturnFrame(int line)
	{
		return new StackFrame(getpSuite(),line,stack,scopeStack,callStack,continueStack,breakStack,exceptionStack,finallyStack);
	}
	
	private Scope getClosure(Set<String> referencedExternalVars)//if only
	{
		if(referencedExternalVars.isEmpty())
		{
			return null;
		}
//		if(global.scope.keySet().containsAll(referencedExternalVars))
//		{
//			return null;
//		}
		return currentScope().makeClosure(referencedExternalVars);
	}
	public void revertFrame()
	{
		StackFrame f = popFrame();
		revertFrame(f);
	}
	
	public void loadExceptionHandlerState(ExceptionHolder e)
	{
		loadExceptionHandlerState(getMatchingHandler(e),e);
	}
	public void loadExceptionHandlerState(ExceptionHandler handler,ExceptionHolder e)
	{
		FinallyFrame frame  = getFinallyFrame();
		if(handler != null)
		{
			if(frame != null && frame.triggeredBy(handler.returnTo, handler.returnLine))
			{
				frame.setReturn(handler, FinallyFrame.EXCEPT);
				frame.setReturnValue(e);
				revertFrame(frame);
				pushFinally(frame);
			}
			else
			{
				revertFrame(handler);
				pushObject(e.pythonVersion);
			}
		}
		else if(frame != null && frame.triggeredFrom(pSuite, pc))
		{
			frame.setReturn(null, FinallyFrame.EXCEPT_UNCAUGHT);
			frame.setReturnValue(e);
			revertFrame(frame);
			pushFinally(frame);
		}
		else
		{
			if(callStack.isEmpty())
			{
				throw new VMException(e.type,e.message,e.line,e.col);
			}
			else
			{
				revertFrame();//THERE IS ONLY ONE OTHER OPTION:
				//And now we try again
				//FROM SCRATCH ;)
				loadExceptionHandlerState(e);
			}
		}
	}
	
	public void revertBreak(StackFrame f)
	{
		FinallyFrame frame  = getFinallyFrame();
		if(frame != null && frame.triggeredBy(f.returnTo, f.returnLine))
		{
			frame.setReturn(f,FinallyFrame.BREAK);
			revertFrame(frame);
			finallyStack.add(frame);
		}
		else
		{
			revertFrame(f);
		}
	}
	public void revertContinue(StackFrame f)
	{
		FinallyFrame frame  = getFinallyFrame();
		if(frame != null && frame.triggeredBy(f.returnTo, f.returnLine))
		{
			frame.setReturn(f,FinallyFrame.CONTINUE);
			revertFrame(frame);
			pushFinally(frame);
		}
		else
		{
			revertFrame(f);
			pushContinue(f);
		}
	}
	public void revertReturn(StackFrame f, PyObject returnVal)
	{
		FinallyFrame frame  = getFinallyFrame();
		if(frame != null && frame.triggeredBy(f.returnTo, f.returnLine))
		{
			frame.setReturn(f,FinallyFrame.RETURN);
			frame.setReturnValue(returnVal);
			revertFrame(frame);
			pushFinally(frame);
		}
		else
		{
			revertFrame(f);
			pushObject(returnVal);
		}
	}
	
	public void revertFrame(StackFrame f)
	{
		//revert!
		setpSuiteAndPc(f.returnTo,f.returnLine);
		stack.clear();
		scopeStack.clear();
		callStack.clear();
		continueStack.clear();
		breakStack.clear();
		exceptionStack.clear();
		finallyStack.clear();
		stack.addAll(f.stack);
		scopeStack.addAll(f.scopeStack);
		callStack.addAll(f.callStack);
		continueStack.addAll(f.continueStack);
		breakStack.addAll(f.breakStack);
		exceptionStack.addAll(f.exceptionStack);
		finallyStack.addAll(f.finallyStack);
	}
	
	public void pushObject(PyObject o)
	{
		stack.add(0,o);
	}
	
	public void pushScope(Scope s)
	{
		scopeStack.add(0,s);
	}
	
	public void pushFrame(StackFrame f)
	{
		callStack.add(0,f);
	}
	
	public void pushContinue(StackFrame f)
	{
		continueStack.add(0,f);
	}
	
	public void pushBreak(StackFrame f)
	{
		breakStack.add(0,f);
	}
	
	public void pushException(ExceptionHandler h)
	{
		exceptionStack.add(0,h);
	}
	
	public PyObject popObject()
	{
		return stack.remove(0);
	}
	
	public Scope popScope()
	{
		return scopeStack.remove(0);
	}
	
	public StackFrame popFrame()
	{
		return callStack.remove(0);
	}
	
	public StackFrame popContinue()
	{
		return continueStack.remove(0);
	}
	public StackFrame getContinue()
	{
		return continueStack.get(0);
	}
	
	public StackFrame popBreak()
	{
		return breakStack.remove(0);
	}
	
	public ExceptionHandler popException()
	{
		return exceptionStack.remove(0);
	}
	
	public void evaluate(CodeLine line)
	{
		String op = line.getOp();
		String[] tokens = op.split(" ");
		switch(tokens[0])
		{
		case "POP":
			evalPop(tokens);
			break;
		case "PUSH":
			evalPush(tokens);
			break;
		case "CALL":
			evalCall(tokens,line);
			break;
		case "BREAKTO":
			evalBreakTo(tokens);
			break;
		case "CONTINUETO":
			evalContinueTo(tokens);
			break;
		case "LABEL":
			evalLabel(tokens);
			break;
		case "JUMPIFFALSE":
			evalJumpIfFalse(tokens);
			break;
		case "ENDBREAK":
			evalEndBreak();
			break;
		case "ENDCONTINUE":
			evalEndContinue();
			break;
		case "DEF":
			evalDef(line);
			break;
		case "LAMBDA":
			evalLambda(line);
			break;
		case "JUMP":
			evalJump(tokens);
			break;
		case "SELECTMEMBER":
			evalMemberSelect(tokens);
			break;
		case "PUSHMEMBER":
			evalPushMember();
			break;
		case "POPMEMBER":
			evalPopMember();
			break;
		case "EXCEPT":
			evalExcept(tokens);
			break;
		case "ENDEXCEPT":
			evalEndExcept();
			break;
		case "PRINT":
			evalPrint();
			break;
		case "RAISE":
			evalRaise(line);
			break;
		case "BREAK":
			evalBreak();
			break;
		case "RETURN":
			evalReturn();
			break;
		case "ADD":
			evalAdd();
			break;
		case "SUB":
			evalSub();
			break;
		case "MULT":
			evalMult();
			break;
		case "DIV":
			evalDiv();
			break;
		case "MOD":
			evalMod();
			break;
		case "POW":
			evalPow();
			break;
		case "LSHIFT":
			evalLShift();
			break;
		case "RSHIFT":
			evalRShift();
			break;
		case "BITOR":
			evalBitOr();
			break;
		case "BITXOR":
			evalBitXor();
			break;
		case "BITAND":
			evalBitAnd();
			break;
		case "FLOORDIV":
			evalFloorDiv();
			break;
		case "BOOLAND":
			evalBoolAnd();
			break;
		case "BOOLOR":
			evalBoolOr();
			break;
		case "INVERT":
			evalInvert();
			break;
		case "NOT":
			evalNot();
			break;
		case "UADD":
			evalUAdd();
			break;
		case "USUB":
			evalUSub();
			break;
		case "EQ":
			evalEq();
			break;
		case "GT":
			evalGt();
			break;
		case "GTE":
			evalGte();
			break;
		case "IN":
			evalIn();
			break;
		case "IS":
			evalIs();
			break;
		case "ISNOT":
			evalIsNot();
			break;
		case "LT":
			evalLt();
			break;
		case "LTE":
			evalLte();
			break;
		case "NOTEQ":
			evalNoteq();
			break;
		case "NOTIN":
			evalNotin();
			break;
		case "IADD":
			evalIAdd();
			break;
		case "ISUB":
			evalISub();
			break;
		case "IMULT":
			evalIMult();
			break;
		case "IDIV":
			evalIDiv();
			break;
		case "IMOD":
			evalIMod();
			break;
		case "IPOW":
			evalIPow();
			break;
		case "ILSHIFT":
			evalILShift();
			break;
		case "IRSHIFT":
			evalIRShift();
			break;
		case "IBITOR":
			evalIBitOr();
			break;
		case "IBITXOR":
			evalIBitXor();
			break;
		case "IBITAND":
			evalIBitAnd();
			break;
		case "IFLOORDIV":
			evalIFloorDiv();
			break;
		case "PUSHPOINTER":
			evalPushPointer(tokens);
			break;
		case "PUSHMEMBERPOINTER":
			evalPushMemberPointer();
			break;
		case "POPITEM":
			evalPopItem();
			break;
		case "PUSHITEM":
			evalPushItem();
			break;
		case "PUSHITEMPOINTER":
			evalPushItemPointer();
			break;
		case "DELITEM":
			evalDelItem();
			break;
		case "DELMEMBER":
			evalDelMember();
			break;
		case "CONTINUE":
			evalContinue();
			break;
		case "DEL":
			evalDel(tokens);
			break;
		case "FLAGGLOBAL":
			evalFlagGlobal(tokens);
			break;
		case "FINALLY":
			evalFinally(tokens);
			break;
		case "POPFINALLY":
			evalEndFinally();
			break;
		case "REVERTFINALLY":
			evalRevertFinally();
			break;
		case "SLICE":
			evalSlice();
			break;
		case "CLASS":
			evalClass(line);
			break;
		case "IMPORT":
			evalImport(tokens);
			break;
		default:
			System.err.println("UNEXPECTED OPCODE: "+tokens[0]);
			break;
		}
	}
	private void evalImport(String[] tokens)
	{
		pushObject(safeImport.__call__(new PyString(tokens[1])));
		pc++;
	}

	private void evalSlice()
	{
		PyObject lower = popObject();
		PyObject upper = popObject();
		PyObject step = popObject();
		
		this.itemArgs = ItemSelectArgs.Slice(lower, upper, step);
		pc++;
	}
	private void evalRevertFinally()
	{
		FinallyFrame f = getFinallyFrame();
		if(f!=null)
		{
			popFinallyFrame();
			StackFrame returnTo = f.returnTo;
			if(f.tripped)
			{
				if(f.returnMode == FinallyFrame.EXCEPT_UNCAUGHT)//returnTo is null here, there is nowhere to go from an uncaught exception
				{
					ExceptionHolder e = (ExceptionHolder)f.savedValue;
					this.loadExceptionHandlerState(e);
				}
				else
				{
					revertFrame(returnTo);
					switch(f.returnMode)
					{
					case FinallyFrame.BREAK:
						break;
					case FinallyFrame.CONTINUE:
						pushContinue(returnTo);
						break;
					case FinallyFrame.RETURN:
					case FinallyFrame.EXCEPT:
						pushObject(f.savedValue);
						break;
					}
				}
			}
			else
			{
				pc++;
			}
		}
		else
		{
			pc++;
		}
	}
	private void evalEndFinally()
	{
		popFinallyFrame();
		pc++;
	}
	private void evalFinally(String[] tokens)
	{
		pushFinally(makeFinallyFrame(tokens[1]));
		pc++;
	}
	private void evalFlagGlobal(String[] tokens)
	{
		if(currentScope()!=global)
		{
			currentScope().setGlobal(tokens[1],true);
		}
		pc++;
	}
	
	private void evalDel(String[] tokens)
	{
		delVar(tokens[1]);
		pc++;
	}
	
	private void evalDelItem()
	{
		if(itemArgs == null)
		{
			PyObject key = popObject();
			PyObject target = popObject();
			target.__delitem__(key);
		}
		else if(itemArgs.type == ItemSelectArgs.SLICE)
		{
			PyObject target = popObject();
			target.__delslice__(itemArgs.start, itemArgs.end, itemArgs.step);
			itemArgs = null;
		}
		pc++;
	}
	private void evalDelMember()
	{
		PyObject target = popObject();
		target.__delattr__(member);
		pc++;
	}
	private void evalPushItemPointer()
	{
		if(itemArgs == null)
		{
			PyObject key = popObject();
			PyObject target = popObject();
			pushObject(new ItemPointer(target,key));
		}
		else if(itemArgs.type == ItemSelectArgs.SLICE)
		{
			PyObject target = popObject();
			pushObject(new SlicePointer(target,itemArgs));
			itemArgs = null;
		}
		pc++;
	}
	private void evalPushItem()
	{
		if(itemArgs == null)
		{
			PyObject key = popObject();
			PyObject target = popObject();
			pushObject(target.__getitem__(key));
		}
		else if(itemArgs.type == ItemSelectArgs.SLICE)
		{
			PyObject target = popObject();
			pushObject(target.__getslice__(itemArgs.start, itemArgs.end, itemArgs.step));
			itemArgs = null;
		}
		pc++;
	}
	private void evalPopItem()
	{
		if(itemArgs == null)
		{
			PyObject key = popObject();
			PyObject target = popObject();
			PyObject value = popObject();
			target.__setitem__(key, value);
		}
		else if(itemArgs.type == ItemSelectArgs.SLICE)
		{
			PyObject target = popObject();
			PyObject value = popObject();
			target.__setslice__(itemArgs.start, itemArgs.end, itemArgs.step, value);
			itemArgs = null;
		}
		pc++;
	}
	private void evalPushMemberPointer()
	{
		PyObject target = popObject();
		pushObject(new MemberPointer(target,member));
		pc++;
	}
	private void evalPushPointer(String[] tokens)
	{
		pushObject(new Pointer(this,tokens[1]));
		pc++;
	}
	
	
	private void evalIAdd()
	{
		PyObject o1 = popObject();
		Pointer po2 = (Pointer)popObject();
		PyObject o2 = po2.get();
		po2.set(o2._iadd(o1));
		pc++;
	}
	private void evalISub()
	{
		PyObject o1 = popObject();
		Pointer po2 = (Pointer)popObject();
		PyObject o2 = po2.get();
		po2.set(o2._isub(o1));
		pc++;
	}
	private void evalIMult()
	{
		PyObject o1 = popObject();
		Pointer po2 = (Pointer)popObject();
		PyObject o2 = po2.get();
		po2.set(o2._imul(o1));
		pc++;
	}
	private void evalIDiv()
	{
		PyObject o1 = popObject();
		Pointer po2 = (Pointer)popObject();
		PyObject o2 = po2.get();
		po2.set(o2._itruediv(o1));
		pc++;
	}
	private void evalIMod()
	{
		PyObject o1 = popObject();
		Pointer po2 = (Pointer)popObject();
		PyObject o2 = po2.get();
		po2.set(o2._imod(o1));
		pc++;
	}
	private void evalIPow()
	{
		PyObject o1 = popObject();
		Pointer po2 = (Pointer)popObject();
		PyObject o2 = po2.get();
		po2.set(o2._ipow(o1));
		pc++;
	}
	private void evalILShift()
	{
		PyObject o1 = popObject();
		Pointer po2 = (Pointer)popObject();
		PyObject o2 = po2.get();
		po2.set(o2._ilshift(o1));
		pc++;
	}
	private void evalIRShift()
	{
		PyObject o1 = popObject();
		Pointer po2 = (Pointer)popObject();
		PyObject o2 = po2.get();
		po2.set(o2._irshift(o1));
		pc++;
	}
	private void evalIBitOr()
	{
		PyObject o1 = popObject();
		Pointer po2 = (Pointer)popObject();
		PyObject o2 = po2.get();
		po2.set(o2._ior(o1));
		pc++;
	}
	private void evalIBitXor()
	{
		PyObject o1 = popObject();
		Pointer po2 = (Pointer)popObject();
		PyObject o2 = po2.get();
		po2.set(o2._ixor(o1));
		pc++;
	}
	private void evalIBitAnd()
	{
		PyObject o1 = popObject();
		Pointer po2 = (Pointer)popObject();
		PyObject o2 = po2.get();
		po2.set(o2._iand(o1));
		pc++;
	}
	private void evalIFloorDiv()
	{
		PyObject o1 = popObject();
		Pointer po2 = (Pointer)popObject();
		PyObject o2 = po2.get();
		po2.set(o2._ifloordiv(o1));
		pc++;
	}
	
	private void evalEq()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2._eq(o1));;
		pc++;
	}
	
	private void evalGt()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2._gt(o1));
		pc++;
	}
	
	private void evalGte()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2._ge(o1));
		pc++;
	}
	
	private void evalIn()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2._in(o1));
		pc++;
	}
	
	private void evalIs()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2==o1?Py.True:Py.False);
		pc++;
	}
	
	private void evalIsNot()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2!=o1?Py.True:Py.False);
		pc++;
	}
	
	private void evalLt()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2._lt(o1));
		pc++;
	}
	
	private void evalLte()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2._le(o1));
		pc++;
	}
	
	private void evalNoteq()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2._ne(o1));
		pc++;
	}
	
	private void evalNotin()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2._notin(o1));
		pc++;
	}
	
	private void evalInvert()
	{
		pushObject(popObject().__invert__());
		pc++;
	}
	private void evalNot()
	{
		if(popObject().__nonzero__())
		{
			pushObject(Py.False);
		}
		else
		{
			pushObject(Py.True);
		}
		pc++;
	}
	private void evalUAdd()
	{
		pushObject(popObject().__pos__());
		pc++;
	}
	private void evalUSub()
	{
		pushObject(popObject().__neg__());
		pc++;
	}
	
	private void evalBoolAnd()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		if(o2.__nonzero__() && o1.__nonzero__())
		{
			pushObject(Py.True);
		}
		else
		{
			pushObject(Py.False);
		}
		pc++;
	}
	private void evalBoolOr()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		if(o2.__nonzero__() || o1.__nonzero__())
		{
			pushObject(Py.True);
		}
		else
		{
			pushObject(Py.False);
		}
		pc++;
	}
	
	private void evalAdd()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2._add(o1));
		pc++;
	}
	private void evalSub()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2._sub(o1));
		pc++;
	}
	private void evalMult()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2._mul(o1));
		pc++;
	}
	private void evalDiv()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2._truediv(o1));
		pc++;
	}
	private void evalMod()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2._mod(o1));
		pc++;
	}
	private void evalPow()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2._pow(o1));
		pc++;
	}
	private void evalLShift()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2._lshift(o1));
		pc++;
	}
	private void evalRShift()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2._rshift(o1));
		pc++;
	}
	private void evalBitOr()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2._or(o1));
		pc++;
	}
	private void evalBitXor()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2._xor(o1));
		pc++;
	}
	private void evalBitAnd()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2._and(o1));
		pc++;
	}
	private void evalFloorDiv()
	{
		PyObject o1 = popObject();
		PyObject o2 = popObject();
		pushObject(o2._floordiv(o1));
		pc++;
	}
	
	
	private void evalReturn()
	{
		PyObject returnVal = popObject();
		StackFrame f = this.popFrame();
		this.revertReturn(f,returnVal);
	}
	
	
	private void evalBreak()
	{
		StackFrame f = this.popBreak();
		this.revertBreak(f);
	}
	
	private void evalContinue()
	{
		StackFrame f = this.popContinue();
		this.revertContinue(f);
	}

	

	private void evalRaise(CodeLine line)
	{
		PyObject e = popObject();
		if(e.__getattr__("__class__").__getattr__("__name__").asString().equals("type"))
		{
			e=e.__call__();//hope this is a constructor!
		}
		loadExceptionHandlerState(new ExceptionHolder(e,line));
	}

	private void evalPrint()
	{
		PyString str = popObject().__str__();
		stdout.print(str);
		pc++;
	}

	private void evalEndExcept()
	{
		popException();
		pc++;
	}

	private void evalExcept(String[] tokens)
	{
		pushException(new ExceptionHandler(getpSuite(),getLabelLocation(tokens[2]),tokens[1],
				stack,scopeStack,callStack,continueStack,breakStack,exceptionStack,finallyStack));
		pc++;
	}

	private void evalPushMember()
	{
		PyObject target = popObject();
		pushObject(target.__getattr__(member));
		pc++;
	}
	
	private void evalPopMember()
	{
		PyObject target = popObject();
		target.__setattr__(member, popObject());
		pc++;
	}

	private void evalMemberSelect(String[] tokens)
	{
		member = tokens[1];
		pc++;
	}

	private void evalJump(String[] tokens)
	{
		pc = getLabelLocation(tokens[1]);
	}

	private void evalPop(String[] tokens)
	{
		setVar(tokens[1],popObject());
		pc++;
	}

	private void evalDef(CodeLine line)
	{
		if(line instanceof DefLine)
		{
			DefLine def = (DefLine)line;
			Function f = def.defined;
			PyObject defaults = null;
			if(hasReadable(f.defaultsSymbol))
			{
				defaults = getVar(f.defaultsSymbol);
			}
			if(scopeStack.size()>1)
			{
				setVar(f.name,new FunctionInstance(f,defaults,getClosure(f.internals.referencedExternalVars)));
			}
			else
			{
				setVar(f.name,new FunctionInstance(f,defaults));
			}
		}
		pc++;
	}
	
	private void evalLambda(CodeLine line)
	{
		if(line instanceof LambdaLine)
		{
			LambdaLine def = (LambdaLine)line;
			Function f = def.defined;
			if(scopeStack.size()>1)
			{
				pushObject(new FunctionInstance(f,getVar(f.defaultsSymbol),getClosure(f.internals.referencedExternalVars)));
			}
			else
			{
				pushObject(new FunctionInstance(f,getVar(f.defaultsSymbol)));
			}
		}
		pc++;
	}

	private int getLabelLocation(String label)
	{
		if(labelLocations.containsKey(label))
		{
			return labelLocations.get(label);
		}
		else
		{
			for(int i = 0; i<getpSuite().code.size(); i++)
			{
				String op = getpSuite().getLine(i).getOp();
				if(op.startsWith("LABEL") && op.contains(label))
				{
					labelLocations.put(label, i);
					return i;
				}
			}
		}
		throw new RuntimeException("Cannot find label "+label);
	}

	private void evalEndContinue()
	{
		popContinue();
		pc++;
	}

	private void evalEndBreak()
	{
		popBreak();
		pc++;
	}

	private void evalJumpIfFalse(String[] tokens)
	{
		PyObject val = popObject();
		if(!val.__nonzero__())
		{
			String label = tokens[1];
			pc = getLabelLocation(label);
		}
		else
		{
			pc++;
		}
	}

	private void evalLabel(String[] tokens)
	{
		String label = tokens[1];
		if(!labelLocations.containsKey(label))
		{
			labelLocations.put(label, pc);
		}
		pc++;
	}

	private void evalContinueTo(String[] tokens)
	{
		int line = getLabelLocation(tokens[1]);
		pushContinue(getReturnFrame(line));
		pc++;
	}

	private void evalBreakTo(String[] tokens)
	{
		int line = getLabelLocation(tokens[1]);
		pushBreak(getReturnFrame(line));
		pc++;
	}

	private void evalCall(String[] tokens,CodeLine line)
	{
		PyObject callable = popObject();
		
		PyObject[] args;
		if(tokens.length==2)
		{
			int numArgs = Integer.parseInt(tokens[1]);
			args = new PyObject[numArgs];
			for(int i = numArgs-1; i>=0; i--)
			{
				args[i] = popObject();
			}
		}
		else
		{
			PyObject argsList = popObject();
			//for now, let's assume this is finite
			int numArgs = argsList.__len__();
			args = new PyObject[numArgs];
			for(int i = 0; i<numArgs; i++)
			{
				args[i] = argsList.__getitem__(i);
			}
		}
		
		if(callable instanceof ClassInstance)
		{
			PyObject target = ((ClassInstance) callable).construct();
			PyList initArgs = new PyList(args);
			args = new PyObject[]{target,initArgs};
			callable = Constructor.globalInstance;
		}
		if(callable instanceof ObjectInstance)
		{
			callable = callable.__getattr__("__call__");
		}
		
		
		if(callable instanceof FunctionInstance)
		{
			FunctionInstance f = (FunctionInstance)callable;
			pushFrame(getReturnFrame());
			breakStack.clear();
			continueStack.clear();
			finallyStack.clear();
			exceptionStack.clear();
			setpSuiteAndPc(f.internals,0);
			pushScope(f.call(args));
		}
		else//exit point here! This is how the code can interact with the rest of the world.
		{
			try
			{
				pushObject(callable.__call__(args,new String[]{}));
			}
			catch(PyException e)
			{
				loadExceptionHandlerState(new ExceptionHolder(e,line));
			}
			catch(Exception e)
			{
				loadExceptionHandlerState(new ExceptionHolder(e,line));
			}
			pc++;
		}
	}
	private void evalClass(CodeLine line)
	{
		ClassLine def = (ClassLine)line;
		PyObject[] bases = new PyObject[def.numBases];
		for(int i = 0; i<bases.length; i++)
		{
			bases[bases.length-i-1] = popObject();
		}
		ClassInstance ci;
		if(scopeStack.size()>1)
		{
			ci = new ClassInstance(def.defined,getClosure(def.defined.internals.referencedExternalVars),bases);
		}
		else
		{
			ci = new ClassInstance(def.defined,bases);
		}
		setVar(def.defined.name,ci);
		
		pushFrame(getReturnFrame());
		breakStack.clear();
		continueStack.clear();
		finallyStack.clear();
		exceptionStack.clear();
		setpSuiteAndPc(ci.internals,0);
		pushScope(ci.call());
		//begin evaluating the class
	}

	public void evalPush(String[] tokens)
	{
		if(tokens.length == 2)
		{
			pushObject(getVar(tokens[1]));
			pc++;
			return;
		}
		else
		{
			switch(tokens[1])
			{
			case "NUMBER":
				try
				{
					int i = Integer.parseInt(tokens[2]);
					pushObject(new PyInteger(i));
					pc++;
					return;
				}
				catch(NumberFormatException e)
				{
					
				}
				try
				{
					double d = Double.parseDouble(tokens[2]);
					pushObject(new PyFloat(d));
					pc++;
					return;
				}
				catch(NumberFormatException e)
				{
					
				}
				break;
			case "STRING":
				StringBuilder out = new StringBuilder();
				for(int i = 2; i<tokens.length;i++)
				{
					out.append(tokens[i]);
					if(i<tokens.length-1)
					{
						out.append(' ');
					}
				}
				String result = out.substring(1, out.length()-1);
				result = result.replaceAll("\\\\n", "\n");
				pushObject(new PyString(result));
				pc++;
				break;
			}
		}
	}

	public void stop()
	{
		running = false;
	}
}
