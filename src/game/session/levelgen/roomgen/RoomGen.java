package game.session.levelgen.roomgen;

import java.util.function.Supplier;

import game.map.Map;
import game.map.unit.Unit;
import game.session.levelgen.MapConfig;
import game.session.levelgen.MapUtil;
import graphics.Sprite;
import graphics.entity.particles.Particle;
import graphics.entity.particles.ParticleBins;
import graphics.entity.particles.ParticleSystem;
import state.viewport.LightSystem;
import state.viewport.MapEntity;
import state.workbench.Camera;
import util.Color;
import util.CompoundBoundingBox;

public class RoomGen
{
	public final Map map;
	public final ParticleSystem ambientSpores;
	public final MapConfig config;
	public RoomGen(int roomSize, int rooms, MapConfig config)
	{
		PartialMap partial = new PartialMap(roomSize);
		for(int i = 0; i<rooms; i++)
		{
			partial.addNewRoom();
		}
		map = partial.getMap(config);
		this.config = config;
		
		ambientSpores = buildParticleSystem(map.getWidth(),map.getHeight(),config);
		
		CompoundBoundingBox levelBox = MapUtil.initNeighbors(map.tiles);
		
		ambientSpores.setBounds(levelBox);
	}
	
	
	public void populate(Supplier<Unit> supplier, double freq)
	{
		for(int x = 0; x<map.getWidth(); x++)
		{
			for(int y = 0; y<map.getHeight(); y++)
			{
				if(map.tiles[x][y].isFloor() && map.tiles[x][y].getUnit() == null)
				{
					if(Math.random()<freq)
					{
						map.tiles[x][y].setUnit(supplier.get());
					}
				}
			}
		}
	}
	
	public Map getMap()
	{
		return map;
	}
	
	public MapEntity build(float scale, Camera camera, LightSystem lightSystem)
	{
		MapEntity me = new MapEntity(0,0,0,scale,map.tiles,camera);
		me.setAmbientParticles(ambientSpores);
		me.setLightSystem(lightSystem);
		return me;
	}
	
	public static MapEntity rebuild(Map map, float scale, Camera camera, LightSystem lightSystem)
	{
		MapEntity me = new MapEntity(0,0,0,scale,map.tiles,camera);
		
		ParticleSystem ambientSpores = buildParticleSystem(map.getWidth(),map.getHeight(),map.getConfig());
		CompoundBoundingBox levelBox = MapUtil.initNeighbors(map.tiles);
		ambientSpores.setBounds(levelBox);
		me.setAmbientParticles(ambientSpores);
		me.setLightSystem(lightSystem);
		return me;
	}
	
	public static ParticleSystem buildParticleSystem(int mapWidth, int mapHeight, MapConfig config)
	{
		ParticleSystem ambientSpores = new ParticleSystem(
				0, 0, 16*6,
				100,
				16*mapWidth, 16*mapHeight, 0, 
				config.getAmbientParticles().toArray(new Sprite[0]))
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
		return ambientSpores;
	}
}
