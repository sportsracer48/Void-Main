package state.workbench;

import math.Matrix;

public class Camera
{
	float x, y, scale, screenWidth, screenHeight;
	public Camera(float x, float y, float screenWidth, float screenHeight, float scale)
	{
		this.x=x;
		this.y=y;
		this.scale = scale;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}
	
	public float getLeftX()
	{
		return (x*scale-screenWidth/2)/scale;
	}
	public float getTopY()
	{
		return (y*scale-screenHeight/2)/scale;
	}
	public float getRightX()
	{
		return (x*scale+screenWidth/2)/scale;
	}
	public float getBottomY()
	{
		return (y*scale+screenHeight/2)/scale;
	}
	
	public void setLeftX(float x)
	{
		this.x = x+screenWidth/(2*scale);
	}
	public void setRightX(float x)
	{
		this.x = x-screenWidth/(2*scale);
	}
	public void setTopY(float y)
	{
		this.y = y+screenHeight/(2*scale);
	}
	public void setBottomY(float y)
	{
		this.y = y-screenHeight/(2*scale);
	}
	
	public float getScreenHeight()
	{
		return screenHeight/scale;
	}
	public float getScreenWidth()
	{
		return screenWidth/scale;
	}
	
	
	public Matrix getView()
	{
		if(scale<1)
		{
			scale = 1;
		}
		if(scale>32)
		{
			scale = 32;
		}
		return new Matrix(new float[]{
				Math.round(scale),0,0,Math.round(-x*scale)+screenWidth/2,
				0,Math.round(scale),0,Math.round(-y*scale)+screenHeight/2,
				0,0,Math.round(scale),0,
				0,0,0,1
				},4);
	}
	public Matrix getInverseView()
	{
		float invScale = 1f/scale;
		return new Matrix(new float[]{
				invScale,0,0,x-screenWidth/(2*scale),
				0,invScale,0,y-screenHeight/(2*scale),
				0,0,invScale,0,
				0,0,0,1
				},4);
	}
}
