package computer;

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

import static state.programming.Modifiers.*;

public class Vi implements RenderedExecutable
{
	StringBuffer output;
	RegisteredFont font;
	int cols,rows;
	private int cursor;
	
	int selectStart=-1;
	int selectEnd=-1;
	
	int charHeight,charWidth;
	
	int cursorX;
	
	int blinkTime = 0;
	int blinkPeriod = 500;
	
	boolean insert = true;
	
	IntConsumer makeVisible;
	
	public void setup(String[] args, StringBuffer output, RegisteredFont consoleFont, int cols, int rows, IntConsumer makeVisible)
	{
		this.output = output;
		this.cols = cols;
		this.rows = rows;
		this.font = consoleFont;
		this.makeVisible = makeVisible;
		FontMetrics metrics = font.metrics;
		charHeight = metrics.getHeight();
		charWidth = metrics.charWidth(' ');
		
		for(int i = 0; i<256; i++)
		{
			output.append(i);
			output.append(": ");
			output.append((char)i);
			output.append('\n');
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
		return true;
	}

	public void stop()
	{
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
		int x=0;
		int y = row*charHeight;
		
		int col = 0;
		for(int i = 0; i<output.length() && row<rows; i++)
		{
			char character = output.charAt(i);
			if(character!='\n')
			{
				if(row>=0)
				{
					if(i>= selectStart && i<selectEnd)
					{
						c.pushTransform();
						c.appendTransform(Matrix.scaling(charWidth,charHeight,0));
						c.appendTransform(Matrix.translation(x,y,0));
						UtilSprites.white.render(c);
						c.popTransform();
					}
					if(i==getCursor())
					{
						GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT);
						GL14.glBlendFuncSeparate(GL11.GL_ONE,GL11.GL_DST_COLOR,GL11.GL_ZERO,GL11.GL_ZERO);
						GL20.glBlendEquationSeparate(GL14.GL_FUNC_SUBTRACT,GL14.GL_FUNC_ADD);
						renderCursor(x,y,c);
						GL11.glPopAttrib();
					}
					Sprite s = font.getSprite(character);
					if(s!=null)
					{
						GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT);
						
						GL14.glBlendFuncSeparate(GL11.GL_ONE,GL11.GL_DST_COLOR,GL11.GL_ZERO,GL11.GL_ZERO);
						GL20.glBlendEquationSeparate(GL14.GL_FUNC_SUBTRACT,GL14.GL_FUNC_ADD);
						
						c.pushTransform();
						c.appendTransform(Matrix.translation(x,y,0));
						s.render(c);
						c.popTransform();
						c.resetColor();
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
					x = 0;
					col = 0;
				}
			}
			else
			{
				if(i==getCursor())
				{
					renderCursor(x,y,c);
				}
				y += charHeight;
				row++;
				x = 0;
				col = 0;
			}
		}
		if(getCursor()==output.length() && row<rows && row>=0)
		{
			renderCursor(x,y,c);
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
			if(i==getCursor())
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
	
	int clickPos=-1;
	public void mouseClicked(int x, int y)
	{
		setCursorPos(x,y);
		clickPos = cursor;
	}
	public void mouseMoved(int x, int y)
	{
		if(clickPos!=-1)
		{
			setCursorPos(x,y);
			selectStart = Math.min(cursor, clickPos);
			selectEnd = Math.max(cursor, clickPos);
		}
	}

	public void mouseReleased()
	{
		clickPos = -1;
	}
	
	public int getCursorY()
	{
		int y = 0;
		int x = 0;
		for(int i = 0; i<output.length(); i++)
		{
			if(i==getCursor())
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
		if(isShiftDown(modFlags) && getCursor() > 0)
		{
			if(selectStart == -1)
			{
				selectStart = getCursor()-1;
				selectEnd = getCursor();
				setCursor(getCursor() - 1);
				cursorX = getCursorX();
			}
			else if(selectEnd == getCursor() && selectStart == getCursor())
			{
				selectStart--;
				setCursor(getCursor() - 1);
				cursorX = getCursorX();
			}
			else if(selectEnd == getCursor())
			{
				selectEnd--;
				setCursor(getCursor() - 1);
				cursorX = getCursorX();
			}
			else if(selectStart == getCursor())
			{
				selectStart --;
				setCursor(getCursor() - 1);
				cursorX = getCursorX();
			}
		}
		else if(getCursor()>0)
		{
			setCursor(getCursor() - 1);
			cursorX = getCursorX();
			selectStart = selectEnd = -1;
		}
	}
	private void rightPress(int modFlags)
	{
		if(isShiftDown(modFlags) && getCursor() < output.length())
		{
			if(selectStart == -1)
			{
				selectStart = getCursor();
				selectEnd = getCursor()+1;
				setCursor(getCursor() + 1);
				cursorX = getCursorX();
			}
			else if(selectEnd == getCursor() && selectStart == getCursor())
			{
				selectEnd++;
				setCursor(getCursor() + 1);
				cursorX = getCursorX();
			}
			else if(selectEnd == getCursor())
			{
				selectEnd++;
				setCursor(getCursor() + 1);
				cursorX = getCursorX();
			}
			else if(selectStart == getCursor())
			{
				selectStart++;
				setCursor(getCursor() + 1);
				cursorX = getCursorX();
			}
		}
		else if(getCursor() < output.length())
		{
			setCursor(getCursor() + 1);
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
			int posA=getCursor(),posB=getCursor();
			if(selectStart == -1)
			{
				posA = newPos;
				posB = getCursor();
				setCursor(newPos);
			}
			else if(selectEnd == getCursor())
			{
				posA = selectStart;
				posB = newPos;
				setCursor(newPos);
			}
			else if(selectStart == getCursor())
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
			int posA=getCursor(),posB=getCursor();
			if(selectStart == -1)
			{
				posA = newPos;
				posB = getCursor();
				setCursor(newPos);
			}
			else if(selectEnd == getCursor())
			{
				posA = selectStart;
				posB = newPos;
				setCursor(newPos);
			}
			else if(selectStart == getCursor())
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
			insertChar('\n');
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
			else if(getCursor()>0 && getCursor()<=output.length())
			{
				output.deleteCharAt(getCursor()-1);
				setCursor(getCursor() - 1);
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
			else if(getCursor()<output.length())
			{
				output.deleteCharAt(getCursor());
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
		output.insert(getCursor(), c);
		setCursor(getCursor() + 1);
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
			if(output.charAt(getCursor()) == '\n')
			{
				output.insert(getCursor(), c);
			}
			else
			{
				output.setCharAt(getCursor(), c);
			}
			setCursor(getCursor() + 1);
			cursorX = getCursorX();
		}
	}

	int getCursor()
	{
		return cursor;
	}

	void setCursor(int cursor)
	{
		this.cursor = cursor;
		makeVisible.accept(getCursorY());
	}
}