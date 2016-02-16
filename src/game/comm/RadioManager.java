package game.comm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import game.session.GlobalState;

public class RadioManager implements Serializable
{
	private static final long serialVersionUID = 851246162852347917L;
	
	HashSet<Serializable>[] broadcasters;
	List<RadioHook> radioHooks = new ArrayList<>();
	RadioHook sensorFeedUpdate;
	
	@SuppressWarnings("unchecked")
	public RadioManager()
	{
		broadcasters = new HashSet[256];
		for(int i = 0; i<broadcasters.length; i++)
		{
			broadcasters[i] = new HashSet<Serializable>();
		}
	}
	
	public RadioHook getSensorFeedUpdate()
	{
		return sensorFeedUpdate;
	}
	public void setSensorFeedUpdate(RadioHook sensorFeedUpdate)
	{
		if(this.sensorFeedUpdate != null)
		{
			radioHooks.remove(this.sensorFeedUpdate);
		}
		this.sensorFeedUpdate = sensorFeedUpdate;
		addRadioHook(sensorFeedUpdate);
	}
	public void addRadioHook(RadioHook hook)
	{
		radioHooks.add(hook);
	}
	public void updateHooks()
	{
		for(RadioHook hook:radioHooks)
		{
			hook.radioUpdate();
		}
	}
	
	public boolean isOn(int channel)
	{
		return broadcasters[channel].size() > 0;
	}
	public void broadcast(int channel, Serializable broadcaster)
	{
		boolean wasOn = isOn(channel);
		broadcasters[channel].add(broadcaster);
		if(isOn(channel) != wasOn)
		{
			GlobalState.radioUpdateAll();
			updateHooks();
			
		}
	}
	public void stopBroadcast(Serializable broadcaster)
	{
		for(int channel = 0; channel<broadcasters.length; channel++)
		{
			if(broadcasters[channel].contains(broadcaster))
			{
				stopBroadcast(channel,broadcaster);
			}
		}
	}
	public void stopBroadcast(int channel, Serializable broadcaster)
	{
		boolean wasOn = isOn(channel);
		broadcasters[channel].remove(broadcaster);
		if(isOn(channel) != wasOn)
		{
			GlobalState.radioUpdateAll();
			updateHooks();
		}
	}
}
