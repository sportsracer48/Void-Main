package game.item;

import java.util.ArrayList;
import java.util.List;

import state.workbench.PinHighlight;
import state.workbench.WiringMode;
import graphics.Sprite;
import graphics.entity.Entity;

public class Item
{
	ItemType type;
	List<Pin> pins;
	
	public Item(ItemType type)
	{
		this.type = type;
		pins = type.getPins();
	}
	
	public Entity getInvEntity()
	{
		return new Entity(0,0,0,getInvSprite());
	}
	
	public Sprite getInvSprite()
	{
		return type.inventory;
	}
	
	public Sprite getWorldSprite()
	{
		return type.workbench;
		
	}
	
	public ItemType getType()
	{
		return type;
	}
	
	public Entity getWorldEntity()
	{
		return new Entity(-type.offsetX,-type.offsetY,0,getWorldSprite());
	}
	
	public boolean existsInWorld()
	{
		return getWorldSprite()!=null;
	}
	
	public List<PinHighlight> getPinHighlights(WiringMode mode)
	{
		List<PinHighlight> toReturn = new ArrayList<>();
		
		pins.forEach(c->{
			toReturn.add(new PinHighlight(c.x-1,c.y-1,type.highlight,type.wireEnd,type.wireFade,type.pinMask,mode,c));
		});
		
		type.stripEndLocations.forEach(c->{
			toReturn.add(new PinHighlight(c.x-1,c.y-1,type.endCap,type.wireEnd,type.wireFade,type.pinMask,null,mode,false));
		});
		
		return toReturn;
	}
}
