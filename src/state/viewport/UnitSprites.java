package state.viewport;

import java.util.List;

import graphics.Sprite;
import graphics.entity.Entity;
import graphics.entity.FramerateEntity;

public class UnitSprites
{
	FramerateEntity[] entities;
	Entity[] stillEntities;
	Entity onlyEntity;
	public final int id;
	
	public UnitSprites(int id,List<Sprite[]> animationList, float fps)
	{
		entities = new FramerateEntity[4];
		stillEntities = new Entity[4];
		this.id = id;
		for(int i = 0; i<entities.length; i++)
		{
			entities[i] = new FramerateEntity(0,0,0,animationList.get(i),fps);
			entities[i].setPos(0, 16-entities[i].getHeight());
			stillEntities[i] = new Entity(0,0,0,animationList.get(i)[0]);
			stillEntities[i].setPos(0, 16-stillEntities[i].getHeight());
		}
	}
	
	public UnitSprites(int id,Entity onlyEntity)
	{
		this.onlyEntity = onlyEntity;
		this.id = id;
	}
	
	public Entity getEntity(int direction, boolean moving)
	{
		return getMovingEntity(direction);
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
