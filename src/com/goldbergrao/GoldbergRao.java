package com.goldbergrao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.goldbergTarjan.GoldbergTarjan;

public class GoldbergRao {

	public static void main(String args[]) {

		URL url = GoldbergRao.class.getResource("inputFile.txt");
		File inputFile = new File(url.getPath());
		BufferedReader br = null;
		List<FlowNetwork> flowNetworks = new ArrayList<FlowNetwork>();
		try {
			br = new BufferedReader(new FileReader(inputFile));
			// Skipping comment
			br.readLine();
			int noOfTopologies = Integer.parseInt(br.readLine());

			for (int i = 0; i < noOfTopologies; i++) {
				FlowNetwork flowNetwork = new FlowNetwork();

				// Read and construct graphs
				String graph[] = br.readLine().trim().split(" ");
				int noOfEdges = Integer.parseInt(graph[0]);
				Node sourceNode = new Node(Integer.parseInt(graph[1]));
				Node sinkNode = new Node(Integer.parseInt(graph[2]));
				flowNetwork.setSourceNode(sourceNode);
				flowNetwork.setSinkNode(sinkNode);

				for (int j = 0; j < noOfEdges; j++) {
					String edge[] = br.readLine().trim().split(" ");
					if (edge.length != 3) {
						System.out
						.println("Skipping Edge. Reason: Edge should have format 'FromNode ToNode Capacity");
						continue;
					}

					Node fromNode = flowNetwork.getNode(Integer
							.parseInt(edge[0]));
					if (fromNode == null) {
						fromNode = new Node(Integer.parseInt(edge[0]));
					}

					Node toNode = flowNetwork
							.getNode(Integer.parseInt(edge[1]));
					if (toNode == null) {
						toNode = new Node(Integer.parseInt(edge[1]));
					}
					int capacity = Integer.parseInt(edge[2]);

					FlowEdge flowEdge = new FlowEdge(fromNode, toNode, capacity);
					try {
						flowNetwork.addEdge(flowEdge);
					} catch (Exception e) {
						System.out.println("Cannot Add Edge " + e.toString()
								+ " to the Graph. Reason: " + e.getMessage());
					}

				}
				flowNetworks.toString();
				flowNetworks.add(flowNetwork);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			if (br != null) {
				br.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (FlowNetwork flowNetwork : flowNetworks) {
			long startTime = System.currentTimeMillis();
			System.out.println("Calculating Max Flow for Graph(" + flowNetwork.getV() +", "+ flowNetwork.E() +") from " + flowNetwork.getSourceNode().getValue() +" --> " + flowNetwork.getSinkNode().getValue() );
			int maxFlow = goldbergRao(flowNetwork);
			System.out.println("MaxFlow: " + maxFlow);
			long totalTime = System.currentTimeMillis() - startTime;
			System.out.println("Total time: " + totalTime + "ms.");
		}
	}

	private static int goldbergRao(FlowNetwork flowNetwork) {

		int m = flowNetwork.E();
		int n = flowNetwork.getV();
		double m23 = Math.pow(m, 0.66);
		double n12 = Math.pow(n, 0.5);
		double carat = Math.min(m23, n12);
		double F = n * flowNetwork.getU();

		while (F > 1) {
			double delta = Math.ceil(F / carat);
			updateDistanceLabels(delta, flowNetwork);
			ArrayList<ArrayList<Node>> stronglyConnectedComponents = findStronglyConnectedComponents(flowNetwork);
			HashMap<Integer, ArrayList<Node>> superNodeToSCCMap = contractStronglyConnectedComponents(stronglyConnectedComponents);
			int flow = golbergTarjanBlockingFlow(flowNetwork, delta);
			adjustingFlow(flow, delta, flowNetwork);
			routingFlow(superNodeToSCCMap, flowNetwork, delta);
			F = updateF(F, flowNetwork);
		}

		return flowNetwork.getSinkNode().getExcess();
	}

	private static void adjustingFlow(int flow, double delta,
			FlowNetwork flowNetwork) {
		if(flow > delta) {
			int U = flow - (int)delta;
			ArrayList<Node> reverseTopologicalOrder = topologicalSort(flowNetwork);
			Collections.reverse(reverseTopologicalOrder);
		}
	}

	private static double updateF(double F, FlowNetwork flowNetwork) {

		Node source = flowNetwork.getSourceNode();

		int[] canonicalCut = new int[source.getDist() + 1];
		Arrays.fill(canonicalCut, Integer.MAX_VALUE);

		for (FlowEdge edge : flowNetwork.edges()) {
			Node v = edge.getFromNode();
			Node w = edge.getToNode();
			if (v.getDist() > w.getDist()) {
				canonicalCut[v.getDist() - 1] = edge.getResidualCapacity();
			}

		}

		int min = Integer.MAX_VALUE;
		for (int i : canonicalCut) {
			if (i < min) {
				min = i;
			}
		}
		if (min < (F / 2)) {
			F = min;
		}

		return F;

	}

	private static void routingFlow(
			HashMap<Integer, ArrayList<Node>> superNodeToSCCMap,
			FlowNetwork flowNetwork, double delta) {
		for (Node node : flowNetwork.getNodes()) {

			int supply = 0;
			// Calculating Supply == Sum of all incoming flows
			for (FlowEdge inEdge : node.getInEdges()) {
				supply += inEdge.getFlow();
			}
			node.setSupply(supply);

			int demand = 0;
			// Calculating Demand == Sum of all outgoing flows
			for (FlowEdge outEdge : node.getOutEdges()) {
				demand += outEdge.getFlow();
			}
			node.setDemand(demand);
		}

		for (ArrayList<Node> scc : superNodeToSCCMap.values()) {
			Node v = scc.get(0);
			buildInTree(v, scc, delta, flowNetwork);
			buildOutTree(v, scc, delta, flowNetwork);
			route(v, delta);
		}
	}

	private static void route(Node v, double delta) {
		int descendantDemand = calculateDescendantDemandsRecursively(v);
		v.setDescendantDemand(descendantDemand);
		moveSupplyForwardRecursively(v, delta);
		moveDemandBackwardRecursively(v, delta);
	}

	private static void moveDemandBackwardRecursively(Node v, double delta) {
		for (Node w : v.getOutTree()) {
			moveDemandBackwardRecursively(w, delta);
		}
		int demandToMove = v.getDemand() - v.getSupply();

		Node w = v.getOutTreeParent();
		if (w != null) {
			for (FlowEdge outEdge : w.getOutEdges()) {
				if (outEdge.getToNode() == v) {
					int newFlow = outEdge.getFlow() + demandToMove;
					outEdge.updateFlow(newFlow);
				}
			}
			int newDemand = w.getDemand() + demandToMove;
			w.setDemand(newDemand);
		}
	}

	private static void moveSupplyForwardRecursively(Node v, double delta) {
		for (Node w : v.getInTree()) {
			moveSupplyForwardRecursively(w, delta);
		}
		int supplyToMove = (int) Math.min(v.getSupply(),
				delta - v.getDescendantDemand());
		int newSupply = v.getSupply() - supplyToMove;
		v.setSupply(newSupply);
		Node w = v.getInTreeParent();

		if (w != null) {
			for (FlowEdge inEdge : w.getInEdges()) {
				if (inEdge.getFromNode() == v) {
					int newFlow = inEdge.getFlow() + supplyToMove;
					inEdge.updateFlow(newFlow);
				}
			}
			int wNewSupply = w.getSupply() + supplyToMove;
			w.setSupply(wNewSupply);

		}
	}

	private static int calculateDescendantDemandsRecursively(Node v) {
		int descendantDemand = v.getDemand();
		for (Node w : v.getOutTree()) {
			descendantDemand = descendantDemand
					+ calculateDescendantDemandsRecursively(w);
		}
		return descendantDemand;
	}

	private static void buildInTree(Node v, ArrayList<Node> scc, double delta,
			FlowNetwork flowNetwork) {

		LinkedList<Node> queue = new LinkedList<Node>();
		queue.add(v);
		while (!queue.isEmpty()) {
			Node w = queue.pop();
			for (FlowEdge inEdge : w.getInEdges()) {
				Node u = inEdge.getFromNode();
				if (scc.contains(u)
						&& binaryLength(inEdge, delta, flowNetwork) == 0) {
					if (u.getInTreeParent() == null) {
						u.setInTreeParent(w);
						w.addToInTree(u);
						queue.add(u);
					}
				}
			}
		}
	}

	private static void buildOutTree(Node v, ArrayList<Node> scc, double delta,
			FlowNetwork flowNetwork) {

		LinkedList<Node> queue = new LinkedList<Node>();
		queue.add(v);
		while (!queue.isEmpty()) {
			Node w = queue.pop();
			for (FlowEdge outEdge : w.getOutEdges()) {
				Node u = outEdge.getFromNode();
				if (scc.contains(u)
						&& binaryLength(outEdge, delta, flowNetwork) == 0) {
					if (u.getOutTreeParent() == null) {
						u.setOutTreeParent(w);
						w.addToOutTree(u);
						queue.add(u);
					}
				}
			}

		}
	}

	private static HashMap<Integer, ArrayList<Node>> contractStronglyConnectedComponents(
			ArrayList<ArrayList<Node>> stronglyConnectedComponents) {
		int i = 0;
		HashMap<Integer, ArrayList<Node>> superNodeToSCCMap = new HashMap<Integer, ArrayList<Node>>();
		for (ArrayList<Node> scc : stronglyConnectedComponents) {
			i++;
			superNodeToSCCMap.put(i, scc);
			for (Node node : scc) {
				node.setSuperNode(i);
			}
		}

		return superNodeToSCCMap;
	}

	private static ArrayList<ArrayList<Node>> findStronglyConnectedComponents(
			FlowNetwork flowNetwork) {

		int i = 0;
		Stack<Node> stack = new Stack<Node>();

		ArrayList<ArrayList<Node>> stronglyConnectedComponents = new ArrayList<ArrayList<Node>>();

		ArrayList<Node> topologicallySortedNodes = topologicalSort(flowNetwork);
		for (Node node : topologicallySortedNodes) {
			// Not visited
			if (node.getIndex() < 0) {
				scc(node, i, stack, stronglyConnectedComponents);
			}
		}

		return stronglyConnectedComponents;

	}

	private static void scc(Node v, int i, Stack<Node> stack,
			ArrayList<ArrayList<Node>> stronglyConnectedComponents) {

		v.setIndex(i);
		v.setLowLink(i);
		i++;
		stack.push(v);
		// Verify the friend nodes
		for (FlowEdge edge : v.getOutEdges()) {
			if (edge.getResidualCapacity() > 0) {
				Node w = edge.getToNode();
				if (stack.contains(w)) {
					v.setLowLink(Math.min(v.getLowLink(), w.getIndex()));
				}
				if (w.getIndex() < 0) {
					scc(w, i, stack, stronglyConnectedComponents);
					v.setLowLink(Math.min(v.getLowLink(), w.getLowLink()));
				}

			}
		}
		if (v.getLowLink() == v.getIndex()) {
			ArrayList<Node> subList = new ArrayList<Node>();

			Node n = stack.pop();
			subList.add(n);
			while (n.getValue() != v.getValue()) {
				n = stack.pop();
				subList.add(n);
			}
			stronglyConnectedComponents.add(subList);
		}
	}

	private static void updateDistanceLabels(double delta,
			FlowNetwork flowNetwork) {
		int i = 0;
		LinkedList<Node> currentQueue = new LinkedList<Node>();
		LinkedList<Node> nextQueue = new LinkedList<Node>();

		flowNetwork.getSinkNode().setDist(0);
		currentQueue.add(flowNetwork.getSinkNode());

		while (!currentQueue.isEmpty() || !nextQueue.isEmpty()) {
			while (currentQueue.isEmpty() == false) {
				Node v = currentQueue.pop();
				// Process nodes which have not been processed only
				v.setDist(i);
				for (FlowEdge incomingEdge : v.getInEdges()) {
					Node w = incomingEdge.getFromNode();
					if (w.getDist() == Integer.MAX_VALUE
							&& incomingEdge.getResidualCapacity() > 0) {
						int l = binaryLength(w, v, delta, flowNetwork);
						if (l == 0) {
							currentQueue.add(w);
						} else {
							nextQueue.add(w);
						}
					}
				}
			}

			if (currentQueue.isEmpty() && nextQueue.isEmpty() == false) {
				i++;
				currentQueue = nextQueue;
				nextQueue = new LinkedList<Node>();
			}
		}

		while (currentQueue.isEmpty() == false) {
			Node v = currentQueue.getFirst();
			for (FlowEdge incomingEdge : v.getInEdges()) {
				Node w = incomingEdge.getFromNode();
				if (incomingEdge.getResidualCapacity() > 0) {
					int l = binaryLength(w, v, delta, flowNetwork);
					if (l == 0) {
						currentQueue.add(w);
					} else {
						nextQueue.add(w);
					}
				}
			}
			if (currentQueue.isEmpty()) {
				i++;
				currentQueue = nextQueue;
			}
		}

	}

	private static int golbergTarjanBlockingFlow(FlowNetwork flowNetwork,
			double delta) {
		
		GoldbergTarjan goldbergTarjanBlockingFlow = new GoldbergTarjan(flowNetwork, delta);
		int blockingFlow = goldbergTarjanBlockingFlow.maxFlow(flowNetwork);
		return blockingFlow;
	}

	private static void dfs(FlowNetwork flowNetwork,
			HashMap<Node, Boolean> markedNodesMap, ArrayList<Node> sortedList,
			Node node) {
		markedNodesMap.remove(node);
		markedNodesMap.put(node, true);
		for (FlowEdge edge : node.getOutEdges())
			if (markedNodesMap.get(edge.getToNode()) == false)
				dfs(flowNetwork, markedNodesMap, sortedList, edge.getToNode());
		sortedList.add(node);
	}

	private static ArrayList<Node> topologicalSort(FlowNetwork flowNetwork) {

		HashMap<Node, Boolean> markedNodesMap = new HashMap<Node, Boolean>();
		for (Node node : flowNetwork.getNodes()) {
			markedNodesMap.put(node, false);
		}

		ArrayList<Node> sortedList = new ArrayList<Node>();
		for (Node node : flowNetwork.getNodes()) {
			if (markedNodesMap.get(node) == false) {
				dfs(flowNetwork, markedNodesMap, sortedList, node);
			}
		}
		Collections.reverse(sortedList);
		return sortedList;
	}

	private static int binaryLength(FlowEdge edge, double delta,
			FlowNetwork flowNetwork) throws IllegalArgumentException {

		if (edge.getResidualCapacity() >= (3 * delta)
				|| isSpecialEdge(edge, delta)) {
			return 0;
		} else {
			return 1;
		}
	}

	private static int binaryLength(Node fromNode, Node toNode, double delta,
			FlowNetwork flowNetwork) throws IllegalArgumentException {

		FlowEdge flowEdge = null;
		for (FlowEdge edge : fromNode.getOutEdges()) {
			if (edge.getToNode() == toNode) {
				flowEdge = edge;
				break;
			}
		}

		if (flowEdge.getResidualCapacity() >= (3 * delta) || isSpecialEdge(flowEdge, delta)) {
			return 0;
		} else {
			return 1;
		}
	}

	private static boolean isSpecialEdge(FlowEdge flowEdge, double delta) {

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
