package state;

public class ModeManager
{
	public Mode current;
	public ModeManager(Mode startingMode)
	{
		this.current = startingMode;
	}
	public Mode getMode()
	{
		return current;
	}
	public void setMode(Mode m)
	{
		boolean change = false;
		if(m!=current)
		{
			current.disable();
			change = true;
		}
		this.current = m;
		if(change)
		{
			current.enable();
		}
	}
}
