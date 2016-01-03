package computer;

import java.util.function.IntConsumer;

import graphics.registry.RegisteredFont;
import state.programming.AppendOnlyBuffer;

public class ExecutableHolder
{
	public static final int WRITE_ONLY=0;
	public static final int INTERACTIVE=1;
	public static final int RENDERED=2;
	
	int mode;
	WriteOnlyExecutable writeOnlyExec;
	InteractiveExecutable interactiveExec;
	RenderedExecutable renderedExec;
	
	public ExecutableHolder(WriteOnlyExecutable exec)
	{
		writeOnlyExec = exec;
		mode = WRITE_ONLY;
	}
	public ExecutableHolder(InteractiveExecutable exec)
	{
		interactiveExec = exec;
		mode = INTERACTIVE;
	}
	public ExecutableHolder(RenderedExecutable exec)
	{
		renderedExec = exec;
		mode = RENDERED;
	}
	
	public void init(String[] args, AppendOnlyBuffer appendOnly, StringBuffer editable, RegisteredFont consoleFont, int cols, int rows, IntConsumer makeVisible)
	{
		switch(mode)
		{
		case WRITE_ONLY:
			writeOnlyExec.run(args, appendOnly);
			break;
		case INTERACTIVE:
			interactiveExec.setup(args, appendOnly);
			break;
		case RENDERED:
			renderedExec.setup(args, editable, consoleFont, cols, rows, makeVisible);
			break;
		}
	}
	
	public boolean isInteractive()
	{
		return mode == INTERACTIVE;
	}
	public boolean isRendered()
	{
		return mode == RENDERED;
	}
	public InteractiveExecutable getInteractiveExec()
	{
		return interactiveExec;
	}
	public RenderedExecutable getRenderedExec()
	{
		return renderedExec;
	}
}
