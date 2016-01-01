package graphics.registry;

import graphics.Sprite;

public class UtilSprites
{
	public static Sprite white;
	public static void init(SpriteAtlas atlas)
	{
		white = atlas.getSpriteGlobal("res/sprite/util/white.png");
	}
}
