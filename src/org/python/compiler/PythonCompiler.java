package org.python.compiler;

import game.item.Pin;

import java.io.FileNotFoundException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.python.antlr.base.mod;
import org.python.core.CompileMode;
import org.python.core.ParserFacade;
import org.python.core.Py;
//import org.python.util.PythonInterpreter;

import program.OuinoEnvironment;
import program.OuinoPythonEnvironment;
import util.FileLoader;

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
	
	public static void main(String[] args) throws FileNotFoundException
	{
		Py.initPython();
		
//		PythonInterpreter test = new PythonInterpreter();
//		test.exec(FileLoader.getFileContents("laptop/blink.py"));
//		test.close();
		
		CompiledCode code = compile(FileLoader.getFileContents("laptop/blink.py"));
		code.dump();
		Interpreter terp = new Interpreter(code,OuinoPythonEnvironment.getGlobals(new OuinoEnvironment(Stream.generate(()->new Pin(null,0,0)).limit(100).collect(Collectors.toList()))));
		terp.executeAll();
		terp.dump();
	}
}
