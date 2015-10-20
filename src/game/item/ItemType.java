package game.item;

import graphics.Sprite;

public class ItemType //kinda reflexive, but hey, whatever
{
	float offsetX=0,offsetY=0;
	int workbenchWidth,workbenchHeight;
	Sprite workbench, inventory;
	public ItemType(Sprite workbench, Sprite inventory)
	{
		this.workbench = workbench;
		this.inventory = inventory;
	}
	public void setOffsetX(float offsetX)
	{
		this.offsetX = offsetX;
	}
	public void setOffsetY(float offsetY)
	{
		this.offsetY = offsetY;
	}
	public float getOffsetX()
	{
		return offsetX;
	}
	public float getOffsetY()
	{
		return offsetY;
	}
	public int getWorkbenchWidth()
	{
		return workbenchWidth;
	}
	public void setWorkbenchWidth(int workbenchWidth)
	{
		this.workbenchWidth = workbenchWidth;
	}
	public int getWorkbenchHeight()
	{
		return workbenchHeight;
	}
	public void setWorkbenchHeight(int workbenchHeight)
	{
		this.workbenchHeight = workbenchHeight;
	}
	
}
