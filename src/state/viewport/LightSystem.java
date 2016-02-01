package state.viewport;

import java.util.HashSet;

public class LightSystem
{
	HashSet<LightSource> lights = new HashSet<>();
	float ambient;
	public LightSystem(float ambient)
	{
		this.ambient = ambient;
	}
	public void addLight(LightSource source)
	{
		lights.add(source);
	}
	public float getIntensity(float x, float y)
	{
		float intensity = ambient;
		for(LightSource source:lights)
		{
			intensity += source.getIntensity(x, y);
		}
		return intensity;
	}
	public void act(int dt)
	{
		for(LightSource source:lights)
		{
			source.act(dt);
		}
	}
}
