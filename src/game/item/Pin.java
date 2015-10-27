package game.item;

import state.workbench.PinHighlight;

public class Pin
{
	int x, y;
	Wire attatched;
	public transient PinHighlight highlight;
	
	public Pin(int x, int y)
	{
		this.x=x;
		this.y=y;
	}
	
	public Wire getAttatched()
	{
		return attatched;
	}
	
	public void setAttatched(Wire attatched)
	{
		this.attatched = attatched;
	}
}
