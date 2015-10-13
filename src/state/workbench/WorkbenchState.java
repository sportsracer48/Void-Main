package state.workbench;

import java.util.ArrayList;
import java.util.List;

import math.Matrix;

import org.lwjgl.glfw.GLFW;

import entry.GlobalInput;
import graphics.Context;
import graphics.Sprite;
import graphics.entity.Entity;
import graphics.registry.SpriteAtlas;
import state.GameState;
import state.ui.Button;
import state.ui.HighlightArea;
import state.ui.MouseoverContext;
import state.ui.Window;

public class WorkbenchState extends GameState
{
	public WorkbenchState(GlobalInput input)
	{
		super(input);
	}
	Entity inventory,partMounting;
	
	DragContext grabContext = new DragContext();
	MouseoverContext mouseContext = new MouseoverContext();
	
	
	List<Entity> ui = new ArrayList<Entity>();
	List<Entity> screen = new ArrayList<Entity>();
	
	public void keyPressed(int key)
	{
		if(key == GLFW.GLFW_KEY_E)
		{
			if(isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT))
			{
				inventory.setVisible(true);
			}
			else
			{
				partMounting.setVisible(true);
			}
		}
	}
	
	public void mousePressed(int button)
	{
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT && mouseContext.hasMouseHolder())
		{
			clickableSearch:
			{
				for(int i = ui.size()-1; i>=0; i--)
				{
					if(ui.get(i).handleClick(getMouseX(), getMouseY()))
					{
						break clickableSearch;
					}
				}
				for(int i = screen.size()-1; i>=0; i--)
				{
					if(screen.get(i).handleClick(getMouseX(), getMouseY()))
					{
						break clickableSearch;
					}
				}
			}
		}
	
	}
	
	public void mouseReleased(int button)
	{
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
		{
			grabContext.resetGrabbed();
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
			{
				for(Entity e: ui)
				{
					e.handleRelease();
				}
				for(Entity e:screen)
				{
					e.handleRelease();
				}
			}
		}
	}
	
	public void mouseMoved(float x, float y)
	{
		grabContext.mouseMoved(x,y);
		mouseContext.setMouseHolder(null);
		for(int i = ui.size()-1; i>=0; i--)
		{
			ui.get(i).handleMove(getMouseX(), getMouseY(),mouseContext);
		}
		for(int i = screen.size()-1; i>=0; i--)
		{
			screen.get(i).handleMove(getMouseX(), getMouseY(),mouseContext);
		}
	}
	
	public void eachFrame(int dt)
	{
		float x = getMouseX();
		float y = getMouseY();
		
		for(Entity e: ui)
		{
			e.handleMove(x, y);
		}
		for(Entity e:screen)
		{
			e.handleMove(x, y);
		}
		
		if(grabContext.grabbed != null)
		{
			if(uiList.getList().contains(grabContext.grabbed.target))
			{
				uiList.floatToTop(grabContext.grabbed.target);
				ui.remove(grabContext.grabbed.target);
				ui.add(grabContext.grabbed.target);
			}
		}
	}
	
	public void renderAll(Context context)
	{
		context.setView(Matrix.scaling(2,2,1));
		context.setModel(Matrix.identity(4));
		render(context);
		context.setView(Matrix.identity(4));
		renderUI(context);
	}
	
	public void init(SpriteAtlas sprites)
	{
		sprites.setNamespace("res\\sprite\\workbench\\");
		Entity bg = new Entity(0, 0, 0, sprites.getSprite("background.png"));
		addRenderable(bg);
		Entity chassis = new Entity(40,40,1,sprites.getSprite("Chassis plate.png"));
		

		
		Button closeButton = new Button(0,0,
				sprites.getSprite("Button raised.png"),
				sprites.getSprite("Button pressed selected.png"),
				sprites.getSprite("Button pressed unselected.png")
				);
		Button closeButton2 = new Button(0,0,
				sprites.getSprite("Button raised.png"),
				sprites.getSprite("Button pressed selected.png"),
				sprites.getSprite("Button pressed unselected.png")
				);
		
		
		
		partMounting = new Window(120,100,sprites.getSprite("part mounting ui.png"), closeButton,grabContext);
		inventory = new Window(220,100,sprites.getSprite("Inventory UI.png"),closeButton2,grabContext);
		
		
		//create a local namespace, because why not
		{
			Sprite invHighlight = sprites.getSprite("inv-slot highlight.png");
			float x = 48, y = 76,step = 43;
			for(int col = 0; col <10; col++)
			{
				for(int row = 0; row<7; row++)
				{
					HighlightArea area = new HighlightArea(x+col*step,y+row*step,invHighlight);
					//area.getArea().setPadding(3, 3);
					inventory.addChild(area);
					inventory.addClickableArea(area.getArea());
				}
			}
			
			
			x=178;
			y=65;
			float yStep = 42;
			
			for(int row = 0; row<8; row+=2)
			{
				for(int col = 0; col<4; col++)
				{
					HighlightArea area = new HighlightArea(x+col*step,y+row*yStep,invHighlight);
					partMounting.addChild(area);
					partMounting.addClickableArea(area.getArea());
				}
			}
			
			x=112;
			y=149;
			
			for(int row = 0; row<4; row++)
			{
				int col = 0;
				HighlightArea area = new HighlightArea(x+col*step,y+row*yStep,invHighlight);
				partMounting.addChild(area);
				partMounting.addClickableArea(area.getArea());
			}
			
			x=373;
			for(int row = 0; row<4; row++)
			{
				int col = 0;
				HighlightArea area = new HighlightArea(x+col*step,y+row*yStep,invHighlight);
				partMounting.addChild(area);
				partMounting.addClickableArea(area.getArea());
			}
		}
		
		addActable(partMounting);
		addActable(inventory);
		
		ui.add(partMounting);
		ui.add(inventory);
		
		addRenderable(chassis);
		addUI(partMounting);
		addUI(inventory);
		Entity laptop = new Entity(700,100,1, sprites.getSprite("laptop.png"));
		addRenderable(laptop);
		
		sprites.resetNamespace();
	}
	
}
