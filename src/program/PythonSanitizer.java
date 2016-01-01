package program;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PythonSanitizer
{
	static String pyNameExpr = "[a-zA-Z_][a-zA-Z0-9_]*";
	static String pyPrivateNameExpr = "_[a-zA-Z0-9_]*";
	static Pattern pyNamePattern = Pattern.compile(pyNameExpr);
	static Pattern pyPrivateNamePattern = Pattern.compile(pyPrivateNameExpr);
	static List<String> keyWords = Arrays.asList(
			"and","del","from","not","whileas","elif","global","or",
			" with","assert","else","if"," pass","yield","break",
			"except","import","print","class","exec","in"," raise",
			"continue","finally","is"," return","def","for","lambda","try");
	
	String source;
	boolean[] inString;
	String problem;
	int start;
	
	public PythonSanitizer(String source)
	{
		this.source = source;
	}
	
	public boolean isLegal()
	{
		return getProblemString() == null;
	}
	
	public String getProblemString()
	{
		detectStrings();
		Matcher names = pyNamePattern.matcher(source);
		while(names.find())
		{
			if(!inString[names.start()] && !keyWords.contains(names.group()) && Pattern.matches(pyPrivateNameExpr, names.group()))
			{
				problem = names.group();
				start = names.start();
				return names.group();
			}
		}
		return null;
	}
	
	public void throwException()
	{
		if(problem==null)
		{
			return;
		}
		int index = 0;
		int lineIndex = 1;
		int colIndex = 0;
		String[] lines = source.split("\n");
		for(String line:lines)
		{
			if(index+line.length()>start)
			{
				colIndex = start-index+1;
				break;
			}
			else
			{
				index += line.length()+1;
				lineIndex++;
			}
		}
		throw new RuntimeException("SyntaxError: (\"Illegal varibale name or private variable access: "+problem+"\", ('<script>', "+lineIndex+", "+colIndex+", '    import = None;\n'))")
		{
			private static final long serialVersionUID = 1L;

			public void printStackTrace(PrintStream s)
			{
				s.println(this.getMessage());
			}
		};
	}
	
	private void detectStrings()
	{
		boolean currentlyInString = false;
		boolean escaped = false;
		char startOfString = 0;
		inString = new boolean[source.length()];
		for(int i = 0; i<source.length(); i++)
		{
			char nextChar = source.charAt(i);
			if(!currentlyInString)
			{
				if(nextChar == '\'' || nextChar == '"')
				{
					currentlyInString = true;
					escaped = false;
					startOfString = nextChar;
				}
			}
			else
			{
				if(!escaped && nextChar == startOfString)
				{
					currentlyInString = false;
					escaped = false;
					startOfString = 0;
				}
				else
				{
					inString[i] = true;
				}
				if(!escaped && nextChar == '\\')
				{
					escaped = true;
				}
				else
				{
					escaped = false;
				}
			}
		}
	}
}
