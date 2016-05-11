package com.edmondKarp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import com.goldbergrao.GoldbergRao;

public class Edmond_Karp {
	private boolean[] colored; // true if there is s->v path in residual network
	private FlowEdge[] edge; // last edge on s->v path
	private int value; // value of flow
	public Edmond_Karp(FlowNetwork G, int s, int t)
	{
		 value = 0;
		 while (hasAugmentingPath(G, s, t))
		 {
			 int bottle = Integer.MAX_VALUE;
			 //find bottleneck 
			 for (int v = t; v != s; v = edge[v].other(v))
				 bottle = Math.min(bottle, edge[v].residualCapacityTo(v));
			 for (int v = t; v != s; v = edge[v].other(v))
				 edge[v].addResidualFlowTo(v, bottle);
			 value += bottle;
		 }
	}
	public int value()
	{
		return value; 
	}
	 /**
     * checks if V is reachable from S
     */
	public boolean inCut(int v)
	{ 
		return colored[v]; 
	}
	 /**
     * BFS for finding augmented path which takes O(V+E)
     */
	private boolean hasAugmentingPath(FlowNetwork G, int s, int t)
	{
		  edge = new FlowEdge[G.V()];
		  colored = new boolean[G.V()];
		  Queue<Integer> queue = new Queue<Integer>();
		  queue.enqueue(s);
		  colored[s] = true;
		  while (!queue.isEmpty())
		  {
			  int v = queue.dequeue();
			  for (FlowEdge e : G.adj(v))
			  {
				  int w = e.other(v);
				  //scheck if there is a path in residual graph
				  if (e.residualCapacityTo(w) > 0 && !colored[w])
				  {
					  edge[w] = e;
					  colored[w] = true;
					  queue.enqueue(w);
				  }
			  }
		  }
		  // return augmenting path
		  return colored[t-1];
	}
 public static void main(String[] args){
     /* create flow network with V vertices and E edges
      * read from file the no. of vertices , edges and the graph connectivity with capacity
	 */
	 String fileName = "GraphInput.txt";
	 String readLine =null;
	 int V =0, E=0,i=0;
	 FlowNetwork G = null;
	 try
	 {
		 URL url = Edmond_Karp.class.getResource(fileName);
		 File inputFile = new File(url.getPath());
		 FileReader fileReader = new FileReader(inputFile); 
		 BufferedReader bufferedReader = new BufferedReader(fileReader);
		 V = Integer.parseInt(bufferedReader.readLine());
		 E = Integer.parseInt(bufferedReader.readLine());

		 String[] input = new String[E];
		 while((readLine = bufferedReader.readLine()) != null)
		 {
			 input[i]=readLine;
			 i++;
		 }
		 G= new FlowNetwork(V,input);
		 bufferedReader.close();
		 
		  int src = 0, sink = V-1;    
		     StdOut.println(V+E);
		     //StdOut.println(E);
		     // compute maximum flow and minimum cut
		     
		     long startTime = System.currentTimeMillis();
		     Edmond_Karp maxflow = new Edmond_Karp(G, src, sink);
		     StdOut.println("Finding the Max flow from " + src + " to " + sink);
		     StdOut.println("Intermediate Flow: ");
		     StdOut.println("From \t To \t Flow");
		     for (int v = 0; v < G.V(); v++) {
		         for (FlowEdge e : G.adj(v)) {
		             if ((v == e.from()) && e.flow() > 0)
		                 StdOut.println(e.from()+"\t    "+e.to()+"\t   "+ e.flow());
		         }
		     }
		     long totalTime = System.currentTimeMillis() - startTime;
			 System.out.println("Total time in ms " + totalTime);
		     
		     // print min-cut
		     StdOut.print("Min cut: ");
		     for (int v = 0; v < G.V(); v++) {
		         if (maxflow.inCut(v)) 
		        	 StdOut.print(v + " ");
		     }
		     StdOut.println();

		     StdOut.println("Max flow value = " +  maxflow.value());
	 }
	 catch(FileNotFoundException ex) {
		 ex.printStackTrace();             
     }
     catch(IOException ex) {
         ex.printStackTrace();
     }    
    }
 }


