package state.workbench;

import static game.item.ItemTypes.*;
import static state.workbench.ButtonThemes.*;
import game.item.Item;
import graphics.Sprite;
import graphics.entity.Entity;
import graphics.registry.SpriteAtlas;
import state.ui.Button;
import state.ui.Window;
import state.workbench.controller.DragContext;
import state.workbench.controller.ItemManipulator;
import state.workbench.graphics.InventorySlot;
import util.Grid;
import util.Grid.QuadConsumer;

public class WindowBuilder
{
	Entity inventory,partMounting,tools;
	
	InventorySlot[] top = new InventorySlot[4];
	InventorySlot[] bot = new InventorySlot[4];
	InventorySlot[] left = new InventorySlot[4];
	InventorySlot[] right = new InventorySlot[4];
	InventorySlot[][] hGrid = {top,right,left,bot};
	InventorySlot[] front = new InventorySlot[4];
	InventorySlot[] back = new InventorySlot[4];
	
	public WindowBuilder(SpriteAtlas sprites, ItemManipulator itemManip, DragContext grabContext, int screenHeight, int screenWidth)
	{
		Sprite invHighlight = sprites.getSprite("inv-slot highlight.png");
		inventory = new Window(screenWidth-700,440,sprites.getSprite("Inventory UI.png"),close.build(),grabContext);
		inventory.setEnabled(false);
		
		partMounting = new Window(screenWidth-700,40,sprites.getSprite("part mounting ui.png"), close.build(),grabContext);
		partMounting.setEnabled(false);
		
		Button disabledClose = close.build();
		disabledClose.setEnabled(false);
		
		tools = new Window(screenWidth/2-200,screenHeight-200,sprites.getSprite("Tools.png"),disabledClose,grabContext);
		
		QuadConsumer<Float,Float,Integer,Integer> addToInventory = (x2,y2,i,j) ->
		{
			InventorySlot slot;
			
			double rand = Math.random();
			if(rand<.25)
			{
				slot = new InventorySlot(x2,y2,invHighlight,new Item(eightBitCounter),itemManip);
			}
			else if(rand<.5)
			{
				slot = new InventorySlot(x2,y2,invHighlight,new Item(bitShiftRegister),itemManip);
			}
			else if(rand<.75)
			{
				slot = new InventorySlot(x2,y2,invHighlight,new Item(antenna),itemManip);
			}
			else
			{
				slot = new InventorySlot(x2,y2,invHighlight,new Item(breadboard),itemManip);
			}
			
			inventory.addChild(slot);
		};
		
		new Grid(48,76,43,43,7,10).//grid for inventory window
		forEachWithIndicies(addToInventory);
		
		new Grid(178,65,43,42*2,4,4).//grid for top, left, right, bottom
		forEachWithIndicies((x2,y2,row,col)->{
			InventorySlot slot = new InventorySlot(x2,y2,invHighlight,null,itemManip);;
			partMounting.addChild(slot);
			hGrid[col][row] = slot;
		});
		
		new Grid(112,149,43,42,4,1).//grid for front
		forEachWithIndicies((x2,y2,row,col)->{
			InventorySlot slot = new InventorySlot(x2,y2,invHighlight,null,itemManip);;
			partMounting.addChild(slot);
			front[col] = slot;
		});

		new Grid(373,149,43,42,4,1).//grid for back
		forEachWithIndicies((x2,y2,row,col)->{
			InventorySlot slot = new InventorySlot(x2,y2,invHighlight,null,itemManip);;
			partMounting.addChild(slot);
			back[col] = slot;
		});
		
		new Grid(48,76,43,43,1,10).
		forEach((x2,y2)->{
			InventorySlot slot = new InventorySlot(x2,y2,invHighlight,null,itemManip);
			tools.addChild(slot);
		});
		
		for(InventorySlot s:left)
		{
			s.setContents(new Item(poweredWheel));
		}
		for(InventorySlot s:right)
		{
			s.setContents(new Item(poweredWheel));
		}
	}
}
