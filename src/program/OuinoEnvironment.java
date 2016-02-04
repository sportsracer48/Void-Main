package program;

import entry.GlobalState;
import game.item.Pin;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.python.compiler.Interpreter;
import org.python.compiler.PythonCompiler;
import org.python.compiler.CompiledCode;

import util.Pipe;

public class OuinoEnvironment implements Environment
{
	public static final int HIGH = 1024;
	public static final int LOW = 0;
	public static final int OUTPUT = 0;
	public static final int INPUT = 1;
	public static final int INPUT_PULLUP = 2;
	public static final int NO_CONNECTION = 3;
	public static final int CONSTANT = 4;
	boolean delaying = false;
	int delayTime = 0;
	int goalDelay = 0;
	public static String bootStrap =
			  "\n"
			+ "setup()\n"
			+ "while True:\n"
			+ "  loop()";
	
	
	List<Pin> globalPins;
	int[] pinModes;
	
	Pin vIn;
	Pin[] ground;
	Pin vcc3;
	Pin vcc5;
	
	String program;
	Interpreter interpreter;
	
	Pipe serialOut;
	float cyclesPerSecond = 1_000_000f; //1000 khz
	float cyclesPerMs = cyclesPerSecond/1000;

	public OuinoEnvironment(List<Pin> pins)
	{
		serialOut = new Pipe(2048);
		globalPins = pins;
		initialize();
	}
	
	public InputStream getSerialStream()
	{
		return serialOut.getInputStream();
	}
	
	private void initialize()
	{
		pinModes = new int[]{NO_CONNECTION,NO_CONNECTION,CONSTANT,CONSTANT,INPUT,INPUT,INPUT,INPUT,INPUT,INPUT,      INPUT,INPUT,INPUT,INPUT,INPUT,INPUT,INPUT,INPUT,
				NO_CONNECTION,NO_CONNECTION,INPUT,CONSTANT,CONSTANT,CONSTANT,CONSTANT,INPUT,              INPUT,INPUT,INPUT,INPUT,INPUT};
		vIn = globalPins.get(25);
		ground = new Pin[]{globalPins.get(3),globalPins.get(23),globalPins.get(24)};
		vcc3 = globalPins.get(21);
		vcc5 = globalPins.get(22);
		updatePower();
	}
	private void resetState()
	{
		for(int i = 0; i<13; i++)
		{
			digitalWrite(i,LOW);
			pinMode(i,INPUT);
		}
	}
	

	public void uplode(String program)
	{
		this.program = program;
		reset();
	}
	
	public void act(int dt)
	{
		updatePower();
		if(powered())
		{
			if(interpreter!=null)
			{
				if(delaying)
				{
					delayTime+=dt;
					if(delayTime>=goalDelay)
					{
						dt = delayTime-delayTime;
						delaying = false;
						delayTime = 0;
					}
				}
				if(!delaying)
				{
					try
					{
						int cycles = interpreter.execute((int)(cyclesPerMs));
						if(delaying)
						{
							delayTime += (dt - cycles/cyclesPerMs);
							System.out.println(delayTime);
						}
					}
					catch(Exception e)
					{
						PrintStream serialOut = new PrintStream(this.serialOut.getOutputStream());
						serialOut.println("\nFATAL ERROR\n");
						e.printStackTrace(serialOut);
						interpreter = null;
						resetState();
					}
				}
			}
		}
		for(Pin p:globalPins)
		{
			p.act(dt);
		}
	}
	
	public void reset()
	{
		initialize();
		resetState();
		CompiledCode programSuite = PythonCompiler.compile(program+bootStrap);
		PrintStream serialOut = new PrintStream(this.serialOut.getOutputStream());
		interpreter = new Interpreter(programSuite,OuinoPythonEnvironment.getGlobals(this),serialOut);
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
		goalDelay = ms;
		delayTime = 0;
		delaying = true;
		interpreter.stop();
	}
	
	public boolean powered()
	{
		if(GlobalState.DEBUG)
		{
			return true;
		}
		else
		{
			return vIn.getReceivedPotential()>0 && isGrounded();
		}
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
