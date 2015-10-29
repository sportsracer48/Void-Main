package state.ui;

import state.workbench.conroller.DragArea;
import state.workbench.conroller.DragContext;
import graphics.Sprite;
import graphics.entity.Entity;

public class Window extends Entity
{
	public static final float BORDER = 17;
	public static final float PADDING = 2;
	public Window(float x, float y, Sprite base, Button close, DragContext context)
	{
		super(x, y, 0, base);
		close.setPos(
				getWidth()-BORDER-close.getWidth(), 
				BORDER);
		
		new DragArea(BORDER,BORDER,
				getWidth()-BORDER*2-close.getWidth()-PADDING,
				close.getHeight(),
				context,
				this);
		setRoot(new ClickableArea(BORDER,BORDER,getWidth()-2*BORDER,getHeight()-2*BORDER));
		addChild(close);
		close.setOnPress(()->this.setEnabled(false));
	}
	
	
}
