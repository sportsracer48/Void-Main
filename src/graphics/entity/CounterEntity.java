package graphics.entity;

import game.item.Item;
import game.item.ItemEntity;
import graphics.Sprite;

public class CounterEntity extends ItemEntity
{
	/*
	 *  000
	 * 1   2
	 * 1   2
	 * 1   2
	 *  333
	 * 4   5
	 * 4   5
	 * 4   5
	 *  666
	 */
	public static final int zero = 0x1;
	public static final int one = 0x2;
	public static final int two = 0x4;
	public static final int three = 0x8;
	public static final int four = 0x10;
	public static final int five = 0x20;
	public static final int six = 0x40;
	
	public static final int zeroPattern = zero|one|two|four|five|six;
	public static final int onePattern = two|five;
	public static final int twoPattern = zero|two|three|four|six;
	public static final int threePattern = zero|two|three|five|six;
	public static final int fourPattern = one|two|three|five;
	public static final int fivePattern = zero|one|three|five|six;
	public static final int sixPattern = zero|one|three|four|five|six;
	public static final int sevenPattern = zero|two|five;
	public static final int eightPattern = zero|one|two|three|four|five|six;
	public static final int ninePattern = zero|one|two|three|five|six;
	
	public static final int[] patterns = {zeroPattern,onePattern,twoPattern,threePattern,fourPattern,fivePattern,sixPattern,sevenPattern,eightPattern,ninePattern};
	
	DigitEntity[] digits;
	Sprite H, V;
	
	public CounterEntity(float x, float y, float z, float cx, float cy, Item item, int digits, Sprite H, Sprite V)
	{
		super(x, y, z, item);
		this.H = H;
		this.V = V;
		this.digits = new DigitEntity[3];
		for(int i = 0; i<digits; i++)
		{
			this.digits[i] = new DigitEntity(cx+i*7,cy,0);
			addChild(this.digits[i]);
		}
	}
	
	public void setNumber(int number)
	{
		int accum = 0;
		for(int digit = digits.length-1; digit>=0; digit--)
		{
			int place = (int) Math.pow(10, 3-digit-1);
			int digitValue = (number%(place*10) - accum)/place;
			accum += digitValue*place;
			digits[digit].setDigit(digitValue);//like pokemon
		}
	}
	
	public void turnOff()
	{
		for(DigitEntity e:digits)
		{
			e.turnOff();
		}
	}
	
	class DigitEntity extends Entity
	{
		Entity[] panels = new Entity[7];
		
		public DigitEntity(float x, float y, float z)
		{
			super(x, y, z, null);
			panels[0] = new Entity(1,0,0,H);
			panels[1] = new Entity(0,1,0,V);
			panels[2] = new Entity(4,1,0,V);
			panels[3] = new Entity(1,5,0,H);
			panels[4] = new Entity(0,6,0,V);
			panels[5] = new Entity(4,6,0,V);
			panels[6] = new Entity(1,10,0,H);
			for(Entity e:panels)
			{
				e.setEnabled(false);
				addChild(e);
			}
		}
		public void turnOff()
		{
			for(int i = 0; i<7; i++)
			{
				panels[i].setEnabled(false);
			}
		}
		public void setDigit(int digit)
		{
			int pattern = patterns[digit];
			for(int bit = 0; bit<7; bit++)
			{
				panels[bit].setEnabled((pattern>>bit & 1) == 1);
			}
		}
	}
}
