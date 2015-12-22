package util;

import java.util.function.BooleanSupplier;

public class Condition
{
	BooleanSupplier value;
	public Condition(BooleanSupplier value)
	{
		this.value = value;
	}
	
	public void update()
	{
		if(value.getAsBoolean())
		{
			synchronized(this)
			{
				notifyAll();
			}
		}
	}
	
	public void waitUntilTrue()
	{
		while(!value.getAsBoolean())
		{
			synchronized(this)
			{
				try
				{
					wait();
				}
				catch (InterruptedException e)
				{
					Thread.currentThread().interrupt();
				}
			}
		}
	}
}
