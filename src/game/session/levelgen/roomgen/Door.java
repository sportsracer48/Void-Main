package game.session.levelgen.roomgen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Door
{
	int x, y;
	public Door(int x, int y)
	{
		this.x=x;
		this.y=y;
	}
	public List<Room> getValidRooms(int maxSize, Predicate<Room> validator,int n)
	{
		List<Room> result = new ArrayList<>();
		for(int left = -maxSize; left<= maxSize; left++)
		{
			for(int right = left+2; right<=maxSize;right++)
			{
				for(int top = -maxSize; top<=maxSize; top++)
				{
					for(int bot = top+2; bot <=maxSize; bot++)
					{
						if(top!=0 && bot!=0 && left!=0 && right!=0)//not on the door
						{
							continue;
						}
						if((top == 0 || bot == 0) && (left == 0 || right ==0))//on a corner
						{
							continue;
						}
						Room candidate = new Room(x+left,y+top,right-left+1,bot-top+1,n);
						if(!candidate.contains(x, y))
						{
							continue;
						}
						if(validator.test(candidate))
						{
							result.add(candidate);
						}
					}
				}
			}
		}
		return result;
	}
}
