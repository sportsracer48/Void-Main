package state.workbench.game;

import state.workbench.ItemAcceptor;
import state.workbench.ItemManipulator;
import util.GridBuilder;
import util.GridBuilder.Coord;
import util.Color;
import game.item.Item;
import game.item.ItemType;
import graphics.Sprite;
import graphics.entity.Entity;
import graphics.entity.FluidEntity;

public class ChassisGrid extends Entity
{
	Item[][] contents;
	Entity[][] entities;
	ItemAcceptor[][] slots;
	int width, height;
	GridBuilder grid;
	FluidEntity preview;
	
	public ChassisGrid(float x, float y, float z, Sprite base,ItemManipulator manip)
	{
		this(10,10,21,38,55,36,x,y,z,base, manip);
	}
	
	private ChassisGrid(int width, int height, float offsetX, float offsetY, float xStep, float yStep,float x, float y, float z, Sprite base, ItemManipulator manip)
	{
		super(x,y,z,base);
		grid = new GridBuilder(offsetX,offsetY,xStep,yStep,width,height);
		contents = new Item[width][height];
		entities = new Entity[width][height];
		slots = new ItemAcceptor[width][height];
		preview = new FluidEntity(0,0,height+4);
		preview.setVisible(false);
		addChild(preview);
		this.width = width;
		this.height = height;
		
		grid.forEachWithIndicies((x2,y2,col,row)->
		{
			ItemAcceptor a = new ItemAcceptor(x2,y2,20,xStep,yStep,manip)
			{
				public boolean canAccept(Item i)
				{
					return canPlace(i,col,row);
				}

				public void accept(Item i)
				{
					place(i, col, row);
				}

				public boolean displayedItem(Item i)
				{
					return true;
				}

				public void preview(Item i) 
				{
					if(i == null)
					{
						preview.setVisible(false);
						return;
					}
					ItemType type = i.getType();
					preview.setSpriteAndSize(i.getWorldSprite());
					preview.setPos(x2-type.getOffsetX(), y2-type.getOffsetY());
					preview.setVisible(true);
					if(canAccept(i))
					{
						preview.setColor(new Color(1,1,1,.4f));
					}
					else
					{
						preview.setColor(new Color(2,.5f,.5f,.4f));
					}
				}
			};
			slots[col][row] = a;
			a.getArea().addOnClick((i,j)->{
				Item item = contents[col][row];
				if(item == null)
				{
					return;
				}
				Coord c = getCoords(item);
				remove(contents[col][row]);
				manip.grabItem(item, 0, 0, getSlot(c));
			});
			addChild(a);
		});
	}
	
	public boolean canPlace(Item i, int x, int y)
	{
		if(!i.existsInWorld())
		{
			return false;
		}
		ItemType type = i.getType();
		for(int col = 0; col<type.getWorkbenchWidth(); col++)
		{
			for(int row = 0; row<type.getWorkbenchHeight(); row++)
			{
				if(x+col>=width || y+row>= height || contents[x+col][y+row] != null)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public Item remove(Item i)
	{
		grid.forEachWithIndicies((a,b,x,y)->{
			boolean found = false;
			if(contents[x][y]==i)
			{
				if(!found)
				{
					removeChild(entities[x][y]);
					found = true;
				}
				contents[x][y]=null;
			}
		});
		return i;
	}
	
	public Item remove(int x, int y)
	{
		return remove(contents[x][y]);
	}
	
	public Coord getCoords(Item i)
	{
		for(int col = 0; col<width; col++)
		{
			for(int row = 0; row<height; row++)
			{
				if(contents[col][row]==i)
				{
					return new Coord(col,row);
				}
			}
		}
		return null;
	}
	
	public ItemAcceptor getSlot(int x, int y)
	{
		return slots[x][y];
	}
	
	public ItemAcceptor getSlot(Coord c)
	{
		return slots[c.x][c.y];
	}
	
	public void place(Item i, int x, int y)
	{
		if(!i.existsInWorld())
		{
			throw new RuntimeException("Cannot pace item, does not exist physically");
		}
		ItemType type = i.getType();
		Entity newEntity = i.getWorldEntity();
		
		newEntity.setPos(grid.getX(x)-i.getType().getOffsetX(), grid.getY(y)-i.getType().getOffsetY());
		newEntity.setZ(y);
		this.addChild(newEntity);
		for(int col = 0; col<type.getWorkbenchWidth(); col++)
		{
			for(int row = 0; row<type.getWorkbenchHeight(); row++)
			{
				if(x+col>=width || y+row>= height || contents[x+col][y+row] != null)
				{
					throw new RuntimeException("Cannot pace item, something in the way");
				}
				else
				{
					contents[x+col][y+row] = i;
					entities[x+col][y+row] = newEntity;
				}
			}
		}
	}
}
