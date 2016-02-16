package game.session;

import game.comm.RadioManager;
import game.comm.SensorManager;
import game.map.Map;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import computer.system.Computer;
import state.workbench.game.ExportState;

public class GameSession implements Serializable
{
	private static final long serialVersionUID = 3969755727836227722L;
	
	Map map;
	ExportState workbenchExport;
	RadioManager radio;
	Computer laptop;
	SensorManager sensorManager;
	
	public GameSession()
	{
		radio = GlobalState.radio;
		laptop = GlobalState.laptop;
		
		map = GlobalState.currentViewport.map;
		workbenchExport = GlobalState.currentWorkbench.grid.export();
		sensorManager = GlobalState.sensorManager;
	}
	
	public void save(String filePath)
	{
		File f = new File(filePath);
		try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f)))
		{
			out.writeObject(this);
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void cleanup()
	{
		workbenchExport.revertBreakouts(GlobalState.currentWorkbench.grid.getBreakouts());
	}
	
	public void load()
	{
		GlobalState.radio = radio;
		GlobalState.laptop = laptop;
		GlobalState.sensorManager = sensorManager;
		GlobalState.currentViewport.setMap(map);
		
		map.makeEntities();
		
		//GlobalState.currentWorkbench.load(workbenchExport);
	}
}
