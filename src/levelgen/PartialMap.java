package levelgen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import state.viewport.MapFactory;
import state.viewport.Tile;

public class PartialMap
{
	List<Room> rooms = new ArrayList<>();
	List<Door> doors = new ArrayList<>();
	ThreadLocalRandom rand = ThreadLocalRandom.current();
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
	
	public int getStartX(Tile[][] map){
		for (int i = 0; i < map.length; i++){
			for (int j = 0; j < map[i].length; j++){
				if (map[i][j].isFloor()) return 1 - i;
			}
		}
		return -1;
	}
	
	public int getStartY(Tile[][] map){
		for (int i = 0; i < map.length; i++){
			for (int j = 0; j < map[i].length; j++){
				if (map[i][j].isFloor()) return 1 - j;
			}
		}
		return -1;
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
	
	public Tile[][] getMapPerfect(MapFactory factory){
		int width = 60;
		int height = 60;
		
		Tile[][] map = new Tile[width][height];
		
		for(int y = 0; y<height; y++)
		{
			for(int x = 0; x<width; x++)
			{
				map[x][y] = new Tile(x,y,16,9);
			}
		}
		
		// Create maze
		createMaze(map, factory);
		// Add rooms
		addRooms(map, factory);
		// Kill dead ends
		killDeadEnds(map, factory);
		// Build walls
		buildWalls(map, factory);
		
		/*
		System.out.println("DUMPING PERFECT MAP");
		
		for(int y = 0; y<height; y++)
		{
			String line = "";
			for(int x = 0; x<width; x++)
			{
				if (map[x][y].isFloor()){
					line += '.';
				}else if (map[x][y].isWall()){
					line += '#';
				}else{
					line += ' ';
				}
			}
			System.out.println(line);
		}*/
		
		return map;
	}
	
	private int randInRange(int max){
		return randInRange(0, max);
	}
	
	private int randInRange(int min, int max){
		float f = rand.nextFloat();
		int range = max - min;
		int offset = (int) ((float) range * f);
		return min + offset;
	}
	
	private void createMaze(Tile[][] map, MapFactory factory){
		divide(map, 0, 0, map.length, map[0].length, factory);
	}
	
	private void divide(Tile[][] map, int x, int y, int width, int height, MapFactory factory){
		if (width <= 4 || height <= 4) return;
		
		boolean isVertical = width >= height;
		
		if (isVertical){
			x++;
			width -= 2;
		}else{
			y++;
	        height -= 2;
		}
		
		int wx = x + (isVertical? randInRange(0, width - 2) : 0);
		int wy = y + (isVertical? 0 : randInRange(0, height - 2));
		
		int dx = isVertical? 0 : 1;
	    int dy = isVertical? 1 : 0;
	    
	    int length = isVertical? height : width;
	    
	    for (int i = 0; i < length; i++){
	        factory.assignFloor(map[wx][wy]);
	        wx += dx;
	        wy += dy;
	    }
	    
	    int nx = isVertical? x - 1: x;
	    int ny = isVertical? y : y - 1;
	    int nw = isVertical? wx - x + 2: width;
	    int nh = isVertical? height : wy - y + 2;
	    
	    divide(map, nx, ny, nw, nh, factory);
	    
	    nx = isVertical? wx + 1 : x;
	    ny = isVertical? y : wy + 1;
	    nw = isVertical? x + width - wx - 0 : width;
	    nh = isVertical? height : y + height - wy - 0;
	    
	    divide(map, nx, ny, nw, nh, factory);
	}
	
	private void addRooms(Tile[][] map, MapFactory factory){
		int MIN_ROOM_SIZE = 6;
		int MAX_ROOM_SIZE = 8;
		int numRooms = 2 + ((map.length * map[0].length) / (MIN_ROOM_SIZE * MAX_ROOM_SIZE)) / 6;
		
		int rmWidth, rmHeight, x, y;
		for (int i = 0; i < numRooms; i++){
	        rmWidth = randInRange(MIN_ROOM_SIZE, MAX_ROOM_SIZE + 1);
	        rmHeight = randInRange(MIN_ROOM_SIZE, MAX_ROOM_SIZE + 1);

	        x = randInRange(1, map.length - rmWidth - 1);
	        y = randInRange(1, map[0].length - rmHeight - 1);

	        //cout << "Room x: " << x << ", y: " << y << endl;

	        for (int j = x; j < x + rmWidth; j++){
	            for (int k = y; k < y + rmHeight; k++){
	                //mainMap[j][k] = FLOOR_CHAR;
	                factory.assignFloor(map[j][k]);
	            }
	        }
	    }
	}

	private void killDeadEnds(Tile[][] map, MapFactory factory){
		boolean flag = true;
	    while(flag){
	        flag = false;
	        for (int x = 0; x < map.length; x++){
	            for (int y = 0; y < map[0].length; y++){
	                if (!map[x][y].isFloor()) continue;
	                if (deadEnd(map, x, y)){
	                    flag = true;
	                    //mainMap[x][y] = BACKGROUND_CHAR;
	                    map[x][y] = new Tile(x,y,16,9);
	                    
	                }
	            }
	        }
	    }
	}
	
	private boolean deadEnd(Tile[][] map, int x, int y){
	    int n = 0;
	    if (x >= map.length - 1 || !map[x + 1][y].isFloor()) n++;
	    if (x <= 0 || !map[x - 1][y].isFloor()) n++;
	    if (y >= map[0].length - 1 || !map[x][y + 1].isFloor()) n++;
	    if (y <= 0 || !map[x][y - 1].isFloor()) n++;

	    return n >= 3;
	}

	private void buildWalls(Tile[][] map, MapFactory factory){
		for (int x = 0; x < map.length; x++){
	        for (int y = 0; y < map[0].length; y++){
	            if (map[x][y].isFloor()) continue;
	            if (touchingFloor(map, x, y)){
	                //mainMap[x][y] = WALL_CHAR;
	            	factory.assignWall(map[x][y]);
	            }
	        }
	    }
	}
	
	private boolean touchingFloor(Tile[][] map, int x, int y){
	    for (int dx = -1; dx <= 1; dx++){
	        for (int dy = -1; dy <= 1; dy++){
	            if (dx == 0 && dy == 0) continue;
	            if (x + dx < 0 || x + dx >= map.length || y + dy < 0 || y + dy >= map[0].length) continue;
	            if (map[x + dx][y + dy].isFloor()) return true;
	        }
	    }

	    return false;
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
				test[x][y] = '_';
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
		
		System.out.println("Width x Height: " + test.length + " x " + test[0].length);
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
		// Gets the furthest left coordinate of all rooms
		return rooms.stream().mapToInt(r->r.x).min().orElse(0);
	}
	public int getRight()
	{
		// Gets the furthest right coordinate of all rooms
		return rooms.stream().mapToInt(r->r.x+r.width).max().orElse(12);
	}
	public int getTop()
	{
		// Gets the lowest top coordinate of all rooms
		return rooms.stream().mapToInt(r->r.y).min().orElse(0);
	}
	public int getBot()
	{
		// Gets the furthest bottom coordinate of all rooms
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
		// Checks if any rooms have valid doors
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
