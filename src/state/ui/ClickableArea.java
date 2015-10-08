package state.ui;

import math.Matrix;
import math.Rectangle;

public abstract class ClickableArea
{
	public boolean containsMouse;
	public boolean mouseHeld = false;
	Rectangle bounds;
	public ClickableArea(float x, float y, float width, float height)
	{
		bounds = new Rectangle(x,y,width,height);
	}
	
	public boolean contains(float x, float y, Matrix model)
	{
		return bounds.contains(model,x, y);
	}
	
	public void handleMove(float x, float y, Matrix model)
	{
		if(containsMouse && !bounds.contains(model, x, y))
		{
			containsMouse = false;
			mouseExited();
		}
		if(!containsMouse && bounds.contains(model, x, y))
		{
			containsMouse = true;
			mouseEntered();
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
}
