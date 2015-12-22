package program;

import org.python.core.PyObject;

public class SafeImport extends PyObject
{
	private static final long serialVersionUID = 1L;
	private PyObject unsafeImport;
	public SafeImport(PyObject unsafeImport)
	{
		this.unsafeImport = unsafeImport;
	}
	public PyObject __call__(PyObject[] args, String[] keywords)
	{
		int nameArg = 0;
		for(int i = 0; i<keywords.length; i++)
		{
			if(keywords[i].equals("name"))
			{
				nameArg = args.length - keywords.length + i;
			}
		}
		String name = args[nameArg].__str__().toString();
		System.out.println("\n"+name+"\n");
		
		return unsafeImport.__call__(args,keywords);
	}
}
