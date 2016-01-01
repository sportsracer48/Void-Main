package state.workbench;

import graphics.registry.SpriteAtlas;
import state.ui.Button.ButtonTheme;

public class ButtonThemes
{
	static ButtonTheme close;
	static ButtonTheme inv;
	public static void init(SpriteAtlas sprites)
	{
		close = new ButtonTheme(
				sprites.getSprite("Button raised.png"),
				sprites.getSprite("Button pressed selected.png"),
				sprites.getSprite("Button pressed unselected.png"));
		inv = new ButtonTheme(
				sprites.getSprite("Inv Button Raised.png"),
				sprites.getSprite("Inv Button Pressed.png"),
				sprites.getSprite("Inv Button Gray.png")
				);
	}
}
