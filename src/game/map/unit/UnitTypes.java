package game.map.unit;

import java.util.List;

import state.viewport.UnitSprites;
import game.session.levelgen.MapUtil;
import graphics.Sprite;
import graphics.entity.Entity;
import graphics.entity.FramerateEntity;
import graphics.registry.SpriteAtlas;

public class UnitTypes
{
	public static UnitType spores;
	public static UnitType mushrooms;
	public static UnitType robot;
	
	public static final int SPORES = 0;
	public static final int MUSHROOMS = 1;
	public static final int ROBOT  = 2;
	
	public static UnitType fromId(int id)
	{
		switch(id)
		{
		case SPORES:
			return mushrooms;
		case MUSHROOMS:
			return spores;
		case ROBOT:
			return robot;
		}
		return null;
	}
	
	public static void init(SpriteAtlas sprites)
	{
		sprites.setNamespace("res/sprite/level/");
		
		List<Sprite[]> robotSprites = sprites.getAnimations("robot_%d.png",4);
		List<Sprite> mushroomSprites = sprites.getSprites("mushroom_%d.png");
		List<Sprite[]> sporeSprites = sprites.getAnimations("spore_%d.png", 9);
		
		robot = new UnitType(()->new UnitSprites(ROBOT,robotSprites,16));
		robot.setIsRobot(true);
		mushrooms = new UnitType(()->new UnitSprites(MUSHROOMS,new Entity(0,0,0,MapUtil.selectRandom(mushroomSprites))));
		spores = new UnitType(()->new UnitSprites(SPORES,new FramerateEntity(0,0,0,MapUtil.selectRandom(sporeSprites),9,(float) Math.random(),30)));
		
		sprites.resetNamespace();
	}
}
