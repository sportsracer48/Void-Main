package state.ui;

import state.workbench.DragArea;
import state.workbench.DragContext;
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
		addClickableArea(close.getArea());
		setRoot(new ClickableArea(BORDER,BORDER,getWidth()-2*BORDER,getHeight()-2*BORDER)
		{
			public void mouseEntered()
			{
			}

			public void mouseExited()
			{
			}

			public void onClick(float x, float y)
			{
			}

			public void onRelease()
			{
			}
		});
		addChild(close);
		close.setOnPress(()->this.setVisible(false));
	}
	
	
}
