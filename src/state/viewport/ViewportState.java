package state.viewport;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import math.Matrix;
import entry.GlobalInput;
import graphics.Context;
import graphics.Sprite;
import graphics.entity.Entity;
import graphics.entity.FramerateEntity;
import graphics.entity.Particle;
import graphics.entity.ParticleSystem;
import graphics.registry.SpriteAtlas;
import state.GameState;
import state.workbench.Camera;

public class ViewportState extends GameState
{
	Tile[][] map;
	Camera camera = new Camera(screenWidth()/2,screenHeight()/2,screenWidth(),screenHeight(),1);
	
	Entity robot;
	int robotX = 1;
	int robotY = 1;
	
	public ViewportState(GlobalInput input, long window)
	{
		super(input, window);
	}
	
	public void keyPressed(int key)
	{
		if(key == GLFW.GLFW_KEY_ESCAPE)
		{
			systemExit();
		}
		int dxRobot = 0;
		int dyRobot = 0;
		if(key == GLFW.GLFW_KEY_W)
		{
			dyRobot = -1;
		}
		if(key == GLFW.GLFW_KEY_S)
		{
			dyRobot = 1;
		}
		if(key == GLFW.GLFW_KEY_A)
		{
			dxRobot = -1;
		}
		if(key == GLFW.GLFW_KEY_D)
		{
			dxRobot = 1;
		}
		if(dxRobot != 0 || dyRobot != 0)
		{
			Tile start = map[robotX][robotY];
			Tile target = map[robotX+dxRobot][robotY+dyRobot];
			if(target.unit != null)
			{
				target.destroyUnit();
			}
			else
			{
				robotX+=dxRobot;
				robotY+=dyRobot;
				start.unit = null;
				target.unit = robot;
			}
			
			
		}
	}
	
	public void init(SpriteAtlas sprites)
	{
		sprites.setNamespace("res/sprite/level/");
		
		FloorTileType floor = new FloorTileType(sprites,"surface_floor_%d.png");
		WallTileType wall = new WallTileType(sprites,"surface_wall_%d.png","surface_front_%d.png");
		List<Sprite> mushroomSprites = sprites.getSprites("mushroom_%d.png");
		List<Sprite[]> sporeSprites = sprites.getAnimations("spore_%d.png",9);
		ParticleSystem ambientSpores = new ParticleSystem(
				64,64,1024,20000,
				6*8*16,6*8*16,100,
				new Sprite[]{sprites.getSprite("spore_particle_0.png"),sprites.getSprite("spore_particle_1.png")})
		{
			public Particle createParticle()
			{
				float x = this.getRandX();
				float y = this.getRandY();
				float z = this.getRandZ();
				
				Sprite sprite = this.getRandomParticleSrpite();
				Particle result = new Particle(x,y,z,sprite,60000,this)
				{
					int timeOnGround = 0;
					float phaseAngle = (float) (Math.random()*2*Math.PI);
					public void update(int dt, int lifeTime, float x, float y, float z)
					{
						float dx = (float) (Math.cos(lifeTime*.0005f+phaseAngle))*.04f;
						if(this.z == 0)
						{
							dx = 0;
						}
						float dz = -.05f;
						setX(x+dx*dt);
						setZ(z+dz*dt);
						if(this.z<0)
						{
							setZ(0);
							timeOnGround+=dt;
							if(timeOnGround>5000)
							{
								kill();
							}
						}
					}
				};
				result.setScale(4);
				return result;
			}

			public void update(int dt)
			{
				for(int i = 0; i<dt; i++)
				{
					if(Math.random()<.001)
					{
						spawnParticle();
					}
				}
			}
		
		};
		map = new Tile[8][8];
		for(int x = 0; x<map.length; x++)
		{
			for(int y = 0; y<map[0].length; y++)
			{
				if(x == 0 || y == 0 || x == 7 || y == 7)
				{
					map[x][y] = wall.makeTile();
				}
				else
				{
					map[x][y] = floor.makeTile();
					if(Math.random()>.5)
					{
						map[x][y].setUnit(new Entity(0,0,0,mushroomSprites.get((int)(Math.random()*mushroomSprites.size()))));
					}
					else if(Math.random()>.75)
					{
						map[x][y].setUnit(new FramerateEntity(0,0,0,sporeSprites.get((int)(Math.random()*sporeSprites.size())),7f,(float)Math.random(),30));
					}
				}
			}
		}
		robot = new Entity(0,0,0,sprites.getSprite("robot.png"));
		map[1][1].setUnit(robot);
		
		MapEntity me = new MapEntity(0,0,0,map);
		
		add(me);
		add(ambientSpores);
		for(int i = 0; i<5000; i++)
		{
			ambientSpores.act(7);
		}
		
		sprites.resetNamespace();
	}

	public void renderAll(Context context)
	{
		context.resetColor();
		context.setView(camera.getView());
		context.setModel(Matrix.identity(4));
		render(context);
		context.setView(Matrix.identity(4));
		renderUI(context);
	}

}
