package state.viewport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.lwjgl.glfw.GLFW;

import levelgen.PartialMap;
import math.Matrix;
import entry.GlobalInput;
import graphics.Context;
import graphics.Sprite;
import graphics.entity.Entity;
import graphics.entity.FramerateEntity;
import graphics.entity.particles.Particle;
import graphics.entity.particles.ParticleBins;
import graphics.entity.particles.ParticleSystem;
import graphics.registry.SpriteAtlas;
import state.GameState;
import state.workbench.Camera;
import util.Color;
import util.CompoundBoundingBox;

public class ViewportState extends GameState
{
	Camera camera = new Camera(screenWidth()/2,screenHeight()/2,screenWidth(),screenHeight(),1);
	Tile[][] map;
	Unit robot;
	int robotX = 1;
	int robotY = 1;
	float scale = 4f;
	Clip bgMusic;
	List<Integer> pressQueue = new ArrayList<>();
	LightSystem lightSystem = new LightSystem(.1f);
	
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
		if(!robot.isMoving())
		{
			if(key == GLFW.GLFW_KEY_W)
			{
				robot.setDirection(Unit.NORTH);
				robot.setMoving(true);
				robot.tick(map);
			}
			else if(key == GLFW.GLFW_KEY_S)
			{
				robot.setDirection(Unit.SOUTH);
				robot.setMoving(true);
				robot.tick(map);
			}
			else if(key == GLFW.GLFW_KEY_A)
			{
				robot.setDirection(Unit.WEST);
				robot.setMoving(true);
				robot.tick(map);
			}
			else if(key == GLFW.GLFW_KEY_D)
			{
				robot.setDirection(Unit.EAST);
				robot.setMoving(true);
				robot.tick(map);
			}
		}
		else if(key == GLFW.GLFW_KEY_W || key == GLFW.GLFW_KEY_S || key == GLFW.GLFW_KEY_A || key == GLFW.GLFW_KEY_D)
		{
			pressQueue.add(key);
			while(pressQueue.size()>1)
			{
				pressQueue.remove(1);
			}
		}
	}
	
	public void afterInput(int dt)
	{
		if(!robot.isMoving())
		{
			if(isKeyPressed(GLFW.GLFW_KEY_W) && robot.getDirection() == Unit.NORTH)
			{
				robot.setMoving(true);
				robot.tick(map);
			}
			else if(isKeyPressed(GLFW.GLFW_KEY_S) && robot.getDirection() == Unit.SOUTH)
			{
				robot.setMoving(true);
				robot.tick(map);
			}
			else if(isKeyPressed(GLFW.GLFW_KEY_A) && robot.getDirection() == Unit.WEST)
			{
				robot.setMoving(true);
				robot.tick(map);
			}
			else if(isKeyPressed(GLFW.GLFW_KEY_D) && robot.getDirection() == Unit.EAST)
			{
				robot.setMoving(true);
				robot.tick(map);
			}
		}
		while(!robot.isMoving() && !pressQueue.isEmpty())
		{
			int key = pressQueue.remove(0);
			if(key == GLFW.GLFW_KEY_W)
			{
				robot.setDirection(Unit.NORTH);
				robot.setMoving(true);
				robot.tick(map);
			}
			else if(key == GLFW.GLFW_KEY_S)
			{
				robot.setDirection(Unit.SOUTH);
				robot.setMoving(true);
				robot.tick(map);
			}
			else if(key == GLFW.GLFW_KEY_A)
			{
				robot.setDirection(Unit.WEST);
				robot.setMoving(true);
				robot.tick(map);
			}
			else if(key == GLFW.GLFW_KEY_D)
			{
				robot.setDirection(Unit.EAST);
				robot.setMoving(true);
				robot.tick(map);
			}
		}
		if(robot.goal != null && robot.goal.getUnit()!=null)
		{
			robot.goal.destroyUnit(0, 0);
		}
		camera.x = ((robot.getX()+.5f)*16+robot.xOffset)*scale;
		camera.y = ((robot.getY()+.5f)*16+robot.yOffset)*scale;
		lightSystem.act(dt);
	}
	
	public void cleanup()
	{
		if(bgMusic!=null)
		{
			bgMusic.stop();
		}
	}
	
	public void init(SpriteAtlas sprites)
	{
		try
		{
			File file = new File("res/mus/Overworld_Theme.wav");
			AudioInputStream sound = AudioSystem.getAudioInputStream(file);
			bgMusic = AudioSystem.getClip();
			bgMusic.open(sound);
			bgMusic.setFramePosition(0);
			bgMusic.loop(Clip.LOOP_CONTINUOUSLY);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		sprites.setNamespace("res/sprite/level/");

		MapFactory factory = new MapFactory(
				sprites.getSprites("surface_wall_%d.png"),
				sprites.getSprites("surface_front_%d.png"),
				sprites.getSprites("surface_floor_%d.png")
				);
		
		UnitSprites robotSprites = new UnitSprites(sprites,"robot_%d.png",16,4);
		robot = new Unit(robotSprites);
		
		List<Sprite> mushrooms = sprites.getSprites("mushroom_%d.png");
		List<Sprite[]> spores = sprites.getAnimations("spore_%d.png", 9);
		
		PartialMap mapBuilder = new PartialMap();
		for(int i = 0; i<100; i++)
		{
			mapBuilder.addNewRoom();
		}
		//mapBuilder.dump();
		Tile[][] map2;
		//map2 = mapBuilder.getMap(factory);
		map = mapBuilder.getMapPerfect(factory);
		robotX = mapBuilder.getStartX(map);
		robotY = mapBuilder.getStartY(map);
		System.out.println("Robot coords: " + robotX + ", " + robotY);
		
		int mapWidth = map.length;
		int mapHeight = map[0].length;
		
		
		for(int x = 0; x<mapWidth; x++)
		{
			for(int y = 0; y<mapHeight; y++)
			{
				if(map[x][y].isFloor())
				{
					if(Math.random()<.1)
					{
						map[x][y].setUnit(new Unit(new UnitSprites(new Entity(0,0,0,MapFactory.selectRandom(mushrooms)))));
					}
					else if(Math.random()<.2)
					{
						map[x][y].setUnit(new Unit(new UnitSprites(new FramerateEntity(0,0,0,MapFactory.selectRandom(spores),9,(float) Math.random(),30))));
					}
				}
			}
		}
		map[mapBuilder.getStartX()][mapBuilder.getStartY()].setUnit(robot);
		
		ParticleSystem ambientSpores = new ParticleSystem(
				0, 0, 16*6,
				100,
				16*mapWidth, 16*mapHeight, 0, 
				sprites.getSprites("spore_particle_%d.png").toArray(new Sprite[0]))
		{

			public Particle createParticle()
			{
				Sprite s = this.getRandomParticleSrpite();
				float x = this.getRandX();
				float y = this.getRandY();
				float z = this.getRandZ();
				Particle toReturn = new Particle(x,y,z,s,(int)(Math.random()*60000),this)
				{
					float dz = (float) (-Math.random()*.005)-.005f;
					float tx = (float) (Math.random()*.001);
					float ax = (float) (Math.random()*.009);
					float ty = (float) (Math.random()*.001);
					float ay = (float) (Math.random()*.009);
					float alpha = 1;
					public void update(int dt, int lifeTime, float x, float y,float z)
					{
						if(this.hasBounds() && this.getBounds().onBoundZ(z))
						{
							return;
						}
						if(this.hasBounds() && !this.getBounds().inBounds(x, y, z))
						{
							alpha *= .99f;
							this.setColor(Color.changeAlpha(this.getColor(),alpha));
							if(alpha<.05)
							{
								kill();
								return;
							}
						}
						else if(alpha != 1)
						{
							alpha += (1-alpha)*.01f;
							this.setColor(Color.changeAlpha(this.getColor(),alpha));
						}
						
						float dx = (float) (Math.sin(lifeTime*tx)*ax);
						float dy = (float) (Math.sin(lifeTime*ty)*ay);
						tx+=(Math.random()-.5)*.00001;
						ax+=(Math.random()-.5)*.00001;
						ty+=(Math.random()-.5)*.00001;
						ay+=(Math.random()-.5)*.00001;
						
						setZ(Math.max(z+dz*dt,0));
						setX(x+dx*dt);
						setY(y+dy*dt);
					}
				};
				toReturn.setScale((float) Math.random());
				float color = (float) ((Math.random()-.5)*.25+.75);
				toReturn.setColor(new Color(
						(float) (color + (Math.random()-.5)*.0125)
						,(float) (color*1.1 + (Math.random()-.5)*.0125)
						,(float) (color*1.3 + (Math.random()-.5)*.0125)
						,1));
				return toReturn;
			}

			public void update(int dt)
			{
				for(int i = 0; i<dt; i++)
				{
					if(Math.random()<.1f)
					{
						spawnParticle();
					}
				}
			}
			
		};
		ambientSpores.setBins(new ParticleBins(16, mapWidth, mapHeight));
		ambientSpores.setAllowOOB(true);
		
		CompoundBoundingBox levelBox = MapFactory.initNeighbors(map);
		
		ambientSpores.setBounds(levelBox);
		
		lightSystem.addLight(new LightSource(robot,5f));
		
		MapEntity me = new MapEntity(0,0,0,scale,map,camera);
		me.setAmbientParticles(ambientSpores);
		me.setLightSystem(lightSystem);
		me.setCenter(robot, 5);
		
		add(me);
		
		
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
