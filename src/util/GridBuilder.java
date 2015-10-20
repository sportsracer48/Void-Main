package util;

import java.util.function.BiConsumer;

public class GridBuilder
{
	final float x,y,xStep,yStep;
	final int rows,cols;
	
	public GridBuilder(float x, float y, float xStep, float yStep, int rows, int cols)
	{
		this.x = x;
		this.y = y;
		this.xStep = xStep;
		this.yStep = yStep;
		this.rows = rows;
		this.cols = cols;
	}
	
	public void forEach(BiConsumer<Float,Float> consumer)
	{
		for(int row = 0; row<rows; row++)
		{
			for(int col = 0; col<cols; col++)
			{
				consumer.accept(x+xStep*col, y+yStep*row);
			}
		}
	}
	
	public void forEachWithIndicies(QuadConsumer<Float,Float,Integer,Integer> consumer)
	{
		for(int row = 0; row<rows; row++)
		{
			for(int col = 0; col<cols; col++)
			{
				consumer.accept(x+xStep*col, y+yStep*row,col,row);
			}
		}
	}
	
	@FunctionalInterface
	public static interface QuadConsumer<T1, T2, T3, T4>
	{
		public void accept(T1 a, T2 b, T3 c, T4 d);
	}
	
	public float getX(int x)
	{
		return this.x+xStep*x;
	}
	
	public float getY(int y)
	{
		return this.y+yStep*y;
	}
	
	public static class Coord
	{
		public final int x, y;
		public Coord(int x, int y)
		{
			this.x=x;
			this.y=y;
		}
	}
}
