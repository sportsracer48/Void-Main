package action;

import java.util.HashSet;

public class ActList
{
	HashSet<Actable> actableList = new HashSet<>();
	HashSet<Actable> toAdd = new HashSet<>();
	HashSet<Actable> toRemove = new HashSet<>();
	
	public void act(int dt)
	{
		updateList();
		for(Actable a:actableList)
		{
			a.act(dt);
		}
		updateList();
	}
	
	private void updateList()
	{
		actableList.addAll(toAdd);
		actableList.removeAll(toRemove);
		toAdd.clear();
		toRemove.clear();
	}
	
	/**
	 * Request that the list be cleared. Will be processed before and after the each call to act.
	 */
	public void clear()
	{
		toRemove.addAll(actableList);
	}
	
	/**
	 * Request a to be added to the list. Will be processed before and after the each call to act.
	 * @param a
	 */
	public void add(Actable a)
	{
		toAdd.add(a);
	}
	
	/**
	 * Request a to be removed from the list. Will be processed before and after the each call to act.
	 * @param a
	 */
	public void remove(Actable a)
	{
		toRemove.add(a);
	}
}
