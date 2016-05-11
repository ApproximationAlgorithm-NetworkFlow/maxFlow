package com.goldbergrao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class FlowNetwork {
	private int V;
	private int E;
	private int U = Integer.MIN_VALUE;
	// SrcNode to Edge Map
	private HashMap<Node, ArrayList<FlowEdge>> edgeToIdMap = new HashMap<Node, ArrayList<FlowEdge>>();
	private HashSet<FlowEdge> edges = new HashSet<FlowEdge>();
	private HashSet<Node> nodes = new HashSet<Node>();
	private Node sourceNode;
	private Node sinkNode;

	// Empty Graph
	public FlowNetwork() {
		this.V = 1;
		this.E = 0;
	}

	public void addEdge(FlowEdge e) throws IllegalArgumentException {
		E++;
		nodes.add(e.getFromNode());
		nodes.add(e.getToNode());
		e.getFromNode().addOutEdge(e);
		e.getToNode().addInEdge(e);
		if (edgeToIdMap.containsKey(e.getFromNode())) {
			ArrayList<FlowEdge> edges = edgeToIdMap.get(e.getFromNode());
			edges.add(e);
			edgeToIdMap.put(e.getFromNode(), edges);
		} else {
			ArrayList<FlowEdge> edges = new ArrayList<FlowEdge>();
			edges.add(e);
			edgeToIdMap.put(e.getFromNode(), edges);
		}
		edges.add(e);
		e.updateFlow(0);

		if (e.getCapacity() > this.U) {
			this.U = e.getCapacity();
		}

	}

	public int getV() {
		return nodes.size();
	}

	public int E() {
		return E;
	}

	/*
	 * @return list of all edges - excludes self loops
	 */
	public Collection<FlowEdge> edges() {
		return this.edges;
	}

	public Collection<Node> getNodes() {
		return nodes;
	}

	public int getU() {
		return this.U;
	}

	public Node getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(Node sourceNode) {
		this.sourceNode = sourceNode;
		nodes.add(sourceNode);
	}

	public Node getSinkNode() {
		return sinkNode;
	}

	public void setSinkNode(Node sinkNode) {
		this.sinkNode = sinkNode;
		nodes.add(sinkNode);
	}

	public Node getNode(int value) {
		for (Node node : this.nodes) {
			if (node.value == value) {
				return node;
			}
		}
		return null;
	}

}
