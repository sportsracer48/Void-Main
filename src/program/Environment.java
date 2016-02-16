package program;

import game.map.unit.UnitController;

import java.io.InputStream;
import java.io.Serializable;

public interface Environment extends Serializable
{
	public void uplode(String program);
	public void act(int dt);
	public void reset();
	public InputStream getSerialStream();
	public void setUnitController(UnitController controller);
	public UnitController getUnitController();
	public void logicUpdate();
}