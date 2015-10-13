package state.ui;

import graphics.Sprite;
import graphics.entity.Entity;

public class HighlightArea extends Entity
{
	ClickableArea area;
	
	public HighlightArea(float x, float y, Sprite highlight)
	{
		super(x,y,0,highlight);
		area = new ClickableArea(x,y,getWidth(),getHeight())
		{
			public void mouseEntered()
			{
			}

			public void mouseExited()
			{
			}

			public void onClick(float x, float y)
			{
			}

			public void onRelease()
			{
			}
		};
	}
	
	public void setPos(float x, float y)
	{
		super.setPos(x, y);
		area.setPos(x,y);
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
			setVisible(true);
		}
		else
		{
			setVisible(false);
		}
	}

}
