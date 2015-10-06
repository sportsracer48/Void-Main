package graphics.entity;

import graphics.Context;
import graphics.Renderable;
import graphics.Sprite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import math.Matrix;

public class Entity implements Renderable
{
	List<Entity> children = new ArrayList<>();
	float x,y,z;
	Sprite base;
	Matrix translation;
	
	public Entity(float x, float y, float z, Sprite base)
	{
		this.x=x;
		this.y=y;
		this.z=z;
		this.base=base;
		this.translation = Matrix.translation(x,y,0);
	}
	
	public void addChild(Entity child)
	{
		children.add(child);
	}
	
	public void moveTo(float x, float y)
	{
		this.x=x;
		this.y=y;
		this.translation = Matrix.translation(x,y,0);
	}
	
	public void setZ(float z)
	{
		this.z=z;
	}
	
	public void render(Context c)
	{
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
