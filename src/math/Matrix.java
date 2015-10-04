package math;

import graphics.shader.Uniform;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Matrix
{
	static FloatBuffer tempMatrix = BufferUtils.createFloatBuffer(16);
	
	
	public float[] mat;
	int cols;
	int rows;
	
	public Matrix(int rows, int cols)
	{
		this.rows = rows;
		this.cols = cols;
		mat = new float[cols*rows];
	}
	
	public Matrix(float[] data, int rows)
	{
		this.rows = rows;
		this.cols = data.length/rows;
		mat = data;
	}
	
	/**
	 * Constructs a column vector
	 * @param data
	 */
	public Matrix(float[] data)
	{
		this(data,data.length);
	}
	
	public Matrix dot(Matrix m)
	{
		if(m.rows != cols)
		{
			throw new RuntimeException("illegal matricies");
		}
		Matrix out = new Matrix(rows,m.cols);
		
		for(int row = 0; row<out.rows; row++)
		{
			for(int col = 0; col<out.cols; col++)
			{
				float dotProduct = 0;
				for(int i=0; i<cols;i++)
				{
					dotProduct += get(row,i)*m.get(i, col);
				}
				out.set(row, col, dotProduct);
			}
		}
		return out;
	}
	
	public Matrix add(Matrix m)
	{
		if(m.rows != rows || m.cols != cols)
		{
			throw new RuntimeException("illegal matricies");
		}
		Matrix out = new Matrix(rows, cols);
		for(int row = 0; row<out.rows; row++)
		{
			for(int col = 0; col<out.cols; col++)
			{
				out.set(row, col, get(row,col)+m.get(row, col));
			}
		}
		return out;
	}
	
	public Matrix subtract(Matrix m)
	{
		if(m.rows != rows || m.cols != cols)
		{
			throw new RuntimeException("illegal matricies");
		}
		Matrix out = new Matrix(rows, cols);
		for(int row = 0; row<out.rows; row++)
		{
			for(int col = 0; col<out.cols; col++)
			{
				out.set(row, col, get(row,col)-m.get(row, col));
			}
		}
		return out;
	}
	
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		
		for(int row = 0; row<rows; row++)
		{
			for(int col = 0; col<cols; col++)
			{
				out.append(String.format("%.4f ", get(row,col)));
			}
			out.append('\n');
		}
		
		return out.toString();
	}
	
	public Matrix rotate(double degrees)
	{
		return rotationd(degrees).dot(this);
	}
	
	public Matrix scale(float... scales)
	{
		return scaling(scales).dot(this);
	}
	
	public Matrix translate(float... offsets)
	{
		return translation(offsets).dot(this);
	}
	
	public float get(int row, int col)
	{
		return mat[row*cols+col];
	}
	
	private void set(int row, int col, float val)
	{
		mat[row*cols+col] = val;
	}
	
	public static Matrix identity(int size)
	{
		Matrix out = new Matrix(size,size);
		for(int i = 0; i<size; i++)
		{
			out.set(i, i, 1);
		}
		return out;
	}
	
	public static Matrix translation(float...offsets)
	{
		int size = offsets.length;
		Matrix out = new Matrix(size+1,size+1);
		for(int i = 0; i<size; i++)
		{
			out.set(i, i, 1);
			out.set(i, size, offsets[i]);
		}
		out.set(size, size, 1);
		return out;
	}
	
	public static Matrix rotation(double radians)
	{
		float[] mat = {
			(float) Math.cos(radians),(float) -Math.sin(radians),0,0,
			(float) Math.sin(radians),(float) Math.cos(radians) ,0,0,
			0,                        0,                         1,0,
			0,                        0,                         0,1
		};
		return new Matrix(mat, 4);
	}
	
	public static Matrix rotationd(double degrees)
	{
		return rotation(Math.toRadians(degrees));
	}
	
	public static Matrix scaling(float...scales)
	{
		int size = scales.length;
		Matrix out = new Matrix(size+1,size+1);
		for(int i = 0; i<size; i++)
		{
			out.set(i, i, scales[i]);
		}
		out.set(size, size, 1);
		return out;
	}
	
	public static Matrix gluOrtho(float left, float right, float bottom, float top, float near, float far)
	{
		float[] mat = {
				2f/(right-left),	0,				0,				-(right+left)/(right-left),
				0,				2f/(top-bottom),	0,				-(top+bottom)/(top-bottom),
				0,				0,				    -2f/(far-near),	-(far+near)/(far-near),
				0,				0,					0,				1f
		};
		return new Matrix(mat,4);
	}
	
	public static void uniformMatrix(Matrix matrix, Uniform location)
	{
		tempMatrix.clear();
		tempMatrix.put(matrix.mat);
		tempMatrix.flip();
		
		location.setMat4(tempMatrix);
	}
}
