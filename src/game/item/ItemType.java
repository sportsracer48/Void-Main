package game.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import program.Environment;
import program.ProgramCoordinator;
import util.Grid;
import util.Grid.Coord;
import game.map.Unit;
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
	List<String> pinTooltips = new ArrayList<>();
	List<Coord> stripEndLocations = new ArrayList<>();
	int numBreakoutPins;
	
	BiConsumer<List<Pin>,Item> pinUpdate;
	BiConsumer<List<Pin>,ItemEntity> graphicsUpdate;
	QuinConsumer<List<Pin>, List<Pin>, Integer,Unit,Item> exportPinUpdate;
	BiConsumer<List<Pin>,Item> radioUpdate;
	
	Sprite endCap, highlight, wireEnd, wireEndOpaque,  wireFade, pinMask;
	
	public final int typeId;
	
	public ItemType(int typeId,Sprite workbench, Sprite inventory)
	{
		this.typeId = typeId;
		this.workbench = workbench;
		this.inventory = inventory;
	}
	
	public ItemType(int typeId, Sprite inventory)
	{
		this.typeId = typeId;
		this.inventory = inventory;
	}
	
	public ItemType(int typeId, Sprite inventory, int externalPins)
	{
		this.typeId = typeId;
		this.inventory = inventory;
		this.numBreakoutPins = externalPins;
	}
	
	public void setTooltips(String... tooltips)
	{
		for(String s: tooltips)
		{
			pinTooltips.add(s);
		}
	}
	public void setDebugTooltips()
	{
		for(int i = 0; i<pinLocations.size(); i++)
		{
			pinTooltips.add(String.valueOf(i));
		}
		for(int i = 0; i<numBreakoutPins; i++)
		{
			pinTooltips.add(String.valueOf(i));
		}
	}
	public void fillDebugTooltips()
	{
		for(int i = pinTooltips.size(); i<pinLocations.size(); i++)
		{
			pinTooltips.add(String.valueOf(i));
		}
		while(pinTooltips.size()>pinLocations.size())
		{
			pinTooltips.remove(pinTooltips.size()-1);
		}
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
	public ItemEntity getWorldEntity(Item instance)
	{
		return new ItemEntity(-offsetX,-offsetY,0,instance);
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
	public void sortPins()
	{
		Collections.sort(pinLocations,(c1,c2)->{
			if(c1.y<c2.y)
			{
				return -1;
			}
			if(c1.y>c2.y)
			{
				return 1;
			}
			if(c1.x<c2.x)
			{
				return -1;
			}
			if(c1.x>c2.x)
			{
				return 1;
			}
			return 0;
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
	
	public List<Pin> getBreakoutPins(Item instance)
	{
		List<Pin> toReturn = new ArrayList<Pin>();
		for(int i = 0; i<numBreakoutPins; i++)
		{
			toReturn.add(new Pin(instance,0,0));
		}
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

	public List<String> getTooltips()
	{
		return pinTooltips;
	}
	
	public BiConsumer<List<Pin>,Item> getLogicUpdate()
	{
		return pinUpdate;
	}
	
	public BiConsumer<List<Pin>, ItemEntity> getGraphicsPinUpdate()
	{
		return graphicsUpdate;
	}
	
	public QuinConsumer<List<Pin>,List<Pin>,Integer,Unit,Item> getExportPinUpdate()
	{
		return exportPinUpdate;
	}
	
	public void setExportPinUpdate(QuinConsumer<List<Pin>,List<Pin>,Integer,Unit,Item> pinUpdate)
	{
		this.exportPinUpdate = pinUpdate;
	}
	
	public void setGraphicsPinUpdate(BiConsumer<List<Pin>, ItemEntity> pinUpdate)
	{
		this.graphicsUpdate = pinUpdate;
	}
	
	public void setLogicUpdate(Consumer<List<Pin>> logicUpdate)
	{
		this.pinUpdate = (pins,item)->logicUpdate.accept(pins);//kinda like currying
	}
	public void setLogicUpdate(BiConsumer<List<Pin>,Item> logicUpdate)
	{
		this.pinUpdate = logicUpdate;
	}
	
	public BiConsumer<List<Pin>,Item> getRadioUpdate()
	{
		return radioUpdate;
	}
	public void setRadioUpdate(BiConsumer<List<Pin>,Item> radioUpdate)
	{
		this.radioUpdate = radioUpdate;
	}

	public Environment getEnvironmentFor(List<Pin> pins, ProgramCoordinator coordinator)
	{
		return null;
	}
	
	@FunctionalInterface
	public interface QuinConsumer<A,B,C,D,E>
	{
		public void accept(A a, B b, C c, D d, E e);
	}	
}
