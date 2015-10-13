package state.ui;

import graphics.Sprite;
import graphics.entity.FrameEntity;

public class Button extends FrameEntity
{
	public static final int INVISIBLE = -1;
	public static final int MOUSEOVER = 0;
	public static final int PRESSED = 1;
	public static final int PRESSEDGRAY = 2;
	
	Runnable onPress;
	ClickableArea area;
	
	public Button(float x, float y, Sprite mouseOver, Sprite pressed, Sprite pressedGray)
	{
		super(x,y,0,mouseOver, pressed,pressedGray);
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
				if(onPress != null && area.containsMouse())
				{
					onPress.run();
				}
			}
		};
	}
	
	public void setPos(float x, float y)
	{
		super.setPos(x, y);
		area.setPos(x,y);
	}
	
	public void setOnPress(Runnable onPress)
	{
		this.onPress = onPress;
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
			if(area.isMouseHeld())
			{
				setFrame(PRESSED);
			}
			else
			{
				setFrame(MOUSEOVER);
			}
		}
		else
		{
			if(area.isMouseHeld())
			{
				setFrame(PRESSEDGRAY);
			}
			else
			{
				setFrame(-1);
			}
		}
	}
}
