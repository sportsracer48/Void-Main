package game.session.levelgen;

import graphics.registry.SpriteAtlas;

public class MapTypes
{
	public static MapConfig surface;
	public static final int SURFACE = 0;
	
	public static MapConfig fromId(int id)
	{
		switch(id)
		{
		case SURFACE:
			return surface;
		}
		return null;
	}
	
	public static void init(SpriteAtlas sprites)
	{
		sprites.setNamespace("res/sprite/level/");
		
		surface = new MapConfig(
				SURFACE,
				sprites.getSprites("surface_wall_%d.png"),
				sprites.getSprites("surface_front_%d.png"),
				sprites.getSprites("surface_floor_%d.png"),
				sprites.getSprites("spore_particle_%d.png")
				);
		
		sprites.resetNamespace();
	}
}
