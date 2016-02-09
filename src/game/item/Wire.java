package game.item;

import java.io.Serializable;

import util.Color;

public class Wire implements Serializable
{
	private static final long serialVersionUID = -5001928836113260611L;
	
	public static final int START = 0;
	public static final int END = 1;
	Color c;
	Pin start,end;
	
	public Pin getStart()
	{
		return start;
	}
	
	public int getPosition(Pin p)
	{
		if(p==start)
		{
			return START;
		}
		if(p==end)
		{
			return END;
		}
		return -1;
	}
	public void setPin(int position,Pin pin)
	{
		if(position == START)
		{
			setStart(pin);
		}
		else if(position == END)
		{
			setEnd(pin);
		}
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
			start = null;
		}
		if(end!=null)
		{
			end.setAttatched(null);
			end = null;
		}
	}
	
	public boolean isAttatchedOnBothSides()
	{
		return end!=null && start!=null;
	}
	public boolean isAttatchedOnNoSide()
	{
		return end==null && start == null;
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

	public void attachSelf()
	{
		if(start!=null)
		{
			start.setAttatched(this);
		}
		if(end != null)
		{
			end.setAttatched(this);
		}
	}
	
	public Wire clone()
	{
		Wire w = new Wire(c);
		w.start = start;
		w.end = end;
		return w;
	}
	public boolean equals(Object o)
	{
		if(o instanceof Wire)
		{
			Wire w = (Wire)o;
			return w.start == start && w.end == end || w.start == end && w.end == start;
		}
		return false;
	}
	public int hashCode()
	{
		int hash = 17;
		if(start!=null)
		{
			hash = hash*23 + start.hashCode();
		}
		else
		{
			hash*=23;
		}
		if(end!=null)
		{
			hash = hash*23 + end.hashCode();
		}
		else
		{
			hash*=23;
		}
		return hash;
	}
}
