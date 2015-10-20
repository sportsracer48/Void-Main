package state.ui;

import graphics.Sprite;
import graphics.entity.FrameEntity;

public class HighlightArea extends FrameEntity
{
	ClickableArea area;
	
	public HighlightArea(float x, float y, Sprite highlight)
	{
		super(x,y,0,highlight);
		area = new ClickableArea(0,0,getWidth(),getHeight());
		addClickableArea(area);
	}
	
	public ClickableArea getArea()
	{
		return area;
	}
	
	public void act(int dt)
	{
		super.act(dt);
		if(area.ownsMouse())
		{
			setFrame(0);
		}
		else
		{
			setFrame(-1);//still visible, so that it can be interacted with, but nothing will be rendered
		}
	}

}
