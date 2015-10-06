package state.workbench;

import org.lwjgl.glfw.GLFW;

import entry.GlobalInput;
import graphics.entity.Entity;
import graphics.registry.SpriteAtlas;
import state.GameState;

public class WorkbenchState extends GameState
{
	public WorkbenchState(GlobalInput input)
	{
		super(input);
	}
	Entity chassis;
	
	public void mousePressed(int button)
	{
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
		{
			chassis.moveTo(getMouseX(), getMouseY());
		}
	}
	
	
	public void init(SpriteAtlas sprites)
	{
		sprites.setNamespace("res\\sprite\\workbench\\");
		Entity bg = new Entity(0, 75, 0, sprites.getSprite("background.png"));
		addRenderable(bg);
		chassis = new Entity(120,100,1,sprites.getSprite("Chassis plate.png"));
		chassis.addChild(new Entity(20,-50,1,sprites.getSprite("mount-ui-parts_02.png")));
		chassis.addChild(new Entity(20,200,1,sprites.getSprite("mount-ui-parts_05.png")));
		chassis.addChild(new Entity(150,-50,1,sprites.getSprite("mount-ui-parts_03.png")));
		chassis.addChild(new Entity(150,200,1,sprites.getSprite("mount-ui-parts_06.png")));
		chassis.addChild(new Entity(-100,90,1,sprites.getSprite("mount-ui-parts_01.png")));
		chassis.addChild(new Entity(310,90,1,sprites.getSprite("mount-ui-parts_04.png")));
		addRenderable(chassis);
		Entity laptop = new Entity(600,100,1, sprites.getSprite("laptop.png"));
		addRenderable(laptop);
		
		sprites.resetNamespace();
	}
	
}
