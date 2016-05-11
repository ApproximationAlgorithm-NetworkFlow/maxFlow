package com.edmondKarp;
public class FlowEdge
{
	//vertex from and to
	 private int v, w; 
	 private int capacity; 
	 private int flow; 
	 public FlowEdge(int v, int w, int capacity)
	 {
		 if (this.capacity < 0) 
			 System.out.println("Edge cannot  have negative capacity");
		 this.v = v;
		 this.w = w;
		 this.capacity = capacity;
		 this.flow =0;
	 }
    public FlowEdge(int v, int w, int capacity, int flow) {
        if (capacity < 0) 
        	System.out.println("Edge cannot  have negative capacity");
        this.v         = v;
        this.w         = w;  
        this.capacity  = capacity;
        this.flow      = flow;
    }
	 public int from() 
	 { 
		 return v; 
	 }
	 public int to() 
	 { 
		 return w; 
	 }
	 public int capacity() 
	 { 
		 return capacity;
	 }
	 public int flow() 
	 { 
		 return flow; 
	 }
	 public int other(int vertex)
	 {
		 if (vertex == v) return w;
		 else if (vertex == w) return v;
		 else throw new RuntimeException("endpoint not correct");
	 }
	
	public int residualCapacityTo(int vertex)
	{
		if (vertex == v) return flow;
		else if (vertex == w) return capacity - flow;
		else throw new IllegalArgumentException();
	}
	public void addResidualFlowTo(int vertex, int delta)
	{
		if (vertex == v) flow -= delta;
		else if (vertex == w) flow += delta;
		else throw new IllegalArgumentException();
	} 
}