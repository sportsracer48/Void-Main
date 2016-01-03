package state.programming;

public class Modifiers
{
	public static final int SHIFT_FLAG = 0x1;
	public static final int CONTROL_FLAG = 0x2;
	public static final int ALT_FLAG = 0x4;
	
	public static boolean isShiftDown(int flags)
	{
		return (flags & SHIFT_FLAG) != 0;
	}
	public static boolean isControlDown(int flags)
	{
		return (flags & CONTROL_FLAG) != 0;
	}
	public static boolean isAltDown(int flags)
	{
		return (flags & ALT_FLAG) != 0;
	}
}
