package entry;

import state.programming.ProgrammingState;
import state.workbench.WorkbenchState;
import computer.system.Computer;

public class GlobalState
{
	public static final boolean DEBUG = false;

	public static Computer laptop;
	
	
	//BAD BAD NAUGHTY BAD BAD
	//TODO REMOVE GLOBAL STATE
	public static WorkbenchState currentWorkbench;
	public static ProgrammingState currentProgramming;
	
	public static void init()
	{
		laptop = new Computer("laptop");
	}
}
