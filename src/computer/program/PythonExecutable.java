package computer.program;

import java.io.OutputStream;
import java.io.PrintStream;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.util.InteractiveInterpreter;

import computer.system.Computer;

import program.PythonSanitizer;
import state.programming.AppendOnlyBuffer;

public class PythonExecutable implements InteractiveExecutable
{
	AppendOnlyBuffer out;
	InteractiveInterpreter interpreter;
	OutputStream stdOut;
	
	volatile boolean running;
	
	public void setup(String[] args, AppendOnlyBuffer out, Computer system)
	{
		this.out = out;
		interpreter = new InteractiveInterpreter();
		running = true;
		stdOut = out.getOuputStream();
		interpreter.setOut(stdOut);
	}
	
	public boolean isRunning()
	{
		return running;
	}

	public void acceptCommand(String command)
	{
		out.appendln(getPrompt()+command);
		try
		{
			PythonSanitizer sanitizer = new PythonSanitizer(command);
			if(!sanitizer.isLegal())
			{
				sanitizer.throwException();
			}
			try
			{
				PyObject output = interpreter.eval(command);
				out.appendln(output.toString());
			}
			catch(Exception e)
			{
				interpreter.exec(command);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(new PrintStream(stdOut));
		}
		
	}

	public void stop()
	{
		running = false;
		interpreter.interrupt(Py.getThreadState());
		interpreter.close();
	}
	
	public String getPrompt()
	{
		return ">>> ";
	}

	public void act(int dt)
	{
		
	}

}
