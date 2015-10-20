package graphics.entity;

import graphics.Context;
import graphics.RenderList;
import graphics.Sprite;

import java.util.ArrayList;
import java.util.List;

import action.Actable;
import state.ui.ClickableArea;
import state.ui.MouseoverContext;
import util.Color;
import math.Matrix;

public class Entity implements Comparable<Entity>, Actable
{
	public static final double percentPerFrame = .3;
	public static final double msPerFrame = 16;
	public static final double percentChangePerMs = Math.pow((1-percentPerFrame),1/msPerFrame);
	
	RenderList children = new RenderList();
	List<ClickableArea> clickable = new ArrayList<>();
	ClickableArea root = null;
	
	float x,y,z;
	float targetX, targetY, targetZ;
	Sprite base;
	Matrix translation;
	boolean enabled = true;
	boolean visible = true;
	Matrix color = Color.white;
	boolean colored = false;
	
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
	
	public void removeChild(Entity child)
	{
		children.remove(child);
	}
	
	public void addClickableArea(ClickableArea area)
	{
		clickable.add(area);
	}
	
	public boolean handleClick(float x, float y, Matrix model)
	{
		if(!enabled)
		{
			return false;
		}
		Matrix translation = model.dot(this.translation);
		for(Entity e: children)
		{
			e.handleClick(x, y, translation);
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
	
	public boolean handleClick(float x, float y)
	{
		return(handleClick(x,y,Matrix.identity(4)));
	}
	
	public void handleRelease()
	{
		for(Entity e: children)
		{
			e.handleRelease();
		}
		for(ClickableArea a: clickable)
		{
			if(a.isMouseHeld())
			{
				a.handleRelease();
			}
			a.handleAnyRelease();
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
	
	public void handleMove(float x, float y, MouseoverContext context, Matrix model)
	{
		if(!enabled)
		{
			return;
		}
		Matrix translation = model.dot(this.translation);
		for(Entity e: children)
		{
			e.handleMove(x, y, context, translation);
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
	
	public void handleMove(float x, float y, MouseoverContext context)
	{
		handleMove(x,y,context,Matrix.identity(4));
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	public void setColor(Matrix c)
	{
		if(c.equals(Color.white))
		{
			return;
		}
		else
		{
			colored = true;
			color = c;
		}
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
		updateTranslation();
	}
	
	public void updateTranslation()
	{
		this.translation = Matrix.translation(x,y,0);
	}
	
	public void act(int dt)
	{
		if(x!=targetX || y!=targetY)
		{
			x += (targetX-x)*(1-Math.pow(percentChangePerMs,dt));
			y += (targetY-y)*(1-Math.pow(percentChangePerMs,dt));
			if(Math.abs(targetX-x)<1)
			{
				x = targetX;
			}
			if(Math.abs(targetY-y)<1)
			{
				y = targetY;
			}
			updateTranslation();
		}
		this.z=targetZ;
		children.update();
		for(Entity e: children)
		{
			e.act(dt);
		}
		children.update();
	}
	
	public void setZ(float z)
	{
		this.targetZ=this.z=z;
	}
	
	public final void render(Context c)
	{
		if(!enabled || !visible)
		{
			return;
		}
		if(colored)
		{
			c.setColor(color);
		}
		c.pushTransform();
		c.prependTransform(translation);
		
		renderBase(c);
		
		children.render(c);
		c.popTransform();
		if(colored)
		{
			c.resetColor();
		}
	}
	
	public void renderBase(Context c)
	{
		if(base!=null)
		{
			base.render(c);
		}
	}
	
	public float getX()
	{
		return x;
	}
	
	public float getY()
	{
		return y;
	}
	
	public float getZ()
	{
		return z;
	}

	public ClickableArea getRoot()
	{
		return root;
	}

	public void setRoot(ClickableArea root)
	{
		this.root = root;
	}
	
	public int compareTo(Entity r)
	{
		return Float.compare(getZ(), r.getZ());
	}
}
