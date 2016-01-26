package org.python.compiler;

import org.python.antlr.base.mod;
import org.python.core.CompileMode;
import org.python.core.ParserFacade;
import org.python.core.Py;

public class PythonCompiler
{
	public static CompiledCode compile(String source)
	{
		mod node = ParserFacade.parse(source, CompileMode.exec, "<script>", Py.getCompilerFlags());
		return compile(node);
	}
	
	public static CompiledCode compile(mod node)
	{
        VMCompiler test = new VMCompiler();
        test.parse(node);
        return test.root;
    }
}
