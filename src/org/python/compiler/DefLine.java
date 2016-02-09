package org.python.compiler;

public class DefLine extends CodeLine
{
	private static final long serialVersionUID = -3867652192239002411L;
	
	Function defined;
	public DefLine(Function defined, int line, int col)
	{
		super("DEF",line,col);
		this.defined = defined;
	}
	public String toString(int tabs)
	{
		return super.toString(tabs)+" "+defined.toString(tabs);
	}
}
