package org.python.compiler;

public class ClassLine extends CodeLine
{
	Klass defined;
	int numBases;
	public ClassLine(Klass defined, int numBases, int line, int col)
	{
		super("CLASS",line, col);
		this.defined = defined;
		this.numBases = numBases;
	}
	public String toString(int tabs)
	{
		return super.toString(tabs) +" "+defined.toString(tabs);
	}

}
