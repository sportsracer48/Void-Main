package graphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderlist
{
	List<Renderable> renderList = new ArrayList<>();
	
	public void add(Renderable r)
	{
		renderList.add(r);
	}
	
	public void remove(Renderable r)
	{
		renderList.remove(r);
	}
	
	public void render(Context c)
	{
		Collections.sort(renderList);
		
		for(Renderable r: renderList)
		{
			r.render(c);
		}
	}
}
