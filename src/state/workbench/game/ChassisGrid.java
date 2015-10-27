package state.workbench.game;

import state.ModeManager;
import state.workbench.GrabBound;
import state.workbench.ItemAcceptor;
import state.workbench.ItemManipulator;
import state.workbench.WiringMode;
import util.Grid;
import util.Grid.Coord;
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
	FluidEntity preview;
	FluidEntity returnPreview;
	Coord previewCoord;
	GrabBound<Coord> grabbedSquare = new GrabBound<>();
	GrabBound<Boolean> showReturnPreview = new GrabBound<>();
	int width, height;
	Grid grid;
	ItemManipulator manip;
	ModeManager manager;
	WiringMode wiring;
	
	
	public ChassisGrid(float x, float y, float z, Sprite base,ItemManipulator manip,ModeManager manager,WiringMode wiring)
	{
		this(10,10,21,38,55,36,x,y,z,base, manip,manager,wiring);
	}
	
	private ChassisGrid(int width, int height, float offsetX, float offsetY, float xStep, float yStep, float x, float y, float z, Sprite base, ItemManipulator manip,ModeManager manager,WiringMode wiring)
	{
		super(x,y,z,base);
		
		//bind grab bound objects. These get reset to their default values on a drop event.
		grabbedSquare.setDefault(new Coord(0,0));
		showReturnPreview.setDefault(false);
		manip.addGrabBound(grabbedSquare);
		manip.addGrabBound(showReturnPreview);
		
		//a grid to be iterated over
		grid = new Grid(offsetX,offsetY,xStep,yStep,width,height);
		
		//initialize arrays
		contents = new Item[width][height];
		entities = new Entity[width][height];
		slots = new ItemAcceptor[width][height];
		
		preview = new FluidEntity(0,0,height+4);
		preview.setVisible(false);
		
		returnPreview = new FluidEntity(0,0,height+3);
		returnPreview.setVisible(false);
		
		addChild(preview);
		addChild(returnPreview);
		
		this.width = width;
		this.height = height;
		this.manip = manip;
		this.manager = manager;
		this.wiring = wiring;
		
		grid.forEachWithIndicies((x2,y2,col,row)->
		{
			ItemAcceptor a = new ItemAcceptor(x2,y2,20,xStep,yStep,manip)
			{
				public boolean canAccept(Item i)
				{
					Coord grabbed = grabbedSquare.getValue();
					return canPlace(i,col-grabbed.x,row-grabbed.y);
				}

				public void accept(Item i)
				{
					Coord grabbed = grabbedSquare.getValue();
					place(i, col-grabbed.x, row-grabbed.y);
				}

				public boolean displayedItem(Item i)
				{
					return true;
				}

				public void preview(Item i) 
				{
					Coord grabbed = grabbedSquare.getValue();
					if(i != null)
					{
						enablePreview(i,col-grabbed.x,row-grabbed.y);
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
				if(manager.getMode()!=wiring)
				{
					Coord c = getCoords(item);
					Coord squareGrabbed = new Coord(col-c.x,row-c.y);
					remove(item);
					manip.grabItem(item, 16, 16, getSlot(c));
					enableReturnPreview(item,col-squareGrabbed.x,row-squareGrabbed.y);
					showReturnPreview.setValue(true);
					grabbedSquare.setValue(squareGrabbed);
				}
				else
				{
					wiring.showSelector(item);
				}
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
		if(x<0 || y<0)
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
	
	public void enableReturnPreview(Item i, int x, int y)
	{
		ItemType type = i.getType();
		returnPreview.setSpriteAndSize(i.getWorldSprite());
		returnPreview.setPos(grid.getX(x)-type.getOffsetX(), grid.getY(y)-type.getOffsetY());
		returnPreview.setZ(y);
		returnPreview.setVisible(true);
		returnPreview.setColor(new Color(1f,1f,1f,.2f));
	}
	
	public void enablePreview(Item i,int x, int y)
	{
		previewCoord = new Coord(x,y);
		ItemType type = i.getType();
		preview.setSpriteAndSize(i.getWorldSprite());
		preview.setPos(grid.getX(x)-type.getOffsetX(), grid.getY(y)-type.getOffsetY());
		preview.setVisible(true);
		if(canPlace(i,x,y))
		{
			preview.setColor(new Color(1,1,1,.4f));
		}
		else
		{
			preview.setColor(new Color(2,.5f,.5f,.4f));
		}
	}
	public void disablePreview(int x, int y)
	{
		if(previewCoord != null && previewCoord.equals(x,y))
		{
			disablePreview();
		}
	}
	public void disablePreview()
	{
		preview.setVisible(false);
		previewCoord = null;
	}
	public void disableReturnPreview()
	{
		returnPreview.setVisible(false);
	}
	public boolean ownsMouse()
	{
		return grid.anyMatch((x,y)->slots[x][y].getArea().ownsMouse());
	}
	public void act(int dt)
	{
		super.act(dt);
		if(!manip.hasItem() || !ownsMouse())
		{
			disablePreview();
		}
		if(!showReturnPreview.getValue())
		{
			disableReturnPreview();
		}
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
