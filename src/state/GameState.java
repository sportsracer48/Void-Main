package state;

import org.lwjgl.glfw.GLFW;

import entry.GlobalInput;
import action.ActList;
import action.Actable;
import graphics.Context;
import graphics.RenderList;
import graphics.entity.Entity;
import graphics.registry.SpriteAtlas;

/**
 * Think of it like pokemon. Battles are a state, overworld is a state, menus are a state, etc.
 * In this case, the workbench is a state, programming is a state, running is a state.
 * @author Henry
 *
 */
public abstract class GameState
{
	public RenderList renderList = new RenderList();
	public RenderList uiList = new RenderList(false);
	public ActList actList = new ActList();
	protected GlobalInput input;
	long window;
	
	public GameState(GlobalInput input, long window)
	{
		this.input = input;
		this.window = window;
	}
	
	public void hideCursor()
	{
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
	}
	public void showCursor()
	{
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
	}
	
	public String getClipboardString()
	{
		return GLFW.glfwGetClipboardString(window);
	}
	
	public int screenWidth()
	{
		return input.getScreenWidth();
	}
	
	public int screenHeight()
	{
		return input.getScreenHeight();
	}
	
	public float getMouseX()
	{
		return input.getMouseX();
	}
	public float getMouseY()
	{
		return input.getMouseY();
	}
	public boolean isKeyPressed(int key)
	{
		return input.isKeyPressed(key);
	}
	public boolean isButtonPressed(int button)
	{
		return input.isButtonPressed(button);
	}
	public void systemExit()
	{
		input.systemExit();
	}
	
	public void add(Entity t)
	{
		renderList.add(t);
		actList.add(t);
	}
	
	public void remove(Entity t)
	{
		renderList.remove(t);
		actList.remove(t);
	}
	
	public void addRenderable(Entity r)
	{
		renderList.add(r);
	}
	
	public void addUI(Entity r)
	{
		uiList.add(r);
		actList.add(r);
	}
	
	public void addActable(Actable a)
	{
		actList.add(a);
	}
	
	public void removeRenderable(Entity r)
	{
		renderList.remove(r);
	}
	
	public void removeUI(Entity r)
	{
		uiList.remove(r);
		actList.remove(r);
	}
	
	public void removeActable(Actable a)
	{
		actList.remove(a);
	}
	
	/**
	 * first thing updated during the update step.
	 * @param dt
	 */
	public void beforeInput(int dt){}
	public void afterInput(int dt){}
	public void afterUpdate(int dt){}
	public void mouseMoved(float x, float y){}
	public void keyPressed(int key){}
	public void keyRealeased(int key){}
	public void keyRepeated(int key){}
	public void mousePressed(int button){}
	public void mouseReleased(int button){}
	public void charTyped(char c){}
	public void scrollMoved(float dx, float dy){}
	public void fileDropped(String path){}
	
	public abstract void init(SpriteAtlas sprites);
	public abstract void renderAll(Context c);
	
	public String getPerformanceString(){return "";}
	
	public void update(int dt)
	{
		afterInput(dt);
		actList.act(dt);
		afterUpdate(dt);
		renderList.update();
		uiList.update();
	}
	
	public void render(Context c)
	{
		renderList.render(c);
	}
	
	public void renderUI(Context c)
	{
		uiList.render(c);
	}
}
