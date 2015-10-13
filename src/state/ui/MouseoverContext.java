package state.ui;

public class MouseoverContext
{
	public ClickableArea mouseHolder;
	public boolean hasMouseHolder()
	{
		return mouseHolder != null;
	}
	public void setMouseHolder(ClickableArea holder)
	{
		if(mouseHolder != null)
		{
			mouseHolder.setOwnsMouse(false);
		}
		mouseHolder = holder;
		if(mouseHolder != null)
		{
			mouseHolder.setOwnsMouse(true);
		}
	}
	public ClickableArea getMouseHolder()
	{
		return mouseHolder;
	}
}
