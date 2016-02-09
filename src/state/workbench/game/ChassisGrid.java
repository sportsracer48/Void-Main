package state.workbench.game;

import java.util.HashSet;
import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import entry.GlobalState;
import state.Mode;
import state.ModeManager;
import state.ui.ClickableArea;
import state.workbench.ZoomTransition;
import state.workbench.controller.GrabBound;
import state.workbench.controller.ItemAcceptor;
import state.workbench.controller.ItemManipulator;
import state.workbench.graphics.InventorySlot;
import state.workbench.graphics.WireRenderer;
import util.Grid;
import util.Grid.Coord;
import util.Color;
import game.item.Item;
import game.item.ItemType;
import game.item.ItemTypes;
import graphics.Sprite;
import graphics.entity.Entity;
import graphics.entity.FluidEntity;

public class ChassisGrid extends Entity
{
	Item[][] contents;
	Entity[][] entities;
	ItemAcceptor[][] slots;
	BreakoutItems breakouts;
	FluidEntity preview;
	FluidEntity returnPreview;
	WireRenderer wireRenderer;
	Coord previewCoord;
	GrabBound<Coord> grabbedSquare = new GrabBound<>();
	GrabBound<Boolean> showReturnPreview = new GrabBound<>();
	int width, height;
	Grid grid;
	ItemManipulator manip;
	ModeManager manager;
	WiringMode wiring;
	Mode programming;
	EditHistory history;
	
	public ChassisGrid(float x, float y, float z, 
			Sprite base,ItemManipulator manip,ModeManager manager,WiringMode wiring, Mode programmingMode, EditHistory history,
			Sprite wireSegmentX, Sprite wireSegmentY, Sprite wireSegmentZ,
			ZoomTransition programmingTransition)
	{
		this(10,10,21,37,55,36,x,y,z,base, manip,manager,wiring,programmingMode,history,wireSegmentX,wireSegmentY,wireSegmentZ,programmingTransition);
	}
	
	private ChassisGrid(int width, int height, float offsetX, float offsetY, float xStep, float yStep, float x, float y, float z, 
			Sprite base, ItemManipulator manip,ModeManager manager,WiringMode wiring, Mode programmingMode, EditHistory history,
			Sprite wireSegmentX, Sprite wireSegmentY, Sprite wireSegmentZ,
			ZoomTransition programmingTransition)
	{
		super(x,y,z,base);
		
		//bind grab bound objects. These get reset to their default values on a drop event.
		grabbedSquare.setDefault(new Coord(0,0));
		showReturnPreview.setDefault(false);
		manip.addGrabBound(grabbedSquare);
		manip.addGrabBound(showReturnPreview);
		manip.addOnSuccessfulDrop(history::saveState);
		
		//register this as the view of the edit history
		this.history = history;
		history.view = this;
		
		//a grid to be iterated over
		grid = new Grid(offsetX,offsetY,xStep,yStep,width,height);
		
		//initialize arrays
		contents = new Item[width][height+1];
		entities = new Entity[width][height];
		slots = new ItemAcceptor[width][height];
		
		preview = new FluidEntity(0,0,height+4);
		preview.setVisible(false);
		
		returnPreview = new FluidEntity(0,0,height+3);
		returnPreview.setVisible(false);
		
		wireRenderer = new WireRenderer(height+5,grid,contents,wireSegmentX,wireSegmentY,wireSegmentZ);
		
		addChild(preview);
		addChild(returnPreview);
		addChild(wireRenderer);
		
		this.width = width;
		this.height = height;
		this.manip = manip;
		this.manager = manager;
		this.wiring = wiring;
		this.programming = programmingMode;
		
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
					return i.existsInWorld();
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
			a.getArea().addOnClick((i,j,button)->{
				if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
				{
					Item item = contents[col][row];
					if(item == null)
					{
						return;
					}
					if(manager.getMode()==wiring)
					{
						wiring.showSelector(item);
					}
					else if(manager.getMode()==programming)
					{
						GlobalState.laptop.setConnected(item.getEnvironment());
						programmingTransition.start();
					}
					else
					{
						Coord c = getCoords(item);
						Coord squareGrabbed = new Coord(col-c.x,row-c.y);
						remove(item);
						manip.grabItem(item, 16, 16, getSlot(c));
						enableReturnPreview(item,col-squareGrabbed.x,row-squareGrabbed.y);
						showReturnPreview.setValue(true);
						grabbedSquare.setValue(squareGrabbed);
					}
				}
			});
			addChild(a);
		});
	}
	public HashSet<Item> getItems()
	{
		HashSet<Item> allItems = new HashSet<>();
		grid.forEachWithIndicies((i,j,x,y)->{
			if(contents[x][y] != null)
			{
				allItems.add(contents[x][y]);
			}
		});
		for(ExternalBreakout breakout:breakouts.allBreakouts)
		{
			for(InventorySlot slot:breakout.slots)
			{
				Item i = slot.getContents();
				if(i!=null)
				{
					allItems.add(i);
				}
			}
		}
		return allItems;
	}
	public void forEachItem(Consumer<Item> consumer)
	{
		getItems().forEach(consumer);
	}
	
	public ExportState export()
	{
		return new ExportState(contents,breakouts,history);
	}
	
	public void addExternalBreakouts(ItemType breakoutType,Sprite pinSprite, Sprite bgSprite, InventorySlot[] topSlots, InventorySlot[] botSlots, InventorySlot[] leftSlots, InventorySlot[] rightSlots, InventorySlot[] frontSlots, InventorySlot[] backSlots)
	{
		ExternalBreakout top =   new ExternalBreakout(pinSprite,bgSprite,breakoutType,wiring,topSlots);
		ExternalBreakout bot =   new ExternalBreakout(pinSprite,bgSprite,breakoutType,wiring,botSlots);
		ExternalBreakout left =  new ExternalBreakout(pinSprite,bgSprite,breakoutType,wiring,leftSlots);
		ExternalBreakout right = new ExternalBreakout(pinSprite,bgSprite,breakoutType,wiring,rightSlots);
		ExternalBreakout front = new ExternalBreakout(pinSprite,bgSprite,breakoutType,wiring,frontSlots);
		ExternalBreakout back =  new ExternalBreakout(pinSprite,bgSprite,breakoutType,wiring,backSlots);
		
		breakouts = new BreakoutItems(top,bot,left,right,front,back);
		
		int x = 0;
		for(ExternalBreakout b: breakouts.allBreakouts)
		{
			Entity e = b.getEntity(grid.getX(x), grid.getY(10), 0);
			contents[x][10] = b.itemInterface;
			x++;
			if(x==2 || x==6)
			{
				x+=2;
			}
			e.addClickableArea(new ClickableArea(0,0,grid.getXStep(),grid.getYStep()){
				public void onLeftClick(float x, float y)
				{
					if(manager.getMode()==wiring)
					{
						wiring.showSelector(b.itemInterface);
					}
				}
			});
			addChild(e);
		}
		
		history.init();
	}
	
	public BreakoutItems getBreakouts()
	{
		return breakouts;
	}
	
	
	
	public void revalidateEntities()
	{
		grid.forEachWithIndicies((i,j,x,y)->
		{
			if(entities[x][y] !=null)
			{
				removeChild(entities[x][y]);
				entities[x][y] = null;
			}
		});
		HashSet<Item> found = new HashSet<>();
		grid.forEachWithIndicies((wx,wy,x,y)->
		{
			if(contents[x][y] !=null)
			{
				if(!found.contains(contents[x][y]))
				{
					Item i =contents[x][y];
					ItemType type = i.getType();
					Entity newEntity = i.getWorldEntity();
					newEntity.setPos(wx-type.getOffsetX(), wy-type.getOffsetY());
					newEntity.setZ(y);
					addChild(newEntity);
					found.add(i);
					for(int col = 0; col<type.getWorkbenchWidth(); col++)
					{
						for(int row = 0; row<type.getWorkbenchHeight(); row++)
						{
							entities[x+col][y+row] = newEntity;
						}
					}
				}
			}
		});
		if(wiring.getSelector() != null && !found.contains(wiring.getSelector().getItem()))
		{
			wiring.setSelector(null);
		}
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

	public void clear()
	{
		grid.forEachWithIndicies((i,j,x,y)->{
			if(contents[x][y] != null && contents[x][y].getType().typeId != ItemTypes.BREAKOUT)
			{
				remove(x,y);
			}
		});
		for(ExternalBreakout breakout:breakouts.allBreakouts)
		{
			breakout.clear();
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
