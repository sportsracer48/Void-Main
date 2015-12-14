package program;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import game.item.Item;
import game.item.Pin;

public class ProgramThread extends Thread
{
	public static final int OUTPUT = 0;
	public static final int INPUT = 1;
	public static final int INPUT_PULLUP = 2;
	public static final int NO_CONNECTION = 3;
	public static final int CONSTANT = 4;
	
	int[] pinModes;
	List<Pin> threadSafeStates = new ArrayList<>();
	Item host;
	Consumer<List<Pin>> script;
	
	public ProgramThread(Item host, Consumer<List<Pin>> script, int[] pinModes)//THIS IS A HACK.
	{
		this.setDaemon(true);
		this.script = script;
		this.host = host;
		this.pinModes = pinModes;
		List<Pin> pins = host.getPins();
		threadSafeStates = pins;
	}
	
	public void run()
	{
		script.accept(threadSafeStates);
	}
	
	public void act(int dt)
	{
		for(Pin p:threadSafeStates)
		{
			p.act(dt);
		}
	}
}
