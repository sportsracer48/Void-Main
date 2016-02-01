package util;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import graphics.Sprite;
import graphics.entity.particles.Particle;
import graphics.entity.particles.ParticleSystem;

public class DestructionParticle
{
	public static void spawnParticlesFor(Sprite s, Sprite[] sprites,ParticleSystem system, float vBiasX, float vBiasY)
	{
		BufferedImage im = s.image;
		WritableRaster raster = im.getRaster();
		int imWidth = s.imWidth;
		int imHeight = s.imHeight;
		
		for(int x = 0; x<imWidth; x++)
		{
			for(int y = 0; y<imHeight; y++)
			{
				if(raster.getSample(x, y, 3)==255 && Math.random()>.9)
				{
					Particle toReturn = new Particle(x,(float) ((16-Math.random()*imHeight)),y,sprites[(int)Math.random()*sprites.length],(int)(1000*Math.random()),system)
					{
						float dx = (float) (Math.random()-.5)*.05f+vBiasX;
						float dy = (float) (Math.random()-.5)*.05f+vBiasY;
						float dz = (float) (Math.random())*.025f;
						float ay = -.001f;
						float af = -.01f;
						public void update(int dt, int lifeTime, float x,float y, float z)
						{
							BoundingInterface bounds = getBounds();
							if(bounds.onBoundZ(z))
							{
								float xFriction = af*Math.signum(dx)*dt;
								float yFriction = af*Math.signum(dy)*dt;
								if(Math.abs(xFriction)<Math.abs(dx))
								{
									dx += af*Math.signum(dx)*dt;
								}
								else
								{
									dx = 0;
								}
								if(Math.abs(yFriction)<Math.abs(dy))
								{
									dy += af*Math.signum(dy)*dt;
								}
								else
								{
									dy = 0;
								}
							}
							setX(x+dx*dt);
							setY(y+dy*dt);
							dz += ay*dt;
							setZ(z+dz*dt);
						}
					};
					toReturn.setScale((float)Math.random());
					toReturn.setColor(new Color(im.getRGB(x, y)));
					system.addParticle(toReturn);
				}
			}
		}
	}
}
