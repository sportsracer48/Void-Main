package util;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import graphics.Sprite;
import graphics.entity.Particle;
import graphics.entity.ParticleSystem;

public class DestructionParticle
{
	public static void spawnParticlesFor(Sprite s,int scale,Sprite[] sprites,ParticleSystem system)
	{
		BufferedImage im = s.image;
		WritableRaster raster = im.getRaster();
		
		for(int x = 0; x<im.getWidth(); x++)
		{
			for(int y = 0; y<im.getHeight(); y++)
			{
				if(raster.getSample(x, y, 3)==255 &&Math.random()>.9)
				{
					Particle toReturn = new Particle(x*scale,y*scale,1,sprites[(int)Math.random()*sprites.length],500,system)
					{
						float dx = (float) (Math.random()-.5)*.2f;
						float dy = (float) (Math.random()-.5)*.2f;
						float dz = (float) (Math.random())*.2f;
						float a = -.001f;
						public void update(int dt, int lifeTime, float x,float y, float z)
						{
							if(z==0)
							{
								return;
							}
							setX(x+dx*dt);
							setY(y+dy*dt);
							dz += a*dt;
							setZ(z+dz*dt);
							if(this.z<0)
							{
								this.z=0;
							}
						}
					};
					toReturn.setScale(scale);
					toReturn.setColor(new Color(im.getRGB(x, y)));
					system.addParticle(toReturn);
				}
			}
		}
	}
}
