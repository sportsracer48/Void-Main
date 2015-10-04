package graphics.geom;

import graphics.registry.VertexArrayBuilder;

public interface Model
{
	public void addPositions(VertexArrayBuilder builder);
	public int bindIndicies();
	public int nVerticies();
	public int nIndicies();
}
