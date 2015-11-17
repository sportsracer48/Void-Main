package state.workbench.game;

import java.util.ArrayList;
import java.util.List;

import game.item.Item;
import game.item.ItemType;
import game.item.Pin;
import graphics.Sprite;
import graphics.entity.AnimatedEntity;
import graphics.entity.Entity;
import state.workbench.graphics.InventorySlot;
import util.Grid;

public class ExternalBreakout
{
	InventorySlot[] slots;
	Sprite pinSprite;
	Sprite bgSprite;
	Pin[][] pinGrid = new Pin[8][4];
	Grid grid = new Grid(2,2,6,6,4,8);
	Item itemInterface;
	WiringMode mode;
	
	public ExternalBreakout(Sprite pinSprite, Sprite bgSprite, ItemType breakoutType, WiringMode mode, InventorySlot... slots)
	{
		if(slots.length != 4)
		{
			throw new RuntimeException("ExternalBreakout must have four slots");
		}
		itemInterface = new Item(breakoutType)
		{
			public List<Pin> getPins()
			{
				List<Pin> pins = new ArrayList<>();
				for(int y = 0; y<grid.getRows(); y++)
				{
					int x=0;
					Item i = slots[y].getContents();
					if(i!=null)
					{
						ItemType type = i.getType();
						for(;x<type.getNumBreakoutPins(); x++)
						{
							pins.add(pinGrid[x][y]);
						}
					}
				}
				return pins;
			}
			public Entity getWorldEntity()
			{
				return getEntity(0,0,0);
			}
		};
		this.slots = slots;
		this.pinSprite = pinSprite;
		this.bgSprite = bgSprite;
		this.mode = mode;
		grid.forEachWithIndicies((i,j,x,y)->
		{
			pinGrid[x][y] = new Pin(itemInterface,i.intValue()+2,j.intValue()+2);
		});
		int i = 0;
		for(InventorySlot s: slots)
		{
			final int row = i;
			i++;
			s.addOnChange(()->{
				clearPins(row);
				if(mode.getSelector()!=null && mode.getSelector().getItem() == itemInterface)
				{
					//Maybe this should be somewhere else. Fuck it! It will likely stay here forever.
					mode.getSelector().updatePins();
				}
			});
		}
	}
	
	private void clearPins(int row)
	{
		for(int x = 0; x<8; x++)
		{
			Pin p = pinGrid[x][row];
			if(p.getAttatched()!=null)
			{
				p.getAttatched().reset();
			}
		}
	}

	public Entity getEntity(float x, float y, float z)
	{
		return new BreakoutEntity(x,y,z);
	}
	
	private class BreakoutEntity extends AnimatedEntity
	{
		Entity[][] pinsEntities = new Entity[8][4];
		
		public BreakoutEntity(float x, float y, float z)
		{
			super(x, y, z, bgSprite);
			grid.forEachWithIndicies((x2,y2,col,row)->{
				pinsEntities[col][row] = new Entity(x2,y2,0,pinSprite);
				addChild(pinsEntities[col][row]);
			});
		}

		public void animate(int dt)
		{
			for(int y = 0; y<grid.getRows(); y++)
			{
				int x=0;
				Item i = slots[y].getContents();
				if(i!=null)
				{
					ItemType type = i.getType();
					for(;x<type.getNumBreakoutPins(); x++)
					{
						pinsEntities[x][y].setEnabled(true);
					}
				}
				for(;x<grid.getCols();x++)
				{
					pinsEntities[x][y].setEnabled(false);
				}
			}
		}	
	}

	public InventorySlot[] getSlots()
	{
		return slots;
	}
}
