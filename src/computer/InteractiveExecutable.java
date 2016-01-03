package computer;

import state.programming.AppendOnlyBuffer;

public interface InteractiveExecutable
{
	public void setup(String[] args, AppendOnlyBuffer out);
	public default String getPrompt()
	{
		return ">";
	}
	public void acceptCommand(String command);
	public void act(int dt);
	public boolean isRunning();
	public void stop();
}
