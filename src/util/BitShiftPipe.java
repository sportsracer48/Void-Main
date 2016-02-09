package util;

import java.io.Serializable;

public class BitShiftPipe implements Serializable
{
	private static final long serialVersionUID = -26527772360111530L;
	
	Pipe pipe;
	byte partialByte;
	int bit = 0;
	
	public BitShiftPipe(int bufferSize)
	{
		pipe = new Pipe(bufferSize);
	}
	public void writeBit(boolean bit)
	{
		partialByte |= (bit?1:0)<<this.bit;
		this.bit++;
		if(this.bit == 8)
		{
			pipe.write(partialByte);
			this.bit = 0;
			partialByte = 0;
		}
	}
	public boolean hasData()
	{
		return pipe.available() > 0;
	}
	public int readByte()
	{
		if(pipe.available() > 0)
		{
			return pipe.read() & 0xFF;
		}
		else
		{
			return -1;
		}
	}
}
