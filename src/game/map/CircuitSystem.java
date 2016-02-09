package game.map;

import java.io.Serializable;

public interface CircuitSystem extends Serializable
{
	public void update();
	public void tick();
	public void radioUpdate();
}
