package computer.program;

import java.io.PrintStream;

import org.python.compiler.Interpreter;
import org.python.core.PyObject;

import program.OuinoPythonEnvironment;
import computer.system.Computer;
import state.programming.AppendOnlyBuffer;

public class PythonExecutable implements InteractiveExecutable
{
	AppendOnlyBuffer out;
	PrintStream stdOut;
	int indents = 0;
	String multiLineCommand = "";
	
	Interpreter interpreter;
	
	volatile boolean running;
	boolean runningInterpreter;
	float cyclesPerSecond = 1_000_000f; //1000 khz
	float cyclesPerMs = cyclesPerSecond/1000;
	
	public void setup(String[] args, AppendOnlyBuffer out, Computer system)
	{
		this.out = out;
		running = true;
		stdOut = new PrintStream(out.getOuputStream());
		interpreter = new Interpreter(OuinoPythonEnvironment.getGlobals(null),stdOut);
	}
	
	public boolean isRunning()
	{
		return running;
	}

	public void acceptCommand(String command)
	{
		if(runningInterpreter)
		{
			return;
		}
		out.appendln(getPrompt()+command);
		if(!multiLineCommand.equals(""))
		{
			if(command.startsWith(" "))
			{
				multiLineCommand += "\n"+command;
				return;
			}
		}
		else if(command.endsWith(":"))
		{
			if(multiLineCommand.equals(""))
			{
				multiLineCommand = command;
			}
			else
			{
				multiLineCommand += "\n"+command;
			}
			return;
		}
		try
		{
			if(!multiLineCommand.equals(""))
			{
				command = multiLineCommand;
				multiLineCommand = "";
			}
			interpreter.beginEval(command);
			runningInterpreter = true;
		}
		catch(Exception e)
		{
			e.printStackTrace(new PrintStream(stdOut));
		}
		
	}

	public void stop()
	{
		running = false;
	}
	
	public String getPrompt()
	{
		if(multiLineCommand.equals(""))
		{
			return ">>> ";
		}
		else
		{
			return "... ";
		}
	}

	public void act(int dt)
	{
		if(runningInterpreter)
		{
			try
			{
				interpreter.execute((int) (dt*cyclesPerMs));
			}
			catch(Exception e)
			{
				e.printStackTrace(stdOut);
				runningInterpreter = false;
				return;
			}
			if(!interpreter.isRunning())
			{
				PyObject value = interpreter.evalValue();
				stdOut.println(value.__repr__().asString());
				runningInterpreter = false;
			}
		}
	}

}
