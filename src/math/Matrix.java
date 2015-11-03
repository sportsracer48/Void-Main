package math;

import graphics.shader.Uniform;

import java.nio.FloatBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;

public class Matrix
{
	static FloatBuffer tempMatrix = BufferUtils.createFloatBuffer(16);
	static FloatBuffer tempVector = BufferUtils.createFloatBuffer(4);
	
	
	private final float[] mat;
	public final int cols;
	public final int rows;
	
	static Matrix I4 = _identity(4);
	
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
	
	public Matrix scale(float f)
	{
		Matrix out = new Matrix(rows,cols);
		for(int row = 0; row<out.rows; row++)
		{
			for(int col = 0; col<out.cols; col++)
			{
				out.set(row, col, get(row,col)*f);
			}
		}
		return out;
	}
	
	public Matrix subMat(int pivotRow, int pivotCol)
	{
		Matrix toReturn = new Matrix(rows-1,cols-1);
		for(int row = 0,outRow = 0; row<rows; row++)
		{
			if(row == pivotRow)
			{
				continue;
			}
			for(int col = 0,outCol=0; col<cols; col++)
			{
				if(col == pivotCol)
				{
					continue;
				}
				toReturn.set(outRow, outCol, get(row,col));
				outCol++;
			}
			outRow++;
		}
		return toReturn;
	}
	
	public float det()
	{
		if(rows == 1 && cols == 1)
		{
			return mat[0];
		}
		float sum = 0;
		for(int col = 0; col<cols; col++)
		{
			sum += get(0,col)*cofactor(0,col);
		}
		return sum;
	}
	
	public float minor(int row, int col)
	{
		return subMat(row,col).det();
	}
	
	public float cofactor(int row, int col)
	{
		return minor(row,col)*(((col+row)%2)*-2+1);
	}
	
	public Matrix cofactor()
	{
		Matrix out = new Matrix(rows,cols);
		for(int row = 0; row<out.rows; row++)
		{
			for(int col = 0; col<out.cols; col++)
			{
				out.set(row, col, cofactor(row,col));
			}
		}
		return out;
	}
	
	public Matrix transpose()
	{
		Matrix out = new Matrix(cols,rows);
		for(int row = 0; row<out.rows; row++)
		{
			for(int col = 0; col<out.cols; col++)
			{
				out.set(row, col, get(col,row));
			}
		}
		return out;
	}
	
	public Matrix adjoint()
	{
		return cofactor().transpose();
	}
	
	public Matrix inverse()
	{
		float det = det();
		if(det == 0)
		{
			throw new RuntimeException("Illegal matrix");
		}
		return adjoint().scale(1/det());
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
	
	public float x()
	{
		return mat[0];
	}
	public float y()
	{
		return mat[1];
	}
	public float z()
	{
		return mat[2];
	}
	public float w()
	{
		return mat[3];
	}
	public float r()
	{
		return mat[0];
	}
	public float g()
	{
		return mat[1];
	}
	public float b()
	{
		return mat[2];
	}
	public float a()
	{
		return mat[3];
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof Matrix)
		{
			Matrix m = (Matrix)o;
			return rows==m.rows && cols == m.cols && Arrays.equals(mat, m.mat);
		}
		return false;
	}
	
	public static Matrix identity(int size)
	{
		if(size == 4)
		{
			return I4;
		}
		return _identity(size);
	}
	
	private static Matrix _identity(int size)
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
	
	public static Matrix yToZ()
	{
		float[] mat = {
				1,0,0,0,
				0,0,0,0,
				0,1,0,0,
				0,0,0,1
		};
		return new Matrix(mat,4);
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
	public static Matrix rpgOrtho(float left, float right, float bottom, float top, float near, float far)
	{
		Matrix ortho = gluOrtho(left,right,bottom,top,near,far);
		float[] mat = {
				1,0,0,0,
				0,1,-1,0,
				0,1,1,0,
				0,0,0,1,
		};
		Matrix rpg = new Matrix(mat,4);
		return ortho.dot(rpg);
	}
	
	public static void uniformMatrix(Matrix matrix, Uniform location)
	{
		tempMatrix.clear();
		tempMatrix.put(matrix.mat);
		tempMatrix.flip();
		
		location.setMat4(tempMatrix);
	}
	
	public static void uniformVector(Matrix vector, Uniform location)
	{
		tempVector.clear();
		tempVector.put(vector.mat);
		tempVector.flip();
		
		location.setVec4(tempVector);
	}
}
