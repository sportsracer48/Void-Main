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
import graphics.entity.BoxEntity;
import graphics.entity.Entity;
import graphics.entity.FluidEntity;
import graphics.entity.TextEntity;
import graphics.registry.SpriteAtlas;
import state.GameState;
import state.Mode;
import state.ModeManager;
import state.ui.Button;
import state.ui.Button.ButtonTheme;
import state.ui.ClickableArea;
import state.ui.MouseoverContext;
import state.ui.Window;
import state.workbench.conroller.DragContext;
import state.workbench.conroller.ItemManipulator;
import state.workbench.game.ChassisGrid;
import state.workbench.graphics.InvenorySlot;
import util.Color;
import util.Grid;

public class WorkbenchState extends GameState
{
	public WorkbenchState(GlobalInput input, long window)
	{
		super(input,window);
		ui = uiList.getList();
		screen = renderList.getList();
		globalClickArea.setDesiresMouse(false);
	}
	
	Matrix view = Matrix.scaling(2,2,2);
	Matrix uiView = Matrix.identity(4);
	Matrix invView = view.inverse();
	
	Entity inventory,partMounting,tools;
	
	
	DragContext grabContext = new DragContext();
	MouseoverContext mouseContext = new MouseoverContext();
	ClickableArea globalClickArea = new ClickableArea(0,0,0,0);
	ItemManipulator itemManip = new ItemManipulator(grabContext,this,globalClickArea);
	FluidEntity mouseCompanion = new FluidEntity(0,0,0);
	
	Sprite wireSymbol;
	
	Mode edit = new Mode()
	{

		public void enable()
		{
		}

		public void disable()
		{
			if(itemManip.hasItem())
			{
				itemManip.handleFail();
			}
		}
		
	};
	WiringMode wiring = new WiringMode(mouseCompanion,this,screenWidth(),screenHeight())
	{

		public void enable()
		{
			super.enable();
			mouseCompanion.setSpriteAndSize(wireSymbol);
		}

		public void disable()
		{
			super.disable();
			mouseCompanion.setSpriteAndSize(null);
			mouseCompanion.setColor(Color.white);
		}
		
	};
	ModeManager manager = new ModeManager(edit);
	
	List<Entity> ui;
	List<Entity> screen;
	
	ChassisGrid grid;
	
	public void keyPressed(int key)
	{
		if(key == GLFW.GLFW_KEY_ESCAPE)
		{
			if(manager.getMode() == wiring)
			{
				if(wiring.getSelector() != null)
				{
					wiring.setSelector(null);
				}
				else
				{
					manager.setMode(edit);
				}
			}
			else
			{
				systemExit();
			}
		}
		if(key == GLFW.GLFW_KEY_E)
		{
			manager.setMode(wiring);
		}
		else if(key == GLFW.GLFW_KEY_Q)
		{
			manager.setMode(edit);
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
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
		{
			globalClickArea.handleClick(getMouseX(), getMouseY(), Matrix.identity(4));
		}
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
			globalClickArea.handleAnyRelease();
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
		mouseCompanion.setPos(x+16, y+16);
		globalClickArea.handleMove(x, y, Matrix.identity(4), mouseContext);
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
		
		if(grabContext.getGrabbed() != null)
		{
			if(uiList.getList().contains(grabContext.getGrabbed().getTarget()))
			{
				uiList.floatToTop(grabContext.getGrabbed().getTarget());
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
		mouseCompanion.render(context);
	}
	
	public void init(SpriteAtlas sprites)
	{
		sprites.setNamespace("res/sprite/workbench/");
		
		Entity bg = new Entity(0, 0, 0, sprites.getSprite("background.png"));
		addRenderable(bg);
		ButtonTheme close = new ButtonTheme(
				sprites.getSprite("Button raised.png"),
				sprites.getSprite("Button pressed selected.png"),
				sprites.getSprite("Button pressed unselected.png"));
		ButtonTheme inv = new ButtonTheme(
				sprites.getSprite("Inv Button Raised.png"),
				sprites.getSprite("Inv Button Pressed.png"),
				sprites.getSprite("Inv Button Gray.png")
				);
		
		Button closeButton3 = close.build();
		closeButton3.setEnabled(false);
		
		partMounting = new Window(1300,40,sprites.getSprite("part mounting ui.png"), close.build(),grabContext);
		partMounting.setEnabled(false);
		partMounting.setMode(edit, manager);
		inventory = new Window(1300,440,sprites.getSprite("Inventory UI.png"),close.build(),grabContext);
		inventory.setEnabled(false);
		inventory.setMode(edit, manager);
		tools = new Window(650,screenHeight()-200,sprites.getSprite("Tools.png"),closeButton3,grabContext);
		
		wireSymbol = sprites.getSprite("wire symbol.png");
		
		ItemType.setDefaultWireSprites(
				sprites.getSprite("pin highlight end.png"),
				sprites.getSprite("pin highlight.png"),
				sprites.getSprite("wire end.png"),
				sprites.getSprite("wire end opaque.png"),
				sprites.getSprite("wire fade.png"));
		
		Sprite invHighlight = sprites.getSprite("inv-slot highlight.png");
		ItemType microController = new ItemType(sprites.getSprite("ouino.png"),sprites.getSprite("ouino item.png"));
		ItemType battery = new ItemType(sprites.getSprite("battery.png"),sprites.getSprite("battery item.png"));
		ItemType antenna = new ItemType(sprites.getSprite("antenna ic.png"),sprites.getSprite("antenna item.png"));
		ItemType breadboard = new ItemType(sprites.getSprite("breadboard.png"),sprites.getSprite("breadboard item.png"));
		antenna.setOffsets(-1, -1);
		battery.setOffsets(-1, -1);
		
		
		microController.setOffsets(0, 7);
		microController.setWorkbenchSize(2,2);
		microController.setWireSpritesToDefault(sprites.getSprite("pin mask.png"));
		microController.addPinStrip(35, 1, 10);
		microController.addPinStrip(72, 1, 8);
		microController.addPinStrip(50, 62, 8);
		microController.addPinStrip(81, 62, 5);
		
		
		breadboard.setOffsets(-2, 5);
		breadboard.setWorkbenchSize(2,2);
		breadboard.setWireSpritesToDefault(null);
		breadboard.setWireEnd(sprites.getSprite("wire end no pin.png"));
		breadboard.setWireEndOpaque(sprites.getSprite("wire end no pin opaque.png"));
		breadboard.addPins(new Grid(7,14,3,3,5,30));
		breadboard.addPins(new Grid(7,38,3,3,5,30));
		new Grid(10,4,18,3,1,5).forEach((x2,y2)->{
			breadboard.addPins(new Grid(x2,y2,3,3,2,5));
		});
		new Grid(10,57,18,3,1,5).forEach((x2,y2)->{
			breadboard.addPins(new Grid(x2,y2,3,3,2,5));
		});
		
		BiConsumer<Float,Float> addToInventory = (x2,y2) ->
		{
			InvenorySlot slot;
			if(Math.random()>.5)
			{
				slot = new InvenorySlot(x2,y2,invHighlight,new Item(microController),itemManip);
			}
			else
			{
				slot = new InvenorySlot(x2,y2,invHighlight,new Item(antenna),itemManip);
			}
			
			inventory.addChild(slot);
		};
		BiConsumer<Float,Float> addToPartMount = (x2,y2) ->
		{
			InvenorySlot slot;
			if(Math.random()>.5)
			{
				slot = new InvenorySlot(x2,y2,invHighlight,new Item(battery),itemManip);
			}
			else
			{
				slot = new InvenorySlot(x2,y2,invHighlight,new Item(breadboard),itemManip);
			}
			partMounting.addChild(slot);
		};
		new Grid(48,76,43,43,7,10).//grid for inventory window
		forEach(addToInventory);
		
		new Grid(178,65,43,42*2,4,4).//grid for top, left, right, bottom
		forEach(addToPartMount);
		
		new Grid(112,149,43,42,4,1).//grid for front
		forEach(addToPartMount);

		new Grid(373,149,43,42,4,1).//grid for back
		forEach(addToPartMount);
		
		new Grid(48,76,43,43,1,10).
		forEach((x2,y2)->{
			InvenorySlot slot = new InvenorySlot(x2,y2,invHighlight,null,itemManip);
			tools.addChild(slot);
		});
		
		Button[] toolButtons = new Button[10];
		
		new Grid(48,119,43,43,1,10).
		forEachWithIndicies((x2,y2,i,j)->{
			Button b = inv.build();
			b.setPos(x2, y2);
			toolButtons[i] = b;
			tools.addChild(b);
		});
		toolButtons[0].setOnPress(()->inventory.setEnabled(true));
		toolButtons[1].setOnPress(()->partMounting.setEnabled(true));
		toolButtons[3].setOnPress(()->{
			manager.setMode(wiring);
		});
	
		
		tools.addChild(new Entity(48,119,0,sprites.getSprite("icons.png")));
		grid = new ChassisGrid(40,20,1,
				sprites.getSprite("Chassis plate.png"),itemManip,manager,wiring,
				sprites.getSprite("wire segment x.png"),sprites.getSprite("wire segment y.png"),sprites.getSprite("wire segment z.png"));
		
		add(grid);
		addUI(partMounting);
		addUI(inventory);
		addUI(tools);
		Entity laptop = new Entity(700,100,1, sprites.getSprite("laptop.png"));
		addRenderable(laptop);
		BoxEntity tooltipBg = new BoxEntity(0,0,0,
				sprites.getSprite("fade corner.png"),
				sprites.getSprite("fade top.png"),
				sprites.getSprite("fade left.png"),
				sprites.getSprite("fade center.png"));
		
		
		
		addActable(mouseCompanion);
		TextEntity tooltip = new TextEntity(0,0,0,"Hello world!\nAnnother line!");
		tooltip.setBackGround(tooltipBg);
		mouseCompanion.setTo(tooltip);
		
		sprites.resetNamespace();

	}
	
}
