package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Pipe
{
	byte[] buffer;
	int readPtr=0;
	int writePtr=0;
	
	public Pipe(int buffersize)
	{
		buffer = new byte[buffersize];
	}
	public int available()
	{
		if(readPtr<=writePtr)
		{
			return writePtr-readPtr;
		}
		else
		{
			return buffer.length-(readPtr-writePtr);
		}
	}
	public byte read()
	{
		if(writePtr == readPtr)
		{
			return 0;
		}
		byte toReturn = buffer[readPtr];
		readPtr++;
		readPtr %= buffer.length;
		return toReturn;
	}
	public int read(byte[] buf, int off, int len)
	{
		int read = Math.min(available(), len);
		if(readPtr + read<buffer.length)
		{
			System.arraycopy(buffer, readPtr, buf, off, read);
		}
		else
		{
			int beforeWrap = buffer.length-readPtr;
			int afterWrap = read-beforeWrap;
			System.arraycopy(buffer, readPtr, buf, off, beforeWrap);
			System.arraycopy(buffer, 0, buf, off+beforeWrap, afterWrap);
		}
		readPtr+=read;
		readPtr %= buffer.length;
		return read;
	}
	public int read(byte[] buf)
	{
		return read(buf,0,buf.length);
	}
	public void write(byte b)
	{
		buffer[writePtr] = b;
		writePtr++;
		writePtr %= buffer.length;
	}
	public int write(byte[] buf, int off, int len)
	{
		if(len>buffer.length)
		{
			off += len-buffer.length;
			len = buffer.length;
		}
		if(writePtr + len<buffer.length)
		{
			System.arraycopy(buf, off, buffer, writePtr, len);
		}
		else
		{
			int beforeWrap = buffer.length-writePtr;
			int afterWrap = len-beforeWrap;
			System.arraycopy(buf, off, buffer, writePtr, beforeWrap);
			System.arraycopy(buf, off+beforeWrap, buffer, 0, afterWrap);
		}
		writePtr+=len;
		writePtr %= buffer.length;
		return len;
	}
	public int write(byte[] buf)
	{
		return write(buf,0,buf.length);
	}
	public InputStream getInputStream()
	{
		final Pipe pipe = this;
		return new InputStream()
		{
			public int read() throws IOException
			{
				return pipe.read();
			}
			public int read(byte[] buf, int off, int len)
			{
				return pipe.read(buf,off,len);
			}
			public int read(byte[] buf)
			{
				return pipe.read(buf);
			}
			public int available()
			{
				return pipe.available();
			}
		};
	}
	public OutputStream getOutputStream()
	{
		final Pipe pipe = this;
		return new OutputStream()
		{
			public void write(int b) throws IOException
			{
				pipe.write((byte)(b&0xFF));
			}
			public void write(byte[] buf, int off, int len)
			{
				pipe.write(buf, off, len);
			}
			public void write(byte[] buf)
			{
				pipe.write(buf);
			}
		};
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
