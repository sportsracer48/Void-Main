package computer.program;

import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.function.IntConsumer;

import graphics.Context;
import graphics.Sprite;
import graphics.registry.RegisteredFont;
import graphics.registry.UtilSprites;
import math.Matrix;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;

import computer.system.Computer;

import util.Color;
import static state.programming.Modifiers.*;

public class Vi implements RenderedExecutable
{
	private StringBuffer output;
	private RegisteredFont font;
	private int cols,rows;
	private int cursor;
	private int leftBorder = 6;
	
	private int selectStart=-1;
	private int selectEnd=-1;
	private int clickPos=-1;
	private int charHeight,charWidth;
	private int cursorX;
	private int blinkTime = 0;
	private int blinkPeriod = 500;
	private boolean insert = true;
	private IntConsumer makeVisible;
	
	private String fileName;
	private boolean running = false;
	private Computer system;
	
	public void setup(String[] args, StringBuffer output, RegisteredFont consoleFont, int cols, int rows, IntConsumer makeVisible, Computer system)
	{
		if(args.length<2)
		{
			output.append("usage: vi [filename]");
			return;
		}
		else
		{
			fileName = args[1];
		}
		
		running = true;
		
		this.system = system;
		this.output = output;
		this.cols = cols-getLeftBorder();
		this.rows = rows;
		this.font = consoleFont;
		this.makeVisible = makeVisible;
		FontMetrics metrics = font.metrics;
		charHeight = metrics.getHeight();
		charWidth = metrics.charWidth(' ');
		
		if(system.exists(fileName))
		{
			output.append(system.read(fileName));
		}
	}
	
	public char getCursorChar()
	{
		if(insert)
		{
			return '_';
		}
		else
		{
			return 22;
		}
	}

	public void act(int dt)
	{
		blinkTime = (blinkTime+dt)%blinkPeriod;
	}

	public boolean isRunning()
	{
		return running;
	}

	public void stop()
	{
		this.output.setLength(0);
		running = false;
	}
	
	public boolean hasSelection()
	{
		return selectStart != -1 && selectEnd != -1;
	}
	public String getSelection()
	{
		return output.substring(selectStart, selectEnd);
	}
	
	//WE ENJOY TYPING!!!!!!!!
	//actually it is important that the programs be able to sustain themselves, so if that means re-writing code, then that's what it means
	//they really do need full control over their own destiny
	//What I mean by this rambling comment is that this method is more or less the same as the one found in ConsoleEntity
	//But that's important.
	public void render(Context c, int startRow)
	{
		int row = startRow;
		int x= charWidth*getLeftBorder();
		int y = row*charHeight;
		int col = 0;
		int line = 0;
		
		if(row>=0)
		{
			renderPreLine(y,line,c);
		}
		
		for(int i = 0; i<output.length() && row<rows; i++)
		{
			char character = output.charAt(i);
			if(character!='\n')
			{
				if(row>=0)
				{
					renderChar(character,x,y,c);
					if(i>= selectStart && i<selectEnd)
					{
						renderSelect(x,y,c);
					}
					if(i==cursor)
					{
						GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT);
						GL14.glBlendFuncSeparate(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_ZERO);
						GL20.glBlendEquationSeparate(GL14.GL_FUNC_ADD, GL14.GL_FUNC_ADD);
						renderCursor(x,y,c);
						GL11.glPopAttrib();
					}
					c.resetColor();
				}
				x+=charWidth;
				col++;
				if(col >= cols)
				{
					y+= charHeight;
					row++;
					x = charWidth*getLeftBorder();
					col = 0;
				}
			}
			else//character == '\n'
			{
				if(i >= selectStart && i<selectEnd && row>=0)
				{
					renderSelect(x,y,c);
				}
				if(i==cursor && row>=0)
				{
					renderCursor(x,y,c);
				}
				y += charHeight;
				row++;
				x = charWidth*getLeftBorder();
				col = 0;
				line++;
				if(row>=0 && row<rows)
				{
					renderPreLine(y,line,c);
				}
			}
		}
		if(cursor==output.length() && row<rows && row>=0)
		{
			renderCursor(x,y,c);
		}
	}
	
	private void renderSelect(int x, int y, Context c)
	{
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT);
		GL14.glBlendFuncSeparate(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_ZERO);
		GL20.glBlendEquationSeparate(GL14.GL_FUNC_ADD, GL14.GL_FUNC_ADD);
		c.setColor(Color.white);
		c.pushTransform();
		c.prependTransform(Matrix.translation(x,y,0));
		c.prependTransform(Matrix.scaling(charWidth,charHeight,0));
		UtilSprites.white.render(c);
		c.popTransform();
		GL11.glPopAttrib();
	}
	
	private void renderPreLine(int y,int line, Context c)
	{
		c.setColor(Color.blue);
		String number = String.valueOf(line)+"|  ";;
		int rightBorder = getLeftBorder()-number.length();
		for(int i = 0; i<number.length() && i<getLeftBorder(); i++)
		{
			renderChar(number.charAt(i),(i+rightBorder)*charWidth,y,c);
		}
		c.resetColor();
	}
	
	private void renderChar(char character, int x, int y, Context c)
	{
		Sprite s = font.getSprite(character);
		if(s!=null)
		{
			c.pushTransform();
			c.appendTransform(Matrix.translation(x,y,0));
			s.render(c);
			c.popTransform();
		}
	}

	private void renderCursor(int x, int y, Context c)
	{
		if(blinkTime<blinkPeriod/2)
		{
			c.setColor(Color.white);
			c.pushTransform();
			c.appendTransform(Matrix.translation(x,y,0));
			font.getSprite(getCursorChar()).render(c);
			c.popTransform();
		}
	}
	
	private int getLeftBorder()
	{
		return leftBorder;
	}
	
	public int getPos(int x0, int y0)
	{
		if(y0<0)
		{
			y0=0;
		}
		if(x0<0)
		{
			x0=0;
		}
		int x = 0;
		int y = 0;
		for(int i = 0; i<output.length(); i++)
		{
			if(x==x0 && y==y0)
			{
				return i;
			}
			if(output.charAt(i)=='\n' || x+1 == cols)
			{
				if(y==y0)
				{
					return i;
				}
				y++;
				x=0;
			}
			else
			{
				x++;
			}
		}
		return output.length();
	}
	
	public void setCursorPos(int x, int y)
	{
		setCursor(getPos(x,y));
	}
	
	public int getCursorX()
	{
		int x = 0;
		for(int i = 0; i<output.length(); i++)
		{
			if(i==cursor)
			{
				return x;
			}
			if(output.charAt(i)=='\n' || x+1 == cols)
			{
				x=0;
			}
			else
			{
				x++;
			}
		}
		return x;
	}
	
	public int getCursorY()
	{
		int y = 0;
		int x = 0;
		for(int i = 0; i<output.length(); i++)
		{
			if(i==cursor)
			{
				return y;
			}
			if(output.charAt(i)=='\n' || x+1 == cols)
			{
				y++;
				x=0;
			}
			else
			{
				x++;
			}
		}
		return y;
	}
	
	public void mouseClicked(int x, int y)
	{
		selectStart = selectEnd = -1;
		setCursorPos(x-getLeftBorder(),y);
		clickPos = cursor;
	}
	public void mouseMoved(int x, int y)
	{
		if(clickPos!=-1)
		{
			setCursorPos(x-getLeftBorder(),y);
			selectStart = Math.min(cursor, clickPos);
			selectEnd = Math.max(cursor, clickPos);
		}
	}
	public void mouseReleased()
	{
		clickPos = -1;
	}
	
	
	
	public void acceptPaste(String s)
	{
		if(s.equals(""))
		{
			return;
		}
		if(hasSelection())
		{
			setCursor(selectStart);
			output.delete(selectStart, selectEnd);
			selectStart = selectEnd = -1;
		}
		s.chars().forEach(this::insertChar);
	}
	private void leftPress(int modFlags)
	{
		if(isShiftDown(modFlags) && cursor > 0)
		{
			if(selectStart == -1)
			{
				selectStart = cursor-1;
				selectEnd = cursor;
				setCursor(cursor - 1);
				cursorX = getCursorX();
			}
			else if(selectEnd == cursor && selectStart == cursor)
			{
				selectStart--;
				setCursor(cursor - 1);
				cursorX = getCursorX();
			}
			else if(selectEnd == cursor)
			{
				selectEnd--;
				setCursor(cursor - 1);
				cursorX = getCursorX();
			}
			else if(selectStart == cursor)
			{
				selectStart --;
				setCursor(cursor - 1);
				cursorX = getCursorX();
			}
		}
		else if(cursor>0)
		{
			setCursor(cursor - 1);
			cursorX = getCursorX();
			selectStart = selectEnd = -1;
		}
	}
	private void rightPress(int modFlags)
	{
		if(isShiftDown(modFlags) && cursor < output.length())
		{
			if(selectStart == -1)
			{
				selectStart = cursor;
				selectEnd = cursor+1;
				setCursor(cursor + 1);
				cursorX = getCursorX();
			}
			else if(selectEnd == cursor && selectStart == cursor)
			{
				selectEnd++;
				setCursor(cursor + 1);
				cursorX = getCursorX();
			}
			else if(selectEnd == cursor)
			{
				selectEnd++;
				setCursor(cursor + 1);
				cursorX = getCursorX();
			}
			else if(selectStart == cursor)
			{
				selectStart++;
				setCursor(cursor + 1);
				cursorX = getCursorX();
			}
		}
		else if(cursor < output.length())
		{
			setCursor(cursor + 1);
			cursorX = getCursorX();
			selectStart = selectEnd = -1;
		}
	}
	private void upPress(int modFlags)
	{
		int newX = cursorX;
		int newY = getCursorY() - 1;
		int newPos = getPos(newX, newY);
		
		if(isShiftDown(modFlags))
		{
			int posA=cursor,posB=cursor;
			if(selectStart == -1)
			{
				posA = newPos;
				posB = cursor;
				setCursor(newPos);
			}
			else if(selectEnd == cursor)
			{
				posA = selectStart;
				posB = newPos;
				setCursor(newPos);
			}
			else if(selectStart == cursor)
			{
				posA = selectEnd;
				posB = newPos;
				setCursor(newPos);
			}
			selectStart = Math.min(posA, posB);
			selectEnd = Math.max(posA, posB);
		}
		else
		{
			setCursor(newPos);
			selectStart = selectEnd = -1;
		}
	}
	private void downPress(int modFlags)
	{
		int newX = cursorX;
		int newY = getCursorY() + 1;
		int newPos = getPos(newX, newY);
		
		if(isShiftDown(modFlags))
		{
			int posA=cursor,posB=cursor;
			if(selectStart == -1)
			{
				posA = newPos;
				posB = cursor;
				setCursor(newPos);
			}
			else if(selectEnd == cursor)
			{
				posA = selectStart;
				posB = newPos;
				setCursor(newPos);
			}
			else if(selectStart == cursor)
			{
				posA = selectEnd;
				posB = newPos;
				setCursor(newPos);
			}
			selectStart = Math.min(posA, posB);
			selectEnd = Math.max(posA, posB);
		}
		else
		{
			setCursor(newPos);
			selectStart = selectEnd = -1;
		}
	}
	
	
	public void keyPressed(int key, int modFlags)
	{
		if(key == GLFW.GLFW_KEY_LEFT)
		{
			leftPress(modFlags);
		}
		else if(key == GLFW.GLFW_KEY_RIGHT)
		{
			rightPress(modFlags);
		}
		else if(key == GLFW.GLFW_KEY_UP)
		{
			upPress(modFlags);
		}
		else if(key == GLFW.GLFW_KEY_DOWN)
		{
			downPress(modFlags);
		}
		else if(key == GLFW.GLFW_KEY_ENTER)
		{
			boolean endsInColon = cursor>0 && cursor-1 < output.length() && output.charAt(cursor-1)==':';
			//boolean endLine = cursor == output.length() || output.charAt(cursor)=='\n';
			int lineStart = cursor;
			for(int i = cursor-1; i>=0 && output.charAt(i)!='\n';i--)
			{
				lineStart = i;
			}
			String lastLine = output.substring(lineStart, cursor);
			insertChar('\n');
			for(int i = 0; i<lastLine.length() && lastLine.charAt(i) == ' '; i++)
			{
				insertChar(' ');
			}
			if(endsInColon)
			{
				insertChar(' ');
				insertChar(' ');
			}
		}
		else if(key == GLFW.GLFW_KEY_TAB)
		{
			insertChar(' ');
			insertChar(' ');//three or more oh?
		}
		else if(key == GLFW.GLFW_KEY_BACKSPACE)
		{
			if(hasSelection())
			{
				setCursor(selectStart);
				output.delete(selectStart, selectEnd);
				selectStart = selectEnd = -1;
			}
			else if(cursor>0 && cursor<=output.length())
			{
				output.deleteCharAt(cursor-1);
				setCursor(cursor - 1);
			}
		}
		else if(key == GLFW.GLFW_KEY_DELETE)
		{
			if(hasSelection())
			{
				setCursor(selectStart);
				output.delete(selectStart, selectEnd);
				selectStart = selectEnd = -1;
			}
			else if(cursor<output.length())
			{
				output.deleteCharAt(cursor);
				makeVisible.accept(getCursorY());
			}
		}
		else if(key == GLFW.GLFW_KEY_A && isControlDown(modFlags))
		{
			selectStart = 0;
			selectEnd = output.length();
			setCursor(output.length());
			cursorX = getCursorX();
		}
		else if(key == GLFW.GLFW_KEY_C && isControlDown(modFlags) && hasSelection())
		{
			StringSelection selection = new StringSelection(getSelection());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
		}
		else if(key == GLFW.GLFW_KEY_X && isControlDown(modFlags) && hasSelection())
		{
			StringSelection selection = new StringSelection(getSelection());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
			setCursor(selectStart);
			output.delete(selectStart, selectEnd);
			selectStart = selectEnd = -1;
		}
		else if(key == GLFW.GLFW_KEY_S && isControlDown(modFlags))
		{
			system.write(fileName, output.toString());
		}
		else if(key == GLFW.GLFW_KEY_INSERT)
		{
			insert = !insert;
		}
	}

	public void keyReleased(int key, int modFlags)
	{
	}

	public void insertChar(int c)
	{
		insertChar((char)c);
	}
	
	public void insertChar(char c)
	{
		if(hasSelection())
		{
			setCursor(selectStart);
			output.delete(selectStart, selectEnd);
			selectStart = selectEnd = -1;
		}
		output.insert(cursor, c);
		setCursor(cursor + 1);
		cursorX = getCursorX();
	}
	public void charTyped(char c)
	{
		if(insert)
		{
			insertChar(c);
		}
		else
		{
			if(hasSelection())
			{
				setCursor(selectStart);
				output.delete(selectStart, selectEnd);
				selectStart = selectEnd = -1;
			}
			if(cursor == output.length() || output.charAt(cursor) == '\n')
			{
				output.insert(cursor, c);
			}
			else
			{
				output.setCharAt(cursor, c);
			}
			setCursor(cursor + 1);
			cursorX = getCursorX();
		}
	}
	void setCursor(int cursor)
	{
		this.cursor = cursor;
		makeVisible.accept(getCursorY());
	}
}