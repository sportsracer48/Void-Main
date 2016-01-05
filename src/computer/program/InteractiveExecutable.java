package computer.program;

import computer.system.Computer;

import state.programming.AppendOnlyBuffer;

public interface InteractiveExecutable
{
	public void setup(String[] args, AppendOnlyBuffer out,Computer system);
	public default String getPrompt()
	{
		return ">";
	}
	public void acceptCommand(String command);
	public void act(int dt);
	public boolean isRunning();
	public void stop();
}
