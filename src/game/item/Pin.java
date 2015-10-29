package game.item;

import state.workbench.graphics.PinHighlight;
import util.Grid.Coord;

public class Pin
{
	int x, y;
	Item parent;
	Wire attatched;
	public transient PinHighlight highlight;
	
	public Pin(Item parent,int x, int y)
	{
		this.x=x;
		this.y=y;
		this.parent = parent;
	}
	
	public Wire getAttatched()
	{
		return attatched;
	}
	
	public void setAttatched(Wire attatched)
	{
		this.attatched = attatched;
	}

	public void strip()
	{
		if(attatched!=null) attatched.reset();
	}
	
	public Item getParent()
	{
		return parent;
	}
	
	public Coord getLocation()
	{
		return new Coord(x,y);
	}
}
