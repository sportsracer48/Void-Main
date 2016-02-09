package state.viewport;

import state.workbench.Camera;
import util.Color;
import game.map.Tile;
import game.map.Unit;
import graphics.Context;
import graphics.entity.Entity;
import graphics.entity.particles.ParticleSystem;

public class MapEntity extends Entity
{
	Tile[][] grid;
	ParticleSystem ambientParticles;
	Camera camera;
	float scale;
	
	LightSystem lightSystem;
	
	Unit center;
	int vision;
	
	public MapEntity(float x, float y, float z, float scale, Tile[][] map, Camera camera)
	{
		super(x, y, z, null);
		this.grid = map;
		this.scale = scale;
		this.camera = camera;
		this.setScale(scale, scale);
	}
	
	public void setCenter(Unit center, int vision)
	{
		this.center = center;
		this.vision = vision;
	}
	
	public void setAmbientParticles(ParticleSystem ambient)
	{
		this.ambientParticles = ambient;
	}
	
	public void setLightSystem(LightSystem system)
	{
		this.lightSystem = system;
	}
	
	public boolean shouldRender(Tile t)
	{
		float x = t.x*t.spriteSize*scale;
		float y = t.y*t.spriteSize*scale;
		float width = t.spriteSize*scale;
		float height = t.spriteSize*scale;
		if(t.isWall())
		{
			y -= t.wallHeight*scale;
			height += t.wallHeight*scale;
		}
		return camera.intersects(
				x,y,width,height);
	}
	public boolean isVisible(Tile t)
	{
		if(center == null)
		{
			return false;
		}
		float dx = t.x-center.getX();
		float dy = t.y-center.getY();
		return dx*dx+dy*dy<vision*vision;
	}
	
	public void renderChildren(Context c)
	{
		for(Tile[] col:grid)
		{
			for(Tile tile:col)
			{
				if(shouldRender(tile))
				{
					if(isVisible(tile))
					{
						float color = lightSystem.getIntensity(tile.x, tile.y);
						c.setGroupColor(new Color(color,color,color,1));
					}
					else
					{
						float color = lightSystem.getIntensity(tile.x, tile.y);
						c.setGroupColor(new Color(color,color,color,1));
					}
					if(tile.isExplored())
					{
						tile.renderFloor(c);
					}
					c.resetGroupColor();
				}
			}
		}
		
		for(Tile[] col:grid)
		{
			for(Tile tile:col)
			{
				if(shouldRender(tile))
				{
					if(isVisible(tile))
					{
						float color = lightSystem.getIntensity(tile.x, tile.y);
						c.setGroupColor(new Color(color,color,color,1));
					}
					else
					{
						float color = lightSystem.getIntensity(tile.x, tile.y);
						c.setGroupColor(new Color(color,color,color,1));
					}
					if(tile.isExplored())
					{
						tile.renderParticles(c);
					}
					c.resetGroupColor();
				}
			}
		}
		
		for(int y = 0; y<grid[0].length; y++)
		{
			for(int x = 0; x<grid.length; x++)
			{
				if(shouldRender(grid[x][y]))
				{
					if(isVisible(grid[x][y]))
					{
						float color = lightSystem.getIntensity(x, y);
						c.setGroupColor(new Color(color,color,color,1));
					}
					else
					{
						float color = .5f*lightSystem.getIntensity(x, y);
						c.setGroupColor(new Color(color,color,color,1));
					}
					if(ambientParticles.hasBins())
					{
						ambientParticles.getBins().renderBin(c, x, y);
					}
					if(grid[x][y].isExplored())
					{
						grid[x][y].renderWall(c);
					}
					if(isVisible(grid[x][y]))
					{
						grid[x][y].renderUnit(c);
					}
					c.resetGroupColor();
				}
			}
		}
		if(!ambientParticles.hasBins())
		{
			ambientParticles.render(c);
		}
	}
	public void act(int dt)
	{
		for(Tile[] col:grid)
		{
			for(Tile tile:col)
			{
				if(isVisible(tile))
				{
					tile.setExplored(true);
				}
				tile.act(dt);
			}
		}
		ambientParticles.act(dt);
	}
}
