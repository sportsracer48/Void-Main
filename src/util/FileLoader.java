package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileLoader
{
	public static String getFileContents(String path) throws FileNotFoundException
	{
		StringBuilder builder = new StringBuilder();
		BufferedReader in = new BufferedReader(new FileReader(new File(path)));
		String line=null;
		try
		{
			line = in.readLine();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		while(line!=null)
		{
			builder.append(line);
			builder.append('\n');
			try
			{
				line = in.readLine();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			in.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return builder.toString();
	}
}
