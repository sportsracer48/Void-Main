package org.python.compiler;

import java.io.Serializable;

public class Klass implements Serializable
{
	private static final long serialVersionUID = -1582031628196223517L;
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
