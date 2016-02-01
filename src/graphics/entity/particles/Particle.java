package graphics.entity.particles;

import util.BoundingInterface;
import graphics.Sprite;
import graphics.entity.Entity;

public abstract class Particle extends Entity
{
	boolean alive = true;
	int lifeTime = 0;
	int lifespan;
	ParticleSystem system;
	protected float x,y,z;
	
	
	
	public Particle(float x, float y, float z, Sprite base, int lifespan,ParticleSystem system)
	{
		super(x, y, 0, base);
		this.x = x;
		this.y = y;
		this.z = z;
		this.lifespan = lifespan;
		this.system = system;
	}
	
	public abstract void update(int dt, int lifeTime, float x, float y, float z);
	
	public void kill()
	{
		alive = false;
	}
	
	public void setZ(float z)
	{
		this.z = z;
	}
	public void setX(float x)
	{
		this.x=x;
	}
	public void setY(float y)
	{
		this.y=y;
	}
	
	public boolean hasBounds()
	{
		return system.hasBounds();
	}
	public BoundingInterface getBounds()
	{
		return system.getBounds();
	}
	
	public void act(int dt)
	{
		float lastX = x;
		float lastY = y;
		float lastZ = z;
		update(dt,lifeTime,x,y,z);
		lifeTime += dt;
		
		if(lifeTime>=lifespan)
		{
			kill();
		}
		if(!alive)
		{
			system.removeParticle(this);
			return;
		}
		if(hasBounds() && !system.allowOOB())
		{
			BoundingInterface bounds = getBounds();
			float[] xyz = bounds.constrain(lastX,lastY,lastZ,x,y,z);
			x = xyz[0];
			y = xyz[1];
			z = xyz[2];
		}
		if(system.hasBins() && system.getBins().moved(lastX, lastY, x, y))
		{
			system.getBins().move(this, lastX, lastY, x, y);
		}
		super.setPos(x, y-z);
		super.act(dt);
	}
}
