package state.workbench.conroller;

import graphics.entity.Entity;
import state.ui.ClickableArea;

public class DragArea extends ClickableArea
{
	DragContext context;
	private Entity target;
	
	public DragArea(float x, float y, float width, float height, DragContext context, Entity target)
	{
		super(x, y, width, height);
		this.context = context;
		this.target = target;
		target.addClickableArea(this);
	}

	public void onClick(float x, float y)
	{
		context.setGrabbed(this, x+this.getX(), y+this.getY());
		
	}

	public void move(float x, float y)
	{
		target.moveTo(x, y);
	}

	public Entity getTarget()
	{
		return target;
	}
}
