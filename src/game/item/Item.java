package game.item;

import graphics.Sprite;
import graphics.entity.Entity;

public class Item
{
	ItemType type;
	
	public Item(ItemType type)
	{
		this.type = type;
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
}
