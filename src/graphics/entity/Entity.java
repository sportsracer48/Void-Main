package graphics.entity;

import graphics.Context;
import graphics.RenderList;
import graphics.Sprite;

import java.util.ArrayList;
import java.util.List;

import action.Actable;
import state.Mode;
import state.ModeManager;
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
	Matrix scale = Matrix.identity(4);
	Matrix rotation = Matrix.identity(4);
	float scaleX=1,scaleY=1;
	Matrix model;
	boolean enabled = true;
	boolean visible = true;
	Matrix color = Color.white;
	float groupAlpha = 1;
	boolean colored = false;
	boolean translateZ = false;
	Mode mode;
	ModeManager manager;
	
	
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
		updateModel();
	}
	
	public void setScale(float scale)
	{
		this.scale = Matrix.scaling(scale,scale,1);
		this.scaleX = scale;
		this.scaleY = scale;
		updateModel();
	}
	
	public void setScale(float xScale, float yScale)
	{
		this.scale = Matrix.scaling(xScale,yScale,1);
		this.scaleX = xScale;
		this.scaleY = yScale;
		updateModel();
	}
	
	public void setScale(float xScale, float yScale, float zScale)
	{
		this.scale = Matrix.scaling(xScale,yScale,zScale);
		this.scaleX = xScale;
		this.scaleY = yScale;
		updateModel();
	}
	
	public void setRotation(Matrix rot)
	{
		this.rotation = rot;
		updateModel();
	}
	
	protected final void updateModel()
	{
		this.model = translation.dot(this.scale.dot(this.rotation));
	}
	
	public float getSpriteWidth()
	{
		if(base != null)
		{
			return base.imWidth;
		}
		return 0;
	}
	
	public float getSpriteHeight()
	{
		if(base != null)
		{
			return base.imHeight;
		}
		return 0;
	}
	
	public float getWidth()
	{
		return getSpriteWidth()*scaleX;
	}
	
	public float getHeight()
	{
		return getSpriteHeight()*scaleY;
	}
	
	public void addChild(Entity child)
	{
		children.add(child);
	}
	
	public void removeChild(Entity child)
	{
		children.remove(child);
	}
	
	public void clearChildren()
	{
		children.clear();
	}
	
	public void addClickableArea(ClickableArea area)
	{
		clickable.add(area);
	}
	
	public boolean modeEnabled()
	{
		return manager==null || manager.getMode() == mode;
	}
	
	public void setMode(Mode interactableMode, ModeManager manager)
	{
		this.manager = manager;
		this.mode = interactableMode;
	}
	
	public boolean handleClick(float x, float y, int button, Matrix model)
	{
		if(!enabled || !modeEnabled())
		{
			return false;
		}
		Matrix nextModel = model.dot(this.model);
		for(Entity e: children)
		{
			e.handleClick(x, y, button, nextModel);
		}
		for(ClickableArea a: clickable)
		{
			if(a.contains(x, y, nextModel))
			{
				a.handleClick(x,y,button,nextModel);
				return true;
			}
		}
		if(root!=null)
		{
			if(root.contains(x,y,nextModel))
			{
				root.handleClick(x, y, button, nextModel);
				return true;
			}
		}
		return false;
	}
	
	public boolean handleClick(float x, float y, int button)
	{
		return(handleClick(x,y,button,Matrix.identity(4)));
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
		if(!enabled || !modeEnabled())
		{
			return;
		}
		Matrix nextModel = model.dot(this.model);
		for(Entity e: children)
		{
			e.handleMove(x, y, context, nextModel);
		}
		for(ClickableArea a:clickable)
		{
			a.handleMove(x, y, nextModel, context);
		}
		if(root!=null)
		{
			root.handleMove(x, y, nextModel, context);
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
			colored = false;
			return;
		}
		else
		{
			colored = true;
			color = c;
		}
	}
	
	public Matrix getColor()
	{
		return color;
	}
	
	public float getGroupAlpha()
	{
		return groupAlpha;
	}

	public void setGroupAlpha(float groupAlpha)
	{
		this.groupAlpha = groupAlpha;
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
	
	protected void updateTranslation()
	{
		this.translation = Matrix.translation(x,y,translateZ?getZ():0);
		updateModel();
	}
	
	public void setTranslateZ(boolean translateZ)
	{
		if(translateZ != this.translateZ)
		{
			this.translateZ = translateZ;
			updateTranslation();
		}
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
	
	public void render(Context c)
	{
		if(!enabled || !visible)
		{
			return;
		}
		if(groupAlpha!=1)
		{
			c.setAlpha(groupAlpha);
		}
		if(colored)
		{
			c.setColor(new Color(color.r(),color.g(),color.b(),color.a()*c.getAlpha()));
		}
		else if(c.getAlpha() != 1)
		{
			c.setColor(new Color(1,1,1,c.getAlpha()));
		}
		c.pushTransform();
		c.prependTransform(model);
		
		renderBase(c);
		
		if(colored)
		{
			c.resetColor();
		}
		renderChildren(c);
		if(groupAlpha!=1)
		{
			c.setAlpha(1);
		}
		c.popTransform();
	}
	
	public void renderChildren(Context c)
	{
		children.render(c);
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
	
	public void enableRoot()
	{
		this.root = new ClickableArea(0,0,getSpriteWidth(),getSpriteHeight());
	}
	
	public int compareTo(Entity r)
	{
		return Float.compare(getZ(), r.getZ());
	}
}
