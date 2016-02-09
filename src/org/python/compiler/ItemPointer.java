package org.python.compiler;

import org.python.core.PyObject;

public class ItemPointer extends Pointer
{
	private static final long serialVersionUID = -3894546045529543071L;
	PyObject target,key;
	public ItemPointer(PyObject target, PyObject key)
	{
		super(null,null);
		this.target = target;
		this.key = key;
		this.value = target.__getitem__(key);
	}
	public void set(PyObject value)
	{
		target.__setitem__(key, value);
	}
}
