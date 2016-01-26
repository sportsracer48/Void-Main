package org.python.compiler;

import java.util.ArrayList;
import java.util.HashSet;

import org.python.antlr.PythonTree;
import org.python.antlr.Visitor;
import org.python.antlr.ast.Assert;
import org.python.antlr.ast.Assign;
import org.python.antlr.ast.Attribute;
import org.python.antlr.ast.AugAssign;
import org.python.antlr.ast.BinOp;
import org.python.antlr.ast.BoolOp;
import org.python.antlr.ast.Break;
import org.python.antlr.ast.Call;
import org.python.antlr.ast.ClassDef;
import org.python.antlr.ast.Compare;
import org.python.antlr.ast.Continue;
import org.python.antlr.ast.Delete;
import org.python.antlr.ast.Dict;
import org.python.antlr.ast.DictComp;
import org.python.antlr.ast.Ellipsis;
import org.python.antlr.ast.ExceptHandler;
import org.python.antlr.ast.Exec;
import org.python.antlr.ast.Expr;
import org.python.antlr.ast.Expression;
import org.python.antlr.ast.ExtSlice;
import org.python.antlr.ast.For;
import org.python.antlr.ast.FunctionDef;
import org.python.antlr.ast.GeneratorExp;
import org.python.antlr.ast.Global;
import org.python.antlr.ast.If;
import org.python.antlr.ast.IfExp;
import org.python.antlr.ast.Import;
import org.python.antlr.ast.ImportFrom;
import org.python.antlr.ast.Index;
import org.python.antlr.ast.Lambda;
import org.python.antlr.ast.List;
import org.python.antlr.ast.ListComp;
import org.python.antlr.ast.Name;
import org.python.antlr.ast.Num;
import org.python.antlr.ast.Pass;
import org.python.antlr.ast.Print;
import org.python.antlr.ast.Raise;
import org.python.antlr.ast.Repr;
import org.python.antlr.ast.Return;
import org.python.antlr.ast.Set;
import org.python.antlr.ast.SetComp;
import org.python.antlr.ast.Slice;
import org.python.antlr.ast.Str;
import org.python.antlr.ast.Subscript;
import org.python.antlr.ast.Suite;
import org.python.antlr.ast.TryExcept;
import org.python.antlr.ast.TryFinally;
import org.python.antlr.ast.Tuple;
import org.python.antlr.ast.UnaryOp;
import org.python.antlr.ast.While;
import org.python.antlr.ast.With;
import org.python.antlr.ast.Yield;
import org.python.antlr.ast.alias;
import org.python.antlr.ast.boolopType;
import org.python.antlr.ast.cmpopType;
import org.python.antlr.ast.comprehension;
import org.python.antlr.ast.expr_contextType;
import org.python.antlr.ast.operatorType;
import org.python.antlr.base.excepthandler;
import org.python.antlr.base.expr;
import org.python.antlr.base.stmt;
import org.python.core.AstList;
import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PySyntaxError;

public class CompiledCode extends Visitor
{
	HashSet<String> declaredVars = new HashSet<>();
	HashSet<String> referencedExternalVars = new HashSet<>();
	ArrayList<CodeLine> code = new ArrayList<>();
	static long unique = 0;
	int line = 0;
	int col = 0;
	boolean firstLine = true;
	String doc = "";
	
	public static CompiledCode codeFor(PythonTree tree,expr expression) throws Exception
	{
		java.util.List<stmt> expressions = new ArrayList<>();
		expressions.add(new Return(tree,expression));
		CompiledCode result = new CompiledCode(expressions);
		return result;
	}
	
	public CompiledCode(PythonTree code) throws Exception
	{
		init(code.getChildren());
	}
	

	public <T extends PythonTree> CompiledCode(java.util.List<T> code) throws Exception
	{
		init(code);
	}
	
	public CompiledCode()
	{
		
	}
	
	private <T extends PythonTree> void init(java.util.List<T> code) throws Exception
	{
		for(T child:code)
		{
			visit(child);
			if(child instanceof Expr)
			{
				if(((Expr) child).getInternalValue() instanceof Str && firstLine)
				{
					this.doc = ((Str) ((Expr) child).getInternalValue()).getInternalS().toString();//it casts everything to a string okay?
				}
				addLine("POP $");
			}
			firstLine = false;
		}
	}
	public Object visit(PythonTree node) throws Exception 
	{
		line = node.getLine();
		col = node.getCharPositionInLine();
        return super.visit(node);
    }

	public void addLine(String line)
	{
		addLine(new CodeLine(line,this.line,col));
	}
	public void addLine(CodeLine line)
	{
		String[] tokens = line.op.split(" ");
		if(line instanceof ClassLine)
		{
			declaredVars.add(((ClassLine) line).defined.name);
			referencedExternalVars.addAll(((ClassLine) line).defined.internals.referencedExternalVars);
			referencedExternalVars.removeAll(declaredVars);
		}
		else if(line instanceof DefLine)
		{
			declaredVars.add(((DefLine) line).defined.name);
			referencedExternalVars.addAll(((DefLine) line).defined.internals.referencedExternalVars);
			referencedExternalVars.removeAll(declaredVars);
		}
		else if(line instanceof LambdaLine)
		{
			referencedExternalVars.addAll(((LambdaLine) line).defined.internals.referencedExternalVars);
			referencedExternalVars.removeAll(declaredVars);
		}
		else if(tokens[0].equals("PUSH") && tokens.length == 2)
		{
			if(!declaredVars.contains(tokens[1]))
			{
				referencedExternalVars.add(tokens[1]);
			}
		}
		else if(tokens[0].equals("POP") && tokens.length == 2)
		{
			declaredVars.add(tokens[1]);
		}
		code.add(line);
	}
	public void insert(CompiledCode suite)
	{
		code.addAll(suite.code);
		referencedExternalVars.addAll(suite.referencedExternalVars);
	}
	
	public void dump()
	{
		for(CodeLine c:code)
		{
			System.out.println(c.toString(0));
		}
	}
	
	public static String getUnique(String prefix)
	{
		String toReturn = prefix + unique;
		unique++;
		return toReturn;
	}


	public Object visitExpression(Expression node) throws Exception
	{
		traverse(node);
		return null;
	}



	public Object visitSuite(Suite node) throws Exception
	{
		System.out.println("traversing suite?");
		traverse(node);
		return null;
	}



	public Object visitFunctionDef(FunctionDef node) throws Exception
	{
		String name = node.getInternalNameNode().getInternalId();
		CompiledCode internals = new CompiledCode(node.getInternalBody());
		Function result = new Function(name,internals,node.getInternalArgs());
		insert(result.initDefaults);
		addLine(new DefLine(result,line,col));
		return null;
	}



	public Object visitClassDef(ClassDef node) throws Exception
	{
		String name = node.getInternalNameNode().getInternalId();
		CompiledCode internals = new CompiledCode(node.getInternalBody());
		Klass result = new Klass(name,internals);
		for(expr ex:node.getInternalBases())
		{
			visit(ex);
		}
		addLine(new ClassLine(result,node.getInternalBases().size(),line,col));
		addLine("POP $");//running off the end of a code block pushes None, to make functions always return something. Here we need to pop that None.
		return null;
	}



	public Object visitReturn(Return node) throws Exception
	{
		if(node.getInternalValue() != null)
		{
			visit(node.getInternalValue());
		}
		else
		{
			addLine("PUSH None");
		}
		addLine("RETURN");
		return null;
	}



	public Object visitDelete(Delete node) throws Exception
	{
		traverse(node);
		return null;
	}



	public Object visitAssign(Assign node) throws Exception
	{
		visit(node.getInternalValue());
		visit(node.getInternalTargets().get(0));
		return null;
	}
	
	public Object visitAugAssign(AugAssign node) throws Exception
	{
		visit(node.getInternalTarget());
		visit(node.getInternalValue());
		switch(node.getInternalOp())
		{
		case Add:
			addLine("IADD");
			break;
	    case Sub:
	    	addLine("ISUB");
	    	break;
	    case Mult:
	    	addLine("IMULT");
	    	break;
	    case Div:
	    	addLine("IDIV");
	    	break;
	    case Mod:
	    	addLine("IMOD");
	    	break;
	    case Pow:
	    	addLine("IPOW");
	    	break;
	    case LShift:
	    	addLine("ILSHIFT");
	    	break;
	    case RShift:
	    	addLine("IRSHIFT");
	    	break;
	    case BitOr:
	    	addLine("IBITOR");
	    	break;
	    case BitXor:
	    	addLine("IBITXOR");
	    	break;
	    case BitAnd:
	    	addLine("IBITAND");
	    	break;
	    case FloorDiv:
	    	addLine("IFLOORDIV");
	    	break;
	    case UNDEFINED:
	    	throw new RuntimeException("undefined operator");
		}
		return null;
	}



	public Object visitPrint(Print node) throws Exception
	{
		int size = node.getInternalValues().size();
		int i = 0;
		for(expr ex:node.getInternalValues())
		{
			visit(ex);
			addLine("PRINT");
			if(i<size-1)
			{
				addLine("PUSH STRING \" \"");
				addLine("PRINT");
			}
			i++;
		}
		addLine("PUSH STRING \"\\n\"");
		addLine("PRINT");
		return null;
	}



	public Object visitFor(For node) throws Exception
	{
		String start = getUnique("startFor");
		String except = getUnique("exceptFor");
		String iter = getUnique("for$iter");
		String breakCleanup = getUnique("forBreakCleanup");
		String totalEnd = getUnique("forEnd");
		
		visit(node.getInternalIter());
		//setup
		addLine("SELECTMEMBER __iter__");
		addLine("PUSHMEMBER");
		addLine("CALL 0");
		addLine("POP "+iter);
			
		//start
		addLine("CONTINUETO "+start);//continueto is before start, because the interpreter re-pushes the continue frame
		addLine("LABEL "+start);
			//increment
			addLine("EXCEPT StopIteration "+except);
			addLine("PUSH "+iter);
			addLine("SELECTMEMBER next");
			addLine("PUSHMEMBER");
			addLine("CALL 0");
			addLine("ENDEXCEPT");
			visit(node.getInternalTarget());
			
			addLine("BREAKTO "+breakCleanup);
			//body
			CompiledCode body = new CompiledCode(node.getInternalBody());
			insert(body);
			addLine("ENDBREAK");
			addLine("JUMP "+start);
		addLine("LABEL" +except);
		addLine("POP $");
		addLine("ENDCONTINUE");
		//and the or else
			CompiledCode elseBody = new CompiledCode(node.getInternalOrelse());
			insert(elseBody);
		addLine("JUMP "+totalEnd);
		addLine("LABEL "+breakCleanup);
		addLine("ENDCONTINUE");
		addLine("LABEL "+totalEnd);
		
		return null;
	}



	public Object visitWhile(While node) throws Exception
	{
		String start = getUnique("startWhile");
		String end = getUnique("endWhile");
		String totalEnd = getUnique("totalEnd");
		String breakCleanup = getUnique("whileBreakCleanup");

		addLine("CONTINUETO "+start);//this comes before the label, because the interpreter re-pushes continue frames when they are called
		addLine("LABEL "+start);
			visit(node.getInternalTest());
			addLine("JUMPIFFALSE "+end);
			addLine("BREAKTO "+breakCleanup);
			CompiledCode body = new CompiledCode(node.getInternalBody());
			insert(body);
			addLine("ENDBREAK");//when the break is reverted, it reverts to before the break was declared
			addLine("JUMP "+start);
		addLine("LABEL "+end);
		
		addLine("ENDCONTINUE");
			CompiledCode elseBody = new CompiledCode(node.getInternalOrelse());
			insert(elseBody);
		addLine("JUMP "+totalEnd);
		
		addLine("LABEL "+breakCleanup);
		addLine("ENDCONTINUE");
		
		addLine("LABEL "+totalEnd);
		return null;
	}



	public Object visitIf(If node) throws Exception
	{
		String elseLabel = getUnique("else");
		String end = getUnique("endIf");
		visit(node.getInternalTest());
		addLine("JUMPIFFALSE "+elseLabel);
		
			CompiledCode body = new CompiledCode(node.getInternalBody());
			insert(body);
		
		addLine("JUMP "+end);
		addLine("LABEL "+elseLabel);
		
			CompiledCode elseBody = new CompiledCode(node.getInternalOrelse());
			insert(elseBody);
		
		addLine("LABEL "+end);
		return null;
	}



	public Object visitWith(With node) throws Exception
	{
		//TODO
		throw new RuntimeException("NOT SUPPORTED");
	}



	public Object visitRaise(Raise node) throws Exception
	{
		visit(node.getInternalType());
		addLine("RAISE");
		return null;
	}



	public Object visitTryExcept(TryExcept node) throws Exception
	{
		String cleanup = getUnique("tryExceptCleanup");
		String totalEnd = getUnique("tryExceptEnd");
		
		java.util.List<String> exceptStarts = new ArrayList<>();
		
		for(excepthandler handler:node.getInternalHandlers())
		{
			String exceptStart = getUnique("exceptStart");
			exceptStarts.add(exceptStart);
			String exceptType = null;
			PyObject type = handler.__getattr__("type");
			if(type == Py.None)
			{
				exceptType = "All$";
			}
			else
			{
				exceptType = type.__getattr__("id").asString();
			}
			addLine("EXCEPT "+exceptType+" "+exceptStart);
		} 
		//EXCEPT TYPE BLOCK
		//EXCEPT TYPE2 BLOCK2
		//ETC...
		CompiledCode body = new CompiledCode(node.getInternalBody());
		insert(body);
		
		//skip to cleanup
		addLine("JUMP "+cleanup);
		
		for(int i = 0; i<node.getInternalHandlers().size(); i++)
		{
			excepthandler handler = node.getInternalHandlers().get(i);
			addLine("LABEL "+exceptStarts.get(i));
			//We need to manually end the excepts for handlers which were declared before the one which caught the exception
			//if we catch the first one, we don't need to end anything. If we catch the last one, we need to end all but one of them
			for(int i2 = 0; i2<i; i2++)//0, do 0, 1 do 1, n, do n
			{
				addLine("ENDEXCEPT");
			}
			//to the state it was in at the time the EXCEPT was declared
			if(handler.__getattr__("name") != Py.None)
			{
				String name = handler.__getattr__("name").__getattr__("id").asString();
				addLine("POP "+name);
			}
			else
			{
				addLine("POP $");//otherwise pop to a dummy name
			}
			@SuppressWarnings("unchecked")
			CompiledCode exceptBody = new CompiledCode((AstList)handler.__getattr__("body"));
			insert(exceptBody);
			//we cleaned up the exception handlers at the start of the block, so we need not clean them up here.
			addLine("JUMP "+ totalEnd);
		}
		addLine("LABEL "+cleanup);
		for(int i = 0; i<node.getInternalHandlers().size(); i++)
		{
			addLine("ENDEXCEPT");
		}
		//and, since no exceptions were called, we might as well just execute the orElse block
		
		CompiledCode orElseBody=new CompiledCode(node.getInternalOrelse());
		insert(orElseBody);
		
		addLine("LABEL "+totalEnd);
		
		return null;
	}



	public Object visitTryFinally(TryFinally node) throws Exception
	{
		String finallyStart = getUnique("finallyStart");
		String end = getUnique("finallyEnd");
		addLine("FINALLY "+finallyStart);
			CompiledCode body = new CompiledCode(node.getInternalBody());
			insert(body);
		addLine("POPFINALLY");
		
		addLine("LABEL "+finallyStart);
		
			CompiledCode finallyBody = new CompiledCode(node.getInternalFinalbody());
			insert(finallyBody);
		
		addLine("REVERTFINALLY");
		addLine("LABEL "+end);
		
		return null;
	}



	public Object visitAssert(Assert node) throws Exception
	{
		String end = getUnique("assertEnd");
		addLine("PUSH __debug__");
		addLine("JUMPIFFALSE "+end);
		visit(node.getInternalTest());
		addLine("NOT");
		addLine("JUMPIFFALSE "+end);
		if(node.getInternalMsg() != null)
		{
			visit(node.getInternalMsg());
			addLine("PUSH AssertionError");
			addLine("CALL 1");
		}
		else
		{
			addLine("PUSH AssertionError");
		}
		addLine("RAISE");
		addLine("LABEL "+end);
		return null;
	}



	public Object visitImport(Import node) throws Exception
	{
		for(alias name:node.getInternalNames())
		{
			addLine("IMPORT "+name.getInternalName());
			if(name.getInternalAsname() != null)
			{
				addLine("POP "+name.getInternalAsname());
			}
			else
			{
				addLine("POP "+name.getInternalName());
			}
		}
		return null;
	}



	public Object visitImportFrom(ImportFrom node) throws Exception
	{
		String module = getUnique(node.getInternalModule()+"$");
		addLine("IMPORT "+node.getInternalModule());
		addLine("POP "+module);
		
		for(alias name:node.getInternalNames())
		{
			addLine("PUSH "+module);
			addLine("SELECTMEMBER "+name.getInternalName());
			addLine("PUSHMEMBER");
			if(name.getInternalAsname() != null)
			{
				addLine("POP "+name.getInternalAsname());
			}
			else
			{
				addLine("POP "+name.getInternalName());
			}
		}
		addLine("DEL "+module);
		return null;
	}



	public Object visitExec(Exec node) throws Exception
	{
		throw new PySyntaxError("exec statements are not supported in python-=1",line,col,node.getText(),"<script>");
	}



	public Object visitGlobal(Global node) throws Exception
	{
		for(Name n:node.getInternalNameNodes())
		{
			addLine("FLAGGLOBAL "+n.getInternalId());
		}
		return null;
	}



	public Object visitExpr(Expr node) throws Exception
	{
		traverse(node);
		return null;
	}



	public Object visitPass(Pass node) throws Exception
	{
		return null;
	}



	public Object visitBreak(Break node) throws Exception
	{
		addLine("BREAK");
		return null;
	}



	public Object visitContinue(Continue node) throws Exception
	{
		addLine("CONTINUE");
		return null;
	}



	public Object visitBoolOp(BoolOp node) throws Exception
	{
		boolopType type = node.getInternalOp();
		String aVar,elseStart,end;
		switch(type)
		{
		case And: // a && b  <->  (a._booland(b)) if a else False
			aVar = getUnique("a$");
			elseStart = getUnique("ifElse");
			end = getUnique("ifEnd");
			visit(node.getInternalValues().get(0));
			addLine("POP "+aVar);
			addLine("PUSH "+aVar);
			addLine("JUMPIFFALSE "+elseStart);//if a
				addLine("PUSH "+aVar);
				visit(node.getInternalValues().get(1));
				addLine("BOOLAND"); 
			addLine("JUMP "+end);
			addLine("LABEL "+elseStart);
				addLine("PUSH False");
			addLine("LABEL "+end);
			break;
		case Or: // a || b  <->  (a._boolor(b)) if !a else True
			aVar = getUnique("a$");
			elseStart = getUnique("ifElse");
			end = getUnique("ifEnd");
			visit(node.getInternalValues().get(0));
			addLine("POP "+aVar);
			addLine("PUSH "+aVar);
			addLine("NOT");
			addLine("JUMPIFFALSE "+elseStart);//if !a
				addLine("PUSH "+aVar);
				visit(node.getInternalValues().get(1));
				addLine("BOOLOR"); 
			addLine("JUMP "+end);
			addLine("LABEL "+elseStart);
				addLine("PUSH True");
			addLine("LABEL "+end);
			break;
		case UNDEFINED:
			break;
		}
		return null;
	}



	public Object visitBinOp(BinOp node) throws Exception
	{
		visit(node.getInternalLeft());
		visit(node.getInternalRight());
		operatorType type = node.getInternalOp();
		switch(type)
		{
		case Add:
			addLine("ADD");
			break;
	    case Sub:
	    	addLine("SUB");
	    	break;
	    case Mult:
	    	addLine("MULT");
	    	break;
	    case Div:
	    	addLine("DIV");
	    	break;
	    case Mod:
	    	addLine("MOD");
	    	break;
	    case Pow:
	    	addLine("POW");
	    	break;
	    case LShift:
	    	addLine("LSHIFT");
	    	break;
	    case RShift:
	    	addLine("RSHIFT");
	    	break;
	    case BitOr:
	    	addLine("BITOR");
	    	break;
	    case BitXor:
	    	addLine("BITXOR");
	    	break;
	    case BitAnd:
	    	addLine("BITAND");
	    	break;
	    case FloorDiv:
	    	addLine("FLOORDIV");
	    	break;
	    case UNDEFINED:
	    	throw new RuntimeException("undefined operator");
		}
		return null;
	}



	public Object visitUnaryOp(UnaryOp node) throws Exception
	{
		visit(node.getInternalOperand());
		switch(node.getInternalOp())
		{
		case Invert:
			addLine("INVERT");
			break;
		case Not:
			addLine("NOT");
			break;
		case UAdd:
			addLine("UADD");
			break;
		case USub:
			addLine("USUB");
			break;
		case UNDEFINED:
			break;
		}
		return null;
	}



	public Object visitLambda(Lambda node) throws Exception
	{
		CompiledCode internals = codeFor(node,node.getInternalBody());
		Function f = new Function("<lambda>",internals,node.getInternalArgs());
		LambdaLine line = new LambdaLine(f,this.line,col);
		insert(f.initDefaults);
		addLine(line);
		return null;
	}



	public Object visitIfExp(IfExp node) throws Exception
	{
		String elseStart = getUnique("ifExpElseStart");
		String end = getUnique("ifExpEnd");
		visit(node.getInternalTest());
		addLine("JUMPIFFALSE "+elseStart);
		visit(node.getInternalBody());
		addLine("JUMP "+end);
		addLine("LABEL "+elseStart);
		visit(node.getInternalOrelse());
		addLine("LABEL "+end);
		return null;
	}



	public Object visitDict(Dict node) throws Exception
	{
		int len = 0;
		for(int i = 0; i<node.getInternalKeys().size(); i++)
		{
			visit(node.getInternalValues().get(i));
			visit(node.getInternalKeys().get(i));
			len+=2;
		}
		addLine("PUSH $newDict");
		addLine("CALL "+len);
		return null;
	}



	public Object visitSet(Set node) throws Exception
	{
		int len = 0;
		for(expr ex:node.getInternalElts())
		{
			visit(ex);
			len++;
		}
		addLine("PUSH $newSet");
		addLine("CALL "+len);
		return null;
	}



	public Object visitListComp(ListComp node) throws Exception
	{
		
		visitListComp(node,0);
		
		return null;
	}
	
	public void visitListComp(ListComp node,int level) throws Exception
	{
		String iter = getUnique("for$iter");
		String result = getUnique("compResult$");
		String start = getUnique("compStart");
		String end = getUnique("compEnd");
		
		addLine("PUSH $newList");
		addLine("CALL 0");
		addLine("POP "+result);
		
		
		comprehension comp = node.getInternalGenerators().get(level);
		
			visit(comp.getInternalIter());
			addLine("SELECTMEMBER __iter__");
			addLine("PUSHMEMBER");
			addLine("CALL 0");
			addLine("POP "+iter);
			
			
			
			addLine("LABEL "+start);
			
			addLine("EXCEPT StopIteration "+end);
			addLine("PUSH "+iter);
			addLine("SELECTMEMBER next");
			addLine("PUSHMEMBER");
			addLine("CALL 0");
			visit(comp.getInternalTarget());
			addLine("ENDEXCEPT");
			
			
			for(expr ifexpr:comp.getInternalIfs())
			{
				visit(ifexpr);
				addLine("JUMPIFFALSE "+start);
			}
			
			
			if(node.getInternalGenerators().size()==level+1)
			{
				visit(node.getInternalElt());
				addLine("PUSH "+result);
				addLine("SELECTMEMBER append");
				addLine("PUSHMEMBER");
				addLine("CALL 1");
			}
			else
			{
				visitListComp(node,level+1);
				addLine("PUSH "+result);
				addLine("SELECTMEMBER extend");
				addLine("PUSHMEMBER");
				addLine("CALL 1");
			}
			
			addLine("JUMP "+start);
			addLine("LABEL "+end);
			addLine("POP $");
			addLine("PUSH "+result);
	}



	public Object visitSetComp(SetComp node) throws Exception
	{
		visitSetComp(node,0);
		
		return null;
	}
	
	public void visitSetComp(SetComp node,int level) throws Exception
	{
		String iter = getUnique("for$iter");
		String result = getUnique("compResult$");
		String start = getUnique("compStart");
		String end = getUnique("compEnd");
		
		addLine("PUSH $newSet");
		addLine("CALL 0");
		addLine("POP "+result);
		
		
		comprehension comp = node.getInternalGenerators().get(level);
		
			visit(comp.getInternalIter());
			addLine("SELECTMEMBER __iter__");
			addLine("PUSHMEMBER");
			addLine("CALL 0");
			addLine("POP "+iter);
			
			
			
			addLine("LABEL "+start);
			
			addLine("EXCEPT StopIteration "+end);
			addLine("PUSH "+iter);
			addLine("SELECTMEMBER next");
			addLine("PUSHMEMBER");
			addLine("CALL 0");
			visit(comp.getInternalTarget());
			addLine("ENDEXCEPT");
			
			
			for(expr ifexpr:comp.getInternalIfs())
			{
				visit(ifexpr);
				addLine("JUMPIFFALSE "+start);
			}
			
			
			if(node.getInternalGenerators().size()==level+1)
			{
				visit(node.getInternalElt());
				addLine("PUSH "+result);
				addLine("SELECTMEMBER add");
				addLine("PUSHMEMBER");
				addLine("CALL 1");
			}
			else
			{
				visitSetComp(node,level+1);
				addLine("PUSH "+result);
				addLine("SELECTMEMBER update");
				addLine("PUSHMEMBER");
				addLine("CALL 1");
			}
			
			addLine("JUMP "+start);
			addLine("LABEL "+end);
			addLine("POP $");
			addLine("PUSH "+result);
	}


	public Object visitDictComp(DictComp node) throws Exception
	{
		visitDictComp(node, 0);
		return null;
	}
	
	public void visitDictComp(DictComp node, int level) throws Exception
	{
		String iter = getUnique("for$iter");
		String result = getUnique("compResult$");
		String start = getUnique("compStart");
		String end = getUnique("compEnd");
		
		addLine("PUSH $newDict");
		addLine("CALL 0");
		addLine("POP "+result);
		
		
		comprehension comp = node.getInternalGenerators().get(level);
		
			visit(comp.getInternalIter());
			addLine("SELECTMEMBER __iter__");
			addLine("PUSHMEMBER");
			addLine("CALL 0");
			addLine("POP "+iter);
			
			
			
			addLine("LABEL "+start);
			
			addLine("EXCEPT StopIteration "+end);
			addLine("PUSH "+iter);
			addLine("SELECTMEMBER next");
			addLine("PUSHMEMBER");
			addLine("CALL 0");
			visit(comp.getInternalTarget());
			addLine("ENDEXCEPT");
			
			
			for(expr ifexpr:comp.getInternalIfs())
			{
				visit(ifexpr);
				addLine("JUMPIFFALSE "+start);
			}
			
			
			if(node.getInternalGenerators().size()==level+1)
			{
				visit(node.getInternalValue());
				addLine("PUSH "+result);
				visit(node.getInternalKey());
				addLine("POPITEM");
			}
			else
			{
				visitDictComp(node,level+1);
				addLine("PUSH "+result);
				addLine("SELECTMEMBER update");
				addLine("PUSHMEMBER");
				addLine("CALL 1");
			}
			
			addLine("JUMP "+start);
			addLine("LABEL "+end);
			addLine("POP $");
			addLine("PUSH "+result);
	}



	public Object visitGeneratorExp(GeneratorExp node) throws Exception
	{
		//TODO
		throw new RuntimeException("NOT SUPPORTED");
	}



	public Object visitYield(Yield node) throws Exception
	{
		//TODO
		throw new RuntimeException("NOT SUPPORTED");
	}



	public Object visitCompare(Compare node) throws Exception
	{
		visit(node.getInternalLeft());
		for(expr ex: node.getInternalComparators())
		{
			visit(ex);
		}
		for(cmpopType op:node.getInternalOps())
		{
			switch(op)
			{
			case Eq:
				addLine("EQ");
				break;
			case Gt:
				addLine("GT");
				break;
			case GtE:
				addLine("GTE");
				break;
			case In:
				addLine("IN");
				break;
			case Is:
				addLine("IS");
				break;
			case IsNot:
				addLine("ISNOT");
				break;
			case Lt:
				addLine("LT");
				break;
			case LtE:
				addLine("LTE");
				break;
			case NotEq:
				addLine("NOTEQ");
				break;
			case NotIn:
				addLine("NOTIN");
				break;
			case UNDEFINED:
				break;
			}
		}
		return null;
	}



	public Object visitCall(Call node) throws Exception
	{
		if(node.getInternalStarargs() != null) //unknown arg length function call
		{
			String argsSym = getUnique("args$");
			for(expr ex:node.getInternalArgs())
			{
				visit(ex);
			}
			addLine("PUSH $newList");
			addLine("CALL "+node.getInternalArgs().size());
			addLine("POP "+argsSym);
			visit(node.getInternalStarargs());
			addLine("PUSH "+argsSym);
			addLine("SELECTMEMBER extend");
			addLine("PUSHMEMBER");
			addLine("CALL 1");
			addLine("POP $");
			addLine("PUSH "+argsSym);
			visit(node.getInternalFunc());
			addLine("CALL");
		}
		else // normal function call
		{
			for(expr ex:node.getInternalArgs())
			{
				visit(ex);
			}
			visit(node.getInternalFunc());
			addLine("CALL "+node.getInternalArgs().size());
		}
		return null;
	}



	public Object visitRepr(Repr node) throws Exception
	{
		visit(node.getInternalValue());
		addLine("SELECTMEMBER __repr__");
		addLine("PUSHMEMBER");
		addLine("CALL 0");
		return null;
	}



	public Object visitNum(Num node) throws Exception
	{
		addLine("PUSH NUMBER "+node.getInternalN());
		return null;
	}



	public Object visitStr(Str node) throws Exception
	{
		addLine("PUSH STRING \""+node.getInternalS()+"\"");
		return null;
	}



	public Object visitAttribute(Attribute node) throws Exception
	{
		expr_contextType type = node.getInternalCtx();
		visit(node.getInternalValue());
		addLine("SELECTMEMBER "+node.getInternalAttr());
		switch(type)
		{
		case AugLoad:
			System.err.println("AugLoad");
			break;
		case AugStore:
			addLine("PUSHMEMBERPOINTER");
			break;
		case Del:
			addLine("DELMEMBER");
			break;
		case Load:
			addLine("PUSHMEMBER");
			break;
		case Param:
			System.err.println("attribute param");
			break;
		case Store:
			addLine("POPMEMBER");
			break;
		case UNDEFINED:
			break;
		}
		return null;
	}



	public Object visitSubscript(Subscript node) throws Exception
	{
		expr_contextType type = node.getInternalCtx();
		visit(node.getInternalValue());
		visit(node.getInternalSlice());
		switch(type)
		{
		case AugLoad:
			System.err.println("AugLoad");
			break;
		case AugStore:
			addLine("PUSHITEMPOINTER");
			break;
		case Del:
			addLine("DELITEM");
			break;
		case Load:
			addLine("PUSHITEM");
			break;
		case Param:
			System.err.println("param subscript");
			break;
		case Store:
			addLine("POPITEM");
			break;
		case UNDEFINED:
			break;
		}
		return null;
	}



	public Object visitName(Name node) throws Exception
	{
		expr_contextType type = node.getInternalCtx();
		switch(type)
		{
		case AugLoad:
			System.err.println("AugLoad");
			break;
		case AugStore:
			addLine("PUSHPOINTER "+node.getInternalId());
			break;
		case Del:
			addLine("DEL "+node.getInternalId());
			break;
		case Load:
			addLine("PUSH "+node.getInternalId());
			break;
		case Param:
			System.err.println("param name");
			break;
		case Store:
			addLine("POP "+node.getInternalId());
			break;
		case UNDEFINED:
			break;
		}
		return null;
	}



	public Object visitList(List node) throws Exception
	{
		expr_contextType type = node.getInternalCtx();
		switch(type)
		{
		case AugLoad:
			System.err.println("AugLoad list");
			break;
		case AugStore:
			System.err.println("AugStore list");
			break;
		case Del:
			for(expr ex:node.getInternalElts())
			{
				visit(ex);
			}
			break;
		case Load:
			int len = 0;
			for(expr ex:node.getInternalElts())
			{
				visit(ex);
				len++;
			}
			addLine("PUSH $newList");
			addLine("CALL "+len);
			break;
		case Param:
			System.err.println("Param list");
			break;
		case Store:
			String temp = getUnique("temp$");
			addLine("POP "+temp);
			int i = 0;
			for(expr ex:node.getInternalElts())
			{
				addLine("PUSH "+temp);
				addLine("PUSH NUMBER "+i);
				addLine("PUSHITEM");
				i++;
				visit(ex);
			}
			break;
		case UNDEFINED:
			break;
		}
		return null;
	}



	public Object visitTuple(Tuple node) throws Exception
	{
		expr_contextType type = node.getInternalCtx();
		switch(type)
		{
		case AugLoad:
			System.err.println("AugLoad tuple");
			break;
		case AugStore:
			System.err.println("AugStore tuple");
			break;
		case Del:
			for(expr ex:node.getInternalElts())
			{
				visit(ex);
			}
			break;
		case Load:
			int len = 0;
			for(expr ex:node.getInternalElts())
			{
				visit(ex);
				len++;
			}
			addLine("PUSH $newTuple");
			addLine("CALL "+len);
			break;
		case Param:
			System.err.println("Param tuple");
			break;
		case Store:
			String temp = getUnique("temp$");
			addLine("POP "+temp);
			int i = 0;
			for(expr ex:node.getInternalElts())
			{
				addLine("PUSH "+temp);
				addLine("PUSH NUMBER "+i);
				addLine("PUSHITEM");
				i++;
				visit(ex);
			}
			break;
		case UNDEFINED:
			break;
		}
		return null;
	}



	public Object visitEllipsis(Ellipsis node) throws Exception
	{
		addLine("PUSH Ellipsis");
		return null;
	}



	public Object visitSlice(Slice node) throws Exception
	{
		if(node.getInternalStep() != null)
		{
			visit(node.getInternalStep());
		}
		else
		{
			addLine("PUSH None");
		}
		
		if(node.getInternalUpper() != null)
		{
			visit(node.getInternalUpper());
		}
		else
		{
			addLine("PUSH None");
		}
		
		if(node.getInternalLower() != null)
		{
			visit(node.getInternalLower());
		}
		else
		{
			addLine("PUSH None");
		}
		
		addLine("SLICE");
		return null;
	}



	public Object visitExtSlice(ExtSlice node) throws Exception
	{
		//TODO
		throw new RuntimeException("TODO");
	}



	public Object visitIndex(Index node) throws Exception
	{
		visit(node.getInternalValue());
		return null;
	}



	public Object visitExceptHandler(ExceptHandler node) throws Exception
	{
		throw new RuntimeException("THIS SHOULDN'T BE HAPPENING");
	}


	public CodeLine getLine(int i)
	{
		return code.get(i);
	}
}
