package state;

import entry.GlobalInput;
import action.ActList;
import action.Actable;
import graphics.Context;
import graphics.RenderList;
import graphics.Renderable;
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
	
	public GameState(GlobalInput input)
	{
		this.input = input;
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
	
	public <T extends Renderable & Actable> void add(T t)
	{
		renderList.add(t);
		actList.add(t);
	}
	
	public <T extends Renderable & Actable> void remove(T t)
	{
		renderList.remove(t);
		actList.remove(t);
	}
	
	public void addRenderable(Renderable r)
	{
		renderList.add(r);
	}
	
	public void addUI(Renderable r)
	{
		uiList.add(r);
	}
	
	public void addActable(Actable a)
	{
		actList.add(a);
	}
	
	public void removeRenderable(Renderable r)
	{
		renderList.remove(r);
	}
	
	public void removeUI(Renderable r)
	{
		uiList.remove(r);
	}
	
	public void removeActable(Actable a)
	{
		actList.remove(a);
	}
	
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
	
	public void update(int dt)
	{
		actList.act(dt);
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
