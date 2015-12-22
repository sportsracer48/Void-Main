package program;

public interface OuinoInterface
{
	public void pinMode(int pin, int mode);
	public void digitalWrite(int pin, int val);
	public void delay(int ms);
}
