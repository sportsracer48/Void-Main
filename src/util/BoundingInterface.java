package util;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public interface BoundingInterface
{
	public boolean inBounds(float x, float y, float z);
	public boolean onBound(float x, float y, float z);
	public boolean leftBounds(float x0, float y0, float z0, float x1, float y1, float z1);
	public float[] constrain(float x0, float y0, float z0, float x1, float y1, float z1);
	public float[] constrain(float x, float y, float z);
	
	
	public default boolean inBoundsX(float x)
	{
		throw new NotImplementedException();
	}
	public default boolean inBoundsY(float y)
	{
		throw new NotImplementedException();
	}
	public default boolean inBoundsZ(float z)
	{
		throw new NotImplementedException();
	}
	
	public default boolean onBoundX(float x)
	{
		throw new NotImplementedException();
	}
	public default boolean onBoundY(float y)
	{
		throw new NotImplementedException();
	}
	public default boolean onBoundZ(float z)
	{
		throw new NotImplementedException();
	}
	
	public default float constrainX(float x)
	{
		throw new NotImplementedException();
	}
	
	public default float constrainY(float y)
	{
		throw new NotImplementedException();
	}
	
	public default float constrainZ(float z)
	{
		throw new NotImplementedException();
	}
}
