package com.goldbergTarjan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.goldbergrao.FlowEdge;
import com.goldbergrao.FlowNetwork;
import com.goldbergrao.Node;

public class GoldbergTarjan {

	private static LinkedList<Node> fifoQueue = new LinkedList<Node>();
	private static final int Infinite = Integer.MAX_VALUE;
	public FlowNetwork flowNetwork;
	public double delta;

	public GoldbergTarjan(FlowNetwork flowNetwork, double delta) {
		this.flowNetwork = flowNetwork;
		this.delta = delta;
	}

	public int maxFlow(FlowNetwork flowNetwork) {

		HashMap<Node, Integer> nodeToCurrentEdgePointer = new HashMap<Node, Integer>();
		// TODO Verify
		// flowNetwork.getSourceNode().setDist(flowNetwork.getV());
		for (Node node : flowNetwork.getNodes()) {
			nodeToCurrentEdgePointer.put(node, 0);
			// node.setLabel(0);
			node.setExcess(0);
		}

		for (FlowEdge edge : flowNetwork.edges()) {
			// Saturate edges from source. Update the rest of the edges to be 0.
			if (edge.getFromNode() == flowNetwork.getSourceNode()) {
				int capacity = edge.getCapacity();
				edge.updateFlow(capacity);
				edge.getToNode().setExcess(capacity);
				int excessOfSource = flowNetwork.getSourceNode().getExcess()
						- capacity;
				flowNetwork.getSourceNode().setExcess(excessOfSource);
				fifoQueue.add(edge.getToNode());
			} else {
				edge.updateFlow(0);
			}
		}

		// flowNetwork.getSourceNode().setLabel(flowNetwork.getV());

		while (!fifoQueue.isEmpty()) {
			discharge(nodeToCurrentEdgePointer, flowNetwork.getSinkNode());
		}
		return flowNetwork.getSinkNode().getExcess();

	}

	public static void push(FlowEdge edge, boolean isReverse) {
		if (isReverse) {
			int flowAmt = Math
					.min(edge.getToNode().getExcess(), edge.getFlow());
			edge.getToNode().setExcess(edge.getToNode().getExcess() - flowAmt);
			edge.getFromNode().setExcess(
					edge.getFromNode().getExcess() + flowAmt);
			edge.updateFlow(-flowAmt);
		} else {
			int flowAmt = Math.min(edge.getFromNode().getExcess(),
					edge.getResidualCapacity());
			edge.getToNode().setExcess(edge.getToNode().getExcess() + flowAmt);
			edge.getFromNode().setExcess(
					edge.getFromNode().getExcess() - flowAmt);
			// Update flow does current flow + flow Amt and then
			// updatesResidualCapacity()
			edge.updateFlow(flowAmt);
		}
		// edge.setResidualCapacity(edge.getCapacity()-edge.getFlow());
		// return flowAmt;
	}

	public void relabel(Node node) {
		int minLabel = -1;
		ArrayList<FlowEdge> neighbors = node.getOutEdges();
		FlowEdge minEdge = null;

		for (FlowEdge e : neighbors) {

			if (e.getResidualCapacity() > 0) {
				if (minLabel > e.getToNode().getDist() || minLabel == -1) {
					minEdge = e;
					minLabel = e.getToNode().getDist();
				}
			}
		}

		for (FlowEdge e : node.getInEdges()) {
			if (e.getResidualCapacity() == 0) {
				if (minLabel > e.getFromNode().getDist() || minLabel == -1) {
					minLabel = e.getFromNode().getDist();
					minEdge = e;
				}
			}
		}
		int label = minLabel + binaryLength(minEdge, delta, flowNetwork);
		node.setDist(label);
		// node.setLabel(minLabel+1);
	}

	public void pushRelabel(Node v,
			HashMap<Node, Integer> nodeToCurrentEdgePointer) {
		int currentEdge = nodeToCurrentEdgePointer.get(v);
		ArrayList<FlowEdge> edgeList = new ArrayList<FlowEdge>();
		edgeList.addAll(v.getOutEdges());
		edgeList.addAll(v.getInEdges());

		FlowEdge edge = edgeList.get(currentEdge);
		Node u = edge.getToNode();
		if (edge.getToNode() == v) {
			u = edge.getFromNode();
		}

		if (v.getExcess() > 0
				&& (v.getDist() == (u.getDist() + binaryLength(edge,
						this.delta, this.flowNetwork)))
						&& ((edge.getFromNode() == v && edge.getResidualCapacity() > 0) || (edge
								.getToNode() == v && edge.getResidualCapacity() == 0))) {
			if (edge.getToNode() == v) {
				push(edge, true);
			} else {
				push(edge, false);
			}

		} else {
			if (currentEdge != (edgeList.size() - 1)) {
				currentEdge++;
			} else {
				currentEdge = 0;
				boolean eligibleForRelabel = false;
				if (v.getExcess() > 0) {
					for (FlowEdge e : v.getOutEdges()) {
						if (e.getResidualCapacity() > 0) {
							eligibleForRelabel = true;
							break;
						}
					}
					for (FlowEdge e : v.getInEdges()) {
						if (e.getResidualCapacity() == 0) {
							eligibleForRelabel = true;
							break;
						}
					}
					if (eligibleForRelabel) {
						relabel(v);
					}
				}
			}
			nodeToCurrentEdgePointer.put(v, currentEdge);

		}
	}

	public void discharge(HashMap<Node, Integer> nodeToCurrentEdgePointer,
			Node target) {

		Node v = fifoQueue.pop();
		int dist = v.getDist();
		while (true) {
			dist = v.getDist();
			int currentPointer = nodeToCurrentEdgePointer.get(v);
			ArrayList<FlowEdge> edges = new ArrayList<FlowEdge>();
			edges.addAll(v.getOutEdges());
			edges.addAll(v.getInEdges());
			FlowEdge edge = null;
			if (edges != null && !edges.isEmpty()) {
				edge = edges.get(currentPointer);

				int oldResCapacity = edge.getResidualCapacity();

				pushRelabel(v, nodeToCurrentEdgePointer);
				Node w = edge.getToNode();
				if (edge.getToNode() == v) {
					w = edge.getFromNode();
				}
				// for (FlowEdge e : v.getOutEdges()) {
				// Node w = e.getToNode();
				// Checks if push was performed and if w is active aftet the
				// push
				//
				if (edge.getResidualCapacity() != oldResCapacity
						&& w.getExcess() > 0) {
					fifoQueue.add(w);
				}

				// }
				if (v.getExcess() == 0 || v.getDist() > dist || v == target) {
					break;
				}
			}
		}
		if (v.getExcess() > 0 && v != target) {
			fifoQueue.add(v);
		}
	}

	private int binaryLength(FlowEdge edge, double delta,
			FlowNetwork flowNetwork) throws IllegalArgumentException {

		if (edge.getResidualCapacity() >= (3 * delta)
				|| isSpecialEdge(edge, delta)) {
			return 0;
		} else {
			return 1;
		}
	}

	private boolean isSpecialEdge(FlowEdge flowEdge, double delta) {

		if (2 * delta <= flowEdge.getResidualCapacity()
				&& flowEdge.getResidualCapacity() > 3 * delta) {

			FlowEdge backEdge = null;

			Node v = flowEdge.getFromNode();
			Node w = flowEdge.getToNode();

			for (FlowEdge edge : w.getOutEdges()) {
				if (edge.getToNode() == v) {
					backEdge = edge;
					break;
				}
			}

			if ((backEdge.getResidualCapacity() >= (3 * delta))
					&& (v.getDist() == w.getDist())) {
				return true;
			}

		}
		return false;
	}

}
