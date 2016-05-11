package com.goldbergrao;

public class FlowEdge {
	private Node fromNode, toNode; 
	private int capacity = 0;
	private int flow = 0; 
	private int residualCapacity = 0; 

	public FlowEdge(Node v, Node w, int capacity) {
		if (this.capacity < 0)
			throw new RuntimeException("Negative edge capacity");
		this.fromNode = v;
		this.toNode = w;
		this.capacity = capacity;
		this.flow = 0;
	}

	public FlowEdge(Node v, Node w, int capacity, int flow) {
		if (capacity < 0)
			throw new RuntimeException("Negative edge capacity");
		this.fromNode = v;
		this.toNode = w;
		this.capacity = capacity;
		this.flow = flow;
	}

	public void updateFlow(int flow) {
		this.flow += flow;
		updateResidualCapacity(this.flow);
	}

	private void updateResidualCapacity(int newFlow) {
		this.residualCapacity = capacity - newFlow;
	}

	public int getResidualCapacity() {
		return residualCapacity;
	}

	public Node getFromNode() {
		return fromNode;
	}

	public Node getToNode() {
		return toNode;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getFlow() {
		return flow;
	}

	@Override
	public String toString() {
		StringBuilder buider = new StringBuilder();
		buider.append("From-Node: ").append(getFromNode().getValue());
		buider.append("  To-Node: ").append(getToNode().getValue());
		buider.append("  Capacity: ").append(getCapacity());
		if (flow != 0) {
			buider.append("  Flow: ").append(getFlow());
			buider.append("  Residual-Flow: ").append(getResidualCapacity());
		}
		return buider.toString();

	}

}