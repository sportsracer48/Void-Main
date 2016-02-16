package state.viewport;

import game.comm.RadioHook;
import game.comm.RadioManager;
import game.session.GlobalState;

public class ViewportRadioHook implements RadioHook
{
	private static final long serialVersionUID = 5521247503181386471L;
	
	int clockChan, signalChan;
	
	public ViewportRadioHook(int clockChan, int signalChan)
	{
		this.clockChan = clockChan;
		this.signalChan = signalChan;
	}
		
	boolean wasOn = false;
	long sensorFeed;
	public void radioUpdate()
	{
		RadioManager radio = GlobalState.getRadio();
		boolean clockOn = radio.isOn(clockChan);
		boolean signalOn = radio.isOn(signalChan);
		if(!wasOn && clockOn)
		{
			sensorFeed = (sensorFeed >>> 1) | ((signalOn?1L:0L)<<63);
			GlobalState.currentViewport.setSensorFeed(sensorFeed);
		}
		wasOn = clockOn;
	}
}
