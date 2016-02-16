package computer.program;

import state.programming.AppendOnlyBuffer;
import computer.system.Computer;
import game.session.GlobalState;

public class Exit implements WriteOnlyExecutable
{

	public void run(String[] args, AppendOnlyBuffer out, Computer system)
	{
		out.appendln("goodbye");
		GlobalState.currentProgramming.changeTo(GlobalState.currentWorkbench);
	}

}
