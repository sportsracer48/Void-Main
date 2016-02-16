package game.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import game.comm.RadioManager;
import game.comm.SensorManager;
import game.item.Inventory;
import program.ProgramCoordinator;
import state.programming.ProgrammingState;
import state.viewport.ViewportState;
import state.workbench.WorkbenchState;
import computer.system.Computer;

public class GlobalState
{
	public static final boolean DEBUG = true;

	protected static Computer laptop;
	protected static ProgramCoordinator coordinator;
	protected static RadioManager radio;
	protected static SensorManager sensorManager;
	protected static Inventory baseInventory;
	
	//BAD BAD NAUGHTY BAD BAD
	//TODO REMOVE GLOBAL STATE
	public static WorkbenchState currentWorkbench;
	public static ProgrammingState currentProgramming;
	public static ViewportState currentViewport;
	
	public static void init()
	{
		laptop = new Computer("laptop");
		coordinator = new ProgramCoordinator();
		radio = new RadioManager();
		sensorManager = new SensorManager();
		baseInventory = new Inventory(80);
	}
	
	public static Inventory getbaseInventory()
	{
		return baseInventory;
	}
	
	public static Computer getLaptop()
	{
		return laptop;
	}
	public static RadioManager getRadio()
	{
		return radio;
	}
	public static SensorManager getSensorManager()
	{
		return sensorManager;
	}
	public static ProgramCoordinator getCoordinator()
	{
		return coordinator;
	}
	
	public static void save(String filePath)
	{
		GameSession session = export();
		session.save("session.jso");
		session.cleanup();
	}
	public static GameSession load(String filePath) throws ClassNotFoundException, IOException
	{
		File f = new File(filePath);
		try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(f)))
		{
			GameSession session = (GameSession) in.readObject();
			return session;
		}
	}
	
	public static void pinUpdateAll()
	{
		if(currentWorkbench.getCircuit() != null)
		{
			currentWorkbench.getCircuit().update();
		}
		if(currentViewport.getMap() != null)
		{
			currentViewport.getMap().pinUpdateAll();
		}
	}
	
	public static void radioUpdateAll()
	{
		if(currentWorkbench.getCircuit() != null)
		{
			currentWorkbench.getCircuit().radioUpdate();
		}
		if(currentViewport.getMap() != null)
		{
			currentViewport.getMap().radioUpdateAll();
		}
	}
	
	
	public static GameSession export()
	{
		return new GameSession();
	}
}
