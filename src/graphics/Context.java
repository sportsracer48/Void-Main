package graphics;

import graphics.shader.Uniform;

import java.util.ArrayList;
import java.util.List;

import util.Color;
import math.Matrix;

public class Context
{
	Uniform model,view,projection,st,color;
	Uniform[] flags;
	Matrix modelMat;
	Matrix viewMat;
	Matrix projectionMat;
	
	Matrix groupColor;
	
	float alpha=1;
	List<Matrix> projections = new ArrayList<Matrix>();
	List<Matrix> stack = new ArrayList<>();
	int projectionIndex = 0;
	
	public Context(Uniform model, Uniform view, Uniform projection,Uniform st, Uniform color, Uniform... flags)
	{
		this.model=model;
		this.view=view;
		this.projection=projection;
		this.st=st;
		this.color = color;
		this.flags = flags;
	}
	
	public void setFlag(int flag, boolean val)
	{
		flags[flag].setBoolean(val);
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
		viewMat = matrix;
		Matrix.uniformMatrix(matrix, view);
	}
	
	public void setProjection(Matrix matrix)
	{
		projectionMat = matrix;
		Matrix.uniformMatrix(matrix, projection);
		if(projections.size()==0)
		{
			projections.add(matrix);
		}
		else
		{
			projections.set(0, matrix);
		}
	}
	public void addProjection(Matrix matrix)
	{
		projections.add(matrix);
	}
	public void setProjection(int index)
	{
		if(index != projectionIndex)
		{
			projectionIndex = index;
			projectionMat = projections.get(projectionIndex);
			Matrix.uniformMatrix(projectionMat, projection);
		}
	}
	
	public Matrix testProjection(Matrix m)
	{
		return projectionMat.dot(viewMat.dot(modelMat.dot(m)));
	}
	
	public Matrix testView(Matrix m)
	{
		return viewMat.dot(modelMat.dot(m));
	}

	public void updateModelMatrix()
	{
		setModel(modelMat);
	}
	
	public void setSt(Matrix matrix)
	{
		Matrix.uniformMatrix(matrix, st);
	}
	
	public void setGroupColor(Matrix color)
	{
		this.groupColor = color;
	}
	public void setColor(Matrix color)
	{
		if(groupColor == null)
		{
			Matrix.uniformVector(color, this.color);
		}
		else
		{
			Matrix.uniformVector(groupColor.compMult(color), this.color);
		}
	}
	public void resetColor()
	{
		if(groupColor == null)
		{
			Matrix.uniformVector(Color.white, this.color);
		}
		else
		{
			Matrix.uniformVector(groupColor, this.color);
		}
	}
	public boolean hasGroupColor()
	{
		return this.groupColor != null;
	}
	public void resetGroupColor()
	{
		this.groupColor = null;
	}

	public float getAlpha()
	{
		return alpha;
	}

	public void setAlpha(float alpha)
	{
		this.alpha = alpha;
	}

	public Matrix getModel()
	{
		return modelMat;
	}

	public void setColor(java.awt.Color gray)
	{
		setColor(new Color(gray.getRGB()));
	}
}
