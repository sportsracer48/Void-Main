package state.viewport;

import java.util.List;

import graphics.Sprite;
import graphics.entity.Entity;
import graphics.entity.FramerateEntity;
import graphics.registry.SpriteAtlas;

public class UnitSprites
{
	FramerateEntity[] entities;
	Entity[] stillEntities;
	Entity onlyEntity;
	
	public UnitSprites(SpriteAtlas sprites, String prefix, float fps,int frames)
	{
		entities = new FramerateEntity[4];
		stillEntities = new Entity[4];
		List<Sprite[]> animationList = sprites.getAnimations(prefix, frames);
		for(int i = 0; i<entities.length; i++)
		{
			entities[i] = new FramerateEntity(0,0,0,animationList.get(i),fps);
			entities[i].setPos(0, 16-entities[i].getHeight());
			stillEntities[i] = new Entity(0,0,0,animationList.get(i)[0]);
			stillEntities[i].setPos(0, 16-stillEntities[i].getHeight());
		}
	}
	
	public Entity getEntity(int direction, boolean moving)
	{
		if(moving)
		{
			return getMovingEntity(direction);
		}
		else
		{
			return getStillEntity(direction);
		}
	}
	
	public UnitSprites(Entity onlyEntity)
	{
		this.onlyEntity = onlyEntity;
	}
	
	public Entity getOnlyEntity()
	{
		return onlyEntity;
	}
	public Entity getMovingEntity(int direction)
	{
		if(entities == null)
		{
			return onlyEntity;
		}
		return entities[direction];
	}
	public Entity getStillEntity(int direction)
	{
		if(stillEntities == null)
		{
			return onlyEntity;
		}
		return stillEntities[direction];
	}
}
