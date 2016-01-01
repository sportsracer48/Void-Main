package state.workbench.controller;

import java.util.ArrayList;
import java.util.List;

import state.GameState;
import state.ui.ClickableArea;
import game.item.Item;
import graphics.entity.Entity;

public class ItemManipulator
{
	DragContext core;
	GameState root;
	ClickableArea globalArea;
	Item held;
	Entity world;
	ItemAcceptor acceptor;
	ItemAcceptor source;
	List<GrabBound<?>> grabBound = new ArrayList<>();
	List<Runnable> onDrop = new ArrayList<>();
	
	public ItemManipulator(DragContext core, GameState root, ClickableArea globalArea)
	{
		this.core = core;
		this.root = root;
		this.globalArea = globalArea;
		globalArea.addOnAnyRelease(()->
		{
			handleDrop();
		});
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
		resetGrabBound();
		world = i.getInvEntity();
		world.setPos(root.getMouseX()+x,root.getMouseY()+y);
		root.addUI(world);
		DragArea area = new DragArea(0,0,world.getWidth(),world.getHeight(),core,world);
		area.setDesiresMouse(false);
		world.addClickableArea(area);
		core.setGrabbed(area, x, y);
	}
	
	private void resetGrabBound()
	{
		for(GrabBound<?> b: grabBound)
		{
			b.reset();
		}
	}
	private void runOnDrops()
	{
		for(Runnable r: onDrop)
		{
			r.run();
		}
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
		resetGrabBound();
		source.accept(held);
		root.removeUI(world);
		root.removeActable(world);
		held = null;
	}

	public void handleDrop()
	{
		if(held == null)
		{
			return;
		}
		if(acceptor != null && acceptor.canAccept(held))
		{
			acceptor.accept(held);
			root.removeUI(world);
			root.removeActable(world);
			held = null;
			resetGrabBound();
			runOnDrops();
		}
		else
		{
			handleFail();
		}
	}
	
	public void addGrabBound(GrabBound<?> grabBound)
	{
		this.grabBound.add(grabBound);
	}
	public void addOnSuccessfulDrop(Runnable r)
	{
		this.onDrop.add(r);
	}
}
