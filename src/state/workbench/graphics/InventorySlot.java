package state.workbench.graphics;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import state.ui.HighlightArea;
import state.workbench.conroller.ItemAcceptor;
import state.workbench.conroller.ItemManipulator;
import util.Color;
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
	Item contents;
	float width, height;
	List<Runnable> onChange = new ArrayList<>();
	
	public InventorySlot(float x, float y, Sprite highlightSprite, Item contains, ItemManipulator manip)
	{
		super(x, y, 0, null);
		
		this.area=new HighlightArea(0,0,highlightSprite);
		this.width = area.getWidth();
		this.height = area.getHeight();
		preview = new FluidEntity(3,3,0);
		this.contents = contains;
		this.acceptor = new ItemAcceptor(3,3,0,area.getArea(),manip)
		{
			public boolean canAccept(Item i)
			{
				return contents == null;
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
		if(contains != null)
		{
			this.itemEntity = contains.getInvEntity();
			this.itemEntity.setPos(3, 3);
			addChild(this.itemEntity);
		}
		addChild(preview);
		addChild(this.area);
		area.getArea().addOnClick((x2,y2,button)->
		{
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
			{
				if(contents == null)
				{
					return;
				}
				manip.grabItem(contents, 16, 16, acceptor);
				setContents(null);
			}
		});
	}
	public void addOnChange(Runnable r)
	{
		onChange.add(r);
	}
	public void setContents(Item i)
	{
		if(i==contents)
		{
			return;
		}
		if(contents != null)
		{
			removeChild(itemEntity);
		}
		contents = i;
		if(i!=null)
		{
			i.stripPins();
			itemEntity = i.getInvEntity();
			itemEntity.setPos(3, 3);
			addChild(itemEntity);
		}
		for(Runnable r: onChange)
		{
			r.run();
		}
	}
	public Item getContents()
	{
		return contents;
	}
}
