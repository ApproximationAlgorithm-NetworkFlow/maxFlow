package com.goldbergrao;

import java.util.ArrayList;

public class Node {
	int value;
	private boolean isBlocked = false;
	private ArrayList<FlowEdge> outEdges = new ArrayList<>();
	private ArrayList<FlowEdge> inEdges = new ArrayList<>();
	private int excess = 0;
	private int dist = Integer.MAX_VALUE;
	private int label = 0;

	// For Strongly Connected Components
	private int index = -1;
	private int lowLink = -1;

	// Contracting Strongly Connected Components
	private int superNode = -1;

	// For Routing the Flow
	private int supply = -1;
	private int demand = -1;
	private int descendantDemand;
	private Node inTreeParent = null;
	private Node outTreeParent = null;
	private ArrayList<Node> inTree = new ArrayList<Node>();
	private ArrayList<Node> outTree = new ArrayList<Node>();

	// TODO Verify Excess
	public int getExcess() {
		/*
		 * int outDegree = 0, inDegree = 0; for (FlowEdge outEdge : outEdges) {
		 * outDegree += outEdge.getFlow(); }
		 */
		return excess;
	}

	public void setExcess(int excess) {
		this.excess = excess;
	}

	public Node(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isBlocked() {
		return isBlocked;
	}

	public void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public int getDist() {
		return dist;
	}

	public void setDist(int dist) {
		this.dist = dist;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public ArrayList<FlowEdge> getOutEdges() {
		return outEdges;
	}

	public void setOutEdges(ArrayList<FlowEdge> outEdges) {
		this.outEdges = outEdges;
	}

	public ArrayList<FlowEdge> getInEdges() {
		return inEdges;
	}

	public void setInEdges(ArrayList<FlowEdge> inEdges) {
		this.inEdges = inEdges;
	}

	public void addOutEdge(FlowEdge flowEdge) {
		this.outEdges.add(flowEdge);
	}

	public void addInEdge(FlowEdge flowEdge) {
		this.inEdges.add(flowEdge);
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public String toString() {

		return String.valueOf(value);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getLowLink() {
		return lowLink;
	}

	public void setLowLink(int lowLink) {
		this.lowLink = lowLink;
	}

	public int getSuperNode() {
		return superNode;
	}

	public void setSuperNode(int superNode) {
		this.superNode = superNode;
	}

	public int getSupply() {
		return supply;
	}

	public void setSupply(int supply) {
		this.supply = supply;
	}

	public int getDemand() {
		return demand;
	}

	public void setDemand(int demand) {
		this.demand = demand;
	}

	public int getDescendantDemand() {
		return descendantDemand;
	}

	public void setDescendantDemand(int descendantDemand) {
		this.descendantDemand = descendantDemand;
	}

	public Node getInTreeParent() {
		return inTreeParent;
	}

	public void setInTreeParent(Node inTreeParent) {
		this.inTreeParent = inTreeParent;
	}

	public Node getOutTreeParent() {
		return outTreeParent;
	}

	public void setOutTreeParent(Node outTreeParent) {
		this.outTreeParent = outTreeParent;
	}

	public ArrayList<Node> getInTree() {
		return inTree;
	}

	public void addToInTree(Node node) {
		this.inTree.add(node);
	}

	public ArrayList<Node> getOutTree() {
		return outTree;
	}

	public void addToOutTree(Node node) {
		this.outTree.add(node);
	}

}
