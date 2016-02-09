package game.map;

import game.session.levelgen.MapConfig;
import game.session.levelgen.MapTypes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Map implements Serializable
{
	private static final long serialVersionUID = 8458158058292438282L;
	
	public final Tile[][] tiles;
	public final int startX, startY;
	public final int configId;
	private transient MapConfig config;
	
	public Map(Tile[][] tiles, int startX, int startY, MapConfig config)
	{
		this.tiles = tiles;
		this.startX = startX;
		this.startY = startY;
		this.config = config;
		this.configId = config.id;
	}
	
	public void makeEntities()
	{
		for(Tile[] col:tiles)
		{
			for(Tile t:col)
			{
				if(t.isWall())
				{
					t.makeWall();
				}
				if(t.isFloor())
				{
					t.makeFloor();
				}
			}
		}
	}
	

	public void pinUpdateAll()
	{
		for(Tile[] col:tiles)
		{
			for(Tile t:col)
			{
				if(t.getUnit() != null && t.getUnit().getController() != null)
				{
					t.getUnit().getController().update();
				}
			}
		}
	}
	public void radioUpdateAll()
	{
		for(Tile[] col:tiles)
		{
			for(Tile t:col)
			{
				if(t.getUnit() != null && t.getUnit().getController() != null)
				{
					t.getUnit().getController().radioUpdate();
				}
			}
		}
	}
	
	public Unit getUnit(int type)
	{
		for(Tile[] col:tiles)
		{
			for(Tile t:col)
			{
				if(t.getUnit() != null && t.getUnit().sprites.id == type)
				{
					return t.getUnit();
				}
			}
		}
		return null;
	}
	
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
	{
		stream.defaultReadObject();
		this.config = MapTypes.fromId(configId);
	}
	
	public MapConfig getConfig()
	{
		return config;
	}
	
	public Tile getTile(int x, int y)
	{
		return tiles[x][y];
	}
	public void setUnit(int x, int y, Unit unit)
	{
		tiles[x][y].setUnit(unit);
	}
	public void setUnitAtStart(Unit unit)
	{
		setUnit(startX,startY,unit);
	}
	public int getWidth()
	{
		return tiles.length;
	}
	public int getHeight()
	{
		return tiles[0].length;
	}
}
	
