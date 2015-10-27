package graphics.entity;

import graphics.Sprite;

public abstract class AnimatedEntity extends FluidEntity
{
	public AnimatedEntity(float x, float y, float z, Sprite base)
	{
		super(x, y, z);
		setSpriteAndSize(base);
	}
	
	public abstract void animate(int dt);
	
	public void act(int dt)
	{
		super.act(dt);
		animate(dt);
	}
}
