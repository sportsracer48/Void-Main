package org.python.compiler;

import java.io.Serializable;

import org.python.core.PyObject;

public class ItemSelectArgs implements Serializable
{
	private static final long serialVersionUID = -6279085028016753685L;
	public static int SLICE = 0;
	public static int START_MASK = 1;
	public static int END_MASK = 2;
	public static int STEP_MASK = 4;
	
	int type;
	
	PyObject start;
	PyObject end;
	PyObject step;
	
	public static ItemSelectArgs Slice(PyObject start, PyObject end, PyObject step)
	{
		ItemSelectArgs result = new ItemSelectArgs();
		result.type = SLICE;
		result.start = start;
		result.end = end;
		result.step = step;
		return result;
	}
}
