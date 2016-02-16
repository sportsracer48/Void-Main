package state.workbench;

import static state.workbench.ButtonThemes.*;
import game.item.Item;
import game.item.ItemType;
import game.item.ItemTypes;
import game.session.GlobalState;
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
	
	public static ItemType getTypeFor(int i)
	{
		switch(i%10)
		{
		case 0: return ItemTypes.antenna;
		case 1: return ItemTypes.battery;
		case 2: return ItemTypes.bitShiftRegister;
		case 3: return ItemTypes.breadboard;
		case 4: return ItemTypes.transmitter;
		case 5: return ItemTypes.eightBitCounter;
		case 6: return ItemTypes.ledOutput;
		case 7: return ItemTypes.microController;
		case 8: return ItemTypes.poweredWheel;
		case 9: return ItemTypes.sensor;
		}
		return null;
	}
	
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
			slot = new InventorySlot(x2,y2,invHighlight,GlobalState.getbaseInventory(),i+j*10,itemManip);
			slot.setContents(new Item(getTypeFor(i+j*10)));
			
			inventory.addChild(slot);
		};
		
		new Grid(48,76,43,43,7,10).//grid for inventory window
		forEachWithIndicies(addToInventory);
		
		new Grid(178,65,43,42*2,4,4).//grid for top, left, right, bottom
		forEachWithIndicies((x2,y2,row,col)->{
			InventorySlot slot = new InventorySlot(x2,y2,invHighlight,null,col+row*4,itemManip);;
			partMounting.addChild(slot);
			hGrid[col][row] = slot;
		});
		
		new Grid(112,149,43,42,4,1).//grid for front
		forEachWithIndicies((x2,y2,row,col)->{
			InventorySlot slot = new InventorySlot(x2,y2,invHighlight,null,16+row,itemManip);;
			partMounting.addChild(slot);
			front[col] = slot;
		});

		new Grid(373,149,43,42,4,1).//grid for back
		forEachWithIndicies((x2,y2,row,col)->{
			InventorySlot slot = new InventorySlot(x2,y2,invHighlight,null,20+row,itemManip);;
			partMounting.addChild(slot);
			back[col] = slot;
		});
		
		new Grid(48,76,43,43,1,10).
		forEachWithIndicies((x2,y2,row,col)->{
			InventorySlot slot = new InventorySlot(x2,y2,invHighlight,GlobalState.getbaseInventory(),70+col,itemManip);
			tools.addChild(slot);
		});
	}
}
