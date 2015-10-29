package util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import util.Grid.Coord;

public class AStar
{
	static int buffer = 10;
	public static ArrayList<Coord> aStar(Coord start,Coord goal, float allowedZ, float[][]z)
	{
		long startTime = System.currentTimeMillis();
		ArrayList<Coord> path = new ArrayList<>();
		int left = Math.min(start.x, goal.x)-buffer;
		int right = Math.max(start.x, goal.x)+buffer;
		
		int top = Math.min(start.y, goal.y)-buffer;
		int bottom = Math.max(start.y, goal.y)+buffer;
		
		Node[][] grid = new Node[right-left+1][bottom-top+1];
		
		PriorityQueue<Node> open=new PriorityQueue<Node>();  //This list of nodes to check
		Node startNode = getNode(left,top,grid,start.x,start.y,null,goal);
		startNode.g=0;
		open.add(startNode);         //Currently all we need to check is the start
		
		HashSet<Node> closed=new HashSet<Node>();//The list of checked nodes, currently empty
		Node goalNode=getNode(left,top,grid,goal.x,goal.y,null,goal);
		
		while(!closed.contains(goalNode) && !open.isEmpty())
		{
			Node leastFScoreNode=open.poll();			 //This should always be what you check. The node with the lowest f score. That is what A* is all about
			closed.add(leastFScoreNode); 
			
			
			for(Coord neighbor:getNeighbors(leastFScoreNode.loc,left,right,top,bottom,z,allowedZ,goal))	//Now look at its neighbors
			{
				Node neighborNode = getNode(left,top,grid,neighbor.x,neighbor.y,leastFScoreNode,goal);
				if(!closed.contains(neighborNode)) //Have we already checked it?
				{
					if(!open.contains(neighborNode))	//Is it already in the openlist?
					{
						open.add(neighborNode); //If not, add it.
					}
					else if(neighborNode.g>leastFScoreNode.g+1) //If it is on the openlist, have we found a better path to it?
					{
						neighborNode.parent=leastFScoreNode; //If so, make this node the parent
						neighborNode.g=leastFScoreNode.g+1; //and adjust the g score accordingly
						neighborNode.f=neighborNode.g+neighborNode.h; //and adjust the f score accordingly
					}
				}
			}
		}
		if(!closed.contains(goalNode))
		{
			return path;
		}
		
		Node current=goalNode;
		while(current!=startNode) //Go backwards to the start
		{
			path.add(current.loc); //Adding each node to the path
			current=current.parent;
		}
		path.add(current.loc);
		long endTime = System.currentTimeMillis();
		System.out.println(endTime-startTime);
		return path;
	}
	
	private static boolean isValid(Coord c, Coord goal, float[][] z, float allowedZ)
	{
		return goal.equals(c) || z[c.x][c.y]<allowedZ;
	}
	
	private static List<Coord> getNeighbors(Coord c, int left, int right, int top, int bottom,float[][] z, float allowedZ, Coord goal)
	{
		List<Coord> toReturn = new ArrayList<Coord>();
		
		int xMin,xMax,yMin,yMax;
		for(xMin = c.x-1;xMin>=left && isValid(new Coord (xMin,c.y), goal, z, allowedZ);xMin--)
		{
			toReturn.add(new Coord(xMin,c.y));
		}
		for(xMax = c.x+1;xMax<=right && isValid(new Coord (xMax,c.y), goal, z, allowedZ);xMax++)
		{
			toReturn.add(new Coord(xMax,c.y));
		}
		for(yMin = c.y-1;yMin>=top && isValid(new Coord (c.x,yMin), goal, z, allowedZ);yMin--)
		{
			toReturn.add(new Coord(c.x,yMin));
		}
		for(yMax = c.y+1;yMax<=bottom && isValid(new Coord (c.x,yMax), goal, z, allowedZ);yMax++)
		{
			toReturn.add(new Coord(c.x,yMax));
		}
		
		return toReturn;
	}
	
	private static Node getNode(int left, int top, Node[][] grid, int x, int y, Node parent, Coord goal)
	{
		Node n = grid[x-left][y-top];
		if(n==null)
		{
			n = new Node(new Coord(x,y),parent,goal);
			grid[x-left][y-top] = n;
			return n;
		}
		else
		{
			return n;
		}
	}
	
	private static class Node implements Comparable<Node>
	{
		Coord loc;//The node's location
		double g; //Steps from start
		double h; //Distance to goal(estimated minimum cost)
		double f; //g+h;
		Node parent; //the parent, where we walked from
		public Node(Coord loc,Node parent,Coord goal)
		{
			this.loc=loc;
			this.parent=parent;
			if(parent==null)
			{
				this.g=Double.POSITIVE_INFINITY;
			}
			else
			{
				this.g=parent.g+1;
			}
			if(goal==null)
			{
				this.h=0;
			}
			else
			{
				this.h=distance(loc,goal);
			}
			this.f=g+h;
		}
		public boolean equals(Object o)
		{
			return (o instanceof Node && ((Node)o).loc.equals(loc));
		}
		public static double distance(Coord a, Coord b)
		{
			return Math.abs(a.x-b.x)+Math.abs(a.y-b.y);
		}
		public int compareTo(Node o)
		{
			return Double.compare(f, o.f);
		}
	}
}
