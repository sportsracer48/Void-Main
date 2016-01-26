package state.programming;

import math.Matrix;

import org.lwjgl.glfw.GLFW;

import computer.system.Computer;
import entry.GlobalInput;
import entry.GlobalState;
import graphics.Context;
import graphics.Sprite;
import graphics.entity.Entity;
import graphics.registry.SpriteAtlas;
import state.GameState;
import state.workbench.Camera;
import static state.programming.Modifiers.*;

public class ProgrammingState extends GameState
{
	ConsoleEntity console;
	Entity background;
	Entity laptop;
	Entity laptopBg;
	Camera camera;
	boolean mouseMoveThisFrame = false;
	long ibeamCursor;
	long defaultCursor;
	Computer computer;
	
	int laptopX = 700;
	int laptopY = 100;
	
	public ProgrammingState(GlobalInput input, long window)
	{
		super(input, window);
		this.computer = GlobalState.laptop;
	}
	
	public void init(SpriteAtlas sprites)
	{
		sprites.setNamespace("res/sprite/workbench/");
		camera = new Camera(screenWidth()/2,screenHeight()/2,screenWidth(),screenHeight(),1);
		
		Sprite laptopSprite = sprites.getSprite("laptop hud.png");
		
		int screenWidth = screenWidth();
		int screenHeight = screenHeight();
		float aspectRatio = (float)laptopSprite.imWidth/laptopSprite.imHeight;
		
		float goalWidth = screenHeight*aspectRatio;
		float goalHeight = screenWidth/aspectRatio;
		
		if(goalHeight>screenHeight)
		{
			goalHeight = goalWidth/aspectRatio;
		}
		if(goalWidth>screenWidth)
		{
			goalWidth = goalHeight*aspectRatio;
		}
		float xScale = goalWidth/laptopSprite.imWidth;
		float yScale = goalHeight/laptopSprite.imHeight;
		
		float resultWidth = laptopSprite.imWidth*xScale;
		float resultHeight = laptopSprite.imHeight*yScale;
		
		float xOffset = (screenWidth-resultWidth)/2f;
		float yOffset = (screenHeight-resultHeight)/2f;
		
		background = new Entity(-laptopX*xScale+xOffset,-laptopY*yScale+yOffset,0,sprites.getSprite("background.png"));
		background.setScale(yScale, yScale);
		laptopBg = new Entity(xOffset,yOffset,0,sprites.getSprite("laptop.png"));
		laptopBg.setScale(xScale,yScale);
		laptop = new Entity(xOffset,yOffset,0,laptopSprite);
		laptop.setScale(xScale, yScale);
		console = new ConsoleEntity(22*xScale+xOffset+2,18*yScale+yOffset+2,0,193*xScale-4,109*yScale-4,computer);
		addUI(background);
		addUI(laptopBg);
		addUI(laptop);
		addUI(console);
		ibeamCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR);
		defaultCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
		sprites.resetNamespace();
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
			//TODO
			changeTo(GlobalState.currentWorkbench);
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
		console.handleMove(x, y);
		mouseMoveThisFrame = true;
	}
	public void afterInput(int dt)
	{
		if(!mouseMoveThisFrame)
		{
			console.handleMove(getMouseX(), getMouseY());
		}
		mouseMoveThisFrame = false;
		float x = getMouseX();
		float y = getMouseY();
		if(x>=console.getX() && x<console.getX()+console.getWidth() && y>=console.getY() && y<console.getY()+console.getHeight())
		{
			setCursor(ibeamCursor);
		}
		else
		{
			setCursor(defaultCursor);
		}
	}
	public void mouseReleased(int button)
	{
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
		{
			console.handleRelease();
		}
	}
	public void scrollMoved(float dx, float dy)
	{
		console.scroll((int)Math.round(dy),getModFlags());
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
	public void cleanup()
	{
		setCursor(defaultCursor);
	}
}
