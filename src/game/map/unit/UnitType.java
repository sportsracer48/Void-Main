package game.map.unit;

import java.util.function.Supplier;

import state.viewport.UnitSprites;

public class UnitType
{
	Supplier<UnitSprites> spriteSupplier;
	boolean isRobot;
	
	public UnitType(Supplier<UnitSprites> spriteSupplier)
	{
		this.spriteSupplier = spriteSupplier;
	}
	
	public UnitSprites createSprites()
	{
		return spriteSupplier.get();
	}
	
	public boolean isRobot()
	{
		return isRobot;
	}

	public void setIsRobot(boolean isRobot)
	{
		this.isRobot = isRobot;
	}
}
