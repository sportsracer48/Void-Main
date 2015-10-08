package state.workbench;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import entry.GlobalInput;
import graphics.entity.Entity;
import graphics.registry.SpriteAtlas;
import state.GameState;
import state.ui.ClickableArea;

public class WorkbenchState extends GameState
{
	public WorkbenchState(GlobalInput input)
	{
		super(input);
	}
	Entity grabbed;
	float grabOffsetX, grabOffsetY;
	
	List<Entity> ui = new ArrayList<Entity>();
	List<Entity> screen = new ArrayList<Entity>();
	
	Entity chassis;
	Entity window;
	Entity buttonRaised;
	Entity buttonPressedSelected;
	Entity buttonPressedUnselected;
	
	public void keyPressed(int key)
	{
		if(key == GLFW.GLFW_KEY_E)
		{
			window.setVisible(true);
		}
	}
	
	public void mousePressed(int button)
	{
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
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
			grabbed = null;
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
		if(grabbed != null)
		{
			grabbed.moveTo(x-grabOffsetX, y-grabOffsetY);
		}
		for(Entity e: ui)
		{
			e.handleMove(x, y);
		}
		for(Entity e:screen)
		{
			e.handleMove(x, y);
		}
	}
	
	
	public void init(SpriteAtlas sprites)
	{
		sprites.setNamespace("res\\sprite\\workbench\\");
		Entity bg = new Entity(0, 75, 0, sprites.getSprite("background.png"));
		addRenderable(bg);
		chassis = new Entity(120,100,1,sprites.getSprite("Chassis plate.png"));
		
		
		
		window = new Entity(120,100,10,sprites.getSprite("part mounting ui.png"));
		buttonRaised = new Entity(480,17,10,sprites.getSprite("Button raised.png"));
		buttonPressedSelected = new Entity(480,17,10,sprites.getSprite("Button pressed selected.png"));
		buttonPressedUnselected = new Entity(480,17,10,sprites.getSprite("Button pressed unselected.png"));
		buttonRaised.setVisible(false);
		buttonPressedSelected.setVisible(false);
		buttonPressedUnselected.setVisible(false);
		window.addChild(buttonRaised);
		window.addChild(buttonPressedSelected);
		window.addChild(buttonPressedUnselected);
		window.addClickableArea(new ClickableArea(17,17,461,23){

			public void mouseEntered()
			{
			}

			public void mouseExited()
			{
			}

			public void onClick(float x, float y)
			{
				grabbed = window;
				grabOffsetX = x+17;
				grabOffsetY = y+17;
			}

			public void onRelease()
			{
			}
		});
		window.addClickableArea(new ClickableArea(480,17,23,23){
			public void mouseEntered()
			{
				if(!mouseHeld)
				{
					buttonRaised.setVisible(true);
					buttonPressedSelected.setVisible(false);
					buttonPressedUnselected.setVisible(false);
				}
				else
				{
					buttonRaised.setVisible(false);
					buttonPressedSelected.setVisible(true);
					buttonPressedUnselected.setVisible(false);
				}
			}

			public void mouseExited()
			{
				if(!mouseHeld)
				{
					buttonRaised.setVisible(false);
					buttonPressedSelected.setVisible(false);
					buttonPressedUnselected.setVisible(false);
				}
				else
				{
					buttonRaised.setVisible(false);
					buttonPressedSelected.setVisible(false);
					buttonPressedUnselected.setVisible(true);
				}
			}

			public void onClick(float x, float y)//always contains mouse
			{
				buttonRaised.setVisible(false);
				buttonPressedSelected.setVisible(true);
				buttonPressedUnselected.setVisible(false);
			}

			public void onRelease()
			{
				if(containsMouse)
				{
					buttonRaised.setVisible(false);
					buttonPressedSelected.setVisible(false);
					buttonPressedUnselected.setVisible(false);
					window.setVisible(false);
				}
				else
				{
					buttonRaised.setVisible(false);
					buttonPressedSelected.setVisible(false);
					buttonPressedUnselected.setVisible(false);
				}
				
			}
			
		});
		
		ui.add(window);
		
		addRenderable(chassis);
		addUI(window);
		Entity laptop = new Entity(600,100,1, sprites.getSprite("laptop.png"));
		addRenderable(laptop);
		
		sprites.resetNamespace();
	}
	
}
