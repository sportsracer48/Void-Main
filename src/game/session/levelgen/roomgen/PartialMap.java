package game.session.levelgen.roomgen;

import game.map.Map;
import game.map.Tile;
import game.session.levelgen.MapConfig;
import game.session.levelgen.MapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PartialMap
{
	List<Room> rooms = new ArrayList<>();
	List<Door> doors = new ArrayList<>();
	int roomSize;
	int roomNumber = 1;

	public PartialMap(int roomSize)
	{
		this.roomSize = roomSize;
		rooms.add(new Room(0,0,roomSize*2,roomSize*2,0));
	}
	public int getStartX()
	{
		return 1-getLeft();
	}
	public int getStartY()
	{
		return 1-getTop();
	}
	public Map getMap(MapConfig config)
	{
		int left = getLeft();
		int top = getTop();
		int width = getWidth();
		int height = getHeight();
		Tile[][] grid = new Tile[width][height];
		Map map = new Map(grid,getStartX(),getStartY(),config);
		for(int y = 0; y<height; y++)
		{
			for(int x = 0; x<width; x++)
			{
				grid[x][y] = new Tile(x,y,16,9,map);
			}
		}
		for(Room r: rooms)
		{
			for(int x = r.x; x<r.x+r.width; x++)
			{
				grid[x-left][r.y-top].makeWall();
				grid[x-left][r.y+r.height-1-top].makeWall();
			}
			for(int y = r.y; y<r.y+r.height; y++)
			{
				grid[r.x-left][y-top].makeWall();
				grid[r.x+r.width-left-1][y-top].makeWall();
			}
			for(int x = r.x+1; x<r.x+r.width-1; x++)
			{
				for(int y = r.y+1; y<r.y+r.height-1; y++)
				{
					grid[x-left][y-top].makeFloor();
				}
			}
		}
		for(Door d: doors)
		{
			grid[d.x-left][d.y-top].makeFloor();
		}
		return map;
	}
	
	public boolean isFullyConnected()
	{
		int width = getWidth();
		int height = getHeight();
		char[][] test = toGrid();
		char[][] flood = new char[width][height];
		
		for(int y = 0; y<height; y++)
		{
			for(int x = 0; x<width; x++)
			{
				flood[x][y] = test[x][y];
			}
		}
		flood(getStartX(),getStartY(),flood);
		for(int y = 0; y<height; y++)
		{
			for(int x = 0; x<width; x++)
			{
				if(flood[x][y]=='.' || flood[x][y]=='o')
				{
					System.out.println("("+x+","+y+")");
					dump();
					return false;
				}
			}
		}
		return true;
	}
	public char[][] toGrid()
	{
		int left = getLeft();
		int top = getTop();
		int width = getWidth();
		int height = getHeight();
		
		char[][] test = new char[width][height];
		for(int y = 0; y<height; y++)
		{
			for(int x = 0; x<width; x++)
			{
				test[x][y] = ' ';
			}
		}
		for(Room r: rooms)
		{
			for(int x = r.x; x<r.x+r.width; x++)
			{
				test[x-left][r.y-top] = '#';
				test[x-left][r.y+r.height-1-top] = '#';
			}
			for(int y = r.y; y<r.y+r.height; y++)
			{
				test[r.x-left][y-top] = '#';
				test[r.x+r.width-left-1][y-top] = '#';
			}
			for(int x = r.x+1; x<r.x+r.width-1; x++)
			{
				for(int y = r.y+1; y<r.y+r.height-1; y++)
				{
					test[x-left][y-top] = '.';
				}
			}
		}
		for(Door d: doors)
		{
			test[d.x-left][d.y-top] = 'o';
		}
		return test;
	}
	
	public void dump()
	{
		int width = getWidth();
		int height = getHeight();
		
		char[][] test = toGrid();
		char[][] flood = new char[width][height];
		
		for(int y = 0; y<height; y++)
		{
			for(int x = 0; x<width; x++)
			{
				flood[x][y] = test[x][y];
				System.out.print(test[x][y]);
			}
			System.out.println();
		}
		System.out.println();
		flood(getStartX(),getStartY(),flood);
		for(int y = 0; y<height; y++)
		{
			for(int x = 0; x<width; x++)
			{
				System.out.print(flood[x][y]);
			}
			System.out.println();
		}
		System.out.println();
	}
	public void flood(int x, int y, char[][] flood)
	{
		try
		{
			if(flood[x][y] == '.')
			{
				flood[x][y] = '-';
			}
			else if(flood[x][y] == 'o')
			{
				flood[x][y] = 'x';
			}
			else
			{
				return;
			}
			for(int dx = -1; dx<=1;dx++)
			{
				for(int dy = -1; dy<=1; dy++)
				{
					if((dx!=0) ^ (dy!=0))//one but not both are != 0
					{
						flood(x+dx,y+dy,flood);
					}
				}
			}
		}
		catch(Exception e)
		{
			return;
		}
		
		
	}
	public int getLeft()
	{
		return rooms.stream().mapToInt(r->r.x).min().orElse(0);
	}
	public int getRight()
	{
		return rooms.stream().mapToInt(r->r.x+r.width).max().orElse(12);
	}
	public int getTop()
	{
		return rooms.stream().mapToInt(r->r.y).min().orElse(0);
	}
	public int getBot()
	{
		return rooms.stream().mapToInt(r->r.y+r.height).max().orElse(12);
	}
	public int getWidth()
	{
		return getRight()-getLeft();
	}
	public int getHeight()
	{
		return getBot()-getTop();
	}
	public void addNewRoom()
	{
		Door d = makeDoor();
		if(d==null)
		{
			return;
		}
		List<Room> candidates = d.getValidRooms(roomSize,this::validNewRoom,roomNumber);
		if(candidates.size() == 0)
		{
			System.out.println("no candidates");
			return;
		}
		doors.add(d);
		rooms.add(MapUtil.selectRandom(candidates));
		roomNumber++;
	}
	public boolean validNewRoom(Room r)
	{
		return rooms.stream().allMatch(known->!known.collides(r));
	}
	public boolean validNewDoor(Door d)
	{
		return rooms.stream().filter(r -> r.contains(d.x, d.y)).count() == 1 &&
				doors.stream().filter(d2 -> d2.x == d.x && d2.y == d.y).count() == 0 &&
				!d.getValidRooms(roomSize,this::validNewRoom,roomNumber).isEmpty();
	}
	public boolean hasValidDoors()
	{
		return rooms.stream().anyMatch(this::hasValidDoors);
	}
	public boolean hasValidDoors(Room r)
	{
		if(r.knownInvalid)
		{
			return false;
		}
		boolean hasValidDoors = r.getAllDoors().stream().anyMatch(this::validNewDoor);
		if(hasValidDoors)
		{
			return true;
		}
		else
		{
			r.knownInvalid = true;
			return false;
		}
	}
	public Door makeDoor()
	{
		if(!hasValidDoors())
		{
			return null;
		}
		List<Room> validRooms = rooms.stream().filter(this::hasValidDoors).collect(Collectors.toList());
		Room parent = MapUtil.selectRandom(validRooms);
		return makeDoor(parent);
	}
	public Door makeDoor(Room r)
	{
		List<Door> validDoors = r.getAllDoors().stream().filter(this::validNewDoor).collect(Collectors.toList());
		if(validDoors.size() == 0)
		{
			return null;
		}
		return MapUtil.selectRandom(validDoors);
	}
}
