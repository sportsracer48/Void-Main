package game.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import util.BoundingBox;
import util.BoundingInterface;
import util.CompoundBoundingBox;
import util.DestructionParticle;
import graphics.Context;
import graphics.Sprite;
import graphics.entity.Entity;
import graphics.entity.particles.Particle;
import graphics.entity.particles.ParticleSystem;
import graphics.registry.UtilSprites;

public class Tile implements Serializable
{
	private static final long serialVersionUID = 4388163361303692076L;
	
	boolean isWall;
	boolean isFloor;
	public final int x,y;
	private Unit unit;
	Map parent;
	
	transient Entity floorEntity;
	transient Entity wallEntity;
	transient Entity parentEntity;
	transient ParticleSystem tileParticles;
	
	public final int spriteSize;
	public final int wallHeight;
	BoundingInterface tileBounds;
	boolean explored = false;
	
	public Tile(int x, int y, int spriteSize, int wallHeight, Map parent)
	{
		this.x=x;
		this.y=y;
		this.spriteSize = spriteSize;
		this.wallHeight = wallHeight;
		this.parent = parent;
		initParticleSystem();
		parentEntity = new Entity(x*spriteSize, y*spriteSize,0,null);
	}
	
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
	{
		stream.defaultReadObject();
		initParticleSystem();
		parentEntity = new Entity(x*spriteSize, y*spriteSize,0,null);
	}
	private void initParticleSystem()
	{
		this.tileParticles = new ParticleSystem(0)
		{
			public Particle createParticle()
			{
				return null;
			}
			public void update(int dt)
			{
			}
		};
	}
	
	public ParticleSystem getParticles()
	{
		return tileParticles;
	}
	
	public void initNeighbors(Tile[][] map, CompoundBoundingBox levelBox)
	{
		boolean westWall = x == 0 || map[x-1][y].isWall();
		boolean eastWall = x == map.length-1 || map[x+1][y].isWall();
		boolean northWall = y == 0 || map[x][y-1].isWall();
		boolean southWall = y == map[x].length-1 || map[x][y+1].isWall();
		this.tileBounds = new BoundingBox(
				westWall?0:Float.NaN,
				eastWall?16:Float.NaN,
				northWall?0:Float.NaN,
				southWall?16:Float.NaN,
				0,
				Float.NaN
				);
		
		tileParticles.setBounds(new CompoundBoundingBox(levelBox,x*16,y*16));
	}
	
	public void setExplored(boolean explored)
	{
		this.explored = explored;
	}
	public boolean isExplored()
	{
		return explored;
	}
	
	public void setUnit(Unit unit)
	{
		if(unit == this.unit)
		{
			return;
		}
		Unit oldUnit = this.unit;
		this.unit = unit;
		if(oldUnit != null)
		{
			oldUnit.setTile(null);
		}
		if(unit != null)
		{
			unit.setTile(this);
		}
		if(this.unit != null && this.unit.getTile() != this)
		{
			throw new RuntimeException("TILE AND UNIT ARE OUT OF SYNC");
		}
	}
	
	public void setFloorEntity(Sprite floor)
	{
		setFloorEntity(new Entity(0,0,0,floor));
	}
	
	public void setFloorEntity(Entity e)
	{
		this.floorEntity = e;
		this.isWall = false;
		this.isFloor = true;
		this.wallEntity = null;
	}
	
	public void makeFloor()
	{
		parent.getConfig().assignFloor(this);
	}
	
	public void makeWall()
	{
		parent.getConfig().assignWall(this);
	}
	
	public void setWallEntity(Sprite top, Sprite front)
	{
		Entity wallEntity = new Entity(0,0,0,null);
		wallEntity.addChild(new Entity(0,-wallHeight,0,top));
		wallEntity.addChild(new Entity(0,spriteSize-wallHeight,0,front));
		setWallEntity(wallEntity);
	}
	
	public void setWallEntity(Entity e)
	{
		this.wallEntity = e;
		this.isWall = true;
		this.isFloor = false;
		this.floorEntity = null;
	}
	
	public boolean isWall()
	{
		return isWall;
	}
	public boolean isFloor()
	{
		return isFloor;
	}
	
	public void renderFloor(Context c)
	{
		if(isWall() || floorEntity == null)
		{
			return;
		}
		else
		{
			parentEntity.setupContext(c);
			floorEntity.render(c);
			parentEntity.resetContext(c);
		}
	}
	
	public void renderParticles(Context c)
	{
		parentEntity.setupContext(c);
		tileParticles.render(c);
		parentEntity.resetContext(c);
	}
	
	public void renderWall(Context c)
	{
		if(isWall())
		{
			parentEntity.setupContext(c);
			wallEntity.render(c);
			parentEntity.resetContext(c);
		}
	}
	public void renderUnit(Context c)
	{
		if(unit!=null)
		{
			parentEntity.setupContext(c);
			unit.getBaseEntity().render(c);
			parentEntity.resetContext(c);
		}
	}
	
	public void destroyUnit(float vBiasX, float vBiasY)
	{
		DestructionParticle.spawnParticlesFor(unit.getEntity().getBase(), new Sprite[]{UtilSprites.white}, tileParticles, vBiasX, vBiasY);
		setUnit(null);
	}
	
	public void act(int dt)
	{
		if(wallEntity != null)
		{
			wallEntity.act(dt);
		}
		if(floorEntity != null)
		{
			floorEntity.act(dt);
		}
		if(unit != null)
		{
			unit.act(dt);
		}
		tileParticles.act(dt);
	}

	public Unit getUnit()
	{
		return unit;
	}
}
