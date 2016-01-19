package org.python.compiler;

public class LambdaLine extends CodeLine
{
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
