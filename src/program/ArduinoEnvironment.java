package program;

import game.item.Pin;

import java.util.List;
import java.util.function.Consumer;

public class ArduinoEnvironment
{
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	  Blink
	  Turns on an LED on for one second, then off for one second, repeatedly.

	  Most Arduinos have an on-board LED you can control. On the Uno and
	  Leonardo, it is attached to digital pin 13. If you're unsure what
	  pin the on-board LED is connected to on your Arduino model, check
	  the documentation at http://www.arduino.cc

	  This example code is in the public domain.

	  modified 8 May 2014
	  modified 12 December 2015
	  by Scott Fitzgerald/Henry Rachootin
	 */


	// the setup function runs once when you press reset or power the board
	void setup()
	{
		// initialize digital pin 13 as an output.
		pinMode(13, OUTPUT);
	}
	// the loop function runs over and over again forever
	void loop()
	{
		digitalWrite(13, HIGH);   // turn the LED on (HIGH is the voltage level)
		delay(1000);              // wait for a second
		digitalWrite(13, LOW);    // turn the LED off by making the voltage LOW
		delay(1000);              // wait for a second
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static final int HIGH = 1024;
	public static final int LOW = 0;
	public static final int OUTPUT = ProgramThread.OUTPUT;
	public static final int INPUT = ProgramThread.INPUT;
	public static final int INPUT_PULLUP = ProgramThread.INPUT_PULLUP;
	public static final int NO_CONNECTION = ProgramThread.NO_CONNECTION;
	public static final int CONSTANT = ProgramThread.CONSTANT;
	
	
	Consumer<List<Pin>> script;
	List<Pin> globalPins;
	int[] pinModes = {NO_CONNECTION,NO_CONNECTION,CONSTANT,CONSTANT,INPUT,INPUT,INPUT,INPUT,INPUT,INPUT,      INPUT,INPUT,INPUT,INPUT,INPUT,INPUT,INPUT,INPUT,
			NO_CONNECTION,NO_CONNECTION,INPUT,CONSTANT,CONSTANT,CONSTANT,CONSTANT,INPUT,              INPUT,INPUT,INPUT,INPUT,INPUT};
	Pin vIn;
	Pin[] ground;
	Pin vcc3;
	Pin vcc5;
	
	public ArduinoEnvironment()
	{
		script = pins->
		{
			globalPins = pins;
			vIn = globalPins.get(25);
			ground = new Pin[]{globalPins.get(3),globalPins.get(23),globalPins.get(24)};
			vcc3 = globalPins.get(21);
			vcc5 = globalPins.get(22);
			while(true)
			{
				System.out.println("reset");
				while(!powered())
				{
					try
					{
						Thread.sleep(100);
					} 
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				updatePower();
				setup();
				while(powered())
				{
					loop();
				}
				updatePower();
			}
		};
	}
	public boolean powered()
	{
		return vIn.getReceivedPotential()>0 && isGrounded();
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
	
	
	public Consumer<List<Pin>> getScript()
	{
		return script;
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
			e.printStackTrace();
		}
	}
	public int[] getPinModes()
	{
		return pinModes;
	}
	
}
