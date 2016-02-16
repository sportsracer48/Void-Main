package program;

import game.item.Pin;
import game.map.unit.UnitController;
import game.session.GlobalState;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.python.compiler.Interpreter;
import org.python.compiler.PythonCompiler;
import org.python.compiler.CompiledCode;

import util.BitShiftPipe;
import util.Pipe;

public class OuinoEnvironment implements Environment
{
	private static final long serialVersionUID = -6299833059149665874L;
	static float cyclesPerSecond = 1_000_000f; //1000 khz
	static float cyclesPerMs = cyclesPerSecond/1000;
	
	public static final int HIGH = 1024;
	public static final int LOW = 0;
	public static final int OUTPUT = 0;
	public static final int INPUT = 1;
	public static final int INPUT_PULLUP = 2;
	public static final int NO_CONNECTION = 3;
	public static final int CONSTANT = 4;
	
	public static final int SERIAL_INPUT = 5;
	public static final int SERIAL_CLOCK = 6;
	
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
	boolean[] wasOff;
	int[] serialSignalPins;
	BitShiftPipe[] serialDataPipes;
	
	Pin vIn;
	Pin[] ground;
	Pin vcc3;
	Pin vcc5;
	
	String program;
	Interpreter interpreter;
	
	Pipe serialOut;
	UnitController controller;

	public OuinoEnvironment(List<Pin> pins)
	{
		serialOut = new Pipe(2048);
		globalPins = pins;
		initialize();
	}
	private void initialize()
	{
		pinModes = new int[]{NO_CONNECTION,NO_CONNECTION,CONSTANT,CONSTANT,INPUT,INPUT,INPUT,INPUT,INPUT,INPUT,      INPUT,INPUT,INPUT,INPUT,INPUT,INPUT,INPUT,INPUT,
				NO_CONNECTION,NO_CONNECTION,INPUT,CONSTANT,CONSTANT,CONSTANT,CONSTANT,INPUT,              INPUT,INPUT,INPUT,INPUT,INPUT};
		wasOff = new boolean[pinModes.length];
		serialSignalPins = new int[pinModes.length];
		serialDataPipes = new BitShiftPipe[pinModes.length];
		
		Arrays.fill(serialSignalPins, -1);
		vIn = globalPins.get(25);
		ground = new Pin[]{globalPins.get(3),globalPins.get(23),globalPins.get(24)};
		vcc3 = globalPins.get(21);
		vcc5 = globalPins.get(22);
		updatePower();
	}
	
	public void logicUpdate()
	{
		for(int i = 0;i<globalPins.size();i++)
		{
			if(pinModes[i] == SERIAL_CLOCK)
			{
				boolean wasOff = this.wasOff[i];
				Pin clock = globalPins.get(i);
				int signalIndex = serialSignalPins[i];
				if(signalIndex>=0 && signalIndex<globalPins.size() && serialDataPipes[signalIndex] != null)
				{
					Pin signal = globalPins.get(signalIndex);
					if(wasOff && clock.getReceivedPotential()==HIGH)
					{
						serialDataPipes[signalIndex].writeBit(signal.getReceivedPotential()==HIGH);
					}
				}
				this.wasOff[i] = clock.getReceivedPotential()!=HIGH;
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void pinMode(int pinNumber, int val)
	{
		if(!(val==OUTPUT || val==INPUT || val==INPUT_PULLUP))
		{
			return;
		}
		if(pinNumber<0 || pinNumber>13)
		{
			return;
		}
		int truePinNumber = 17-pinNumber;
		pinModes[truePinNumber]=val;
		serialDataPipes[truePinNumber]=null;
	}
	public void serialMode(int clockPin, int signalPin)
	{
		if(clockPin <0 || clockPin>13 || signalPin<0 || signalPin>13)
		{
			return;
		}
		int trueClockPin = 17-clockPin;
		int trueSignalPin = 17-signalPin;
		pinModes[trueClockPin] = SERIAL_CLOCK;
		pinModes[trueSignalPin] = SERIAL_INPUT;
		serialSignalPins[trueClockPin] = trueSignalPin;
		serialDataPipes[trueSignalPin] = new BitShiftPipe(64);
		logicUpdate();
	}
	public int serialRead(int pinNumber)
	{
		if(pinNumber<0 || pinNumber>13)
		{
			return -1;
		}
		int truePinNumber = 17-pinNumber;
		if(pinModes[truePinNumber] != SERIAL_INPUT)
		{
			return -1;
		}
		if(serialDataPipes[truePinNumber] == null)
		{
			return -1;
		}
		return serialDataPipes[truePinNumber].readByte();
	}
	public boolean hasSerialData(int pinNumber)
	{
		if(pinNumber<0 || pinNumber>13)
		{
			return false;
		}
		int truePinNumber = 17-pinNumber;
		if(pinModes[truePinNumber] != SERIAL_INPUT)
		{
			return false;
		}
		if(serialDataPipes[truePinNumber] == null)
		{
			return false;
		}
		return serialDataPipes[truePinNumber].hasData();
	}
	public void digitalWrite(int pinNumber, int val)
	{
		if(pinNumber<0 || pinNumber>13)
		{
			return;
		}
		int truePinNumber = 17-pinNumber;
		if(pinModes[truePinNumber] != OUTPUT)
		{
			return;
		}
		Pin pin = globalPins.get(truePinNumber);
		if(val>LOW)
		{
			pin.setPotential(HIGH);
		}
		else
		{
			pin.setPotential(LOW);
		}
	}
	public void analogWrite(int pinNumber, int val)
	{
		if(pinNumber<0 || pinNumber>13)
		{
			return;
		}
		int truePinNumber = 17-pinNumber;
		if(pinModes[truePinNumber] != OUTPUT)
		{
			return;
		}
		Pin pin = globalPins.get(truePinNumber);
		pin.setPotential(val);
	}
	
	public int digitalRead(int pinNumber)
	{
		if(pinNumber<0 || pinNumber>13)
		{
			return 0;
		}
		int truePinNumber = 17-pinNumber;
		if(pinModes[truePinNumber] != INPUT)
		{
			return 0;
		}
		Pin pin = globalPins.get(truePinNumber);
		int potential = pin.getReceivedPotential();
		if(potential > LOW)
		{
			return HIGH;
		}
		else
		{
			return LOW;
		}
	}
	public int analogRead(int pinNumber)
	{
		if(pinNumber<0 || pinNumber>13)
		{
			return 0;
		}
		int truePinNumber = 17-pinNumber;
		if(pinModes[truePinNumber] != INPUT)
		{
			return 0;
		}
		Pin pin = globalPins.get(truePinNumber);
		return pin.getPotential();
	}
	public boolean pullupRead(int pinNumber)
	{
		if(pinNumber<0 || pinNumber>13)
		{
			return false;
		}
		int truePinNumber = 17-pinNumber;
		if(pinModes[truePinNumber] != INPUT_PULLUP)
		{
			return false;
		}
		return globalPins.get(truePinNumber).isGrounded();
	}
	
	public void delay(int ms)
	{
		goalDelay = ms;
		delayTime = 0;
		delaying = true;
		interpreter.stop();
	}
	public void tick()
	{
		if(controller!=null)
		{
			controller.tick();
			if(isAnimating())
			{
				interpreter.stop();
			}
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();
	}
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		if(interpreter != null)
		{
			PrintStream serialOut = new PrintStream(this.serialOut.getOutputStream());
			interpreter.setStdOut(serialOut);
		}
	}
	
	public InputStream getSerialStream()
	{
		return serialOut.getInputStream();
	}
	
	public void setUnitController(UnitController controller)
	{
		this.controller = controller;
	}
	public UnitController getUnitController()
	{
		return controller;
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
						delaying = false;
						delayTime = 0;
					}
				}
				if(!delaying && !isAnimating())
				{
					try
					{
						interpreter.execute((int)(cyclesPerMs));
						if(delaying)
						{
							delayTime = 0;
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
	}
	
	public void reset()
	{
		initialize();
		resetState();
		CompiledCode programSuite = PythonCompiler.compile(program+bootStrap);
		PrintStream serialOut = new PrintStream(this.serialOut.getOutputStream());
		interpreter = new Interpreter(programSuite,OuinoPythonEnvironment.getGlobals(this),serialOut);
		programSuite.dump();
	}
	
	public void unpowerAll()
	{
		for(Pin p:globalPins)
		{
			p.setPotential(0);
			p.setGrounded(false);
		}
	}
	
	public boolean isAnimating()
	{
		return controller != null && controller.isAnimating();
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
				p.setGrounded(true);
			}
			vcc3.setPotential(614);
			vcc5.setPotential(1024);
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
