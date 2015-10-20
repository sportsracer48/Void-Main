package state.ui;

public class MouseoverContext
{
	public ClickableArea mouseHolder;
	boolean frozen;
	public boolean hasMouseHolder()
	{
		return mouseHolder != null;
	}
	public void setMouseHolder(ClickableArea holder)
	{
		if(frozen || (holder!=null && !holder.desiresMouse()))
		{
			return;
		}
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
	public void setFrozen(boolean isFrozen)
	{
		frozen = isFrozen;
	}
}
