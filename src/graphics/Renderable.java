package graphics;

public interface Renderable extends Comparable<Renderable>
{
	public void render(Context c);
	public float getZ();
	
	public default int compareTo(Renderable r)
	{
		return Float.compare(getZ(), r.getZ());
	}
}
