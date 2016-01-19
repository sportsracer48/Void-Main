package org.python.compiler;

import java.util.ArrayList;
import java.util.List;

import org.python.antlr.ast.Name;
import org.python.antlr.ast.arguments;
import org.python.antlr.base.expr;

public class Function
{	
	List<String> args = new ArrayList<>();
	List<expr> defaults = new ArrayList<>();
	int numDefaults;
	String defaultsSymbol;
	
	CompiledCode initDefaults;
	
	String vararg;
	String kwarg;
	CompiledCode internals;
	String name;
	
	String doc = "";
	
	public Function(CompiledCode internals, String... arguments)
	{
		this.internals = internals;
		for(String s:arguments)
		{
			args.add(s);
		}
		internals.declaredVars.addAll(args);
		internals.referencedExternalVars.removeAll(args);
	}
	
	public Function(String name, CompiledCode internals, arguments arguments)
	{
		this.internals = internals;
		this.name = name;
		this.doc = internals.doc;
		for(expr ex:arguments.getInternalArgs())
		{
			if(ex instanceof Name)
			{
				String argName = ((Name)ex).getInternalId();
				args.add(argName);
			}
			else
			{
				System.err.println("not a name");
				System.err.println(ex.toStringTree());
			}
		}
		
		internals.declaredVars.addAll(args);
		internals.referencedExternalVars.removeAll(args);
		
		for(expr ex:arguments.getInternalDefaults())
		{
			defaults.add(ex);
		}
		numDefaults = defaults.size();
		
		defaultsSymbol = CompiledCode.getUnique(name)+"$defaults";
		
		initDefaults = new CompiledCode(arguments.getInternalDefaults());
		if(!defaults.isEmpty())
		{
			initDefaults.addLine("PUSH $newList");
			initDefaults.addLine("CALL "+defaults.size());
			initDefaults.addLine("POP "+defaultsSymbol);
		}
		
		vararg = arguments.getInternalVararg();
		kwarg = arguments.getInternalKwarg();
	}
	
	public String toString(int tabs)
	{
		StringBuilder toReturn = new StringBuilder();
		
		toReturn.append("def "+name+"(");
		for(int i = 0; i<args.size();i++)
		{
			toReturn.append(args.get(i));
			if(i>=args.size()-defaults.size())
			{
				toReturn.append(" = "+defaults.get(i-args.size()+defaults.size()).toStringTree());
			}
			if(i<args.size()-1 || vararg!=null || kwarg != null)
			{
				toReturn.append(",");
			}
		}
		if(vararg!=null)
		{
			toReturn.append(vararg);
			if(kwarg!=null)
			{
				toReturn.append(",");
			}
		}
		if(kwarg!=null)
		{
			toReturn.append(kwarg);
		}
		toReturn.append("):\n");
		
		
		for(CodeLine line:internals.code)
		{
			toReturn.append(line.toString(tabs+1)+"\n");
		}
		return toReturn.toString();
	}
}
