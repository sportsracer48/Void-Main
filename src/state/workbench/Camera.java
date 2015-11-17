package state.workbench;

import math.Matrix;

public class Camera
{
	float x, y, scale;
	public Camera(float x, float y, float scale)
	{
		this.x=x;
		this.y=y;
		this.scale = scale;
	}
	
	public Matrix getView()
	{
		if(scale<1)
		{
			scale = 1;
		}
		if(scale>16)
		{
			scale = 16;
		}
		return new Matrix(new float[]{
				Math.round(scale),0,0,Math.round(-x*scale),
				0,Math.round(scale),0,Math.round(-y*scale),
				0,0,Math.round(scale),0,
				0,0,0,1
				},4);
	}
	public Matrix getInverseView()
	{
		float invScale = 1f/scale;
		return new Matrix(new float[]{
				invScale,0,0,x,
				0,invScale,0,y,
				0,0,invScale,0,
				0,0,0,1
				},4);
	}
}
