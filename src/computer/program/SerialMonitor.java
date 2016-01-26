package computer.program;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import state.programming.AppendOnlyBuffer;
import computer.system.Computer;

public class SerialMonitor implements InteractiveExecutable
{
	OutputStream out;
	Computer system;
	byte[] buf = new byte[1024];
	
	public void setup(String[] args, AppendOnlyBuffer out, Computer system)
	{
		this.out = out.getOuputStream();
		this.system = system;
	}

	public void acceptCommand(String command)
	{
		//do nothing
	}

	public void act(int dt)
	{
		if(system.hasConnection())
		{
			InputStream in = system.getConnected().getSerialStream();
			try
			{
				int available;
				while((available = in.available()) > 0)
				{
					int read = in.read(buf,0,Math.min(available,buf.length));
					if(read>0)
					{
						out.write(buf, 0, read);
					}
				}
			} 
			catch (IOException e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	public boolean isRunning()
	{
		return true;
	}

	public void stop()
	{
		try
		{
			out.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
