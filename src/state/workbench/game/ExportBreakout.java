package state.workbench.game;

import game.item.Item;
import game.item.ItemType;
import game.item.Pin;
import game.item.Wire;
import game.map.Unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExportBreakout implements Serializable
{
	private static final long serialVersionUID = -1849737606643192021L;
	
	public static final int TOP=0;
	public static final int BOTTOM = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	public static final int FRONT = 4;
	public static final int BACK = 5;
	public static final int INTERNAL = 6;
	List<Item> items = new ArrayList<>();
	int location;
	
	public ExportBreakout(ExternalBreakout breakout, int location)
	{
		this.location = location;
		for(int y = 0; y<breakout.grid.getRows(); y++)
		{
			int x=0;
			Item i = breakout.slots[y].getContents();
			if(i!=null)
			{
				ItemType type = i.getType();
				for(;x<type.getNumBreakoutPins(); x++)
				{
					Pin source = breakout.pinGrid[x][y];
					Pin dest = i.getBreakoutPins().get(x);
					if(source.getAttatched()!=null)
					{
						Wire wire = source.getAttatched();
						int position = wire.getPosition(source);
						wire.extractFrom(source);
						wire.setPin(position, dest);
					}
				}
				items.add(i);
			}
		}
	}
	
	public void load(ExternalBreakout breakout)
	{
		for(int y = 0; y<breakout.grid.getRows(); y++)
		{
			int x=0;
			Item i = breakout.slots[y].getContents();
			if(i!=null)
			{
				ItemType type = i.getType();
				for(;x<type.getNumBreakoutPins(); x++)
				{
					Pin source = i.getBreakoutPins().get(x);
					Pin dest = breakout.pinGrid[x][y];
					if(source.getAttatched()!=null)
					{
						Wire wire = source.getAttatched();
						int position = wire.getPosition(source);
						wire.extractFrom(source);
						wire.setPin(position, dest);
					}
				}
			}
		}
	}
	
	public void update(Unit unit)
	{
		for(Item i:items)
		{
			i.updateExported(location,unit);
		}
	}

	public void pinUpdate()
	{
		for(Item i:items)
		{
			i.pinUpdate();
		}
	}

	public void tick(Unit unit)
	{
		for(Item i:items)
		{
			i.updateExported(location, unit);
		}
	}

	public void radioUpdate()
	{
		for(Item i:items)
		{
			i.radioUpdate();
		}
	}
}
