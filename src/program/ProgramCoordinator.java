package program;

import java.util.ArrayList;
import java.util.List;

public class ProgramCoordinator
{
	List<Environment> environments = new ArrayList<>();
	
	public void addEnvironment(Environment e)
	{
		environments.add(e);
	}
	
	public void act(int dt)
	{
		for(Environment e:environments)
		{
			e.act(dt);
		}
	}
	
}
