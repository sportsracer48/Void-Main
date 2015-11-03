package graphics.registry;

import graphics.Sprite;

import java.awt.FontMetrics;

public class RegisteredFont
{
	public static RegisteredFont defaultFont;
	
	public final String name;
	public final FontMetrics metrics;
	public final SpriteAtlas atlas;
	public RegisteredFont(String name, FontMetrics metrics, SpriteAtlas atlas)
	{
		this.name = name;
		this.metrics = metrics;
		this.atlas = atlas;
	}
	public Sprite getSprite(char c)
	{
		return atlas.getSpriteGlobal(name+"/"+c);
	}
	
	public static void setDefault(RegisteredFont f)
	{
		defaultFont = f;
	}
	
}
