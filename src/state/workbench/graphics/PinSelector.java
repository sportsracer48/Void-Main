package state.workbench.graphics;

import state.workbench.game.WiringMode;
import game.item.Item;
import graphics.Sprite;
import graphics.entity.FluidEntity;

public class PinSelector extends FluidEntity
{
	Item item;
	WiringMode mode;
	
	public PinSelector(Item i, int screenWidth, int screenHeight, int bufferHeight, WiringMode mode)
	{
		super(0,0,0);
		Sprite worldSprite = i.getWorldSprite();
		int widthScale,heightScale;
		if(worldSprite!=null)
		{
			setSpriteAndSize(worldSprite);
			widthScale = screenWidth/worldSprite.imWidth;
			heightScale = screenHeight/(worldSprite.imHeight+bufferHeight);
		}
		else
		{
			setTo(i.getWorldEntity());
			widthScale = (int) (screenWidth/getWidth());
			heightScale = (int) (screenHeight/(getHeight()+bufferHeight));
		}
		setScale(Math.min(widthScale,heightScale)-1);
		enableRoot();
		
		float x = Math.round((screenWidth-getWidth())/2);
		float y = Math.round((screenHeight-getHeight())/2);
		setPos(x,y);
		
		i.getPinHighlights(mode).forEach(c->addChild(c));
		
		setGroupAlpha(.9f);
		
		this.item = i;
		this.mode = mode;
	}
	
	public void updatePins()
	{
		clearChildren();
		item.getPinHighlights(mode).forEach(c->addChild(c));
	}

	public Item getItem()
	{
		return item;
	}
}
