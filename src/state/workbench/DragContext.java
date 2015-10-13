package state.workbench;

public class DragContext
{
	DragArea grabbed;
	float xOffset,yOffset;
	
	public void setGrabbed(DragArea grabbed, float xOffset, float yOffset)
	{
		this.grabbed = grabbed;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	public void resetGrabbed()
	{
		grabbed = null;
	}
	public void mouseMoved(float x, float y)
	{
		if(grabbed != null)
		{
			grabbed.move(x-xOffset,y-yOffset);
		}
	}
}
