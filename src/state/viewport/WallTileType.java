package state.viewport;

import java.util.List;

import graphics.Sprite;
import graphics.entity.Entity;
import graphics.registry.SpriteAtlas;

public class WallTileType extends TileType
{
	List<Sprite> topSprites;
	List<Sprite> frontSprites;
	int wallHeight = 9;
	int spriteHeight = 16;
	float scale = 8;
	public WallTileType(SpriteAtlas sprites, String topPrefix, String frontPrefix)
	{
		topSprites = sprites.getSprites(topPrefix);
		frontSprites = sprites.getSprites(frontPrefix);
	}
	public Entity getTileEntity()
	{
		Entity toReturn = new Entity(0,-wallHeight*scale,0,
				topSprites.get(
						(int)(Math.random()*topSprites.size())
						)
				);
		toReturn.addChild(
				new Entity(0,spriteHeight,0,
						frontSprites.get(
						(int)(Math.random()*frontSprites.size())
						)
				)
				);
		toReturn.updateChildren();
		return toReturn;
	}
}
