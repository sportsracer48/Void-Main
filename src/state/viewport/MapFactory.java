package state.viewport;

import java.util.List;

import util.CompoundBoundingBox;
import graphics.Sprite;

public class MapFactory
{
	List<Sprite> wallTops, wallFronts, floors;
	public MapFactory(List<Sprite> wallTops, List<Sprite> wallFronts, List<Sprite> floors)
	{
		this.wallTops = wallTops;
		this.wallFronts = wallFronts;
		this.floors = floors;
	}
	
	public void assignFloor(Tile t)
	{
		t.setFloorEntity(selectRandom(floors));
	}
	
	public void assignWall(Tile t)
	{
		t.setWallEntity(selectRandom(wallTops), selectRandom(wallFronts));
	}
	
	public static <T> T selectRandom(List<T> list)
	{
		return list.get((int)(Math.random()*list.size()));
	}
	
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
}
