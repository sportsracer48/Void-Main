package graphics.entity;

import graphics.Context;
import graphics.Sprite;

public abstract class ColoredEntity extends Entity
{
	public ColoredEntity(float x, float y, float z, Sprite base)
	{
		super(x, y, z, base);
	}

	public void renderChildren(Context c)
	{
		c.setColor(getColor());
		super.renderChildren(c);
		c.resetColor();
	}
}
