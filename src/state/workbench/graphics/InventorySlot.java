package state.workbench.graphics;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import state.ui.HighlightArea;
import state.workbench.controller.ItemAcceptor;
import state.workbench.controller.ItemManipulator;
import util.Color;
import game.item.Inventory;
import game.item.Item;
import graphics.Sprite;
import graphics.entity.Entity;
import graphics.entity.FluidEntity;

public class InventorySlot extends Entity
{
	public static final float BORDER = 3;
	HighlightArea area;
	ItemAcceptor acceptor;
	Entity itemEntity;
	FluidEntity preview;
	float width, height;
	List<Runnable> onChange = new ArrayList<>();
	
	Inventory parent;
	int index;
	
	public InventorySlot(float x, float y, Sprite highlightSprite, Inventory parent, int index, ItemManipulator manip)
	{
		super(x, y, 0, null);
		
		this.area=new HighlightArea(0,0,highlightSprite);
		this.width = area.getWidth();
		this.height = area.getHeight();
		preview = new FluidEntity(3,3,0);
		this.parent = parent;
		this.index = index;
		this.acceptor = new ItemAcceptor(3,3,0,area.getArea(),manip)
		{
			public boolean canAccept(Item i)
			{
				return parent != null && getContents() == null;
			}

			public void accept(Item i)
			{
				setContents(i);
			}

			public boolean displayedItem(Item i)
			{
				return canAccept(i);
			}

			@Override
			public void preview(Item i) 
			{
				if(i==null)
				{
					preview.setVisible(false);
					return;
				}
				if(canAccept(manip.getItem()))
				{
					Sprite world = i.getInvSprite();
					preview.setSpriteAndSize(world);
					preview.setColor(new Color(1,1,1,.4f));
					preview.setVisible(true);
				}
			}
		};
		acceptor.setDisplayIcon(true);
		
		addChild(this.acceptor);
		if(getContents() != null)
		{
			this.itemEntity = getContents().getInvEntity();
			this.itemEntity.setPos(3, 3);
			addChild(this.itemEntity);
		}
		addChild(preview);
		addChild(this.area);
		area.getArea().addOnClick((x2,y2,button)->
		{
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
			{
				if(getContents() == null)
				{
					return;
				}
				manip.grabItem(getContents(), 16, 16, acceptor);
				setContents(null);
			}
		});
	}
	
	public void setParent(Inventory parent)
	{
		this.parent = parent;
		if(itemEntity != null)
		{
			removeChild(itemEntity);
		}
		if(getContents() != null)
		{
			Item i = getContents();
			i.resetPinsAndState();
			itemEntity = i.getInvEntity();
			itemEntity.setPos(3, 3);
			addChild(itemEntity);
		}
	}
	
	public void act(int dt)
	{
		if(parent == null)
		{
			this.setEnabled(false);
		}
		else
		{
			this.setEnabled(true);
		}
		super.act(dt);
	}
	
	public Item getContents()
	{
		if(parent == null)
		{
			return null;
		}
		return parent.getItem(index);
	}
	
	public void addOnChange(Runnable r)
	{
		onChange.add(r);
	}
	public void setContents(Item i)
	{
		if(i==getContents())
		{
			return;
		}
		if(getContents() != null)
		{
			removeChild(itemEntity);
		}
		parent.setItem(index,i);
		if(i!=null)
		{
			i.resetPinsAndState();
			itemEntity = i.getInvEntity();
			itemEntity.setPos(3, 3);
			addChild(itemEntity);
		}
		for(Runnable r: onChange)
		{
			r.run();
		}
	}
}
