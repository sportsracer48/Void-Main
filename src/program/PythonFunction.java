package program;

import org.python.core.PyObject;

@FunctionalInterface
public interface PythonFunction
{
	public PyObject accept(PyObject[] args, String[] keys);
}
