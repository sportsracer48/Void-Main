package org.python.compiler;

public class Klass
{
	CompiledCode internals;
	String name;
	
	public Klass(String name, CompiledCode internals)
	{
		this.internals = internals;
		this.name = name;
	}
	
	public String toString(int tabs)
	{
		StringBuilder toReturn = new StringBuilder();
		toReturn.append("class "+name+":\n");
		for(CodeLine line:internals.code)
		{
			toReturn.append(line.toString(tabs+1)+"\n");
		}
		return toReturn.toString();
	}

}
