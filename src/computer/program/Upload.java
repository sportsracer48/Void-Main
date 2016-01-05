package computer.program;

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
			system.upload(system.read(args[1]));
			out.appendln("success!");
		}
		else
		{
			out.appendln("failure 38(");
		}
	}

}
