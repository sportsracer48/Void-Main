package game.session.levelgen;

import game.map.Tile;

import java.util.List;

import util.CompoundBoundingBox;

public class MapUtil
{

	public static CompoundBoundingBox initNeighbors(Tile[][] map)
	{
		CompoundBoundingBox levelBox = new CompoundBoundingBox(16,map,t->t.isWall() || !t.isFloor());
		for(Tile[] col:map)
		{
			for(Tile t:col)
			{
				t.initNeighbors(map,levelBox);
			}
		}
		return levelBox;
	}

	public static <T> T selectRandom(List<T> list)
	{
		return list.get((int)(Math.random()*list.size()));
	}

}
