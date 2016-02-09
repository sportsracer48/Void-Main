package game.session.levelgen;

import java.util.List;

import game.map.Tile;
import graphics.Sprite;

public class MapConfig
{
	List<Sprite> wallTops, wallFronts, floors,ambientParticles;
	public final int id;
	public MapConfig(int id,List<Sprite> wallTops, List<Sprite> wallFronts, List<Sprite> floors, List<Sprite> ambientParticles)
	{
		this.wallTops = wallTops;
		this.wallFronts = wallFronts;
		this.floors = floors;
		this.ambientParticles = ambientParticles;
		this.id = id;
	}
	
	public Sprite getAmbientParticle()
	{
		return MapUtil.selectRandom(ambientParticles);
	}
	
	public List<Sprite> getAmbientParticles()
	{
		return ambientParticles;
	}
	
	public void assignFloor(Tile t)
	{
		t.setFloorEntity(MapUtil.selectRandom(floors));
	}
	
	public void assignWall(Tile t)
	{
		t.setWallEntity(MapUtil.selectRandom(wallTops), MapUtil.selectRandom(wallFronts));
	}
}
