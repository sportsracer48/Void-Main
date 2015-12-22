package game.item;

import graphics.entity.AnimatedEntity;

public class ItemEntity extends AnimatedEntity
{
	Item item;
	ItemType type;
	public ItemEntity(float x, float y, float z, Item i)
	{
		super(x, y, z, i.getWorldSprite());
		this.item = i;
		this.type = i.getType();
	}

	public void animate(int dt)
	{
		if(type.getPinUpdate()!=null)
		{
			type.getPinUpdate().accept(item.getPins(),this);
		}
	}

}
