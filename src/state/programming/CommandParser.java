package state.programming;

import java.util.ArrayList;
import java.util.List;

import computer.Echo;
import computer.ExecutableHolder;
import computer.PythonExecutable;
import computer.RepeatEcho;
import computer.Vi;

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
			Echo test = new Echo();
			return new ExecutableHolder(test);
		}
		else if(tokens[0].equals("cons"))
		{
			RepeatEcho test = new RepeatEcho();
			return new ExecutableHolder(test);
		}
		else if(tokens[0].equals("python"))
		{
			PythonExecutable test = new PythonExecutable();
			return new ExecutableHolder(test);
		}
		else if(tokens[0].equals("vi"))
		{
			Vi test = new Vi();
			return new ExecutableHolder(test);
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
