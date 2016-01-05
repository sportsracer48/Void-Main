package computer.program;

import computer.system.Computer;

import state.programming.AppendOnlyBuffer;

public class RepeatEcho implements InteractiveExecutable
{
	AppendOnlyBuffer out;
	public void setup(String[] args, AppendOnlyBuffer out, Computer system)
	{
		this.out = out;
	}

	public void acceptCommand(String command)
	{
		out.appendln(command);
	}
	public void stop()
	{
		
	}
	public boolean isRunning()
	{
		return true;
	}
	public void act(int dt)
	{
	}
}
