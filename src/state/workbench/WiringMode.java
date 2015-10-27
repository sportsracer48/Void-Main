package state.workbench;

import game.item.Item;
import game.item.Pin;
import game.item.Wire;
import graphics.entity.FluidEntity;
import state.GameState;
import state.Mode;
import util.Color;

public class WiringMode extends Mode
{
	static Color[] colors = {Color.red, Color.blue, Color.green, Color.cyan, Color.magenta, Color.yellow, Color.white, Color.orange, Color.brown};
	static int color = 0;
	FluidEntity mouseCompanion;
	PinSelector selector;
	GameState root;
	int screenWidth, screenHeight;
	
	public WiringMode(FluidEntity mouseCompanion, GameState root, int screenWidth, int screenHeight)
	{
		this.mouseCompanion = mouseCompanion;
		this.root = root;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}
	
	public PinSelector getSelector()
	{
		return selector;
	}

	public void showSelector(Item i)
	{
		if(this.selector != null)
		{
			root.removeUI(selector);
		}
		this.selector = new PinSelector(i,screenWidth,screenHeight,0,this);
		if(this.selector != null)
		{
			root.addUI(selector);
		}
	}
	
	
	
	public static Color nextColor()
	{
		Color toReturn = colors[color];
		color = (color + 1)%colors.length;
		return toReturn;
	}
	Wire current;
	
	public void enable()
	{
		nextWire();
	}

	public void disable()
	{
		if(current != null && (current.getStart() == null || current.getEnd() == null))
		{
			reset();
		}
		if(selector!=null)
		{
			root.removeUI(selector);
		}
	}
	
	public void reset()
	{
		current.reset();
	}
	
	public void setCurrent(Wire w)
	{
		this.current = w;
		mouseCompanion.setColor(current.getColor());
	}
	
	public Wire getCurrent()
	{
		return current;
	}
	
	public void nextWire()
	{
		current = new Wire(nextColor());
		mouseCompanion.setColor(current.getColor());
	}
	
	public void bind(Pin p)
	{
		if(current == null)
		{
			nextWire();
		}
		
		if(current.getStart() == null)
		{
			current.setStart(p);
		}
		else if(current.getEnd() == null)
		{
			current.setEnd(p);
		}
		else
		{
			current = null;
			bind(p);
		}
		
		
		if(current.getStart() != null && current.getEnd()!=null)
		{
			nextWire();
		}
	}
	
}
