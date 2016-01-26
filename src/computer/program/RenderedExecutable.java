package computer.program;

import java.util.function.IntConsumer;

import state.programming.AppendOnlyBuffer;
import computer.system.Computer;
import graphics.Context;
import graphics.registry.RegisteredFont;

public interface RenderedExecutable
{
	public void setup(String[] args, StringBuffer output, AppendOnlyBuffer stdout, RegisteredFont consoleFont, float scale, int cols, int rows, IntConsumer makeVisible, Computer system);
	public void act(int dt);
	public boolean isRunning();
	public void stop();
	public void render(Context c,int startRow);
	public void keyPressed(int key, int modFlags);
	public void keyReleased(int key, int modFlags);
	public void charTyped(char c);
	public void acceptPaste(String s);
	public void mouseClicked(float x, float y);
	public void mouseMoved(float i, float j);
	public void mouseReleased();
}
