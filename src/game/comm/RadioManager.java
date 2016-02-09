package game.comm;

import java.io.Serializable;
import java.util.HashSet;

import entry.GlobalState;

public class RadioManager implements Serializable
{
	private static final long serialVersionUID = 851246162852347917L;
	
	HashSet<Serializable>[] broadcasters;
	
	@SuppressWarnings("unchecked")
	public RadioManager()
	{
		broadcasters = new HashSet[256];
		for(int i = 0; i<broadcasters.length; i++)
		{
			broadcasters[i] = new HashSet<Serializable>();
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
		}
	}
	public void stopBroadcast(int channel, Serializable broadcaster)
	{
		boolean wasOn = isOn(channel);
		broadcasters[channel].remove(broadcaster);
		if(isOn(channel) != wasOn)
		{
			GlobalState.radioUpdateAll();
		}
	}
}
