package state.ui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import math.Matrix;
import math.Rectangle;

public class ClickableArea
{
	private boolean containsMouse;
	private boolean ownsMouse = false;
	private boolean mouseHeld = false;
	Rectangle bounds;
	float width,height;
	private boolean desiresMouse = true; //of mouse
	List<ClickListener> onClick =  new ArrayList<>();
	List<Runnable> onRelease = new ArrayList<>();
	List<Runnable> onAnyRelease = new ArrayList<>();
	List<Runnable> onAnyMove = new ArrayList<>();
	
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
		
		
		if(desiresMouse && containsMouse && context!= null && !context.hasMouseHolder())
		{
			context.setMouseHolder(this);
		}
		
		if(!containsMouse && context!= null && context.getMouseHolder()==this)
		{
			context.setMouseHolder(null);
		}
		
		for(Runnable r: onAnyMove)
		{
			r.run();
		}
		onAnyMove();
		
	}
	
	public void handleClick(float x, float y, int button, Matrix model)
	{
		Rectangle worldBounds = bounds.transform(model);
		float xLocal = x - worldBounds.x;
		float yLocal = y - worldBounds.y;
		for(ClickListener c: onClick)
		{
			c.onClick(xLocal,yLocal,button);
		}
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
		{
			mouseHeld = true;
			onLeftClick(xLocal,yLocal);
		}
		onClick(xLocal,yLocal,button);
	}
	
	public void handleRelease()
	{
		mouseHeld = false;
		onRelease();
		for(Runnable r: onRelease)
		{
			r.run();
		}
	}
	

	public void handleAnyRelease()
	{
		onAnyRelease();
		for(Runnable r: onAnyRelease)
		{
			r.run();
		}
	}
	
	public void mouseEntered(){}
	public void mouseExited(){}
	public void onLeftClick(float x, float y){}
	public void onClick(float x, float y, int button){}
	public void onRelease(){}
	public void onAnyRelease(){}
	public void onAnyMove(){}

	public void setPos(float x, float y)
	{
		bounds = new Rectangle(x,y,bounds.width,bounds.height);
	}
	
	public void addPadding(float x, float y)
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
	
	/**
	 * please don't call this method unless the mouse is actually pressed.
	 * @param mouseHeld
	 */
	public void setMouseHeld(boolean mouseHeld)
	{
		this.mouseHeld = mouseHeld;
	}

	public boolean ownsMouse()
	{
		return ownsMouse;
	}
	
	public void addOnClick(ClickListener r)
	{
		onClick.add(r);
	}
	
	public void addOnRelease(Runnable r)
	{
		onRelease.add(r);
	}
	
	public void addOnAnyRelease(Runnable r)
	{
		onAnyRelease.add(r);
	}
	
	public void addOnAnyMove(Runnable r)
	{
		onAnyMove.add(r);
	}

	void setOwnsMouse(boolean ownsMouse)
	{
		this.ownsMouse = desiresMouse && ownsMouse; //don't own the mouse unless you want it
	}
	
	public void setDesiresMouse(boolean desiresMouse)
	{
		this.desiresMouse = desiresMouse;
	}

	public boolean desiresMouse()
	{
		return desiresMouse;
	}
}
