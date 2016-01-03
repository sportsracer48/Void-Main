package state.programming;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AppendOnlyBuffer
{
	List<Integer> lineStarts = new ArrayList<>();
	StringBuffer buffer = new StringBuffer();
	
	private int col = 0;
	private int width;
	
	public AppendOnlyBuffer(int width)
	{
		this.width = width;
	}
	
	public OutputStream getOuputStream()
	{
		return new OutputStream()
		{
			public void write(int b) throws IOException
			{
				append((char)b);
			}
		};
	}
	
	public void append(char c)
	{
		if(lineStarts.isEmpty())
		{
			lineStarts.add(0);
		}
		buffer.append(c);
		if(c=='\n')
		{
			lineStarts.add(length());
			col=0;
		}
		else
		{
			col++;
		}
		if(col>=width)
		{
			append('\n');
		}
	}
	public int height()
	{
		return lineStarts.size();
	}
	public void append(String s)
	{
		for(int i = 0; i<s.length();i++)
		{
			append(s.charAt(i));
		}
	}
	public void appendln(String s)
	{
		append(s);
		appendln();
	}
	public void appendln()
	{
		append('\n');
	}
	public int getLineStart(int line)
	{
		if(line<0)
		{
			return 0;
		}
		if(line >= lineStarts.size())
		{
			return length();
		}
		return lineStarts.get(line);
	}
	public char getCharAt(int i)
	{
		return buffer.charAt(i);
	}
	public int length()
	{
		return buffer.length();
	}
}
