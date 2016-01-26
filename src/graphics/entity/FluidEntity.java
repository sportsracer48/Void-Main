package graphics.entity;

import graphics.Context;
import graphics.Sprite;

public class FluidEntity extends Entity
{
	float width, height, xOffset, yOffset;
	Sprite body;
	Entity self;
	
	public FluidEntity(float x, float y, float z, float width, float height)
	{
		this(x,y,z);
		this.width = width;
		this.height = height;
	}
	
	public FluidEntity(float x, float y, float z)
	{
		super(x,y,z,null);
	}
	
	public Sprite getBase()
	{
		if(self!=null)
		{
			return self.getBase();
		}
		return body;
	}
	
	public float getUnscaledWidth()
	{
		return width;
	}
	
	public float getUnscaledHeight()
	{
		return height;
	}
	
	public void setWidth(float width)
	{
		this.width = width;
	}
	
	public void setHeight(float height)
	{
		this.height = height;
	}
	
	public void setSprite(Sprite body)
	{
		if(self!=null)
		{
			super.removeChild(self);
			self=null;
		}
		this.body = body;
	}
	
	public void setSpriteAndSize(Sprite body)
	{
		setSprite(body);
		if(body == null)
		{
			setWidth(0);
			setHeight(0);
		}
		else
		{
			setWidth(body.imWidth);
			setHeight(body.imHeight);
		}
	}
	
	public void removeChild(Entity e)
	{
		if(e==self)
		{
			return;
		}
		super.removeChild(e);
	}
	public void clearChildren()
	{
		super.clearChildren();
		if(self!=null)
		{
			addChild(self);
		}
	}
	
	public void setTo(Entity e)
	{
		setSpriteAndSize(null);
		this.self = e;
		if(e!=null)
		{
			addChild(e);
			setWidth(e.getWidth());
			setHeight(e.getHeight());
		}
	}
	
	public void renderBase(Context c)
	{
		if(body != null)
		{
			body.render(c);
		}
	}
	
}
