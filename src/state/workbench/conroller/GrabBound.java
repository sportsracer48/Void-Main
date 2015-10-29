package state.workbench.conroller;

public class GrabBound<T>
{
	T value;
	T _default;
	
	public void setValue(T t)
	{
		this.value = t;
	}
	
	public T getValue()
	{
		if(!hasValue())
		{
			return _default;
		}
		return value;
	}
	
	public void setDefault(T _default)
	{
		this._default = _default;
	}
	
	public void reset()
	{
		this.value = null;
	}
	
	public boolean hasValue()
	{
		return value != null;
	}
}
