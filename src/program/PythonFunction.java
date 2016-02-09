package program;

import java.io.Serializable;

import org.python.core.PyObject;

@FunctionalInterface
public interface PythonFunction extends Serializable
{
	public PyObject accept(PyObject[] args, String[] keys);
}
