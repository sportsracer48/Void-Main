package game.item;

import state.workbench.graphics.PinHighlight;
import util.Grid.Coord;

public class Pin
{
	int x, y;
	Item parent;
	Wire attatched;
	int potential = 0;
	int goalPotential = 0;
	boolean grounded = false;
	boolean goalGrounded = false;
	
	
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
	
	public int getPotential()
	{
		return potential;
	}
	public boolean isGround()
	{
		return grounded;
	}
	public boolean leadsToGround()
	{
		if(getAttatched() != null && getAttatched().getOtherEnd(this) != null)
		{
			return getAttatched().getOtherEnd(this).isGround();
		}
		return false;
	}
	public boolean isGrounded()
	{
		return isGround() || leadsToGround();
	}
	public void setPotential(int potential)
	{
		this.goalPotential = potential;
		this.potential = potential;
	}
	public void setGrounded(boolean grounded)
	{
		this.goalGrounded = grounded;
		this.grounded = grounded;
	}
	public void setGoalPotential(int potential)
	{
		this.goalPotential = potential;
	}
	public void setGoalGrounded(boolean grounded)
	{
		this.goalGrounded = grounded;
	}
	public void setTotalPotential(int potential)
	{
		this.potential = potential - getReceivedPotential();
	}
	public void setTotalGrounding(boolean grounded)
	{
		if(grounded)
		{
			this.grounded = !leadsToGround();
		}
		else
		{
			this.grounded = false;
		}
	}
	
	public int getReceivedPotential()
	{
		if(getAttatched() != null && getAttatched().getOtherEnd(this) != null)
		{
			Pin other = getAttatched().getOtherEnd(this);
			return other.getPotential();
		}
		return 0;
	}
	
	public Item getParent()
	{
		return parent;
	}
	
	public Coord getLocation()
	{
		return new Coord(x,y);
	}

	public void act(int dt)
	{
		this.potential = goalPotential;
		this.grounded = goalGrounded;
	}
}
