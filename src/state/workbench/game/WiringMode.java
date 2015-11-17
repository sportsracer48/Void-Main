package state.workbench.game;

import game.item.Item;
import game.item.Pin;
import game.item.Wire;
import graphics.entity.FluidEntity;
import state.GameState;
import state.Mode;
import state.workbench.graphics.PinSelector;
import util.Color;

public class WiringMode extends Mode
{
	static Color[] colors = {Color.red, Color.blue, Color.green, Color.cyan, Color.magenta, Color.yellow, Color.white, Color.orange, Color.brown};
	static int color = 0;
	FluidEntity mouseCompanion;
	PinSelector selector;
	GameState root;
	EditHistory history;
	int screenWidth, screenHeight;
	Wire current;
	
	public WiringMode(FluidEntity mouseCompanion, GameState root, EditHistory history, int screenWidth, int screenHeight)
	{
		this.mouseCompanion = mouseCompanion;
		this.root = root;
		this.history = history;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		history.wireEditor = this;
	}
	
	public PinSelector getSelector()
	{
		return selector;
	}

	public void showSelector(Item i)
	{
		setSelector(new PinSelector(i,screenWidth,screenHeight,25,this));
	}
	
	public void setSelector(PinSelector selector)
	{
		if(this.selector != null)
		{
			root.removeUI(this.selector);
		}
		this.selector = selector;
		if(this.selector != null)
		{
			root.addUI(this.selector);
		}
	}
	
	
	
	public static Color nextColor()
	{
		Color toReturn = colors[color];
		color = (color + 1)%colors.length;
		return toReturn;
	}
	
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
			return;
		}
		if(current.isAttatchedOnBothSides())
		{
			nextWire();
			history.saveState();
		}
	}

	public void unbind(Pin pin)
	{
		Wire w = pin.getAttatched();
		if(w!=current)
		{
			reset();
		}
		w.extractFrom(pin);
		setCurrent(w);
		if(current.isAttatchedOnNoSide())
		{
			history.saveState();
		}
	}

	public void unbind(Wire w)
	{
		w.reset();
		history.saveState();
	}
	
}
