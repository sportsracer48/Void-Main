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
		area = new ClickableArea(0,0,getWidth(),getHeight())
		{
			public void onRelease()
			{
				if(onPress != null && area.containsMouse())
				{
					onPress.run();
				}
			}
		};
		addClickableArea(area);
	}
	public Button(ButtonTheme t)
	{
		super(0,0,0,t.mouseOver, t.pressed,t.gray);
		area = new ClickableArea(0,0,getWidth(),getHeight())
		{
			public void onRelease()
			{
				if(onPress != null && area.containsMouse())
				{
					onPress.run();
				}
			}
		};
		addClickableArea(area);
	}
	
	public void setOnPress(Runnable onPress)
	{
		this.onPress = onPress;
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
	
	public static class ButtonTheme
	{
		public final Sprite mouseOver, pressed, gray;
		
		public ButtonTheme(Sprite mouseOver, Sprite pressed, Sprite gray)
		{
			this.mouseOver = mouseOver;
			this.pressed = pressed;
			this.gray = gray;
		}
		
		public Button build()
		{
			return new Button(this);
		}
	}
}
