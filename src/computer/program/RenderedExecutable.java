package computer.program;

import java.util.function.IntConsumer;

import computer.system.Computer;

import graphics.Context;
import graphics.registry.RegisteredFont;

public interface RenderedExecutable
{
	public void setup(String[] args, StringBuffer output, RegisteredFont consoleFont, int cols, int rows, IntConsumer makeVisible, Computer system);
	public void act(int dt);
	public boolean isRunning();
	public void stop();
	public void render(Context c,int startRow);
	public void keyPressed(int key, int modFlags);
	public void keyReleased(int key, int modFlags);
	public void charTyped(char c);
	public void acceptPaste(String s);
	public void mouseClicked(int x, int y);
	public void mouseMoved(int i, int j);
	public void mouseReleased();
}
