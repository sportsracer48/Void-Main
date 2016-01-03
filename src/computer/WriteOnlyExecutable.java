package computer;

import state.programming.AppendOnlyBuffer;

public interface WriteOnlyExecutable
{
	public void run(String[] args, AppendOnlyBuffer out);
}
