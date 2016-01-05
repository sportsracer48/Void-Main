package program;

import game.item.Pin;

import java.util.List;

import org.python.core.Py;
import org.python.core.PyCode;
import org.python.core.PyObject;
import org.python.core.ThreadState;
import org.python.util.InteractiveInterpreter;

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
	int[] pinModes;
	
	Pin vIn;
	Pin[] ground;
	Pin vcc3;
	Pin vcc5;
	Condition hasPower;
	InteractiveInterpreter interpreter;
	
	String program;
	PyObject setup;
	PyObject loop;
	
	
	volatile Thread interpreterThread;
	volatile ThreadState interpreterThreadState;
	volatile boolean runningUserCode = false;

	public OuinoEnvironment(List<Pin> pins)
	{
		globalPins = pins;
		initialize();
	}
	
	private void initialize()
	{
		pinModes = new int[]{NO_CONNECTION,NO_CONNECTION,CONSTANT,CONSTANT,INPUT,INPUT,INPUT,INPUT,INPUT,INPUT,      INPUT,INPUT,INPUT,INPUT,INPUT,INPUT,INPUT,INPUT,
				NO_CONNECTION,NO_CONNECTION,INPUT,CONSTANT,CONSTANT,CONSTANT,CONSTANT,INPUT,              INPUT,INPUT,INPUT,INPUT,INPUT};
		vIn = globalPins.get(25);
		ground = new Pin[]{globalPins.get(3),globalPins.get(23),globalPins.get(24)};
		vcc3 = globalPins.get(21);
		vcc5 = globalPins.get(22);
		hasPower = new Condition(()->powered());
		unpowerAll();
		updatePower();
	}
	
	public void run()
	{
		//close the old interpreter
		if(interpreter != null)
		{
			interpreter.close();
		}
		//make a new one
		interpreter = new InteractiveInterpreter();
		
		//setup the environment in the new interpreter
		OuinoPythonEnvironment.exec(interpreter.getLocals(), this);
		
		//save the threadstate so we can interrupt it later
		interpreterThreadState = Py.getThreadState();
		
		//if we don't have a program to load, we need not go any further
		if(program!=null)
		{
			PythonSanitizer sanitizer = new PythonSanitizer(program);
			if(!sanitizer.isLegal())
			{
				sanitizer.throwException();
			}
			//compile the script, load it into the interpreter, get the important parts out
			PyCode script = interpreter.compile(program);
			interpreter.exec(script);
			setup = interpreter.get("setup");
			loop = interpreter.get("loop");
			
			//loop until we should stop
			while(!Thread.currentThread().isInterrupted())
			{
				//sanity check
				if(Thread.currentThread() != interpreterThread) break;
				
				//wait until the board has power
				hasPower.waitUntilTrue();
				
				//sanity check
				if(Thread.currentThread() != interpreterThread) break;
				
				updatePower();
				//call the setup
				setup();
				while(powered() && !Thread.currentThread().isInterrupted())
				{
					//sanity check
					if(Thread.currentThread() != interpreterThread) break;
					loop();
				}
				updatePower();
			}
		}
	}
	

	public void uplode(String program)
	{
		this.program = program;
		new PythonSanitizer(program);
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
		if(interpreterThread!=null && interpreterThread.isAlive())
		{
			throw new RuntimeException("The interpreter thread is still running. This is actually not ok.");
		}
		interpreterThread = new Thread(this);
		interpreterThread.setDaemon(true);
		interpreterThread.start();
	}
	
	@SuppressWarnings("deprecation")//no other way
	public void reset()
	{
		if(interpreterThread != null && interpreterThread.isAlive())
		{
			try
			{
				interpreterThread.interrupt();
				interpreter.interrupt(interpreterThreadState);
				try
				{
					interpreterThread.join(7);//you get seven milliseconds to stop running
				} 
				catch (InterruptedException e)
				{
					//this is kinda odd, but probably not an issue in the long run
				}
				if(interpreterThread.isAlive())
				{
					if(!runningUserCode)
					{
						System.err.println("Strangly, the interpreterThread has failed to stop, even though it is not currently running user code");
					}
					else
					{
						System.err.println("Interrupt failed, running user code, of course");
					}
					System.err.println("time's up, force stoping now");
					interpreterThread.stop();//We are so out of options here that this is really the only option.
				}
			}
			catch(ThreadDeath death)
			{
				//DONT CARE LOL
			}
			if(interpreterThread.isAlive())
			{
				try
				{
					//if it is alive, it won't be soon
					System.err.println("the world's most resilient thread it still going");
					interpreterThread.join();
					System.err.println("ok it stopped");
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		initialize();
		start();
	}
	
	public void setup()
	{
		try
		{
			if(setup!=null)
			{
				runningUserCode = true;
				setup.__call__();
				runningUserCode = false;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		catch(Error e)
		{
			//fall through, python interrupt
		}
	}
	public void loop()
	{
		try
		{
			if(loop!=null)
			{
				runningUserCode = true;
				loop.__call__();
				runningUserCode = false;
			}
		}
		catch(Exception e)
		{
			System.err.println("Python exception in loop, "+e.getMessage());
		}
		catch(Error e)
		{
			//fall through, python interrupt
		}
	}
	
	public void unpowerAll()
	{
		for(Pin p:globalPins)
		{
			p.setGoalPotential(0);
			p.setGoalGrounded(false);
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
			unpowerAll();
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
