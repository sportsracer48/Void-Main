package game.session.levelgen.roomgen;

import java.util.ArrayList;
import java.util.List;

public class Room
{
	int x, y, width, height,n;
	boolean knownInvalid = false;
	public Room(int x, int y, int width, int height,int n)
	{
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.n=n;
	}
	public Door getRandomDoor()
	{
		int val = (int)(Math.random()*(width*2+height*2)-7);
		return getDoor(val);
	}
	public boolean collides(Room other)
	{
		if(other.x+other.width<x && other.y+other.height<y || x+width<other.x && y+height<other.y)
		{
			return false;
		}
		return collidesOneWay(other) || other.collidesOneWay(this);
	}
	private boolean collidesOneWay(Room other)
	{
		return contains(other.x+1,other.y+1) || 
				contains(other.x+other.width-2,other.y+1) || 
				contains(other.x+other.width-2,other.y+other.height-2) ||
				contains(other.x+1,other.y+other.height-2)||
				x<other.x && 
				x+width>other.x+other.width && 
				other.y<y && 
				other.y+other.height>y+height;
	}
	public Door getDoor(int val)
	{
		if(val<width-1)
		{
			return new Door(val+x,y);
		}
		val-=width;
		val+=2;
		if(val<height-1)
		{
			return new Door(width-1+x,val+y);
		}
		val-=height;
		val+=2;
		if(val<width-1)
		{
			return new Door(val+x,height-1+y);
		}
		val-=width;
		val+=2;
		if(val<height-1)
		{
			return new Door(x,val+y);
		}
		throw new RuntimeException(val+" is not small enough");
	}
	
	public List<Door> getAllDoors()
	{
		List<Door> result = new ArrayList<>();
		for(int i = 1; i<width*2+height*2-7; i++)
		{
			result.add(getDoor(i));
		}
		return result;
	}
	
	public boolean contains(int x2, int y2)
	{
		return x2>=x && y2>=y && x2<x+width && y2<y+height;
	}
}
