package graphics.entity;

import java.util.Collections;

import graphics.Context;
import graphics.Sprite;

public class FrameEntity extends Entity
{
	int frame = 0;
	Sprite[] sprites;
	public FrameEntity(float x, float y, float z, Sprite... sprites)
	{
		super(x, y, z, sprites[0]);
		this.sprites = sprites;
	}
	
	public void setFrame(int frame)
	{
		this.frame = frame;
	}
	
	public void render(Context c)
	{
		if(!visible)
		{
			return;
		}
		c.pushTransform();
		c.prependTransform(translation);
		
		if(frame>=0 && frame<sprites.length)
		{
			sprites[frame].render(c);
		}
		
		Collections.sort(children);
		for(Entity e:children)
		{
			e.render(c);
		}
		c.popTransform();
	}
	
}
