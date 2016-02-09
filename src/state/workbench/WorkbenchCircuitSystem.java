package state.workbench;

import state.workbench.game.ChassisGrid;
import game.item.Item;
import game.map.CircuitSystem;

public class WorkbenchCircuitSystem implements CircuitSystem
{
	private static final long serialVersionUID = 1112217032155247369L;
	
	ChassisGrid grid;
	
	public WorkbenchCircuitSystem(ChassisGrid grid)
	{
		this.grid = grid;
	}
	public void update()
	{
		grid.forEachItem(Item::pinUpdate);
	}

	public void tick()
	{
		//do nothing. Ticking is meaningless in the workbench
	}

	public void radioUpdate()
	{
		grid.forEachItem(Item::radioUpdate);
	}
}
