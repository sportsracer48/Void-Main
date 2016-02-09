package util;

public class BoundingBox implements BoundingInterface
{
	private static final long serialVersionUID = -2037114124102061666L;
	
	protected boolean westBound,eastBound,topBound,bottomBound,northBound,southBound;
	protected float minX,minY,minZ,maxX,maxY,maxZ;
	
	public BoundingBox(float minX, float maxX, float minY, float maxY, float minZ, float maxZ)
	{
		setBoundsX(minX,maxX);
		setBoundsY(minY,maxY);
		setBoundsZ(minZ,maxZ);
	}
	
	public void setBoundsX(float minX, float maxX)
	{
		this.minX = minX;
		this.westBound = !Float.isNaN(minX);
		this.maxX = maxX;
		this.eastBound = !Float.isNaN(maxX);
	}
	
	public void setBoundsY(float minY, float maxY)
	{
		this.minY = minY;
		this.northBound = !Float.isNaN(minY);
		this.maxY = maxY;
		this.southBound = !Float.isNaN(maxY);
	}
	
	public void setBoundsZ(float minZ, float maxZ)
	{
		this.minZ = minZ;
		this.bottomBound = !Float.isNaN(minZ);
		this.maxZ = maxZ;
		this.topBound = !Float.isNaN(maxZ);
	}
	
	public boolean inBoundsX(float x)
	{
		return (!westBound || x>=minX) && (!eastBound || x<=maxX);
	}
	public boolean inBoundsY(float y)
	{
		return (!northBound || y>=minY) && (!southBound || y<=maxY);
	}
	public boolean inBoundsZ(float z)
	{
		return (!bottomBound || z>=minZ) && (!topBound || z<=maxZ);
	}
	public boolean inBounds(float x, float y, float z)
	{
		return inBoundsX(x) && inBoundsY(y) && inBoundsZ(z);
	}
	
	public boolean onBoundX(float x)
	{
		return x == minX || x == maxX;
	}
	public boolean onBoundY(float y)
	{
		return y == minY || y == maxY;
	}
	public boolean onBoundZ(float z)
	{
		return z == minZ || z == maxZ;
	}
	public boolean onBound(float x, float y, float z)
	{
		return onBoundX(x) || onBoundY(y) || onBoundZ(z);
	}
	
	public boolean leftBounds(float x0, float y0, float z0, float x1, float y1,float z1)
	{
		return inBounds(x0,y0,z0) && !inBounds(x1,y1,z1) ;
	}

	public float[] constrain(float x0, float y0, float z0, float x1, float y1,float z1)
	{
		return constrain(x1,y1,z1);
	}
	
	public float constrainX(float x)
	{
		if(x<minX)
		{
			x = minX;
		}
		if(x>maxX)
		{
			x = maxX;
		}
		return x;
	}
	
	public float constrainY(float y)
	{
		if(y<minY)
		{
			y = minY;
		}
		if(y>maxY)
		{
			y = maxY;
		}
		return y;
	}
	
	public float constrainZ(float z)
	{
		if(z<minZ)
		{
			z = minZ;
		}
		if(z>maxZ)
		{
			z = maxZ;
		}
		return z;
	}
	public float[] constrain(float x, float y, float z)
	{
		return new float[]{constrainX(x),constrainY(y),constrainZ(z)};
	}
}
