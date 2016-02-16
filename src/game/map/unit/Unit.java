package game.map.unit;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import state.viewport.UnitSprites;
import game.item.Inventory;
import game.item.Item;
import game.map.Map;
import game.map.Tile;
import graphics.entity.Entity;
import graphics.entity.FluidEntity;

public class Unit implements Serializable
{
	private static final long serialVersionUID = -3194365822288291865L;
	
	public static final int NORTH = 2;
	public static final int EAST = 0;
	public static final int SOUTH = 3;
	public static final int WEST = 1;
	
	int unitTypeId;
	
	transient FluidEntity base;
	transient UnitSprites sprites;
	transient UnitType type;
	
	
	private Tile tile;
	private Tile goal;
	int direction = NORTH;
	boolean moving = false;
	private float xOffset;
	private float yOffset;
	
	int torque;
	int force;
	
	UnitController controller = null;
	Inventory externalPartMounting;
	Item item;
	
	public Unit(UnitType type)
	{
		this.base = new FluidEntity(0,0,0);
		this.type = type;
		this.sprites = type.createSprites();
		this.unitTypeId = sprites.id;
		base.setTo(getEntity());
		
		if(type.isRobot())
		{
			externalPartMounting = new Inventory(24);
		}
	}
	
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
	{
		stream.defaultReadObject();
		base = new FluidEntity(0,0,0);
		type = UnitTypes.fromId(unitTypeId);
		sprites = type.createSprites();
	}
	
	public Inventory getExternalPartMounting()
	{
		return externalPartMounting;
	}
	
	public void setItem(Item item)
	{
		if(item==null)
		{
			this.item = item;
		}
	}
	
	public void setController(UnitController controller)
	{
		this.controller = controller;
	}
	public UnitController getController()
	{
		return controller;
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
	
	public UnitSprites getSprites()
	{
		return sprites;
	}
	
	public void addForce(int force)
	{
		this.force += force;
	}
	public void addTorque(int torque)
	{
		this.torque += torque;
	}
	
	public void act(int dt)
	{
		if(moving)
		{
			float speed = .1f;
			float dx = (goal.x - tile.x)*speed;
			float dy = (goal.y - tile.y)*speed;
			if(dx!=0 && dy!= 0)
			{
				System.err.println("ILLEGAL MOTION");
				throw new RuntimeException();
			}
			if(Math.abs(xOffset)>=16 || Math.abs(yOffset)>=16)
			{
				setTile(goal);
				xOffset = 0;
				yOffset = 0;
				moving = false;
				goal = null;
			}
			else
			{
				xOffset += dx*dt;
				yOffset += dy*dt;
			}
		}
		if(base != null)
		{
			base.setPos(xOffset, yOffset);
			base.setTo(getEntity());
			if(moving)
			{
				base.act(dt);
			}
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
	
	public void tick(Map map)
	{
		if(moving)
		{
			torque = force = 0;
			return;
		}
		int turnDirection = (int) Math.signum(torque);
		
		if(force>0 && turnDirection == 0)
		{
			moving = true;
		}
		torque = force = 0;
		
		if(turnDirection!=0)
		{
			if(turnDirection==1)
			{
				switch(direction)
				{
				case NORTH:
					direction = WEST;
					break;
				case EAST:
					direction = NORTH;
					break;
				case SOUTH:
					direction = EAST;
					break;
				case WEST:
					direction = SOUTH;
					break;
				}
			}
			else
			{
				switch(direction)
				{
				case NORTH:
					direction = EAST;
					break;
				case EAST:
					direction = SOUTH;
					break;
				case SOUTH:
					direction = WEST;
					break;
				case WEST:
					direction = NORTH;
					break;
				}
			}
			moving = false;
		}
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
				Tile goal = map.getTile(getX()+dx,getY()+dy);
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

	public float getxOffset()
	{
		return xOffset;
	}

	public void setxOffset(float xOffset)
	{
		this.xOffset = xOffset;
	}

	public float getyOffset()
	{
		return yOffset;
	}

	public void setyOffset(float yOffset)
	{
		this.yOffset = yOffset;
	}

	public Tile getGoal()
	{
		return goal;
	}

	public void setGoal(Tile goal)
	{
		this.goal = goal;
	}
}
