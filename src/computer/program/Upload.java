package computer.program;

import java.io.PrintStream;

import state.programming.AppendOnlyBuffer;
import computer.system.Computer;

public class Upload implements WriteOnlyExecutable
{
	public void run(String[] args, AppendOnlyBuffer out, Computer system)
	{
		if(args.length<2)
		{
			out.appendln("usage: uploade [filename]");
		}
		else if(system.hasConnection())
		{
			try
			{
				system.upload(system.read(args[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace(new PrintStream(out.getOuputStream()));
			}
			out.appendln("success!");
		}
		else
		{
			out.appendln("No board connected.");
		}
	}

}
