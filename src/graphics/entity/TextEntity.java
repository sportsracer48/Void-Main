package graphics.entity;

import util.Color;
import graphics.Context;
import graphics.Sprite;
import graphics.registry.RegisteredFont;

public class TextEntity extends ColoredEntity
{
	static Sprite[] defaultBgSprites;
	
	public static void setDefaultBgSprites(Sprite... bgSprites)
	{
		defaultBgSprites = bgSprites;
	}
	
	float height = 0;
	float width = 0;
	RegisteredFont font;
	
	public TextEntity(float x, float y, float z, String text)
	{
		this(x,y,z,text,RegisteredFont.defaultFont);
	}
	
	public void renderChildren(Context c)
	{
		c.pushTransform();
		c.setModel(c.getModel().removeScaling());
		super.renderChildren(c);
		c.popTransform();
	}
	
	public TextEntity(float x, float y, float z, String text, RegisteredFont font)
	{
		super(x,y,z,null);
		float charX = 0;
		float charY = 0;
		float lineWidth = 0;
		this.font = font;
		height = font.metrics.getAscent()+font.metrics.getDescent();
		for(int i = 0; i<text.length(); i++)
		{
			char c = text.charAt(i);
			addChild(new Entity(charX,charY,0,
						font.getSprite(c)
					));
			if(c=='\n')
			{
				charY += font.metrics.getHeight();
				height += font.metrics.getHeight();
				charX = 0;
				lineWidth = 0;
			}
			else
			{
				charX += font.metrics.charWidth(c);
				lineWidth += font.metrics.charWidth(c);
				width = Math.max(lineWidth, width);
			}
		}
		height += font.metrics.getDescent();
		setColor(new Color(0xFFFFF0));
	}
	public void setBackgroundToDefault()
	{
		setBackGround(new BoxEntity(0, 0, 0, defaultBgSprites[0], defaultBgSprites[1], defaultBgSprites[2], defaultBgSprites[3]));
	}
	public float getUnscaledWidth()
	{
		return width;
	}
	public float getUnscaledHeight()
	{
		return height;
	}
	
	public void setBackGround(BoxEntity e)
	{
		e.setZ(-1);
		e.setPos(0,font.metrics.getAscent());
		e.setColor(new Color(0,0,0,.57f));
		e.setSize(getWidth(), getHeight());
		addChild(e);
	}
}
