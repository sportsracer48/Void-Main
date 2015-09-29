package graphics;

import util.MatUtil;

public class Context
{
	Uniform model,view,projection;
	
	public Context(Uniform model, Uniform view, Uniform projection)
	{
		this.model=model;
		this.view=view;
		this.projection=projection;
	}
	
	public void setModel(float[] matrix)
	{
		MatUtil.uniformMatrix(matrix, model);
	}
	
	public void setView(float[] matrix)
	{
		MatUtil.uniformMatrix(matrix, view);
	}
	
	public void setProjection(float[] matrix)
	{
		MatUtil.uniformMatrix(matrix, projection);
	}
}
