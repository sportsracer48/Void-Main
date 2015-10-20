package state.workbench;

import state.GameState;
import game.item.Item;
import graphics.entity.Entity;

public class ItemManipulator
{
	DragContext core;
	GameState root;
	Item held;
	Entity world;
	ItemAcceptor acceptor;
	ItemAcceptor source;
	
	public ItemManipulator(DragContext core, GameState root)
	{
		this.core = core;
		this.root = root;
	}
	
	public void grabItem(Item i, float x, float y, ItemAcceptor source)
	{
		if(core.hasObject())
		{
			try
			{
				throw new RuntimeException("attempt to grab item while annother area is grabbed");
			}
			catch(RuntimeException r)
			{
				r.printStackTrace();
			}
			//This should never happen. I wish.
			return;
		}
		this.source = source;
		held = i;
		world = i.getInvEntity();
		world.setPos(root.getMouseX()+x,root.getMouseY()+y);
		root.addUI(world);
		root.addActable(world);
		DragArea area = new DragArea(0,0,world.getWidth(),world.getHeight(),core,world);
		area.setDesiresMouse(false);
		area.addOnRelease(()->
		{
			handleDrop();
		});
		world.addClickableArea(area);
		core.setGrabbed(area, -x, -y);
		area.setMouseHeld(true);
	}

	public boolean hasItem()
	{
		return held != null;
	}
	
	public Item getItem()
	{
		return held;
	}
	
	public void setAcceptor(ItemAcceptor acceptor)
	{
		this.acceptor = acceptor;
	}
	public void resetAcceptor()
	{
		this.acceptor = null;
	}
	public void act(int dt)
	{
		if(world == null)
		{
			return;
		}
		if(acceptor != null && acceptor.displayedItem(held))
		{
			world.setVisible(false);
		}
		else
		{
			world.setVisible(true);
		}
	}
	
	public void handleFail()
	{
		source.accept(held);
		root.removeUI(world);
		root.removeActable(world);
		held = null;
	}

	public void handleDrop()
	{
		if(acceptor != null && acceptor.canAccept(held))
		{
			acceptor.accept(held);
			root.removeUI(world);
			root.removeActable(world);
			held = null;
		}
		else
		{
			handleFail();
		}
	}
}
