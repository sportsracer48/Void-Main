package game.item;

import graphics.entity.FluidEntity;

public class ItemEntity extends FluidEntity
{
	Item item;
	ItemType type;
	public ItemEntity(float x, float y, float z, Item i)
	{
		super(x, y, z);
		super.setSpriteAndSize(i.getWorldSprite());
		this.item = i;
		this.type = i.getType();
	}
	
	public void act(int dt)
	{
		super.act(dt);
		item.graphicsUpdate(this);
	}
}
