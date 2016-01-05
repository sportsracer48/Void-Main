package graphics.registry;

import graphics.Sprite;

import java.awt.FontMetrics;

public class RegisteredFont
{
	public static RegisteredFont defaultFont;
	public static RegisteredFont defaultFontOutline;
	
	public final String name;
	public final FontMetrics metrics;
	public final SpriteAtlas atlas;
	
	private Sprite[] commonCharacters = new Sprite[256];
	private boolean accelerated = false;
	
	public RegisteredFont(String name, FontMetrics metrics, SpriteAtlas atlas)
	{
		this.name = name;
		this.metrics = metrics;
		this.atlas = atlas;
	}
	private Sprite getSpriteSlow(char c)
	{
		return atlas.getSpriteGlobal(name+"/"+c);
	}
	private void accelerate()
	{
		for(int i = 0; i<commonCharacters.length; i++)
		{
			commonCharacters[i] = getSpriteSlow((char) i);
		}
		accelerated = true;
	}
	
	public Sprite getSprite(char c)
	{
		if(!accelerated)
		{
			accelerate();
		}
		if(c<256)
		{
			return commonCharacters[c];
		}
		return getSpriteSlow(c);
	}
	
	public static void setDefault(RegisteredFont f)
	{
		defaultFont = f;
	}
	public static void setDefaultOutline(RegisteredFont f)
	{
		defaultFontOutline = f;
	}
	
}
