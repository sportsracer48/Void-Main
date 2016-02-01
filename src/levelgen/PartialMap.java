package levelgen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import state.viewport.MapFactory;
import state.viewport.Tile;

public class PartialMap
{
	List<Room> rooms = new ArrayList<>();
	List<Door> doors = new ArrayList<>();
	int roomSize = 6;
	public PartialMap()
	{
		rooms.add(new Room(0,0,12,12));
	}
	public int getStartX()
	{
		return 1-getLeft();
	}
	public int getStartY()
	{
		return 1-getTop();
	}
	public Tile[][] getMap(MapFactory factory)
	{
		int left = getLeft();
		int top = getTop();
		int width = getWidth();
		int height = getHeight();
		Tile[][] map = new Tile[width][height];
		for(int y = 0; y<height; y++)
		{
			for(int x = 0; x<width; x++)
			{
				map[x][y] = new Tile(x,y,16,9);
			}
		}
		for(Room r: rooms)
		{
			for(int x = r.x; x<r.x+r.width; x++)
			{
				factory.assignWall(map[x-left][r.y-top]);
				factory.assignWall(map[x-left][r.y+r.height-1-top]);
			}
			for(int y = r.y; y<r.y+r.height; y++)
			{
				factory.assignWall(map[r.x-left][y-top]);
				factory.assignWall(map[r.x+r.width-left-1][y-top]);
			}
			for(int x = r.x+1; x<r.x+r.width-1; x++)
			{
				for(int y = r.y+1; y<r.y+r.height-1; y++)
				{
					factory.assignFloor(map[x-left][y-top]);
				}
			}
		}
		for(Door d: doors)
		{
			factory.assignFloor(map[d.x-left][d.y-top]);
		}
		return map;
	}
	public void dump()
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
			test[d.x-left][d.y-top] = '.';
		}
		for(int y = 0; y<height; y++)
		{
			for(int x = 0; x<width; x++)
			{
				System.out.print(test[x][y]);
			}
			System.out.println();
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
		List<Room> candidates = d.getValidRooms(roomSize,this::validNewRoom);
		if(candidates.size() == 0)
		{
			System.out.println("no candidates");
			return;
		}
		doors.add(d);
		rooms.add(MapFactory.selectRandom(candidates));
	}
	public boolean validNewRoom(Room r)
	{
		return rooms.stream().allMatch(known->!known.collides(r));
	}
	public boolean validNewDoor(Door d)
	{
		return rooms.stream().filter(r -> r.contains(d.x, d.y)).count() == 1 &&
				doors.stream().filter(d2 -> d2.x == d.x && d2.y == d.y).count() == 0 &&
				!d.getValidRooms(roomSize,this::validNewRoom).isEmpty();
	}
	public boolean hasValidDoors()
	{
		return rooms.stream().anyMatch(this::hasValidDoors);
	}
	public boolean hasValidDoors(Room r)
	{
		return r.getAllDoors().stream().anyMatch(this::validNewDoor);
	}
	public Door makeDoor()
	{
		if(!hasValidDoors())
		{
			return null;
		}
		List<Room> validRooms = rooms.stream().filter(this::hasValidDoors).collect(Collectors.toList());
		Room parent = MapFactory.selectRandom(validRooms);
		return makeDoor(parent);
	}
	public Door makeDoor(Room r)
	{
		List<Door> validDoors = r.getAllDoors().stream().filter(this::validNewDoor).collect(Collectors.toList());
		if(validDoors.size() == 0)
		{
			return null;
		}
		return MapFactory.selectRandom(validDoors);
	}
}
