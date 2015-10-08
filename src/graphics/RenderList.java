package graphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RenderList
{
	List<Renderable> renderList = new ArrayList<>();
	boolean zSorted;
	
	public RenderList()
	{
		this(true);
	}
	public RenderList(boolean zSorted)
	{
		this.zSorted = zSorted;
	}
	
	public void add(Renderable r)
	{
		renderList.add(r);
	}
	
	public void remove(Renderable r)
	{
		renderList.remove(r);
	}
	
	public void floatToTop(Renderable r)
	{
		renderList.remove(r);
		renderList.add(r);
	}
	
	public List<Renderable> getList()
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
		
		for(Renderable r: renderList)
		{
			r.render(c);
		}
	}
}
