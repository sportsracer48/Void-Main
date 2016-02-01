package state.viewport;

import graphics.entity.Entity;
import graphics.entity.FluidEntity;

public class Unit
{
	public static final int NORTH = 2;
	public static final int EAST = 0;
	public static final int SOUTH = 3;
	public static final int WEST = 1;
	
	FluidEntity base;
	private Tile tile;
	Tile goal;
	UnitSprites sprites;
	int direction = NORTH;
	boolean moving = false;
	float xOffset;
	float yOffset;
	
	public Unit(UnitSprites sprites)
	{
		this.base = new FluidEntity(0,0,0);
		this.sprites = sprites;
		base.setTo(getEntity());
	}
	
	public int getX()
	{
		return tile.x;
	}
	public int getY()
	{
		return tile.y;
	}
	
	public FluidEntity getBaseEntity()
	{
		return base;
	}
	public void act(int dt)
	{
		if(moving)
		{
			float speed = .5f;
			float dx = (goal.x - tile.x)*speed;
			float dy = (goal.y - tile.y)*speed;
			if(dx!=0 && dy!= 0)
			{
				System.err.println("ILLEGAL MOTION");
				throw new RuntimeException();
			}
			if(Math.abs(goal.x-(tile.x+xOffset/16))<=Math.abs(dx/16) && Math.abs(goal.y-(tile.y+yOffset/16))<=Math.abs(dy/16))
			{
				setTile(goal);
				xOffset = 0;
				yOffset = 0;
				moving = false;
				goal = null;
			}
			else
			{
				xOffset += dx;
				yOffset += dy;
			}
		}
		if(base != null)
		{
			base.setPos(xOffset, yOffset);
			base.setTo(getEntity());
			base.act(dt);
		}
	}
	
	public int getDirection()
	{
		return direction;
	}
	public void setDirection(int direction)
	{
		this.direction = direction;
	}
	
	public Entity getEntity()
	{
		return sprites.getEntity(direction, moving);
	}
	
	public void tick(Tile[][] map)
	{
		if(moving)
		{
			int dx=0,dy=0;
			switch(direction)
			{
			case NORTH:
				dy = -1;
				break;
			case SOUTH:
				dy = 1;
				break;
			case EAST:
				dx = 1;
				break;
			case WEST:
				dx = -1;
				break;
			}
			try
			{
				Tile goal = map[getX()+dx][getY()+dy];
				if(!goal.isWall())
				{
					this.goal = goal;
				}
				else
				{
					moving = false;
					goal = null;
				}
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				moving = false;
				goal = null;
			}
		}
		if(goal == tile)
		{
			goal = null;
		}
	}
	
	public boolean isMoving()
	{
		return moving;
	}
	public void setMoving(boolean moving)
	{
		this.moving = moving;
	}

	public void setTile(Tile tile)
	{
		if(tile == this.tile)
		{
			return;
		}
		Tile oldTile = this.tile;
		this.tile = tile;
		if(oldTile != null)
		{
			oldTile.setUnit(null);
		}
		if(tile != null)
		{
			tile.setUnit(this);
		}
		if(this.tile != null && this.tile.getUnit() != this)
		{
			throw new RuntimeException("TILE AND UNIT ARE OUT OF SYNC");
		}
	}

	public Tile getTile()
	{
		return tile;
	}
}
