package state.workbench.graphics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import math.Matrix;
import game.item.Item;
import graphics.Sprite;
import graphics.entity.Entity;
import util.Color;
import util.Grid.Coord;

public class WirePath implements Iterable<Coord>
{
	List<Segment> segments;
	List<Coord> path;
	List<Entity> entities;
	float zCoord;
	float zOffset=1000;
	Sprite segmentX, segmentY, segmentZ;
	Color wireColor;
	Item startItem,endItem;
	public WirePath(Coord start, Coord end,Color wireColor, float zStart, float[][] z, Sprite segmentX,Sprite segmentY,Sprite segmentZ, Item startItem, Item endItem)
	{
		this.segmentX = segmentX;
		this.segmentY = segmentY;
		this.segmentZ = segmentZ;
		this.wireColor = wireColor;
		this.startItem = startItem;
		this.endItem = endItem;
		init(start,end,zStart,z);
	}
	
	private void init(Coord start, Coord end, float zStart, float[][] z)
	{
		this.zCoord=zStart;
		segments = new ArrayList<>();
		path = new ArrayList<>();
		
		int width = Math.abs(start.x-end.x);
		int height = Math.abs(start.y-end.y);
		
		if(width>height)
		{
			initSegments(start,end,zStart,z);
		}
		else
		{
			initSegments(start,end,zStart,z);
		}
		
		for(Segment s: segments)
		{
			for(Coord c:s.getLocations())
			{
				path.add(c);
			}
		}
		for(Coord c: path)
		{
			this.zCoord = Math.max(z[c.x][c.y]+1, zCoord);
		}
		segments.add(new ZSegment(start,(int)zCoord,segmentZ,startItem));
		segments.add(new ZSegment(end,(int)zCoord,segmentZ,endItem));
	}
	
	private void initSegments(Coord start, Coord end, float zStart, float[][] z)
	{
		Coord bot, top;
		if(start.y<end.y)
		{
			top = start;
			bot = end;
		}
		else
		{
			top = end;
			bot = start;
		}
		addSegment(bot, new Coord(bot.x,top.y));
		addSegment(new Coord(bot.x,top.y),top);
	}
	
	
	private void addSegment(Coord start, Coord end)
	{
		int dx = end.x-start.x;
		int dy = end.y-start.y;
		if(Math.abs(dx)>0)
		{
			segments.add(new HSegment(start,end.x-start.x,segmentX));
		}
		if(Math.abs(dy)>0)
		{
			segments.add(new VSegment(start,end.y-start.y,segmentY));
		}
	}
	
	public List<Entity> getEntites()
	{
		if(entities != null)
		{
			return entities;
		}
		entities = new ArrayList<>();
		
		for(Segment s:segments)
		{
			Entity segmentEntity = s.getEntity(zCoord,wireColor);
			entities.add(segmentEntity);
		}
		
		return entities;
	}
	
	public List<Coord> getLocations()
	{
		return path;
	}
	
	public Iterator<Coord> iterator()
	{
		return getLocations().iterator();
	}
	
	
	public abstract class Segment
	{
		public abstract Entity getEntity(float z, Color c);
		public abstract Coord getEnd();
		public abstract Coord getStart();
		public List<Coord> getLocations()
		{
			List<Coord> locations = new ArrayList<>();
			Coord current = getStart();
			Coord end = getEnd();
			int x = current.x;
			int y= current.y;
			int dx = (int) Math.signum(end.x-x);
			int dy = (int) Math.signum(end.y-y);
			while(x!=end.x || y!= end.y)
			{
				locations.add(new Coord(x,y));
				x+=dx;
				y+=dy;
			}
			locations.add(end);
			return locations;
		}
	}
	
	public class HSegment extends Segment
	{
		public final Coord start;
		public int length;
		public final Sprite segment;
		public HSegment(Coord start, int length, Sprite segment)
		{
			this.start = start;
			this.length = length;
			this.segment = segment;
		}
		public Entity getEntity(float z, Color c)
		{
			Coord a = start;
			Coord b = new Coord(start.x+length,start.y);
			Coord left, right;
			if(b.x<a.x)
			{
				left = b;
				right =a;
			}
			else
			{
				left = a;
				right = b;
			}
			int size = (right.x-left.x)+1;
			//TODO
			Entity toReturn = new HorizWireEntity(left.x,left.y,z,
					segment);
			toReturn.setScale(size,1);
			toReturn.setColor(c);
			return toReturn;
			
		}
		public Coord getEnd()
		{
			return new Coord(start.x+length,start.y);
		}
		public Coord getStart()
		{
			return start;
		}
		
	}
	
	public class VSegment extends Segment
	{
		public final Coord start;
		public int length;
		public final Sprite segment;
		public VSegment(Coord start, int length, Sprite segment)
		{
			this.start = start;
			this.length = length;
			this.segment = segment;
		}
		public Entity getEntity(float z, Color c)
		{
			Coord a = start;
			Coord b = new Coord(start.x,start.y+length);
			Coord top, bottom;
			if(b.y<a.y)
			{
				top = b;
				bottom =a;
			}
			else
			{
				top = a;
				bottom = b;
			}
			int size = (bottom.y-top.y)+1;
			//TODO
			Entity toReturn = new ForwardWireEntity(top.x,top.y,z,
					segment);
			toReturn.setScale(1,size);
			toReturn.setColor(c);
			return toReturn;
		}
		public Coord getEnd()
		{
			return new Coord(start.x,start.y+length);
		}
		public Coord getStart()
		{
			return start;
		}
	}
	public class ZSegment extends Segment
	{
		public final Coord start;
		public int length;
		public final Sprite segment;
		public final Item item;
		public ZSegment(Coord start, int length, Sprite segment, Item item)
		{
			this.start = start;
			this.length = length;
			this.segment = segment;
			this.item = item;
		}
		
		public Entity getEntity(float z, Color c)
		{
			//TODO
			Entity toReturn = new Entity(0,0,0,null);
			
			Entity wire = new VertWireEntity(start.x,start.y,0,
					segment);
			wire.setRotation(Matrix.yToZ());
			wire.setScale(1,1,length/segment.imHeight);
			wire.setColor(c);
			//return wire;
			toReturn.addChild(wire);
			
			Sprite wireEnd = item.getType().getWireEnd().reverseV();
			Entity endEntity = new Entity(start.x-1,start.y+1,-4,wireEnd);
			endEntity.setRotation(Matrix.yToZ());
			endEntity.setTranslateZ(true);
			//return endEntity;
			toReturn.addChild(endEntity);
			return toReturn;
		}

		public Coord getEnd()
		{
			return start;
		}
		public Coord getStart()
		{
			return start;
		}
	}
	public static class HorizWireEntity extends Entity
	{
		public HorizWireEntity(float x, float y, float z, Sprite base)
		{
			super(x, y, z, base);
			setTranslateZ(true);
		}
	}
	public static class VertWireEntity extends Entity
	{
		public VertWireEntity(float x, float y, float z, Sprite base)
		{
			super(x, y, z, base);
			setTranslateZ(true);
		}
	}
	public static class ForwardWireEntity extends Entity
	{
		public ForwardWireEntity(float x, float y, float z, Sprite base)
		{
			super(x, y, z+.01f, base);
			setTranslateZ(true);
		}
	}
}
