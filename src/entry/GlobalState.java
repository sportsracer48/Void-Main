package entry;

import program.ProgramCoordinator;
import state.programming.ProgrammingState;
import state.workbench.WorkbenchState;
import computer.system.Computer;

public class GlobalState
{
	public static final boolean DEBUG = true;

	public static Computer laptop;
	public static ProgramCoordinator coordinator;
	
	//BAD BAD NAUGHTY BAD BAD
	//TODO REMOVE GLOBAL STATE
	public static WorkbenchState currentWorkbench;
	public static ProgrammingState currentProgramming;
	
	public static void init()
	{
		laptop = new Computer("laptop");
		coordinator = new ProgramCoordinator();
	}
}
