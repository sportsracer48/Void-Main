package game.item;

import java.io.Serializable;

public class Inventory implements Serializable
{
	private static final long serialVersionUID = -6931384324082390980L;
	
	int size;
	Item[] items;
	
	public Inventory(int size)
	{
		this.items = new Item[size];
	}
	public int getSize()
	{
		return size;
	}
	public Item getItem(int index)
	{
		return items[index];
	}
	public void setItem(int index, Item item)
	{
		items[index] = item;
	}
	public boolean hasSpace()
	{
		for(int i = 0; i<items.length; i++)
		{
			if(items[i] == null)
			{
				return true;
			}
		}
		return false;
	}
	public int getSpace()
	{
		int space = 0;
		for(int i = 0; i<items.length; i++)
		{
			if(items[i] == null)
			{
				space++;
			}
		}
		return space;
	}
	public boolean addItem(Item toAdd)
	{
		for(int i = 0; i<items.length; i++)
		{
			if(items[i] == null)
			{
				items[i] = toAdd;
				return true;
			}
		}
		return false;
	}
	public boolean addItem(Item toAdd, int i)
	{
		if(items[i] == null)
		{
			items[i] = toAdd;
			return true;
		}
		return false;
	}
	public boolean removeItem(Item toRemove)
	{
		for(int i = 0; i<items.length; i++)
		{
			if(items[i] == toRemove)
			{
				items[i] = null;
				return true;
			}
		}
		return false;
	}
	public boolean removeItem(int i)
	{
		if(items[i] == null)
		{
			return false;
		}
		items[i] = null;
		return true;
	}
}
