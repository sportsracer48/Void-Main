package org.python.compiler;

import java.util.ArrayList;

import org.python.antlr.PythonTree;
import org.python.antlr.Visitor;
import org.python.antlr.ast.Interactive;
import org.python.antlr.ast.Module;


public class VMCompiler extends Visitor
{
	ArrayList<String> code = new ArrayList<>();
	CompiledCode root;
	
	int level = 0;
	public void parse(PythonTree node)
	{
		try
		{
			visit(node);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public Object visitModule(Module node) throws Exception
	{
		root = new CompiledCode(node);
		return null;
	}



	public Object visitInteractive(Interactive node) throws Exception
	{
		System.err.println("visitInteractive");
		return null;
	}
	
	public void dump()
	{
		root.dump();
	}
}
