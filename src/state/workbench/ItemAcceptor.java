package state.workbench;

import game.item.Item;
import game.item.ItemType;
import graphics.Sprite;
import graphics.entity.Entity;
import graphics.entity.FluidEntity;
import state.ui.ClickableArea;
import util.Color;

public abstract class ItemAcceptor extends Entity
{
	ClickableArea area;
	ItemManipulator manip;
	boolean displayIcon = false;
	FluidEntity display;
	
	Runnable onAnyMove = ()->
	{
		if(manip.hasItem() && area.ownsMouse())
		{
			manip.setAcceptor(this);
			Sprite world;
			Item item = manip.getItem();
			ItemType type = item.getType();
			if(displayIcon)
			{
				world = manip.getItem().getInvSprite();
			}
			else
			{
				world = manip.getItem().getWorldSprite();
				display.setPos(-type.getOffsetX(),-type.getOffsetY());
			}
			display.setSpriteAndSize(world);
			if(canAccept(manip.getItem()))
			{
				display.setColor(new Color(1,1,1,.4f));
			}
			else
			{
				display.setColor(new Color(1.6f,.7f,.7f,.4f));
			}
		}
		else
		{
			display.setSpriteAndSize(null);
		}
	};
	
	public ItemAcceptor(float x, float y, float z,ClickableArea area, ItemManipulator manip)
	{
		super(x,y,z,null);
		display = new FluidEntity(0,0,0);
		addChild(display);
		this.area = area;
		area.addOnAnyMove(onAnyMove);
		this.manip = manip;
	}
	
	public ItemAcceptor(float x, float y, float z, float width, float height, ItemManipulator manip)
	{
		super(x,y,z,null);
		display = new FluidEntity(0,0,0);
		addChild(display);
		this.area = new ClickableArea(0,0,width,height);
		area.addOnAnyMove(onAnyMove);
		addClickableArea(area);
		this.manip = manip;
	}
	
	public abstract boolean canAccept(Item i);
	
	public abstract void accept(Item i);
	
	public abstract boolean displayedItem(Item i);
	
	public void setDisplayIcon(boolean displayIcon)
	{
		this.displayIcon = displayIcon;
	}

	public ClickableArea getArea()
	{
		return area;
	}
}
