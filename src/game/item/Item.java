package game.item;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import program.Environment;
import state.workbench.game.WiringMode;
import state.workbench.graphics.PinHighlight;
import game.map.unit.Unit;
import game.session.GlobalState;
import graphics.Sprite;
import graphics.entity.Entity;

public class Item implements Serializable
{
	private static final long serialVersionUID = -3436670486907081262L;
	
	transient ItemType type;
	int typeId;
	List<Pin> pins;
	List<Pin> breakoutPins;
	List<Long> state = new ArrayList<>();
	Environment env;
	Inventory contents;
	String name;
	Unit unit;
	
	public Item(ItemType type)
	{
		this.type = type;
		pins = type.getPins(this);
		breakoutPins = type.getBreakoutPins(this);
		env = type.getEnvironmentFor(pins);
		typeId = type.typeId;
		if(type.isUnit())
		{
			unit = type.makeUnit();
			unit.setItem(this);
		}
	}
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
	{
		stream.defaultReadObject();
		type = ItemTypes.fromId(typeId);
		if(env!=null)
		{
			GlobalState.getCoordinator().addEnvironment(env);
		}
	}
	public Unit getUnit()
	{
		return unit;
	}
	
	public Entity getInvEntity()
	{
		return new Entity(0,0,0,getInvSprite());
	}
	
	public Sprite getInvSprite()
	{
		return type.inventory;
	}
	
	public Sprite getWorldSprite()
	{
		return type.workbench;
	}
	
	public ItemType getType()
	{
		return type;
	}
	
	public Entity getWorldEntity()
	{
		return type.getWorldEntity(this);
	}
	
	public boolean existsInWorld()
	{
		return getWorldSprite()!=null;
	}
	
	public void updateExported(int location, Unit unit)
	{
		if(type.getExportPinUpdate()!=null)
		{
			type.getExportPinUpdate().accept(getPins(), getBreakoutPins(),location,unit,this);
		}
	}
	
	public void setState(int stateIndex, long stateLong)
	{
		while(state.size()<stateIndex+1)
		{
			state.add(0L);
		}
		state.set(stateIndex, stateLong);
	}
	
	public long getState(int stateIndex)
	{
		while(state.size()<stateIndex+1)
		{
			state.add(0L);
		}
		return state.get(stateIndex);
	}
	
	public void resetState()
	{
		state.clear();
	}
	
	public void pinUpdate()
	{
		if(type.getLogicUpdate() != null)
		{
			type.getLogicUpdate().accept(getPins(),this);
		}
		if(env != null)
		{
			env.logicUpdate();
		}
	}
	public void graphicsUpdate(ItemEntity entity)
	{
		if(type.getGraphicsPinUpdate() == null)
		{
			return;
		}
		type.getGraphicsPinUpdate().accept(pins,entity);
	}
	public void radioUpdate()
	{
		if(type.getRadioUpdate() == null)
		{
			return;
		}
		type.getRadioUpdate().accept(pins,this);
	}
	
	public void resetPinsAndState()
	{
		for(Pin p:getPins())
		{
			p.strip();
			p.setPotential(0);
			p.setGrounded(false);
		}
		resetState();
	}
	public void stripPins()
	{
		for(Pin p:getPins())
		{
			p.strip();
		}
	}
	
	public List<String> getTooltips()
	{
		return type.getTooltips();
	}
	
	public List<PinHighlight> getPinHighlights(WiringMode mode)
	{
		List<PinHighlight> toReturn = new ArrayList<>();
		
		getPins().forEach(c->{
			toReturn.add(new PinHighlight(c.x-1,c.y-1,type.highlight,type.wireEndOpaque,type.wireFade,type.pinMask,mode,c));
		});
		List<String> tooltips = getTooltips();
		
		if(toReturn.size() == tooltips.size())
		{
			for(int i = 0; i<toReturn.size(); i++)
			{
				toReturn.get(i).setTooltip(tooltips.get(i));
			}
		}
		
		type.stripEndLocations.forEach(c->{
			toReturn.add(new PinHighlight(c.x-1,c.y-1,type.endCap,type.wireEndOpaque,type.wireFade,type.pinMask,null,mode,false));
		});
		
		return toReturn;
	}

	public List<Pin> getPins()
	{
		return pins;
	}
	public List<Pin> getBreakoutPins()
	{
		return breakoutPins;
	}

	public Environment getEnvironment()
	{
		return env;
	}

}
