package graphics.entity;

import graphics.Sprite;

public class BoxEntity extends ColoredEntity
{
	Sprite corner, up, left, center;
	public BoxEntity(float x, float y, float z,Sprite corner, Sprite up, Sprite left, Sprite center)
	{
		super(x,y,z,null);
		this.corner = corner;
		this.up=up;
		this.left=left;
		this.center=center;
	}
	
	public void setSize(float width, float height)
	{
		clearChildren();
		//corners
		addChild(new Entity(-corner.imWidth,-corner.imHeight,0,corner));
		addChild(new Entity(width,-corner.imHeight,0,corner.reverseH()));
		addChild(new Entity(-corner.imWidth,height,0,corner.reverseV()));
		addChild(new Entity(width,height,0,corner.reverseH().reverseV()));
		
		
		Entity topEntity = new Entity(0,-up.imHeight,0,up);
		topEntity.setScale(width/up.imWidth, 1);
		addChild(topEntity);
		Entity botEntity = new Entity(0,height,0,up.reverseV());
		botEntity.setScale(width/up.imWidth, 1);
		addChild(botEntity);
		
		
		Entity leftEntity = new Entity(-left.imWidth,0,0,left);
		leftEntity.setScale(1,height/left.imHeight);
		addChild(leftEntity);
		Entity rightEntity = new Entity(width,0,0,left.reverseH());
		rightEntity.setScale(1,height/left.imHeight);
		addChild(rightEntity);
		
		Entity centerEntity = new Entity(0,0,0,center);
		centerEntity.setScale(width/center.imWidth, height/center.imHeight);
		addChild(centerEntity);
		
	}
}
