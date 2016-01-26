package state.viewport;

import java.util.List;

import graphics.Sprite;
import graphics.entity.Entity;
import graphics.registry.SpriteAtlas;

public class FloorTileType extends TileType
{
	List<Sprite> sprites;
	public FloorTileType(SpriteAtlas sprites, String prefix)
	{
		this.sprites = sprites.getSprites(prefix);
	}
	public Entity getTileEntity()
	{
		return new Entity(0,0,0,
				sprites.get(
						(int)(Math.random()*sprites.size())
						)
				);
	}
}
