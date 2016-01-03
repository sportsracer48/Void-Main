package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

public class LinkerThread
{
	public LinkerThread(Supplier<Boolean> running, List<String> output, BufferedReader input)
	{
		Thread linkerThread = new Thread(new Runnable()
		{
			public void run()
			{
				while(running.get())
				{
					try
					{
						String line = input.readLine();
						if(line == null)
						{
							break;
						}
						output.add(line);
					} 
					catch (IOException e)
					{
						break;
					}
				}
				try
				{
					input.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
		linkerThread.start();
	}
}
