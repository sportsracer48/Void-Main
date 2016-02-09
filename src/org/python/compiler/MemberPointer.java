package org.python.compiler;

import org.python.core.PyObject;

public class MemberPointer extends Pointer
{
	private static final long serialVersionUID = 1619792618511733537L;
	
	String member;
	PyObject target;
	public MemberPointer(PyObject target, String member)
	{
		super(null, null);
		this.target = target;
		this.member = member;
		this.value = target.__getattr__(member);
	}
	
	public void set(PyObject value)
	{
		target.__setattr__(member, value);
	}
}
