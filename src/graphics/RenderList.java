package graphics;

import graphics.entity.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RenderList implements Iterable<Entity>
{
	List<Entity> renderList = new ArrayList<>();
	List<Entity> toAdd = new ArrayList<>();
	List<Entity> toRemove = new ArrayList<>();
	boolean zSorted;
	
	public RenderList()
	{
		this(true);
	}
	public RenderList(boolean zSorted)
	{
		this.zSorted = zSorted;
	}
	
	public void add(Entity r)
	{
		toAdd.add(r);
	}
	
	public void remove(Entity r)
	{
		r.setEnabled(false);
		toRemove.add(r);
	}
	
	public void update()
	{
		renderList.removeAll(toRemove);
		toRemove.clear();
		renderList.addAll(toAdd);
		toAdd.clear();
	}
	
	public void floatToTop(Entity r)
	{
		renderList.remove(r);
		renderList.add(r);
	}
	
	public List<Entity> getList()
	{
		if(zSorted)
		{
			Collections.sort(renderList);
		}
		return renderList;
	}
	
	public void render(Context c)
	{
		if(zSorted)
		{
			Collections.sort(renderList);
		}
		
		for(Entity r: renderList)
		{
			r.render(c);
		}
	}
	public Iterator<Entity> iterator()
	{
		return renderList.iterator();
	}
}
