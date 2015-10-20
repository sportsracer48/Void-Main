package state.workbench;

import game.item.Item;
import graphics.entity.Entity;
import state.ui.ClickableArea;

public abstract class ItemAcceptor extends Entity
{
	ClickableArea area;
	ItemManipulator manip;
	boolean displayIcon = false;
	
	Runnable onAnyMove = ()->
	{
		if(manip.hasItem() && area.ownsMouse())
		{
			manip.setAcceptor(this);
			Item item = manip.getItem();
			preview(item);
		}
		else
		{
			preview(null);
		}
	};
	
	public ItemAcceptor(float x, float y, float z,ClickableArea area, ItemManipulator manip)
	{
		super(x,y,z,null);
		this.area = area;
		area.addOnAnyMove(onAnyMove);
		this.manip = manip;
	}
	
	public ItemAcceptor(float x, float y, float z, float width, float height, ItemManipulator manip)
	{
		super(x,y,z,null);
		this.area = new ClickableArea(0,0,width,height);
		area.addOnAnyMove(onAnyMove);
		addClickableArea(area);
		this.manip = manip;
	}
	
	public abstract boolean canAccept(Item i);
	
	public abstract void accept(Item i);
	
	public abstract boolean displayedItem(Item i);
	
	public abstract void preview(Item i);
	
	public void setDisplayIcon(boolean displayIcon)
	{
		this.displayIcon = displayIcon;
	}

	public ClickableArea getArea()
	{
		return area;
	}
}
