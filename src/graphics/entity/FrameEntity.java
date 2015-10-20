package graphics.entity;

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
	
	public void renderBase(Context c)
	{
		if(frame>=0 && frame<sprites.length)
		{
			sprites[frame].render(c);
		}
	}
	
}
