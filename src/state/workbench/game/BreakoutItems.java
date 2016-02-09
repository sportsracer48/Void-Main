package state.workbench.game;

public class BreakoutItems
{
	ExternalBreakout top;
	ExternalBreakout bot;
	ExternalBreakout left;
	ExternalBreakout right;
	ExternalBreakout front;
	ExternalBreakout back;
	
	ExternalBreakout[] allBreakouts;
	
	public BreakoutItems(ExternalBreakout top,ExternalBreakout bot,ExternalBreakout left, ExternalBreakout right,ExternalBreakout front, ExternalBreakout back)
	{
		this.top = top;
		this.bot = bot;
		this.left = left;
		this.right = right;
		this.front = front;
		this.back = back;
		allBreakouts = new ExternalBreakout[]{top,bot,left,right,front,back};
	}
}
