package state.workbench;

import state.ui.HighlightArea;
import game.item.Item;
import graphics.Sprite;
import graphics.entity.Entity;

public class InvenorySlot extends Entity
{
	public static final float BORDER = 3;
	HighlightArea area;
	ItemAcceptor acceptor;
	Entity itemEntity;
	Item contents;
	float width, height;
	
	public InvenorySlot(float x, float y, Sprite highlightSprite, Item contains, ItemManipulator manip)
	{
		super(x, y, 0, null);
		
		this.area=new HighlightArea(0,0,highlightSprite);
		this.width = area.getWidth();
		this.height = area.getHeight();
		this.itemEntity = contains.getInvEntity();
		this.itemEntity.setPos(3, 3);
		this.contents = contains;
		this.acceptor = new ItemAcceptor(3,3,0,area.getArea(),manip)
		{
			public boolean canAccept(Item i)
			{
				return contents == null;
			}

			public void accept(Item i)
			{
				contents = i;
				itemEntity = contains.getInvEntity();
				//itemEntity.setPos(3, 3);
				addChild(itemEntity);
			}

			public boolean displayedItem(Item i)
			{
				return canAccept(i);
			}
		};
		acceptor.setDisplayIcon(true);
		
		addChild(this.acceptor);
		addChild(this.itemEntity);
		addChild(this.area);
		area.getArea().addOnClick((x2,y2)->
		{
			if(contents == null)
			{
				return;
			}
			manip.grabItem(contents, -16, -16, acceptor);
			removeChild(itemEntity);
			contents = null;
		});
	}
}
