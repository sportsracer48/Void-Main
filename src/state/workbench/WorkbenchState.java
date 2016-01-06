package state.workbench;

import java.util.List;

import math.Matrix;

import org.lwjgl.glfw.GLFW;

import program.ProgramCoordinator;
import entry.GlobalInput;
import entry.GlobalState;
import graphics.Context;
import graphics.Sprite;
import graphics.entity.Entity;
import graphics.entity.FluidEntity;
import graphics.entity.TextEntity;
import graphics.registry.SpriteAtlas;
import state.GameState;
import state.Mode;
import state.ModeManager;
import state.ui.ClickableArea;
import state.ui.MouseoverContext;
import state.workbench.controller.DragContext;
import state.workbench.controller.ItemManipulator;
import state.workbench.game.ChassisGrid;
import state.workbench.game.EditHistory;
import state.workbench.game.WiringMode;
import util.Color;
import static state.workbench.ItemTypes.*;

public class WorkbenchState extends GameState
{
	public WorkbenchState(GlobalInput input, long window)
	{
		super(input,window);
		ui = uiList.getList();
		screen = renderList.getList();
		globalClickArea.setDesiresMouse(false);
	}
	Camera camera = new Camera(screenWidth()/4,screenHeight()/4,screenWidth(),screenHeight(),2);
	
	Matrix uiView = Matrix.identity(4);
	
	Entity inventory,partMounting,tools;
	
	float worldHeight = 540;
	float worldWidth = 1920;
	
	DragContext grabContext = new DragContext();
	MouseoverContext mouseContext = new MouseoverContext();
	ClickableArea globalClickArea = new ClickableArea(0,0,0,0);
	ItemManipulator itemManip = new ItemManipulator(grabContext,this,globalClickArea);
	EditHistory history = new EditHistory();
	ProgramCoordinator coordinator = new ProgramCoordinator();
	
	FluidEntity mouseCompanion = new FluidEntity(0,0,0);
	
	Sprite wireSymbol;
	Sprite programmingSymbol;
	
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
	WiringMode wiring = new WiringMode(mouseCompanion,this,history,screenWidth(),screenHeight())
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
	
	Mode programming = new Mode()
	{

		public void enable()
		{
			mouseCompanion.setSpriteAndSize(programmingSymbol);
		}

		public void disable()
		{
			mouseCompanion.setSpriteAndSize(null);
			mouseCompanion.setColor(Color.white);
		}
		
	};
	
	ModeManager manager = new ModeManager(edit);
	
	List<Entity> ui;
	List<Entity> screen;
	
	ChassisGrid grid;
	
	ZoomTransition programmingTransition = new ZoomTransition(camera,700,100);
	
	public void keyPressed(int key)
	{
		if(programmingTransition.isRunning())
		{
			return;
		}
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
			else if(manager.getMode() == programming)
			{
				manager.setMode(edit);
			}
			else
			{
				systemExit();
			}
		}
		else if(key == GLFW.GLFW_KEY_KP_ADD || key == GLFW.GLFW_KEY_EQUAL)
		{
			camera.scale*=2;
		}
		else if(key == GLFW.GLFW_KEY_KP_SUBTRACT || key == GLFW.GLFW_KEY_MINUS)
		{
			camera.scale/=2;
		}
		else if(key == GLFW.GLFW_KEY_Z && (isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) || isKeyPressed(GLFW.GLFW_KEY_RIGHT_CONTROL)))
		{
			history.undo();
		}
		else if(key == GLFW.GLFW_KEY_Y && (isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) || isKeyPressed(GLFW.GLFW_KEY_RIGHT_CONTROL)))
		{
			history.redo();
		}
		else if(key == GLFW.GLFW_KEY_E)
		{
			manager.setMode(wiring);
		}
		else if(key == GLFW.GLFW_KEY_Q)
		{
			manager.setMode(edit);
		}
		else if(key == GLFW.GLFW_KEY_R)
		{
			manager.setMode(programming);
		}
	}
	
	public Matrix worldMouse()
	{
		Matrix mousePos = new Matrix(new float[]{getMouseX(),getMouseY(),0,1});
		return camera.getInverseView().dot(mousePos);
	}
	
	public void mousePressed(int button)
	{
		if(programmingTransition.isRunning())
		{
			return;
		}
		Matrix worldMouse = worldMouse();
		globalClickArea.handleClick(getMouseX(), getMouseY(),button, Matrix.identity(4));
		if(mouseContext.hasMouseHolder())
		{
			clickableSearch://strange never before used feature of java!
			{
				for(int i = ui.size()-1; i>=0; i--)
				{
					if(ui.get(i).handleClick(getMouseX(), getMouseY(), button))
					{
						break clickableSearch;
					}
				}
				for(int i = screen.size()-1; i>=0; i--)
				{
					if(screen.get(i).handleClick(worldMouse.x(), worldMouse.y(), button))
					{
						break clickableSearch;
					}
				}
			}
		}
	
	}
	
	public void mouseReleased(int button)
	{
		if(programmingTransition.isRunning())
		{
			return;
		}
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
		if(programmingTransition.isRunning())
		{
			return;
		}
		Matrix worldMouse = worldMouse();
		mouseCompanion.setPos(Math.round(x+16), Math.round(y+16));
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
		if(programmingTransition.isRunning())
		{
			if(programmingTransition.isDone())
			{
				changeTo(GlobalState.currentProgramming);
				programmingTransition.reset();
			}
			else
			{
				programmingTransition.act(dt);
			}
			return;
		}
		float cameraSpeed = 1f/camera.scale;//pixels per millisecond
		if(
				(isKeyPressed(GLFW.GLFW_KEY_W) ^ isKeyPressed(GLFW.GLFW_KEY_S)) &&
				(isKeyPressed(GLFW.GLFW_KEY_A) ^ isKeyPressed(GLFW.GLFW_KEY_D))
			)
		{
			cameraSpeed /= 1.4142135f;//sqrt(2)
		}
		if(isKeyPressed(GLFW.GLFW_KEY_W) && !isKeyPressed(GLFW.GLFW_KEY_S))
		{
			camera.y -= dt*cameraSpeed;
		}
		if(isKeyPressed(GLFW.GLFW_KEY_S) && !isKeyPressed(GLFW.GLFW_KEY_W))
		{
			camera.y += dt*cameraSpeed;
		}
		if(isKeyPressed(GLFW.GLFW_KEY_A) && !isKeyPressed(GLFW.GLFW_KEY_D))
		{
			camera.x -= dt*cameraSpeed;
		}
		if(isKeyPressed(GLFW.GLFW_KEY_D) && !isKeyPressed(GLFW.GLFW_KEY_A))
		{
			camera.x += dt*cameraSpeed;
		}
		
		if(camera.getLeftX()<0)
		{
			camera.setLeftX(0);
		}
		if(camera.getTopY()<0)
		{
			camera.setTopY(0);
		}
		
		if(camera.getRightX()>worldWidth)
		{
			camera.setRightX(worldWidth);
		}
		if(camera.getBottomY()>worldHeight)
		{
			camera.setBottomY(worldHeight);
		}
		
		if(camera.getScreenHeight()>worldHeight)
		{
			camera.y=worldHeight/2;
		}
		if(camera.getScreenWidth()>worldWidth)
		{
			camera.x=worldWidth/2;
		}
		

		
		mouseMoved(getMouseX(),getMouseY());
		mouseContext.setFrozen(false);
		itemManip.act(dt);
		coordinator.act(dt);
		
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
		context.setView(camera.getView());
		context.setModel(Matrix.identity(4));
		render(context);
		
		context.setView(uiView);
		renderUI(context);
		
		mouseCompanion.render(context);//the highest of them all
	}
	
	public void init(SpriteAtlas sprites)
	{
		sprites.setNamespace("res/sprite/workbench/");
		
		// style init
		
		Entity bg = new Entity(0, 0, 0, sprites.getSprite("background.png"));
		addRenderable(bg);
		ButtonThemes.init(sprites);
		
		// initialize all the item types
		ItemTypes.init(sprites);
		
		// root children init
		
		WindowBuilder windowBuilder = new WindowBuilder(sprites, itemManip, grabContext, screenHeight());
		
		partMounting = windowBuilder.partMounting;
		
		inventory = windowBuilder.inventory;
		
		tools = windowBuilder.tools;
		
		grid = new ChassisGrid(40,5,1,
				sprites.getSprite("Chassis plate.png"),itemManip,manager,wiring,programming,history,coordinator,
				sprites.getSprite("wire segment x.png"),sprites.getSprite("wire segment y.png"),sprites.getSprite("wire segment z.png"),
				programmingTransition);
		
		grid.addExternalBreakouts(breakout,sprites.getSprite("extern pin.png"),sprites.getSprite("breakout bg.png"), 
				windowBuilder.top, windowBuilder.bot, windowBuilder.left, windowBuilder.right, windowBuilder.front, windowBuilder.back);
		
		
		ToolInitializer.init(tools, sprites, inventory, partMounting, manager, wiring, programming, history);
		
		//scene init
		add(grid);
		addUI(partMounting);
		addUI(inventory);
		addUI(tools);
		Entity laptop = new Entity(700,100,1, sprites.getSprite("laptop.png"));
		addRenderable(laptop);
		
		wireSymbol = sprites.getSprite("wire symbol.png");
		programmingSymbol = sprites.getSprite("programming symbol.png");
		addActable(mouseCompanion);
		TextEntity.setDefaultBgSprites(
				sprites.getSprite("fade corner.png"),
				sprites.getSprite("fade top.png"),
				sprites.getSprite("fade left.png"),
				sprites.getSprite("fade center.png"));
		
		sprites.resetNamespace();

	}

	
}
