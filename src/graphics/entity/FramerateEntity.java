package graphics.entity;

import graphics.Sprite;

public class FramerateEntity extends FrameEntity
{
	int frames;
	float time;
	float fps;//hz
	float animationTime;
	float totalAnimationTime;//ms
	public FramerateEntity(float x, float y, float z, Sprite[] sprites, float fps)
	{
		this(x,y,z,sprites,fps,0);
	}
	public FramerateEntity(float x, float y, float z, Sprite[] sprites, float fps, float offset)
	{
		this(x,y,z,sprites,fps,offset,0);
	}
	public FramerateEntity(float x, float y, float z, Sprite[] sprites, float fps, float offset, float breakTime)
	{
		super(x, y, z, sprites);
		this.frames = sprites.length;
		this.fps = fps;
		animationTime = (frames*1000)/fps;
		totalAnimationTime = animationTime+breakTime*1000;
		this.time = offset*totalAnimationTime;
	}
	public void act(int dt)
	{
		time+=dt;
		time %= totalAnimationTime;
		
		if(time<animationTime)
		{
			int frame = (int)((time*frames)/animationTime);
			super.setFrame(frame);
		}
		else
		{
			super.setFrame(0);
		}
		
		
		super.act(dt);
	}
}
