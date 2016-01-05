package computer.program;

import computer.system.Computer;

import state.programming.AppendOnlyBuffer;

public interface WriteOnlyExecutable
{
	public void run(String[] args, AppendOnlyBuffer out, Computer system);
}
