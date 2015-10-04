package graphics;

import graphics.shader.Uniform;

import java.util.ArrayList;
import java.util.List;

import math.Matrix;

public class Context
{
	Uniform model,view,projection,st;
	Matrix modelMat;
	
	List<Matrix> stack = new ArrayList<>();
	
	public Context(Uniform model, Uniform view, Uniform projection,Uniform st)
	{
		this.model=model;
		this.view=view;
		this.projection=projection;
		this.st=st;
	}
	
	public void pushTransform()
	{
		stack.add(0, modelMat);
		if(stack.size()>=1000)
		{
			throw new RuntimeException("context stack size exceded.");
		}
	}
	
	public void popTransform()
	{
		try
		{
			modelMat = stack.remove(0);
		}
		catch(IndexOutOfBoundsException e)
		{
			throw new RuntimeException("context stack underflow");
		}
	}
	
	public void appendTransform(Matrix m)
	{
		modelMat = m.dot(modelMat);
	}
	
	public void prependTransform(Matrix m)
	{
		modelMat = modelMat.dot(m);
	}
	
	
	public void setModel(Matrix matrix)
	{
		modelMat = matrix;
		Matrix.uniformMatrix(matrix, model);
	}
	
	public void setView(Matrix matrix)
	{
		Matrix.uniformMatrix(matrix, view);
	}
	
	public void setProjection(Matrix matrix)
	{
		Matrix.uniformMatrix(matrix, projection);
	}

	public void updateModelMatrix()
	{
		setModel(modelMat);
	}
	
	public void setSt(Matrix matrix)
	{
		Matrix.uniformMatrix(matrix, st);
	}
}
