package state.workbench.graphics;

import java.util.ArrayList;
import java.util.List;

import util.Grid;
import util.Grid.Coord;
import util.Grid.FloatCoord;
import game.item.Item;
import game.item.ItemType;
import game.item.Pin;
import graphics.Sprite;
import graphics.entity.Entity;

public class WireRenderer extends Entity
{
	Item[][] items;
	Grid spacing;
	float[][] z;
	Sprite wireSegmentX,  wireSegmentY,  wireSegmentZ;
	List<RenderedWire> wires = new ArrayList<>();
	int width;
	int height;
	
	public WireRenderer(float z,Grid spacing,Item[][] items, Sprite wireSegmentX, Sprite wireSegmentY, Sprite wireSegmentZ)
	{
		super(0,0,z,null);
		this.items = items;
		this.spacing = spacing;
		this.width = items[0].length;
		this.height = items.length;
		this.wireSegmentX = wireSegmentX;
		this.wireSegmentY = wireSegmentY;
		this.wireSegmentZ = wireSegmentZ;
	}
	
	
	public void act(int dt)
	{
		z = new float[(int) spacing.getTotalWidth()][(int) spacing.getTotalHeight()];
		List<RenderedWire> newWires = new ArrayList<>();
		List<RenderedWire> oldWires = new ArrayList<>();
		
		List<PlacedItem> itemSet = new ArrayList<>();
		for(int x = 0; x<width; x++)
		{
			for(int y = 0; y<height; y++)
			{
				if(items[x][y]!=null)
				{
					PlacedItem newItem = new PlacedItem(items[x][y],new Coord(x,y));
					if(!itemSet.contains(newItem))
					{
						itemSet.add(newItem);
					}
				}
			}
		}
		List<PlacedPin> pinSet = new ArrayList<>();
		for(PlacedItem i: itemSet)
		{
			for(Pin p: i.i.getPins())
			{
				if(p.getAttatched()!=null)
				{
					pinSet.add(new PlacedPin(p,i));
				}
			}
		}
		while(pinSet.size()>1)
		{
			PlacedPin p0;
			
			do
			{
				p0  = pinSet.remove(0);
			}while(!p0.p.getAttatched().isAttatchedOnBothSides());
			
			Pin other = p0.p.getAttatched().getOtherEnd(p0.p);
			PlacedPin p1 = pinSet.stream().filter(placedPin -> placedPin.p==other).findAny().orElse(null);
			if(p1!=null)
			{
				pinSet.remove(p1);
				RenderedWire testWire = new RenderedWire(p0,p1);
				if(wires.contains(testWire))
				{
					oldWires.add(testWire);
				}
				else
				{
					newWires.add(testWire);
				}
			}
		}
		for(RenderedWire w:wires)
		{
			if(oldWires.contains(w))
			{
				addDepthInfo(w.path);
			}
			else
			{
				removeWire(w.path);
			}
		}
		for(RenderedWire w:newWires)
		{
			w.setWirePath(addWire(w.start,w.end));
		}
		
		wires.retainAll(oldWires);
		wires.addAll(newWires);
		
		super.act(dt);
	}
	
	public WirePath addWire(PlacedPin start, PlacedPin end)
	{
		WirePath p = new WirePath(
				getPinLocation(start).toCoord(),
				getPinLocation(end).toCoord(),
				start.p.getAttatched().getColor(),
				5,
				z,
				wireSegmentX, 
				wireSegmentY,  
				wireSegmentZ);
		
		addDepthInfo(p);
		
		for(Entity e:p.getEntites())
		{
			addChild(e);
		}
		return p;
	}
	
	public void removeWire(WirePath p)
	{
		for(Entity e:p.getEntites())
		{
			removeChild(e);
		}
	}
	
	public void addDepthInfo(WirePath p)
	{
		for(Coord c:p)
		{
			z[c.x][c.y] = Math.max(p.zCoord,z[c.x][c.y]);
		}
	}
	
	public FloatCoord getPinLocation(PlacedPin p)
	{
		Coord itemLoc = p.item.loc;
		Coord pinLoc = p.p.getLocation();
		ItemType type = p.item.i.getType();
		return new FloatCoord(
				spacing.getX(itemLoc.x)-type.getOffsetX()+pinLoc.x,
				spacing.getY(itemLoc.y)-type.getOffsetY()+pinLoc.y
				);
	}
	public FloatCoord getPinLocation(PlacedItem i, Pin p)
	{
		Coord itemLoc = i.loc;
		Coord pinLoc = p.getLocation();
		ItemType type = i.i.getType();
		return new FloatCoord(
				spacing.getX(itemLoc.x)-type.getOffsetX()+pinLoc.x,
				spacing.getY(itemLoc.y)-type.getOffsetY()+pinLoc.y
				);
	}
	
	public Coord getCoords(Item i)
	{
		for(int col = 0; col<width; col++)
		{
			for(int row = 0; row<height; row++)
			{
				if(items[col][row]==i)
				{
					return new Coord(col,row);
				}
			}
		}
		return null;
	}
	
	private static class PlacedItem
	{
		public final Item i;
		public final Coord loc;
		public PlacedItem(Item i, Coord loc)
		{
			this.i=i;
			this.loc = loc;
		}
		public boolean equals(Object other)
		{
			if(other instanceof PlacedItem)
			{
				return i == ((PlacedItem) other).i;
			}
			return false;
		}
		public int hashCode()
		{
			return i.hashCode();
		}
	}
	private static class PlacedPin
	{
		public final Pin p;
		public final PlacedItem item;
		public PlacedPin(Pin p, PlacedItem item)
		{
			this.p=p;
			this.item = item;
		}
		public boolean equals(Object other)
		{
			if(other instanceof PlacedPin)
			{
				PlacedPin otherPin = (PlacedPin)other;
				return p==otherPin.p && otherPin.item.loc.equals(item.loc);
				
			}
			return false;
		}
	}
	private static class RenderedWire
	{
		public final PlacedPin start,end;
		WirePath path;
		public RenderedWire(PlacedPin start, PlacedPin end)
		{
			this.start=start;
			this.end = end;
		}
		public void setWirePath(WirePath path)
		{
			this.path = path;
		}
		public boolean equals(Object other)
		{
			if(other instanceof RenderedWire)
			{
				RenderedWire w = (RenderedWire)other;
				return start.equals(w.end) && end.equals(w.start) || start.equals(w.start) && end.equals(w.end);
			}
			return false;
		}
	}
}
