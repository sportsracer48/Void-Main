package state.workbench.graphics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	public WirePath(Coord start, Coord end,Color wireColor, float zStart, float[][] z, Sprite segmentX,Sprite segmentY,Sprite segmentZ)
	{
		this.segmentX = segmentX;
		this.segmentY = segmentY;
		this.segmentZ = segmentZ;
		this.wireColor = wireColor;
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
			initH(start,end,zStart,z);
		}
		else
		{
			initV(start,end,zStart,z);
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
		segments.add(new ZSegment(start,(int)zCoord,segmentZ));
		segments.add(new ZSegment(end,(int)zCoord,segmentZ));
	}
	
	private void initV(Coord start, Coord end, float zStart, float[][] z)
	{
		int midX = (start.x+end.x)/2;
		addSegment(start,new Coord(midX,start.y));
		addSegment(new Coord(midX,start.y),new Coord(midX,end.y));
		addSegment(new Coord(midX,end.y),end);
	}
	
	private void initH(Coord start, Coord end, float zStart, float[][] z)
	{
		int midY = (start.y+end.y)/2;
		addSegment(start,new Coord(start.x,midY));
		addSegment(new Coord(start.x,midY),new Coord(end.x,midY));
		addSegment(new Coord(end.x,midY),end);
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
			Entity segmentEntity = s.getEntity(zCoord);
			segmentEntity.setColor(wireColor);
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
		public abstract Entity getEntity(float z);
		public abstract Coord getEnd();
		public abstract Coord getStart();
		public abstract void retract();
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
		public Entity getEntity(float z)
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
			Entity toReturn = new Entity(left.x,left.y-z,z+zOffset,segment);
			toReturn.setScale(size,1);
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
		public void retract()
		{
			length -= Math.signum(length);
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
		public Entity getEntity(float z)
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
			Entity toReturn = new Entity(top.x,top.y-z,z+zOffset+.1f,segment);
			toReturn.setScale(1,size);
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
		public void retract()
		{
			length -= Math.signum(length);
		}
	}
	public class ZSegment extends Segment
	{
		public final Coord start;
		public int length;
		public final Sprite segment;
		public ZSegment(Coord start, int length, Sprite segment)
		{
			this.start = start;
			this.length = length;
			this.segment = segment;
		}
		
		public Entity getEntity(float z)
		{
			Entity toReturn = new Entity(start.x,start.y-length/segment.imHeight,start.y,segment);
			toReturn.setScale(1, length/segment.imHeight);
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

		public void retract()
		{
			length -= Math.signum(length);
		}
	}
}
