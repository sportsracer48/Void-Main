package state.workbench;

import game.item.Item;
import graphics.Sprite;
import graphics.entity.FluidEntity;

public class PinSelector extends FluidEntity
{
	public PinSelector(Item i, int screenWidth, int screenHeight, int bufferHeight, WiringMode mode)
	{
		super(0,0,0);
		Sprite worldSprite = i.getWorldSprite();
		setSpriteAndSize(worldSprite);
		int widthScale = screenWidth/worldSprite.imWidth;
		int heightScale = screenHeight/(worldSprite.imHeight+bufferHeight);
		setScale(Math.min(widthScale,heightScale)-1);
		enableRoot();
		
		float x = Math.round((screenWidth-getWidth())/2);
		float y = Math.round((screenHeight-getHeight())/2);
		setPos(x,y);
		
		i.getPinHighlights(mode).forEach(c->addChild(c));
	}
}
