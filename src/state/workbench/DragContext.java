package state.workbench;

import state.ui.ClickableArea;

public class DragContext
{
	DragArea grabbed;
	float xOffset,yOffset;
	
	public void setGrabbed(DragArea grabbed, float xOffset, float yOffset)
	{
		this.grabbed = grabbed;
		grabbed.setMouseHeld(true);
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	public ClickableArea getGrabbedArea()
	{
		return grabbed;
	}
	public void resetGrabbed()
	{
		grabbed = null;
	}
	public boolean hasObject()
	{
		return grabbed != null;
	}
	public void mouseMoved(float x, float y)
	{
		if(grabbed != null)
		{
			grabbed.move(x-xOffset,y-yOffset);
		}
	}
}
