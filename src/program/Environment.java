package program;

import java.io.InputStream;

public interface Environment
{
	public void uplode(String program);
	public void act(int dt);
	public void reset();
	public InputStream getSerialStream();
}
