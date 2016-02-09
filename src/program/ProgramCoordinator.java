package program;

import java.io.Serializable;
import java.util.HashSet;

public class ProgramCoordinator implements Serializable
{
	private static final long serialVersionUID = -6175422249147894319L;
	
	HashSet<Environment> environments = new HashSet<>();
	
	public void addEnvironment(Environment e)
	{
		environments.add(e);
	}
	
	public HashSet<Environment> getEnvironments()
	{
		return environments;
	}
	
	public void act(int dt)
	{
		for(Environment e:environments)
		{
			e.act(dt);
		}
	}
	
}
