package state.workbench;

import java.util.List;
import java.util.function.BiConsumer;

import math.Matrix;

import org.lwjgl.glfw.GLFW;

import entry.GlobalInput;
import game.item.Item;
import game.item.ItemType;
import graphics.Context;
import graphics.Sprite;
import graphics.entity.Entity;
import graphics.registry.SpriteAtlas;
import state.GameState;
import state.ui.Button;
import state.ui.MouseoverContext;
import state.ui.Window;
import state.workbench.game.ChassisGrid;
import util.GridBuilder;

public class WorkbenchState extends GameState
{
	public WorkbenchState(GlobalInput input, long window)
	{
		super(input,window);
		ui = uiList.getList();
		screen = renderList.getList();
	}
	
	Matrix view = Matrix.scaling(2,2,2);
	Matrix uiView = Matrix.identity(4);
	Matrix invView = view.inverse();
	
	Entity inventory,partMounting;
	
	DragContext grabContext = new DragContext();
	MouseoverContext mouseContext = new MouseoverContext();
	ItemManipulator itemManip = new ItemManipulator(grabContext,this);
	
	List<Entity> ui;
	List<Entity> screen;
	
	ChassisGrid grid;
	
	public void keyPressed(int key)
	{
		if(key == GLFW.GLFW_KEY_E)
		{
			if(isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT))
			{
				inventory.setEnabled(true);
			}
			else
			{
				partMounting.setEnabled(true);
			}
		}
	}
	
	public Matrix worldMouse()
	{
		Matrix mousePos = new Matrix(new float[]{getMouseX(),getMouseY(),0,1});
		return invView.dot(mousePos);
	}
	
	public void mousePressed(int button)
	{
		Matrix worldMouse = worldMouse();
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
					if(screen.get(i).handleClick(worldMouse.x(), worldMouse.y()))
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
		Matrix worldMouse = worldMouse();
		itemManip.resetAcceptor();
		grabContext.mouseMoved(x,y);//for now, the only grabable things will be ui elements.
		if(grabContext.hasObject() && grabContext.getGrabbedArea().desiresMouse())
		{
			mouseContext.setMouseHolder(grabContext.getGrabbedArea());
			mouseContext.setFrozen(true);
		}
		else
		{
			mouseContext.setMouseHolder(null);
		}
		for(int i = ui.size()-1; i>=0; i--)
		{
			ui.get(i).handleMove(x, y, mouseContext);
		}
		for(int i = screen.size()-1; i>=0; i--)
		{
			screen.get(i).handleMove(worldMouse.x(), worldMouse.y(), mouseContext);
		}
	}
	
	public void afterInput(int dt)
	{
		mouseMoved(getMouseX(),getMouseY());
		mouseContext.setFrozen(false);
		itemManip.act(dt);
		
		if(grabContext.grabbed != null)
		{
			if(uiList.getList().contains(grabContext.grabbed.target))
			{
				uiList.floatToTop(grabContext.grabbed.target);
			}
		}
	}
	
	public void renderAll(Context context)
	{
		context.resetColor();
		context.setView(view);
		context.setModel(Matrix.identity(4));
		render(context);
		context.setView(uiView);
		renderUI(context);
	}
	
	public void init(SpriteAtlas sprites)
	{
		sprites.setNamespace("res/sprite/workbench/");
		Entity bg = new Entity(0, 0, 0, sprites.getSprite("background.png"));
		addRenderable(bg);
		
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
		
		Sprite invHighlight = sprites.getSprite("inv-slot highlight.png");
		Sprite itemSprite = sprites.getSprite("ouino item.png");
		Sprite worldSprite = sprites.getSprite("ouino.png");
		ItemType microController = new ItemType(worldSprite,itemSprite);
		microController.setOffsetX(0);
		microController.setOffsetY(7);
		microController.setWorkbenchHeight(2);
		microController.setWorkbenchWidth(2);
		BiConsumer<Float,Float> addToInventory = (x2,y2) ->
		{
			InvenorySlot slot = new InvenorySlot(x2,y2,invHighlight,new Item(microController),itemManip);	
			inventory.addChild(slot);
		};
		BiConsumer<Float,Float> addToPartMount = (x2,y2) ->
		{
			InvenorySlot slot = new InvenorySlot(x2,y2,invHighlight,new Item(microController),itemManip);	
			partMounting.addChild(slot);
		};
		new GridBuilder(48,76,43,43,7,10).//grid for inventory window
		forEach(addToInventory);
		
		new GridBuilder(178,65,43,42*2,4,4).//grid for top, left, right, bottom
		forEach(addToPartMount);
		
		new GridBuilder(112,149,43,42,4,1).//grid for front
		forEach(addToPartMount);

		new GridBuilder(373,149,43,42,4,1).//grid for back
		forEach(addToPartMount);
		
		addActable(partMounting);
		addActable(inventory);

		grid = new ChassisGrid(40,40,1,sprites.getSprite("Chassis plate.png"),itemManip);
		
		add(grid);
		addUI(partMounting);
		addUI(inventory);
		Entity laptop = new Entity(700,100,1, sprites.getSprite("laptop.png"));
		addRenderable(laptop);
		
		sprites.resetNamespace();
	}
	
}
