package program;

import game.item.Pin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.python.core.PyCode;
import org.python.core.PyFunction;
import org.python.core.PyFunctionTable;
import org.python.core.PyObject;
import org.python.core.PyTableCode;
import org.python.util.PythonInterpreter;

import util.Condition;

public class OuinoEnvironment implements Environment
{
	public static final int HIGH = 1024;
	public static final int LOW = 0;
	public static final int OUTPUT = 0;
	public static final int INPUT = 1;
	public static final int INPUT_PULLUP = 2;
	public static final int NO_CONNECTION = 3;
	public static final int CONSTANT = 4;
	public static PyCode ouinoPy = null;
	
	
	
	List<Pin> globalPins;
	int[] pinModes = {NO_CONNECTION,NO_CONNECTION,CONSTANT,CONSTANT,INPUT,INPUT,INPUT,INPUT,INPUT,INPUT,      INPUT,INPUT,INPUT,INPUT,INPUT,INPUT,INPUT,INPUT,
			NO_CONNECTION,NO_CONNECTION,INPUT,CONSTANT,CONSTANT,CONSTANT,CONSTANT,INPUT,              INPUT,INPUT,INPUT,INPUT,INPUT};
	Pin vIn;
	Pin[] ground;
	Pin vcc3;
	Pin vcc5;
	Condition hasPower;
	PythonInterpreter interpreter;
	
	String program;
	PyObject setup;
	PyObject loop;
	
	
	volatile Thread interpreterThread;

	public OuinoEnvironment(List<Pin> pins)
	{
		globalPins = pins;
		vIn = globalPins.get(25);
		ground = new Pin[]{globalPins.get(3),globalPins.get(23),globalPins.get(24)};
		vcc3 = globalPins.get(21);
		vcc5 = globalPins.get(22);
		hasPower = new Condition(()->powered());
	}
	
	public void run()
	{
		if(interpreter != null)
		{
			interpreter.close();
		}
		interpreter = new PythonInterpreter();
		OuinoPythonEnvironment.exec(interpreter.getLocals(), this);
		if(program!=null)
		{
			PyCode script = interpreter.compile(program);
			interpreter.exec(script);
			while(!Thread.currentThread().isInterrupted())
			{
				hasPower.waitUntilTrue();
				updatePower();
				setup();
				while(powered() && !Thread.currentThread().isInterrupted())
				{
					loop();
				}
				updatePower();
			}
		}
		System.out.println("INTERUPTED");
	}
	

	public void uplode(String program)
	{
		this.program = program;
		reset();
	}
	
	public void act(int dt)
	{
		hasPower.update();
		for(Pin p:globalPins)
		{
			p.act(dt);
		}
	}
	
	public void start()
	{
		interpreterThread = new Thread(this);
		interpreterThread.setDaemon(true);
		interpreterThread.start();
	}
	
	public void reset()
	{
		if(interpreterThread != null)
		{
			try
			{
				interpreterThread.interrupt();
				interpreterThread.join();
			}
			catch(ThreadDeath | InterruptedException e)
			{
				//DONT CARE LOL
			}
		}
		
		start();
	}
	
	public void setup()
	{
		try
		{
			setup = interpreter.get("setup");
			PyFunction test = (PyFunction) setup;
			PyTableCode code = (PyTableCode)test.__code__;
			
			
			Field funcsField = code.getClass().getDeclaredField("funcs");
			funcsField.setAccessible(true);
			PyFunctionTable funcs = (PyFunctionTable)funcsField.get(code);
			
			for(Method m: funcs.getClass().getDeclaredMethods())
			{
				System.out.println(m);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void loop()
	{
		try
		{
			interpreter.exec("loop()");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void digitalWrite(int pinNumber, int val)
	{
		if(pinNumber<0 || pinNumber>13)
		{
			return;
		}
		Pin pin = globalPins.get(17-pinNumber);
		pin.setGoalPotential(val);
	}
	public void pinMode(int pinNumber, int val)
	{
		if(pinNumber<0 || pinNumber>13)
		{
			return;
		}
		pinModes[17-pinNumber]=val;
	}
	public void delay(int ms)
	{
		try
		{
			Thread.sleep(ms);
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
	}
	
	public boolean powered()
	{
		return true;
		//TODO
		//DEBUG
		//return vIn.getReceivedPotential()>0 && isGrounded();
	}
	public void updatePower()
	{
		if(powered())
		{
			for(Pin p:ground)
			{
				p.setGoalGrounded(true);
			}
			vcc3.setGoalPotential(614);
			vcc5.setGoalPotential(1024);
		}
		else
		{
			for(Pin p:ground)
			{
				p.setGoalGrounded(false);
			}
			vcc3.setGoalPotential(0);
			vcc5.setGoalPotential(0);
		}
	}
	public boolean isGrounded()
	{
		for(Pin p: ground)
		{
			if(p.leadsToGround())
			{
				return true;
			}
		}
		return false;
	}

	public int[] getPinModes()
	{
		return pinModes;
	}
	
}
