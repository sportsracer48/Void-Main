package game.comm;

import game.map.unit.Unit;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;

public class SensorManager implements Serializable
{
	private static final long serialVersionUID = -988364273633398498L;
	
	HashMap<Long,Unit> sensorPointers = new HashMap<>();
	Random r = new Random();
	public long getUnique()
	{
		long l = r.nextLong();
		while(sensorPointers.containsKey(l))
		{
			l = r.nextLong();
		}
		return l;
	}
	public void registerUnit(long pointer, Unit unit)
	{
		sensorPointers.put(pointer, unit);
	}
	public void unregisterUnit(long pointer, Unit unit)
	{
		sensorPointers.remove(pointer, unit);
	}
	public Unit getUnit(long pointer)
	{
		return sensorPointers.get(pointer);
	}
	public long registerUnit(Unit unit)
	{
		long key = getUnique();
		registerUnit(key,unit);
		return key;
	}
}
