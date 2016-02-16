package game.item;

import java.util.List;

import program.Environment;
import program.OuinoEnvironment;
import state.workbench.game.ExportBreakout;
import util.Grid;
import breadboard.BreadboardUtil;
import game.map.unit.Unit;
import game.map.unit.UnitTypes;
import game.session.GlobalState;
import graphics.Sprite;
import graphics.entity.CounterEntity;
import graphics.registry.SpriteAtlas;

public class ItemTypes
{
	public static ItemType breakout;
	public static ItemType microController;
	public static ItemType battery;
	public static ItemType antenna;
	public static ItemType breadboard;
	public static ItemType poweredWheel;
	public static ItemType ledOutput;
	public static ItemType bitShiftRegister;
	public static ItemType eightBitCounter;
	public static ItemType sensor;
	public static ItemType transmitter;
	public static ItemType robot;
	
	public static final int BREAKOUT = 0;
	public static final int MICRO_CONTROLLER = 1;
	public static final int BATTERY = 2;
	public static final int ANTENNA = 3;
	public static final int BREADBOARD = 4;
	public static final int POWERED_WHEEL = 5;
	public static final int LED_OUTPUT = 6;
	public static final int BIT_SHIFT_REGISTER = 7;
	public static final int EIGHT_BIT_COUNTER = 8; //:::;)
	public static final int SENSOR = 9;
	public static final int TRANSMITTER  = 10;
	public static final int ROBOT = 11;
	
	public static ItemType fromId(int id)
	{
		switch(id)
		{
		case BREAKOUT:
			return breakout;
		case MICRO_CONTROLLER:
			return microController;
		case BATTERY:  
			return battery;
		case ANTENNA:
			return antenna;
		case BREADBOARD:
			return breadboard;
		case POWERED_WHEEL:
			return poweredWheel;
		case LED_OUTPUT: 
			return ledOutput;
		case BIT_SHIFT_REGISTER:
			return bitShiftRegister;
		case EIGHT_BIT_COUNTER:
			return eightBitCounter;
		case SENSOR:
			return sensor;
		case TRANSMITTER:
			return transmitter;
		case ROBOT:
			return robot;
		}
		return null;
	}
	
	public static void init(SpriteAtlas sprites)
	{
		sprites.setNamespace("res/sprite/workbench/");
		ItemType.setDefaultWireSprites(
				sprites.getSprite("pin highlight end.png"),
				sprites.getSprite("pin highlight.png"),
				sprites.getSprite("wire end.png"),
				sprites.getSprite("wire end opaque.png"),
				sprites.getSprite("wire fade.png"));
		
		final Sprite ledOn = sprites.getSprite("led on.png");
		final Sprite ledOff = sprites.getSprite("led off.png");
		final Sprite counterH = sprites.getSprite("counter segment H.png");
		final Sprite counterV = sprites.getSprite("counter segment V.png");
		
		breakout = new ItemType(BREAKOUT,null);
		microController = new ItemType(MICRO_CONTROLLER,sprites.getSprite("ouino.png"),sprites.getSprite("ouino item.png"))
		{
			public Environment getEnvironmentFor(List<Pin> pins)
			{
				Environment e = new OuinoEnvironment(pins);
				GlobalState.getCoordinator().addEnvironment(e);
				return e;
			}
		};
		battery = new ItemType(BATTERY,sprites.getSprite("battery.png"),sprites.getSprite("battery item.png"));
		antenna = new ItemType(ANTENNA,sprites.getSprite("antenna ic.png"),sprites.getSprite("antenna item.png"));
		breadboard = new ItemType(BREADBOARD,sprites.getSprite("breadboard.png"),sprites.getSprite("breadboard item.png"));
		poweredWheel = new ItemType(POWERED_WHEEL,sprites.getSprite("wheel item.png"),2);
		transmitter = new ItemType(TRANSMITTER,sprites.getSprite("broadcaster item.png"),5);
		sensor = new ItemType(SENSOR,sprites.getSprite("sensor item.png"),4);
		ledOutput = new ItemType(LED_OUTPUT,ledOff, sprites.getSprite("led item.png"));
		bitShiftRegister = new ItemType(BIT_SHIFT_REGISTER,sprites.getSprite("BSR.png"),sprites.getSprite("BSR item.png"));
		eightBitCounter = new ItemType(EIGHT_BIT_COUNTER,sprites.getSprite("8bit counter.png"),sprites.getSprite("counter item.png"))
		{
			public ItemEntity getWorldEntity(Item instance)
			{
				return new CounterEntity(-getOffsetX(),-getOffsetY(),0,20,15,instance,3,counterH,counterV);
			}
		};
		robot = new ItemType(ROBOT,sprites.getSprite("robot item.png"),0)
		{
			public Unit makeUnit()
			{
				return new Unit(UnitTypes.robot);
			}
		};
		
		
		eightBitCounter.setOffsets(-1, -1);
		eightBitCounter.addPins(new Grid(7,2,3,3,1,8));
		eightBitCounter.addPins(new Grid(36,2,3,3,1,2));
		eightBitCounter.setWireSpritesToDefault(sprites.getSprite("pin mask.png"));
		eightBitCounter.setTooltips("b256","b128","b32","b16","b8","b4","b2","b1","+5v","GND");
		eightBitCounter.setGraphicsPinUpdate((pins,entity)->{
			if(pins.get(8).getReceivedPotential()==1024 && pins.get(9).isGrounded())
			{
				int val = fromParallel(pins,0,8);
				if(entity instanceof CounterEntity)
				{
					((CounterEntity) entity).setNumber(val);
				}
			}
			else
			{
				if(entity instanceof CounterEntity)
				{
					((CounterEntity) entity).turnOff();
				}
			}
		});
		
		
		
		
		ledOutput.setOffsets(-1, -1);
		ledOutput.addPins(new Grid(6,4,3,3,1,2));
		ledOutput.setWireSpritesToDefault(sprites.getSprite("pin mask.png"));
		ledOutput.setTooltips("+5v","GND");
		ledOutput.setGraphicsPinUpdate((pins,entity)->{
			if(pins.get(0).getReceivedPotential()==1024 && pins.get(1).isGrounded())
			{
				entity.setSpriteAndSize(ledOn);
			}
			else
			{
				entity.setSpriteAndSize(ledOff);
			}
		});
		
		poweredWheel.setTooltips("+5v","GND");
		poweredWheel.setExportPinUpdate((empty,pins,location,unit,item)->{
			if(pins.get(0).getReceivedPotential()==1024 && pins.get(1).isGrounded())
			{
				unit.addForce(1);
				if(location == ExportBreakout.LEFT)
				{
					unit.addTorque(-1);
				}
				else if(location == ExportBreakout.RIGHT)
				{
					unit.addTorque(1);
				}
			}
			else
			{
				
			}
		});
		
		//sensor = new ItemType(SENSOR,sprites.getSprite("sensor item.png"),4);
		sensor.setTooltips("+5v","GND","CLOCK","SIGNAL");
		sensor.setExportPinUpdate((internalPins,pins,location,unit,item)->
		{
			if(pins.get(0).getReceivedPotential()==1024 && pins.get(1).isGrounded())
			{
				long key = GlobalState.getSensorManager().registerUnit(unit);
				item.setState(0, key);
				for(int bite = 0; bite<8; bite++)
				{
					int biteVal = (int) ((key >>> (bite*8)) & 0xFF);
					serialWrite(biteVal,pins.get(2),pins.get(3));
				}
			}
			else
			{
				GlobalState.getSensorManager().unregisterUnit(item.getState(0), unit);
				item.resetState();
				pins.get(2).setPotential(0);
				pins.get(3).setPotential(0);
			}
		});
		
		
		//new ItemType(TRANSMITTER,sprites.getSprite("broadcaster item.png"),5);
		transmitter.setTooltips("+5v","GND","SIGNAL","CHAN_C","CHAN_S");
		transmitter.setLogicUpdate(($,item)->{
			List<Pin> pins = item.getBreakoutPins();
			if(pins.get(0).getReceivedPotential()==1024 && pins.get(1).isGrounded())
			{
				boolean wasOff = item.getState(0)==1;
				if(wasOff && pins.get(3).getReceivedPotential()==1024)
				{
					int lastChannel = (int) item.getState(1);
					pushBit(item,1,pins.get(4).getReceivedPotential()==1024);
					int nextChannel = (int) item.getState(1);
					if(lastChannel != nextChannel)
					{
						GlobalState.getRadio().stopBroadcast(lastChannel, item);
					}
				}
				
				int channel = (int) item.getState(1);
				if(pins.get(2).getReceivedPotential()==1024)
				{
					GlobalState.getRadio().broadcast(channel, item);
				}
				else
				{
					GlobalState.getRadio().stopBroadcast(channel, item);
				}
				
				if(pins.get(3).getReceivedPotential()!=1024)
				{
					item.setState(0, 1L);
				}
				else
				{
					item.setState(0, 0L);
				}
			}
			else
			{
				GlobalState.getRadio().stopBroadcast(item);
				item.resetState();
			}
		});
		
		bitShiftRegister.setOffsets(-1, -1);
		bitShiftRegister.addPinLocation(4, 3);
		bitShiftRegister.addPins(new Grid(10,3,3,3,1,8));
		bitShiftRegister.addPins(new Grid(4,18,3,3,1,3));
		bitShiftRegister.setWireSpritesToDefault(sprites.getSprite("pin mask.png"));
		bitShiftRegister.setTooltips("CLOCK","b0","b1","b2","b3","b4","b5","b6","b7","+5v","GND","SIGNAL");
		bitShiftRegister.setLogicUpdate((pins,item)->{
			if(pins.get(9).getReceivedPotential()==1024 && pins.get(10).isGrounded())
			{
				boolean wasOff = item.getState(0)==1;
				if(wasOff && pins.get(0).getReceivedPotential()==1024)
				{
					for(int i = 8; i>=2; i--)
					{
						pins.get(i).setPotential(pins.get(i-1).getPotential());
					}
					pins.get(1).setPotential(pins.get(11).getReceivedPotential());
				}
				
				if(pins.get(0).getReceivedPotential()!=1024)
				{
					item.setState(0, 1L);
				}
				else
				{
					item.setState(0, 0L);
				}
			}
			else
			{
				for(int i = 1; i<9; i++)
				{
					pins.get(i).setPotential(0);
				}
				item.resetState();
			}
		});
		
		antenna.setOffsets(-1, -1);
		antenna.addPins(new Grid(4,3,3,3,1,3));
		antenna.addPins(new Grid(16,3,3,3,1,8));
		antenna.setWireSpritesToDefault(sprites.getSprite("pin mask.png"));
		antenna.setTooltips("+5v","GND","SIGNAL","c256","c128","c32","c16","c8","c4","c2","c1");
		
		antenna.setLogicUpdate(pins->{
			if(pins.get(0).getReceivedPotential()==1024 && pins.get(1).isGrounded())
			{
				int channel = fromParallel(pins,3,8);
				if(GlobalState.getRadio().isOn(channel))
				{
					pins.get(2).setPotential(1024);
				}
				else
				{
					pins.get(2).setPotential(0);
				}
			}
			else
			{
				pins.get(2).setPotential(0);
			}
		});
		antenna.setRadioUpdate(antenna.getLogicUpdate());
		
		
		battery.setOffsets(-1, -1);
		battery.addPins(new Grid(9,18,3,3,1,2));
		battery.addPins(new Grid(18,18,3,3,1,2));
		battery.setWireSpritesToDefault(sprites.getSprite("pin mask.png"));
		battery.setTooltips("+12v out","GND out","+12v in","GND in");
		battery.setLogicUpdate(pins->{
			pins.get(0).setPotential(1024);
			pins.get(1).setGrounded(true);
		});
		
		
		microController.setOffsets(0, 7);
		microController.setWorkbenchSize(2,2);
		microController.setWireSpritesToDefault(sprites.getSprite("pin mask.png"));
		microController.addPinStrip(35, 1, 10);
		microController.addPinStrip(72, 1, 8);
		microController.addPinStrip(50, 62, 8);
		microController.addPinStrip(81, 62, 5);
		microController.setTooltips("","","AREF","GND"," 13"," 12","~11","~10","~9"," 8",      " 7","~6","~5"," 4","~3"," 2","TXD>1","RXD<0",
									"","","RESET","3v","5v","GND","GND","VIN",              "A0","A1","A2","A3","A4");
		
		breadboard.setOffsets(-2, 5);
		breadboard.setWorkbenchSize(2,2);
		breadboard.setWireSpritesToDefault(null);
		breadboard.setWireEnd(sprites.getSprite("wire end no pin.png"));
		breadboard.setWireEndOpaque(sprites.getSprite("wire end no pin opaque.png"));
		breadboard.addPins(new Grid(7,14,3,3,5,30));
		breadboard.addPins(new Grid(7,38,3,3,5,30));
		new Grid(10,4,18,3,1,5).forEach((x2,y2)->{
			breadboard.addPins(new Grid(x2,y2,3,3,2,5));
		});
		new Grid(10,57,18,3,1,5).forEach((x2,y2)->{
			breadboard.addPins(new Grid(x2,y2,3,3,2,5));
		});
		breadboard.sortPins();
		breadboard.setLogicUpdate((pins)->{
			BreadboardUtil.linkHoriz(pins, 0, 25);
			BreadboardUtil.linkHoriz(pins, 25, 25);
			BreadboardUtil.linkHoriz(pins, 350, 25);
			BreadboardUtil.linkHoriz(pins, 375, 25);
			for(int start = 50; start<80; start++)
			{
				BreadboardUtil.linkVert(pins, start, 5, 30);
			}
			for(int start = 200; start<230; start++)
			{
				BreadboardUtil.linkVert(pins, start, 5, 30);
			}
		});
		
		
		breakout.setWireSpritesToDefault(null);
		breakout.setWireEnd(sprites.getSprite("wire end no pin.png"));
		breakout.setWireEndOpaque(sprites.getSprite("wire end no pin opaque.png"));
		sprites.resetNamespace();
	}
	
	public static int fromParallel(List<Pin> pins, int start, int len)
	{
		int accum = 0;
		for(int i = 0; i<len; i++)
		{
			Pin p = pins.get(start+i);
			accum = (accum<<1) | (p.getReceivedPotential()==1024?1:0);
		}
		return accum;
	}
	public static void pushBit(Item item, int index, boolean bit)
	{
		item.setState(index,
				(
				(item.getState(index)>>1)|
				(((bit?1:0)&1)<<7)
				)
				&0xFF);
		
	}
	public static void serialWrite(int toWrite, Pin clock, Pin signal)
	{
		for(int bit = 0; bit<8; bit++)
		{
			if((toWrite & (1<<bit)) == (1<<bit))
			{
				signal.setPotential(1024);
			}
			else
			{
				signal.setPotential(0);
			}
			clock.setPotential(1024);
			clock.setPotential(0);
		}
		signal.setPotential(0);
	}
}
