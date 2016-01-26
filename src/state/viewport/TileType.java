package state.viewport;

import graphics.entity.Entity;

public abstract class TileType
{
	public Tile makeTile()
	{
		return new Tile(this);
	}
	public abstract Entity getTileEntity();
}
