package state.programming;

import java.awt.FontMetrics;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import math.Matrix;

import org.lwjgl.glfw.GLFW;

import util.Color;
import graphics.Context;
import graphics.Sprite;
import graphics.entity.Entity;
import graphics.registry.RegisteredFont;
import graphics.registry.UtilSprites;

public class ConsoleEntity extends Entity
{
	AppendOnlyBuffer appendOnly;
	StringBuffer editable = new StringBuffer();
	RegisteredFont font;
	List<String> commandBuffer = new ArrayList<>();
	int commandIndex = 0;
	
	String prompt = "command@fake-pc$>";
	int cursor;
	int commandStart;
	
	float width,height;
	int charWidth, charHeight, rows, cols;
	
	int startLine = 0;
	int blinkTime = 0;
	int blinkPeriod = 500;
	
	boolean insert = true;
	Entity bg;
	
	Runtime runtime;
	
	boolean runningProgram = false;
	
	public ConsoleEntity(float x, float y, float z, float width, float height)
	{
		super(x, y, z, null);
		
		font = RegisteredFont.defaultFont;
		this.width = width;
		this.height = height;
		FontMetrics metrics = font.metrics;
		charWidth = metrics.charWidth(' ');
		charHeight = metrics.getHeight();
		rows = (int) (height/charHeight);
		cols = (int) (width/charWidth);
		
		bg = new Entity(-charWidth,-charHeight,0,UtilSprites.white);
		bg.setScale((cols+2)*charWidth, (rows+2)*charHeight);
		bg.setColor(new Color(.1f,.1f,.1f,.1f));
		
		runtime = Runtime.getRuntime();
		
		appendOnly = new AppendOnlyBuffer(cols);
		
		appendOnly.append("new console\n");
		newPrompt();
	}
	public void act(int dt)
	{
		blinkTime = (blinkTime+dt)%blinkPeriod;
		super.act(dt);
	}
	public char getCursorChar()
	{
		if(insert)
		{
			return '_';
		}
		else
		{
			return '-';
		}
	}
	private void renderCursor(int x, int y, Context c)
	{
		if(blinkTime<blinkPeriod/2)
		{
			c.pushTransform();
			c.appendTransform(Matrix.translation(x,y,0));
			font.getSprite(getCursorChar()).render(c);
			c.popTransform();
		}
	}
	
	public void renderBase(Context c)
	{
		bg.render(c);
		int row = 0;
		//render the immutable section
		int x = 0, y = 0;
		if(startLine<0)
		{
			y = -startLine*charHeight;
			row = -startLine;
		}
		else
		{
			y = 0;
			row = 0;
		}
		for(int i = appendOnly.getLineStart(startLine); i<appendOnly.length() && row<rows; i++)
		{
			char character = appendOnly.getCharAt(i);
			if(character!='\n')
			{
				Sprite s = font.getSprite(character);
				if(s!=null)
				{
					c.pushTransform();
					c.appendTransform(Matrix.translation(x,y,0));
					s.render(c);
					c.popTransform();
				}
				x+=charWidth;
			}
			else
			{
				y += charHeight;
				row++;
				x = 0;
			}
		}
		//render the mutable section
		
		row = -startLine+appendOnly.height();
		x=0;
		y = (-startLine + appendOnly.height())*charHeight;
		
		int col = 0;
		for(int i = 0; i<editable.length() && row<rows; i++)
		{
			char character = editable.charAt(i);
			if(character!='\n')
			{
				if(row>=0)
				{
					Sprite s = font.getSprite(character);
					if(s!=null)
					{
						c.pushTransform();
						c.appendTransform(Matrix.translation(x,y,0));
						s.render(c);
						c.popTransform();
					}
					if(i==cursor)
					{
						renderCursor(x,y,c);
					}
				}
				x+=charWidth;
				col++;
				if(col >= cols)
				{
					y+= charHeight;
					row++;
					x = 0;
					col = 0;
				}
			}
			else
			{
				y += charHeight;
				row++;
				x = 0;
				col = 0;
			}
		}
		if(cursor==editable.length() && row<rows && row>=0)
		{
			renderCursor(x,y,c);
		}
	}
	
	public float getUnscaledWidth()
	{
		return width;
	}
	public float getUnscaledHeight()
	{
		return height;
	}
	public int bufferHeight()
	{
		return appendOnly.height()+commandHeight();
	}
	public int commandHeight()
	{
		return (int) (editable.length()/cols)+1;
	}
	public String getCommand()
	{
		return editable.substring(commandStart);
	}
	public void setCommand(String command)
	{
		editable.delete(commandStart, editable.length());
		editable.append(command);
		cursor = editable.length();
		updateScroll();
	}
	
	private void updateScroll()
	{
		if(bufferHeight()>rows)
		{
			startLine = bufferHeight()-rows;
		}
	}
	
	private void deletePrompt()
	{
		editable.delete(0, editable.length());
	}
	
	private void archivePrompt()
	{
		appendOnly.append('\n');
		appendOnly.append(editable.toString());
		appendOnly.append('\n');
		commandBuffer.add(0,getCommand());
		commandIndex = 0;
		deletePrompt();
	}
	private void newPrompt()
	{
		deletePrompt();
		editable.append(prompt);
		cursor = editable.length();
		commandStart = cursor;
		updateScroll();
	}
	
	private void println(String s)
	{
		appendOnly.append(s);
		appendOnly.append('\n');
	}

	private void enterCommand()
	{
		String command = getCommand();
		archivePrompt();
		
		
//		try
//		{
//			Process p = runtime.exec("cmd /C "+command);
//			InputStream cout = p.getInputStream();
//			OutputStream cin = p.getOutputStream();
//		} 
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//		
		newPrompt();
	}
	
	public void insertCharAtCursor(char c)
	{
		if(c=='\r' || c =='\n')
		{
			enterCommand();
		}
		else
		{
			editable.insert(cursor, c);
			cursor++;
			updateScroll();
		}
	}
	public void overwriteCharAtCursor(char c)
	{
		if(c=='\r' || c =='\n')
		{
			enterCommand();
		}
		else if(cursor == editable.length())
		{
			editable.insert(cursor, c);
			cursor++;
			updateScroll();
		}
		else
		{
			editable.setCharAt(cursor, c);
			cursor++;
			updateScroll();
		}
	}
	
	public void pasteTextAtCursor(String s)
	{
		s=s.replaceAll("\r\n", "\n").replaceAll("\r", "\n");
		for(int i = 0; i<s.length(); i++)
		{
			char c = s.charAt(i);
			insertCharAtCursor(c);
		}
	}
	public void backspaceAtCursor()
	{
		if(cursor>commandStart)
		{
			editable.deleteCharAt(cursor-1);
			cursor--;
		}
	}
	public void deleteAtCursor()
	{
		if(cursor<editable.length())
		{
			editable.deleteCharAt(cursor);
		}
	}

	public void keyPressed(int key)
	{
		if(key == GLFW.GLFW_KEY_ENTER)
		{
			enterCommand();
		}
		else if(key == GLFW.GLFW_KEY_PAGE_UP)
		{
			startLine--;
		}
		else if(key == GLFW.GLFW_KEY_PAGE_DOWN)
		{
			startLine++;
		}
		else if(key == GLFW.GLFW_KEY_LEFT)
		{
			if(cursor>commandStart)
			{
				cursor--;
			}
		}
		else if(key == GLFW.GLFW_KEY_RIGHT)
		{
			if(cursor<editable.length())
			{
				cursor++;
			}
		}
		else if(key == GLFW.GLFW_KEY_BACKSPACE)
		{
			backspaceAtCursor();
		}
		else if(key == GLFW.GLFW_KEY_DELETE)
		{
			deleteAtCursor();
		}
		else if(key == GLFW.GLFW_KEY_INSERT)
		{
			insert = !insert;
		}
		else if(key == GLFW.GLFW_KEY_UP)
		{
			if(commandIndex>=0 && commandIndex<commandBuffer.size())
			{
				setCommand(commandBuffer.get(commandIndex));
				commandIndex++;
			}
		}
		else if(key == GLFW.GLFW_KEY_DOWN)
		{
			if(commandIndex-2>=0 && commandIndex-2<commandBuffer.size())
			{
				setCommand(commandBuffer.get(commandIndex-2));
				commandIndex--;
			}
		}
	}

	public void charTyped(char c)
	{
		if(insert)
		{
			insertCharAtCursor(c);
		}
		else
		{
			overwriteCharAtCursor(c);
		}
	}
}
