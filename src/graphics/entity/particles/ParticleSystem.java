package graphics.entity;

import graphics.Sprite;

public abstract class ParticleSystem extends Entity
{
	Sprite[] particleSprites;
	float x,y,z,width,height,depth;
	public ParticleSystem(float x, float y, float z,float zIndex, float width, float height, float depth, Sprite[] particles)
	{
		super(0,0,zIndex,null);
		this.x=x;
		this.y=y;
		this.z=z;
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.particleSprites = particles;
	}
	
	public ParticleSystem(float zIndex)
	{
		super(0,0,zIndex,null);
		x=y=z=width=height=depth=0;
	}

	public Sprite getRandomParticleSrpite()
	{
		return particleSprites[(int)(Math.random()*particleSprites.length)];
	}
	public float getRandX()
	{
		return (float) (x + width*Math.random());
	}
	public float getRandY()
	{
		return (float) (y + height*Math.random());
	}
	public float getRandZ()
	{
		return (float) (z + depth*Math.random());
	}
	
	public void spawnParticle()
	{
		addParticle(createParticle());
	}
	
	public void addParticle(Particle p)
	{
		addChild(p);
	}
	
	public abstract Particle createParticle();
	
	public abstract void update(int dt);
	
	public void removeParticle(Particle particle)
	{
		removeChild(particle);
	}
	
	public void act(int dt)
	{
		update(dt);
		super.act(dt);
	}
}
