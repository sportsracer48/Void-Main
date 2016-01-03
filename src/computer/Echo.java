package computer;

import state.programming.AppendOnlyBuffer;

public class Echo implements WriteOnlyExecutable
{
	public void run(String[] args, AppendOnlyBuffer out)
	{
		if(args.length >= 2)
		{
			out.appendln(args[1]);
		}
		else
		{
			out.appendln("usage: echo arg");
		}
	}
}
