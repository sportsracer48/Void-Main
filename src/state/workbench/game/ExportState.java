package state.workbench.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import game.item.Item;
import game.item.ItemTypes;
import game.map.Unit;
import static state.workbench.game.ExportBreakout.*;

public class ExportState implements Serializable
{
	private static final long serialVersionUID = 1693930215766367848L;

	List<Item> items = new ArrayList<>();
	
	ExportBreakout top;
	ExportBreakout bot;
	ExportBreakout left;
	ExportBreakout right;
	ExportBreakout front;
	ExportBreakout back;
	ExportBreakout[] allBreakouts;
	
	SavedState save;
	
	public ExportState(Item[][] items, BreakoutItems breakouts,EditHistory history)
	{
		for(int x = 0; x<items.length; x++)
		{
			for(int y = 0; y<items[x].length; y++)
			{
				if(items[x][y] != null && items[x][y].getType() != ItemTypes.breakout && !this.items.contains(items[x][y]))
				{
					this.items.add(items[x][y]);
				}
			}
		}
		top   = new ExportBreakout(breakouts.top,TOP);
		bot   = new ExportBreakout(breakouts.bot,BOTTOM);
		left  = new ExportBreakout(breakouts.left,LEFT);
		right = new ExportBreakout(breakouts.right,RIGHT);
		front = new ExportBreakout(breakouts.front,FRONT);
		back  = new ExportBreakout(breakouts.back,BACK);
		allBreakouts = new ExportBreakout[]{top,bot,left,right,front,back};
		this.save = history.copyState();
	}
	
	public void revertBreakouts(BreakoutItems breakouts)
	{
		top  .load(breakouts.top  );
		bot  .load(breakouts.bot  );
		left .load(breakouts.left );
		right.load(breakouts.right);
		front.load(breakouts.front);
		back .load(breakouts.back );
	}
	
	public List<Item> getItems()
	{
		return items;
	}
	
	public SavedState getSave()
	{
		return save;
	}
	
	public void update()
	{
		for(Item i:items)
		{
			i.pinUpdate();
		}
		for(ExportBreakout breakout:allBreakouts)
		{
			breakout.pinUpdate();
		}
	}
	public void tick(Unit unit)
	{
		for(Item i:items)
		{
			i.updateExported(INTERNAL, unit);
		}
		for(ExportBreakout breakout:allBreakouts)
		{
			breakout.tick(unit);
		}
	}

	public void radioUpdate()
	{
		for(Item i:items)
		{
			i.radioUpdate();
		}
		for(ExportBreakout breakout:allBreakouts)
		{
			breakout.radioUpdate();
		}
	}
}
