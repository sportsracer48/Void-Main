package state.viewport;

public class LightSource
{
	Unit attatched;
	float x,y,intensity;
	float flickerX,flickerY,flickerIntensity;
	float flicker = .4f;
	int timeToFlicker = 100;
	int time = 0;
	public LightSource(Unit attatched, float intensity)
	{
		this.attatched = attatched;
		this.intensity = intensity;
	}
	public LightSource(float x, float y, float intensity)
	{
		this.x=flickerX=x;
		this.y=flickerY=y;
		this.intensity=flickerIntensity=intensity;
	}
	
	public float getX()
	{
		if(attatched == null)
		{
			return x;
		}
		return attatched.getX()+attatched.xOffset/16f;
	}
	public float getY()
	{
		if(attatched == null)
		{
			return y;
		}
		return attatched.getY()+attatched.yOffset/16f;
	}
	
	public void act(int dt)
	{
		time += dt;
		if(time>=timeToFlicker)
		{
			flickerX = (float) ((Math.random()-.5)*flicker);
			flickerY = (float) ((Math.random()-.5)*flicker);
			flickerIntensity = (float) ((Math.random()-.5)*flicker/10f);
			time = 0;
			timeToFlicker = (int) (200*Math.random());
		}
	}
	public float getIntensity(float x0, float y0)
	{
		float dx = x0-(getX()+flickerX);
		float dy = y0-(getY()+flickerY);
		float preIntensity = (intensity+flickerIntensity)/(dx*dx+dy*dy);
		return Math.min(preIntensity, 1);
	}
}
