package state.viewport;

import graphics.Context;
import graphics.entity.Entity;

public class MapEntity extends Entity
{
	Tile[][] map;
	float scale = 8;
	int tileSize = 16;
	
	public MapEntity(float x, float y, float z, Tile[][] map)
	{
		super(x, y, z, null);
		this.map = map;
	}
	public void renderChildren(Context c)
	{
		super.renderChildren(c);
		for(int y = 0; y<map[0].length; y++)
		{
			for(int x = 0; x<map.length; x++)
			{
				if(map[x][y]==null)
				{
					continue;
				}
				map[x][y].renderTile(c, x, y, scale, tileSize);
			}
			for(int x = 0; x<map.length; x++)
			{
				if(map[x][y]==null)
				{
					continue;
				}
				map[x][y].returnUnit(c, x, y, scale, tileSize);
			}
		}
	}
	public void act(int dt)
	{
		for(Tile[] col:map)
		{
			for(Tile t:col)
			{
				t.act(dt);
			}
		}
		super.act(dt);
	}
}
