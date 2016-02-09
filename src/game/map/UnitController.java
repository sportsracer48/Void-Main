package game.map;

import program.Environment;
import game.item.Item;
import state.workbench.game.ExportState;

public class UnitController implements CircuitSystem
{
	private static final long serialVersionUID = 3832220353115260815L;
	
	ExportState partConfig;
	Unit unit;
	public Map map;

	public UnitController(ExportState state, Unit unit, Map map)
	{
		this.partConfig = state;
		this.unit = unit;
		unit.setController(this);
		this.map = map;
		for(Item i:state.getItems())
		{
			Environment env = i.getEnvironment();
			if(env != null)
			{
				env.setUnitController(this);
			}
		}
	}
	
	public void update()
	{
		partConfig.update();
	}
	
	public boolean isAnimating()
	{
		return unit.isMoving();
	}
	
	public void tick()
	{
		partConfig.tick(unit);
		unit.tick(map);
	}

	public ExportState getConfig()
	{
		return partConfig;
	}

	public void radioUpdate()
	{
		partConfig.radioUpdate();
	}
}
