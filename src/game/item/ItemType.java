package game.item;

import java.util.ArrayList;
import java.util.List;

import util.Grid;
import util.Grid.Coord;
import graphics.Sprite;

public class ItemType //kinda reflexive, but hey, whatever
{
	public static Sprite defaultEndCap, defaultHighlight, defaultWireEnd, defaultWireEndOpaque, defaultWireFade;
	
	public static void setDefaultWireSprites(Sprite endCap, Sprite highlight, Sprite wireEnd, Sprite wireEndOpaque, Sprite wireFade)
	{
		defaultEndCap = endCap;
		defaultHighlight = highlight;
		defaultWireEnd = wireEnd;
		defaultWireEndOpaque = wireEndOpaque;
		defaultWireFade = wireFade;
	}
	
	float offsetX=0,offsetY=0;
	int workbenchWidth=1,workbenchHeight=1;
	Sprite workbench, inventory;
	
	List<Coord> pinLocations = new ArrayList<>();
	List<Coord> stripEndLocations = new ArrayList<>();
	int numBreakoutPins;
	
	Sprite endCap, highlight, wireEnd, wireEndOpaque,  wireFade, pinMask;
	
	public ItemType(Sprite workbench, Sprite inventory)
	{
		this.workbench = workbench;
		this.inventory = inventory;
	}
	
	public ItemType(Sprite inventory)
	{
		this.inventory = inventory;
	}
	
	public ItemType(Sprite inventory, int externalPins)
	{
		this.inventory = inventory;
		this.numBreakoutPins = externalPins;
	}
	
	public Sprite getEndCap()
	{
		return endCap;
	}
	public void setEndCap(Sprite endCap)
	{
		this.endCap = endCap;
	}
	public Sprite getHighlight()
	{
		return highlight;
	}
	public void setHighlight(Sprite highlight)
	{
		this.highlight = highlight;
	}
	public Sprite getWireEnd()
	{
		return wireEnd;
	}
	public void setWireEnd(Sprite wireEnd)
	{
		this.wireEnd = wireEnd;
	}
	public Sprite getWireEndOpaque()
	{
		return wireEndOpaque;
	}
	public void setWireEndOpaque(Sprite wireEndOpaque)
	{
		this.wireEndOpaque = wireEndOpaque;
	}
	public Sprite getWireFade()
	{
		return wireFade;
	}
	public void setWireFade(Sprite wireFade)
	{
		this.wireFade = wireFade;
	}
	public Sprite getPinMask()
	{
		return pinMask;
	}
	public void setPinMask(Sprite pinMask)
	{
		this.pinMask = pinMask;
	}
	public void setOffsetX(float offsetX)
	{
		this.offsetX = offsetX;
	}
	public void setOffsetY(float offsetY)
	{
		this.offsetY = offsetY;
	}
	public float getOffsetX()
	{
		return offsetX;
	}
	public float getOffsetY()
	{
		return offsetY;
	}
	public void setOffsets(float x, float y)
	{
		setOffsetX(x);
		setOffsetY(y);
	}
	public void setWorkbenchSize(int width, int height)
	{
		setWorkbenchWidth(width);
		setWorkbenchHeight(height);
	}
	public int getWorkbenchWidth()
	{
		return workbenchWidth;
	}
	public void setWorkbenchWidth(int workbenchWidth)
	{
		this.workbenchWidth = workbenchWidth;
	}
	public int getWorkbenchHeight()
	{
		return workbenchHeight;
	}
	public void setWorkbenchHeight(int workbenchHeight)
	{
		this.workbenchHeight = workbenchHeight;
	}
	public void addPinLocation(int x, int y)
	{
		pinLocations.add(new Coord(x,y));
	}
	public void addPinStrip(int x, int y, int width)
	{
		new Grid(x,y,3,3,1,width).forEach((x2,y2)->{
			addPinLocation(x2.intValue(),y2.intValue());
		});
		stripEndLocations.add(new Coord(x-1,y));
		stripEndLocations.add(new Coord(x+width*3,y));
	}
	public void addPins(Grid g)
	{
		g.forEach((x2,y2)->{
			addPinLocation(x2.intValue(),y2.intValue());
		});
	}
	public List<Coord> getPinLocations()
	{
		return pinLocations;
	}
	public List<Coord> getStripEndLocations()
	{
		return stripEndLocations;
	}
	public void setWireSprites(Sprite endCap, Sprite highlight, Sprite wireEnd, Sprite wireEndOpaque, Sprite wireFade, Sprite pinMask)
	{
		this.endCap = endCap;
		this.highlight = highlight;
		this.wireEnd = wireEnd;
		this.wireEndOpaque = wireEndOpaque;
		this.wireFade = wireFade;
		this.pinMask = pinMask;
	}
	public void setWireSpritesToDefault(Sprite pinMask)
	{
		this.endCap = defaultEndCap;
		this.highlight = defaultHighlight;
		this.wireEnd = defaultWireEnd;
		this.wireEndOpaque = defaultWireEndOpaque;
		this.wireFade = defaultWireFade;
		this.pinMask = pinMask;
	}
	public List<Pin> getPins(Item instance)
	{
		List<Pin> toReturn = new ArrayList<Pin>();
		pinLocations.forEach(c->{
			toReturn.add(new Pin(instance,c.x,c.y));
		});
		return toReturn;
	}
	public int getNumBreakoutPins()
	{
		return numBreakoutPins;
	}
	public void setNumBreakoutPins(int numBreakoutPins)
	{
		this.numBreakoutPins = numBreakoutPins;
	}
}
