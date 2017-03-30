package edu.wit.cs.comp2350;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// represents a graph as a list of Nodes
public class Graph {
	private ArrayList<Node> nodes;
	
	public Graph() {
		nodes = new ArrayList<Node>();
	}
	
	public void AddNode(Node n) {
		nodes.add(n);
	}
	
	public void AddEdge(int src, int dst) {
		nodes.get(src).AddEdge(dst);
	}
	
	public int size() {
		return nodes.size();
	}
	
	public Node[] GetNodes() {
		return nodes.toArray(new Node[nodes.size()]);
	}

	public Node[] getNodesFrom(Node n){
		return Arrays.stream(this.GetNodes()).filter(nn -> IntStream.of(nn.GetEdges()).boxed().collect(Collectors.toList()).contains(n.GetID())).collect(Collectors.toCollection(ArrayList::new)).toArray(new Node[]{});
	}

	public Graph getReverseGraph(){
		Graph reverse = new Graph();
		for (Node n: this.GetNodes())
			reverse.AddNode(n);
		for (Node n: this.GetNodes())
			for (int ep : n.GetEdges())
				reverse.AddEdge(ep, n.GetID());
		return reverse;
	}
	
	public Node[] GetEdges(Node n) {
		int[] edgeInds = n.GetEdges();
		Node[] ret = new Node[edgeInds.length];
		
		for (int i = 0; i < ret.length; i++)
			ret[i] = nodes.get(edgeInds[i]);
		return ret;
	}
}
