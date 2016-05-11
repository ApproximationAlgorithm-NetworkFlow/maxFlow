package com.edmondKarp;

public class FlowNetwork
{
 private int V;
 private int E;
 private Bag<FlowEdge>[] adj;
 //empty graph with V edges
 public FlowNetwork(int V)
 {
	 this.V = V;
	 this.E=0;
	 adj = (Bag<FlowEdge>[]) new Bag[V];
	 for (int v = 0; v < V; v++)
		 adj[v] = new Bag<FlowEdge>();
 }
 // random graph with V vertices and E edges
 public FlowNetwork(int V, int E) {
     this(V);
     for (int i = 0; i < E; i++) {
         int v = StdRandom.uniform(V);
         int w = StdRandom.uniform(V);
         int capacity = StdRandom.uniform(100);
         addEdge(new FlowEdge(v, w, capacity));
     }
 }
 public FlowNetwork(int numVert,String[] inputString) {
	 this(numVert);
	 for(int i=0;i<inputString.length;i++)
	 {
		 String[] temp = inputString[i].split("\\s+");
		 addEdge(new FlowEdge(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]),Integer.parseInt(temp[2])));
	 }

 }
 public void addEdge(FlowEdge e)
 {
	 this.E++;
	 int v = e.from();
	 int w = e.to();
	 adj[v].add(e);
	 adj[w].add(e);
 }
 // public int E() { return E; }
 public Iterable<FlowEdge> adj(int v)
 { 
	 return adj[v]; 
 }
 //return number of vertices and edges
 public int V()
 {
	 return V;
 }
 public int E() 
 { 
	 return E; 
 }
 // return list of all edges - excludes self loops
 public Iterable<FlowEdge> edges() {
     Bag<FlowEdge> list = new Bag<FlowEdge>();
     for (int v = 0; v < V; v++)
         for (FlowEdge e : adj(v)) {
             if (e.to() != v)
                 list.add(e);
         }
     return list;
 }
} 