package graphics.entity;

import graphics.Context;
import graphics.Renderable;
import graphics.Sprite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import action.Actable;
import state.ui.ClickableArea;
import state.ui.MouseoverContext;
import math.Matrix;

public class Entity implements Renderable, Actable
{
	List<Entity> children = new ArrayList<>();
	List<ClickableArea> clickable = new ArrayList<>();
	ClickableArea root = null;
	
	float x,y,z;
	float targetX, targetY, targetZ;
	Sprite base;
	Matrix translation;
	boolean visible = true;
	
	public Entity(float x, float y, float z, Sprite base)
	{
		this.x=x;
		this.y=y;
		this.z=z;
		this.targetX=x;
		this.targetY=y;
		this.targetZ=z;
		this.base=base;
		this.translation = Matrix.translation(x,y,0);
	}
	
	public float getWidth()
	{
		return base.imWidth;
	}
	
	public float getHeight()
	{
		return base.imHeight;
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
		if(root!=null)
		{
			if(root.contains(x,y,translation))
			{
				root.handleClick(x, y, translation);
				return true;
			}
		}
		return false;
	}
	
	public void handleRelease()
	{
		for(ClickableArea a: clickable)
		{
			if(a.isMouseHeld())
			{
				a.handleRelease();
			}
		}
		if(root!=null)
		{
			root.handleRelease();
		}
	}
	
	public void handleMove(float x, float y)
	{
		handleMove(x,y,null);
	}
	
	public void handleMove(float x, float y, MouseoverContext context)
	{
		if(!visible)
		{
			return;
		}
		for(ClickableArea a:clickable)
		{
			a.handleMove(x, y, translation, context);
		}
		if(root!=null)
		{
			root.handleMove(x, y, translation, context);
		}
	}
	
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	public void moveTo(float x, float y)
	{
		targetX=x;
		targetY=y;
	}
	
	public void setPos(float x, float y)
	{
		this.targetX=this.x=x;
		this.targetY=this.y=y;
		this.translation = Matrix.translation(x,y,0);
	}
	
	public void act(int dt)
	{
		setPos(targetX,targetY);
		this.z=targetZ;
		for(Entity e: children)
		{
			e.act(dt);
		}
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

	public int compareTo(Renderable o)
	{
		return 0;
	}

	public ClickableArea getRoot()
	{
		return root;
	}

	public void setRoot(ClickableArea root)
	{
		this.root = root;
	}
	
	
}
