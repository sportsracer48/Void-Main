package game.item;

import util.Color;

public class Wire
{
	Color c;
	Pin start,end;
	
	public Pin getStart()
	{
		return start;
	}

	public void setStart(Pin start)
	{
		this.start = start;
		start.setAttatched(this);
	}

	public Pin getEnd()
	{
		return end;
	}

	public void setEnd(Pin end)
	{
		this.end = end;
		end.setAttatched(this);
	}

	public Wire(Color c)
	{
		this.c=c;
	}
	
	public Color getColor()
	{
		return c;
	}

	public void reset()
	{
		if(start!=null)
		{
			start.setAttatched(null);
		}
		if(end!=null)
		{
			end.setAttatched(null);
		}
	}
	
	public boolean isAttatchedOnBothSides()
	{
		return end!=null && start!=null;
	}

	public void extractFrom(Pin pin)
	{
		if(pin == start)
		{
			start=null;
			pin.setAttatched(null);
		}
		if(pin == end)
		{
			end = null;
			pin.setAttatched(null);
		}
	}

	public Pin getOtherEnd(Pin pin)
	{
		if(pin==start)
		{
			return end;
		}
		if(pin==end)
		{
			return start;
		}
		return null;
	}
	
}
