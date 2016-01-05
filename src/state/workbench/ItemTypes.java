package state.workbench;

import java.util.List;

import program.Environment;
import program.OuinoEnvironment;
import util.Grid;
import breadboard.BreadboardUtil;
import game.item.ItemType;
import game.item.Pin;
import graphics.Sprite;
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
	
	public static void init(SpriteAtlas sprites)
	{
		ItemType.setDefaultWireSprites(
				sprites.getSprite("pin highlight end.png"),
				sprites.getSprite("pin highlight.png"),
				sprites.getSprite("wire end.png"),
				sprites.getSprite("wire end opaque.png"),
				sprites.getSprite("wire fade.png"));
		
		final Sprite ledOn = sprites.getSprite("led on.png");
		final Sprite ledOff = sprites.getSprite("led off.png");
		
		breakout = new ItemType(null);
		microController = new ItemType(sprites.getSprite("ouino.png"),sprites.getSprite("ouino item.png"))
		{
			public Environment getEnvironmentFor(List<Pin> pins)
			{
				return new OuinoEnvironment(pins);
			}
		};
		battery = new ItemType(sprites.getSprite("battery.png"),sprites.getSprite("battery item.png"));
		antenna = new ItemType(sprites.getSprite("antenna ic.png"),sprites.getSprite("antenna item.png"));
		breadboard = new ItemType(sprites.getSprite("breadboard.png"),sprites.getSprite("breadboard item.png"));
		poweredWheel = new ItemType(sprites.getSprite("wheel item.png"),2);
		ledOutput = new ItemType(ledOff, sprites.getSprite("led item.png"));
		
		
		ledOutput.setOffsets(-1, -1);
		ledOutput.addPins(new Grid(6,4,3,3,1,2));
		ledOutput.setWireSpritesToDefault(sprites.getSprite("pin mask.png"));
		ledOutput.setTooltips("+5v","GND");
		ledOutput.setPinUpdate((pins,entity)->{
			if(pins.get(0).getReceivedPotential()==1024 && pins.get(1).isGrounded())
			{
				entity.setSpriteAndSize(ledOn);
			}
			else
			{
				entity.setSpriteAndSize(ledOff);
			}
		});
		
		poweredWheel.setTooltips("+12v","GND");
		
		antenna.setOffsets(-1, -1);
		antenna.addPins(new Grid(4,3,3,3,1,3));
		antenna.setWireSpritesToDefault(sprites.getSprite("pin mask.png"));
		
		
		battery.setOffsets(-1, -1);
		battery.addPins(new Grid(9,18,3,3,1,2));
		battery.addPins(new Grid(18,18,3,3,1,2));
		battery.setWireSpritesToDefault(sprites.getSprite("pin mask.png"));
		battery.setTooltips("+12v out","GND out","+12v in","GND in");
		battery.setPinUpdate((pins,entity)->{
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
		breadboard.setPinUpdate((pins,entity)->{
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
	}
}
