package program;

public interface Environment extends Runnable
{
	public void uplode(String program);
	public void act(int dt);
	public void start();
	public void reset();
}
