package state.viewport;

import util.DestructionParticle;
import math.Matrix;
import graphics.Context;
import graphics.Sprite;
import graphics.entity.Entity;
import graphics.entity.Particle;
import graphics.entity.ParticleSystem;
import graphics.registry.UtilSprites;

public class Tile
{
	TileType type;
	Entity tile;
	Entity unit;
	ParticleSystem tileParticles;
	
	public Tile(TileType type)
	{
		this.type = type;
		this.tile = type.getTileEntity();
		this.tile.setScale(8);
		
		tileParticles = new ParticleSystem(0)
		{
			public Particle createParticle()
			{
				return null;
			}

			public void update(int dt)
			{
				
			}
		};
	}
	
	public void addDecoration(Entity dec)
	{
		tile.addChild(dec);
	}
	
	public void setUnit(Entity unit)
	{
		unit.setScale(8);
		this.unit = unit;
	}
	
	public void renderTile(Context c, int x, int y, float scale, int tileSize)
	{
		c.pushTransform();
		c.prependTransform(Matrix.translation(x*scale*tileSize,y*scale*tileSize,0));
		tile.render(c);
		c.popTransform();
	}
	public void returnUnit(Context c, int x, int y, float scale, int tileSize)
	{
		c.pushTransform();
		c.prependTransform(Matrix.translation(x*scale*tileSize,y*scale*tileSize,0));
		tileParticles.render(c);
		if(unit != null)
		{
			unit.render(c);
		}
		c.popTransform();
	}
	public void destroyUnit()
	{
		DestructionParticle.spawnParticlesFor(unit.getBase(), 8, new Sprite[]{UtilSprites.white}, tileParticles);
		unit = null;
	}

	public void act(int dt)
	{
		tile.act(dt);
		if(unit!=null)
		{
			unit.act(dt);
		}
		tileParticles.act(dt);
	}
}
