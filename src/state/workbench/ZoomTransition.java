package state.workbench;

import static util.GLU.lerp;

public class ZoomTransition
{
	int timeToEnd = 400;
	int time = 0;
	Camera c;
	
	float goalTopY;
	float goalBottomY;
	float goalLeftX;
	float goalRightX;

	float startTopY;
	float startBottomY;
	float startLeftX;
	float startRightX;
	
	boolean running = false;
	boolean returnScaleAllowState;
	
	public ZoomTransition(Camera c, int laptopX, int laptopY, int screenWidth, int screenHeight, float laptopWidth, float laptopHeight)
	{
		this.c=c;
		
		float xScale = screenWidth/laptopWidth;
		float yScale = screenHeight/laptopHeight;
		float scale = Math.min(xScale, yScale);
		
		float resultWidth = laptopWidth*xScale/scale;
		float resultHeight = laptopHeight*yScale/scale;
		
		float xOffset = (resultWidth-laptopWidth)/2;
		float yOffset =	(resultHeight-laptopHeight)/2;
		
		goalTopY = laptopY-yOffset;
		goalLeftX = laptopX-xOffset;
		goalBottomY = goalTopY+resultHeight;
		goalRightX = goalLeftX+resultWidth;
		
	}
	
	public boolean isDone()
	{
		return time>=timeToEnd;
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	public void act(int dt)
	{
		if(running)
		{
			time += dt;
			if(time>timeToEnd)
			{
				time = timeToEnd;
			}
			float t = (float)time/timeToEnd;
			c.setBounds(
					lerp(startLeftX,goalLeftX,t), 
					lerp(startRightX,goalRightX,t),
					lerp(startTopY,goalTopY,t),
					lerp(startBottomY,goalBottomY,t)
					);
		}
	}

	public void reset()
	{
		running = false;
		time = 0;
		c.setBounds(startLeftX, startRightX, startTopY, startBottomY);
		c.setAllowAnyScale(returnScaleAllowState);
	}

	public void start()
	{
		startTopY = c.getTopY();
		startBottomY = c.getBottomY();
		startLeftX = c.getLeftX();
		startRightX = c.getRightX();
		returnScaleAllowState = c.doesAllowAnyScale();
		c.setAllowAnyScale(true);
		running = true;
	}
}
