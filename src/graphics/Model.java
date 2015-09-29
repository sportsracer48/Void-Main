package graphics;

public interface Model
{
	public void addPositions(VertexBufferBuilder builder);
	public int bindIndicies();
	public int nVerticies();
}
