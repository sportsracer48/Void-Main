package org.python.compiler;

import java.io.Serializable;

public class CodeLine implements Serializable
{
	private static final long serialVersionUID = -6144783853978497146L;
	String op;
	int line;
	int col;
	public CodeLine(String op,int line, int col)
	{
		this.op = op;
		this.line = line;
		this.col = col;
	}
	public String toString(int tabs)
	{
		StringBuilder toReturn = new StringBuilder();
		for(int i = 0; i<tabs; i++)
		{
			toReturn.append('\t');
		}
		return toReturn.toString()+"<"+line+", "+col+">: "+op;
	}
	public String getOp()
	{
		return op;
	}
	public int getLine()
	{
		return line;
	}
	public int getCol()
	{
		return col;
	}
}
