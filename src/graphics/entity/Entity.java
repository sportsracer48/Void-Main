package graphics.entity;

import graphics.Context;
import graphics.Renderable;
import graphics.Sprite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import state.ui.ClickableArea;
import math.Matrix;
import math.Rectangle;

public class Entity implements Renderable
{
	List<Entity> children = new ArrayList<>();
	List<ClickableArea> clickable = new ArrayList<>();
	
	float x,y,z;
	Sprite base;
	Matrix translation;
	float width,height;
	Rectangle bounds;
	boolean visible = true;
	
	public Entity(float x, float y, float z, Sprite base)
	{
		this.width = base.imWidth;
		this.height = base.imHeight;
		this.x=x;
		this.y=y;
		this.z=z;
		this.base=base;
		this.width = base.imWidth;
		this.translation = Matrix.translation(x,y,0);
		bounds = new Rectangle(0,0,width,height);
	}
	public void addChild(Entity child)
	{
		children.add(child);
	}
	
	public void addClickableArea(ClickableArea area)
	{
		clickable.add(area);
	}
	
	public boolean handleClick(float x, float y)
	{
		if(!visible)
		{
			return false;
		}
		for(ClickableArea a: clickable)
		{
			if(a.contains(x, y, translation))
			{
				a.handleClick(x,y,translation);
				return true;
			}
		}
		return false;
	}
	
	public void handleRelease()
	{
		for(ClickableArea a: clickable)
		{
			if(a.mouseHeld)
			{
				a.handleRelease();
			}
		}
	}
	
	public void handleMove(float x, float y)
	{
		if(!visible)
		{
			return;
		}
		for(ClickableArea a:clickable)
		{
			a.handleMove(x, y, translation);
		}
	}
	
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	public void moveTo(float x, float y)
	{
		this.x=x;
		this.y=y;
		this.translation = Matrix.translation(x,y,0);
	}
	
	public boolean contains(float x, float y)
	{
		return bounds.contains(translation,x,y);
	}
	
	public void setZ(float z)
	{
		this.z=z;
	}
	
	public void render(Context c)
	{
		if(!visible)
		{
			return;
		}
		c.pushTransform();
		c.prependTransform(translation);
		
		if(base!=null)
		{
			base.render(c);
		}
		Collections.sort(children);
		for(Entity e:children)
		{
			e.render(c);
		}
		c.popTransform();
	}
	public float getZ()
	{
		return z;
	}
	
	
}
