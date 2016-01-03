package state.programming;

import math.Matrix;

import org.lwjgl.glfw.GLFW;

import entry.GlobalInput;
import graphics.Context;
import graphics.registry.SpriteAtlas;
import state.GameState;
import state.workbench.Camera;

import static state.programming.Modifiers.*;

public class ProgrammingState extends GameState
{
	ConsoleEntity console;
	Camera camera;
	
	public ProgrammingState(GlobalInput input, long window)
	{
		super(input, window);
	}
	
	public void init(SpriteAtlas sprites)
	{
		camera = new Camera(screenWidth()/2,screenHeight()/2,screenWidth(),screenHeight(),1);
		console = new ConsoleEntity(0,0,0,1920,1080);
		addUI(console);
	}
	
	public int getModFlags()
	{
		return (isShiftDown()?SHIFT_FLAG:0) | 
			   (isControlDown()?CONTROL_FLAG:0) | 
			   (isAltDown()?ALT_FLAG:0);
	}
	
	public void keyPressed(int key)
	{
		if(key == GLFW.GLFW_KEY_ESCAPE)
		{
			systemExit();
		}
		console.keyPressed(key, getModFlags());
		if(isControlDown() && key == GLFW.GLFW_KEY_V)
		{
			console.paste(getClipboardString().replaceAll("\r\n", "\n"));
		}
	}
	
	public void charTyped(char c)
	{
		console.charTyped(c);
	}
	public void mousePressed(int button)
	{
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
		{
			console.handleClick(getMouseX(), getMouseY(), button);
		}
	}
	public void mouseMoved(float x, float y)
	{
		if(isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT))
		{
			console.handleMove(x, y);
		}
	}
	public void mouseReleased(int button)
	{
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
		{
			console.handleRelease();
		}
	}
	
	
	boolean isShiftDown()
	{
		return isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)||isKeyPressed(GLFW.GLFW_KEY_RIGHT_SHIFT);
	}
	boolean isControlDown()
	{
		return isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) || isKeyPressed(GLFW.GLFW_KEY_RIGHT_CONTROL);
	}
	boolean isAltDown()
	{
		return isKeyPressed(GLFW.GLFW_KEY_LEFT_ALT) || isKeyPressed(GLFW.GLFW_KEY_RIGHT_ALT);
	}
	
	public void keyRepeated(int key)
	{
		keyPressed(key);
	}

	public void renderAll(Context context)
	{
		context.setView(camera.getView());
		context.setModel(Matrix.identity(4));
		context.resetColor();
		renderUI(context);
	}
}
