package graphics.entity.particles;

import graphics.Context;

import java.util.HashSet;

public class ParticleBins
{
	float scale;
	HashSet<Particle>[][] bins;
	@SuppressWarnings("unchecked")
	public ParticleBins(float scale, int width, int height)
	{
		this.scale = scale;
		this.bins = new HashSet[width][height];
		for(int x = 0; x<width;x++)
		{
			for(int y = 0; y<height; y++)
			{
				bins[x][y] = new HashSet<>();
			}
		}
	}
	public int getCoord(float coord)
	{
		return (int)(coord/scale);
	}
	public boolean moved(float x0, float y0, float x1, float y1)
	{
		return getCoord(x0) != getCoord(x1) || getCoord(y0) != getCoord(y1);
	}
	public void move(Particle p,float x0, float y0, float x1, float y1)
	{
		try
		{
			bins[getCoord(x0)][getCoord(y0)].remove(p);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			//do nothing
		}
		try
		{
			bins[getCoord(x1)][getCoord(y1)].add(p);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			//do nothing
		}
	}
	public void renderBin(Context c, int x, int y)
	{
		for(Particle p:bins[x][y])
		{
			p.render(c);
		}
	}
	public void add(Particle p, float x, float y)
	{
		try
		{
			bins[getCoord(x)][getCoord(y)].add(p);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			//do nothing
		}
	}
}
