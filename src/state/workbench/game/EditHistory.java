package state.workbench.game;

import game.item.Wire;

import java.util.ArrayList;
import java.util.List;

public class EditHistory
{
	List<SavedState> history = new ArrayList<>();
	int statePtr;
	ChassisGrid view;
	WiringMode wireEditor;
	
	public SavedState copyState()
	{
		Wire current = wireEditor.getCurrent() == null?null:wireEditor.getCurrent().clone();
		SavedState newState = new SavedState(view.contents,current,view.getBreakouts());
		return newState;
	}
	
	public void saveState()
	{
		SavedState lastState = history.get(statePtr);
		SavedState currentState = copyState();
		if(lastState.equals(currentState)) // no edit
		{
			return;
		}
		while(statePtr+1<history.size())
		{
			history.remove(statePtr+1);
		}
		history.add(currentState);
		statePtr++;
	}
	
	public void undo()
	{
		if(statePtr==0)
		{
			return;
		}
		SavedState newState = history.get(statePtr-1);
		newState.load(view.contents,wireEditor,view.getBreakouts());
		statePtr--;
		view.revalidateEntities();
	}
	
	public void redo()
	{
		if(statePtr == history.size()-1)
		{
			return;
		}
		SavedState newState = history.get(statePtr+1);
		newState.load(view.contents,wireEditor,view.getBreakouts());
		statePtr++;
		view.revalidateEntities();
	}

	public void init()
	{
		history.clear();
		history.add(copyState());
		statePtr = 0;
	}
	public void init(SavedState toLoad)
	{
		history.clear();
		history.add(toLoad);
		statePtr = 0;
		toLoad.load(view.contents, wireEditor, view.getBreakouts());
		view.revalidateEntities();
	}
}
