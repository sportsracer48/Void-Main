package state.programming;

import java.util.ArrayList;
import java.util.List;

import computer.program.Echo;
import computer.program.ExecutableHolder;
import computer.program.Exit;
import computer.program.PythonExecutable;
import computer.program.RepeatEcho;
import computer.program.Upload;
import computer.program.Vi;

public class CommandParser
{
	static final String unitPattern = "";
	AppendOnlyBuffer out;
	
	public CommandParser(AppendOnlyBuffer out)
	{
		this.out = out;
	}
	
	public ExecutableHolder execute(String[] tokens)
	{
		if(tokens.length < 1)
		{
			return null;
		}
		if(tokens[0].equals("echo"))
		{
			return new ExecutableHolder(new Echo());
		}
		else if(tokens[0].equals("cons"))
		{
			return new ExecutableHolder(new RepeatEcho());
		}
		else if(tokens[0].equals("python"))
		{
			return new ExecutableHolder(new PythonExecutable());
		}
		else if(tokens[0].equals("vi"))
		{
			return new ExecutableHolder(new Vi());
		}
		else if(tokens[0].equals("upload"))
		{
			return new ExecutableHolder(new Upload());
		}
		else if(tokens[0].equals("exit"))
		{
			return new ExecutableHolder(new Exit());
		}
		return null;
	}
	
	public String[] parse(String command)
	{
		List<String> tokens = new ArrayList<>();
		
		boolean inString = false;
		boolean escaped = false;
		StringBuilder currentToken = new StringBuilder();
		char stringStart = 0;
		for(int i = 0; i<command.length(); i++)
		{
			char c = command.charAt(i);
			if(c=='\\' && !escaped)
			{
				escaped = true;
				continue;
			}
			if(inString)
			{
				if(!escaped && c==stringStart)
				{
					tokens.add(currentToken.toString());
					currentToken = new StringBuilder();
					inString = false;
					stringStart = 0;
				}
				else
				{
					currentToken.append(c);
				}
			}
			else
			{
				if(!escaped && c == ' ')
				{
					if(currentToken.length()>0)
					{
						tokens.add(currentToken.toString());
						currentToken = new StringBuilder();
					}
				}
				else if(!escaped && (c=='"' || c =='\''))
				{
					if(currentToken.length()>0)
					{
						tokens.add(currentToken.toString());
						currentToken = new StringBuilder();
					}
					inString = true;
					stringStart = c;
				}
				else
				{
					currentToken.append(c);
				}
			}
			escaped = false;
		}
		if(currentToken.length()>0)
		{
			tokens.add(currentToken.toString());
			currentToken = new StringBuilder();
		}
		String[] result = new String[tokens.size()];
		for(int i = 0; i<result.length; i++)
		{
			result[i] = tokens.get(i);
		}
		return result;
	}
}
