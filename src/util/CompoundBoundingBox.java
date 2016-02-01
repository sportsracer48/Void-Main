package util;

import java.util.function.Function;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class CompoundBoundingBox implements BoundingInterface
{
	float scale;
	boolean[][] isWall;
	BoundingBox[][] boxes;
	float xOffset,yOffset;
	
	public CompoundBoundingBox(float scale, boolean[][] grid)
	{
		this(scale,0,0,grid);
	}
	public CompoundBoundingBox(float scale, float xOffset, float yOffset, boolean[][] grid)
	{
		this.scale = scale;
		this.isWall = grid;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		initBoxes();
	}
	public <T> CompoundBoundingBox(float scale, T[][] grid, Function<T,Boolean> mapper)
	{
		this(scale,grid,0,0,mapper);
	}
	public <T> CompoundBoundingBox(float scale, T[][] grid,float xOffset, float yOffset, Function<T,Boolean> mapper)
	{
		this.isWall = new boolean[grid.length][];
		for(int x = 0; x<grid.length; x++)
		{
			this.isWall[x] = new boolean[grid[x].length];
			for(int y = 0; y<grid[x].length; y++)
			{
				this.isWall[x][y] = mapper.apply(grid[x][y]);
			}
		}
		this.scale = scale;
		initBoxes();
	}
	public CompoundBoundingBox(CompoundBoundingBox toCopy, float xOffset, float yOffset)
	{
		this.scale = toCopy.scale;
		this.isWall = toCopy.isWall;
		this.boxes = toCopy.boxes;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	private void initBoxes()
	{
		this.boxes = new BoundingBox[isWall.length][];
		for(int x = 0; x<isWall.length; x++)
		{
			this.boxes[x] = new BoundingBox[isWall[x].length];
			for(int y = 0; y<isWall[x].length; y++)
			{
				initBox(x,y);
			}
		}
	}
	private void initBox(int x, int y)
	{
		boolean westWall = x == 0 || isWall[x-1][y];
		boolean eastWall = x == isWall.length-1 || isWall[x+1][y];
		boolean northWall = y == 0 || isWall[x][y-1];
		boolean southWall = y == isWall[x].length-1 || isWall[x][y+1];
		this.boxes[x][y] = new BoundingBox(
				westWall?x*scale:Float.NaN,
				eastWall?(x+.999f)*scale:Float.NaN,
				northWall?y*scale:Float.NaN,
				southWall?(y+.999f)*scale:Float.NaN,
				0,
				Float.NaN
				);
	}
	public int gridX(float x)
	{
		return (int)((x+xOffset)/scale);
	}
	public int gridY(float y)
	{
		return (int)((y+yOffset)/scale);
	}
	public BoundingBox getBox(float x, float y)
	{
		try
		{
			return boxes[gridX(x)][gridY(y)];
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			return null;
		}
	}

	public boolean inBoundsZ(float z)
	{
		return z>=0;
	}
	public boolean inBounds(float x, float y, float z)
	{
		BoundingBox b = getBox(x,y);
		if(z<0)
		{
			return false;
		}
		if(b==null)
		{
			return false;
		}
		int gridX = gridX(x);
		int gridY = gridY(y);
		if(isWall[gridX][gridY])
		{
			return false;
		}
		return true;
	}
	public boolean onBoundZ(float z)
	{
		return z==0;
	}
	public boolean onBound(float x, float y, float z)
	{
		throw new NotImplementedException();
	}
	public float[] constrain(float x, float y, float z)
	{
		throw new NotImplementedException();
	}
	public boolean leftBounds(float x0, float y0, float z0, float x1, float y1, float z1)
	{
		return inBounds(x0,y0,z0) && !inBounds(x1,y1,z1);
	}
	public float[] constrain(float x0, float y0, float z0, float x1, float y1,float z1)
	{
		float[] result = getBox(x0,y0).constrain(x1+xOffset, y1+yOffset, z1);
		result[0]-=xOffset;
		result[1]-=yOffset;
		return result;
	}
}
