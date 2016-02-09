package org.python.compiler;

public class LambdaLine extends CodeLine
{
	private static final long serialVersionUID = -7704540403659169259L;
	Function defined;
	public LambdaLine(Function defined, int line, int col)
	{
		super("LAMBDA",line,col);
		this.defined = defined;
	}
	public String toString(int tabs)
	{
		return super.toString(tabs)+" "+defined.toString(tabs);
	}
}
