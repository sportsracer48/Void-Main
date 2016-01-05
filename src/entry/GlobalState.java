package entry;

import state.programming.ProgrammingState;
import state.workbench.WorkbenchState;
import computer.system.Computer;

public class GlobalState
{
	public static Computer laptop;
	
	public static WorkbenchState currentWorkbench;
	public static ProgrammingState currentProgramming;
	
	public static void init()
	{
		laptop = new Computer("laptop");
	}
}
