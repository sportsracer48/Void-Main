package org.python.compiler;

import org.python.core.PyObject;

public class SlicePointer extends Pointer
{
	private static final long serialVersionUID = 1L;
	
	PyObject target;
	ItemSelectArgs args;
	
	public SlicePointer(PyObject target, ItemSelectArgs args)
	{
		super(null,null);
		this.target = target;
		this.args = args;	
		this.value = target.__getslice__(args.start, args.end, args.step);
	}
	public void set(PyObject value)
	{
		target.__setslice__(args.start, args.end, args.step,value);
	}
}
