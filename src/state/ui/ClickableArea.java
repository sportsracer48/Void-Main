package state.ui;

import math.Matrix;
import math.Rectangle;

public abstract class ClickableArea
{
	private boolean containsMouse;
	private boolean ownsMouse = false;
	private boolean mouseHeld = false;
	Rectangle bounds;
	float width,height;
	public ClickableArea(float x, float y, float width, float height)
	{
		bounds = new Rectangle(x,y,width,height);
		this.width = width;
		this.height = height;
	}
	
	public boolean contains(float x, float y, Matrix model)
	{
		return bounds.contains(model,x, y);
	}
	
	public void handleMove(float x, float y, Matrix model, MouseoverContext context)
	{
		if(containsMouse() && !bounds.contains(model, x, y))
		{
			containsMouse = false;
			mouseExited();
		}
		if(!containsMouse() && bounds.contains(model, x, y))
		{
			containsMouse = true;
			mouseEntered();
		}
		
		
		if(containsMouse && context!= null && !context.hasMouseHolder())
		{
			context.setMouseHolder(this);
		}
		
		if(!containsMouse && context!= null && context.getMouseHolder()==this)
		{
			context.setMouseHolder(null);
		}
		
	}
	
	public void handleClick(float x, float y, Matrix model)
	{
		Rectangle worldBounds = bounds.transform(model);
		onClick(x-worldBounds.x,y-worldBounds.y);
		mouseHeld = true;
	}
	
	public void handleRelease()
	{
		mouseHeld = false;
		onRelease();
	}
	
	public abstract void mouseEntered();
	public abstract void mouseExited();
	public abstract void onClick(float x, float y);
	public abstract void onRelease();

	public void setPos(float x, float y)
	{
		bounds = new Rectangle(x,y,bounds.width,bounds.height);
	}
	
	public void setPadding(float x, float y)
	{
		bounds = new Rectangle(bounds.x-x,bounds.y-y, width+2*x, height+2*y);
	}
	
	public float getX()
	{
		return bounds.x;
	}
	
	public float getY()
	{
		return bounds.y;
	}

	public boolean containsMouse()
	{
		return containsMouse;
	}

	public boolean isMouseHeld()
	{
		return mouseHeld;
	}

	public boolean ownsMouse()
	{
		return ownsMouse;
	}

	void setOwnsMouse(boolean ownsMouse)
	{
		this.ownsMouse = ownsMouse;
	}
}
