package graphics.entity;

import graphics.Context;
import graphics.Sprite;

public class FluidEntity extends Entity
{
	float width, height, xOffset, yOffset;
	Sprite body;
	
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

	public float getSpriteWidth()
	{
		return width;
	}
	
	public float getSpriteHeight()
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
	
	public void renderBase(Context c)
	{
		if(body != null)
		{
			body.render(c);
		}
	}
	
}
