package state.workbench.game;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import game.item.Item;
import game.item.Pin;
import game.item.Wire;

public class SavedState
{
	HashSet<Wire> wires = new HashSet<>();
	Item[][] savedItems;
	Item[][] savedBreakoutItems;
	public SavedState(Item[][] items, Wire currentWire, List<ExternalBreakout> breakouts)
	{
		this.savedItems = items;
		this.savedBreakoutItems = new Item[breakouts.size()][4];
		{
		int i = 0;
		for(ExternalBreakout b: breakouts)
		{
			for(int j = 0; j<4; j++)
			{
				savedBreakoutItems[i][j] = b.getSlots()[j].getContents();
			}
			i++;
		}
		}
		for(Item[] col:items)
		{
			for(Item i:col)
			{
				if(i==null)
				{
					continue;
				}
				for(Pin p:i.getPins())
				{
					if(p.getAttatched()!=null)
					{
						wires.add(p.getAttatched().clone());
					}
				}
			}
		}
	}
	public void load(Item[][] output, WiringMode wireEditor, List<ExternalBreakout> breakouts)
	{
		HashSet<Item> oldItems = new HashSet<>();
		HashSet<Item> newItems = new HashSet<>();
		for(int x = 0; x<output.length; x++)
		{
			for(int y = 0; y<output[x].length; y++)
			{
				if(output[x][y]!=null)
				{
					oldItems.add(output[x][y]);
				}
				output[x][y] = savedItems[x][y];
				if(output[x][y]!=null)
				{
					output[x][y].stripPins();
					newItems.add(output[x][y]);
				}
			}
		}
		{
		int i = 0;
		for(ExternalBreakout b: breakouts)
		{
		for(int j = 0; j<4; j++)
		{
			if(b.getSlots()[j].getContents()!=null)
			{
				oldItems.add(b.getSlots()[j].getContents());
			}
			b.getSlots()[j].setContents(savedBreakoutItems[i][j]);
			if(savedBreakoutItems[i][j] != null)
			{
				newItems.add(savedBreakoutItems[i][j]);
			}
		}
		i++;
		}
		}
		HashSet<Item> dupedItems = new HashSet<>();
		HashSet<Item> deletedItems = new HashSet<>();
		dupedItems.addAll(newItems);
		dupedItems.removeAll(oldItems);
		
		deletedItems.addAll(oldItems);
		deletedItems.removeAll(newItems);
		
		for(Item i:deletedItems)
		{
			System.out.format("deleted %s%n",i);
		}
		for(Item i:dupedItems)
		{
			System.out.format("duped %s%n",i);
		}
		for(Wire w:wires)
		{
			w.clone().attachSelf();
		}
	}
	public boolean equals(Object o)
	{
		if(o instanceof SavedState)
		{
			SavedState s = (SavedState)o;
			return Arrays.deepEquals(savedItems,s.savedItems)
					&& Arrays.deepEquals(savedBreakoutItems, s.savedBreakoutItems)
					&& wires.equals(s.wires);

		}
		return false;
	}
}
