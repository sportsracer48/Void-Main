package game.comm;

import java.io.Serializable;

@FunctionalInterface
public interface RadioHook extends Serializable
{
	public void radioUpdate();
}
