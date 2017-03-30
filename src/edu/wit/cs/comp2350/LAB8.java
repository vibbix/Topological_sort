package edu.wit.cs.comp2350;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * 
 * @author kreimendahl
 */

// Provides a solution to the topological sorting problem
public class LAB8 {

	//😆😡<- emoji comments now supported in IntelliJ!
	/**
	 * Topographically sorts a
	 * @param g graph to search through
	 * @return the sorted array of nodes
	 */
	public static Node[] FindTopo(Graph g) {
		ArrayList<Node> nodes = new ArrayList<>();
		HashSet<Node> alreadyVisited = new HashSet<>();
		HashSet<Node> hasCompletedSearch = new HashSet<>();
		for (Node n: g.GetNodes())
			DFS(n, g, nodes, alreadyVisited, hasCompletedSearch);
		return nodes.toArray(new Node[]{});
	}

	private static void DFS(Node n, Graph g, List<Node> nodeorder, HashSet<Node> av, HashSet<Node> hcs){
		if (av.contains(n)){
			if(hcs.contains(n)) return;
			throw new IllegalArgumentException("Graph is cyclical. Topological sort cannot be completed");
		}
		av.add(n);
		for (Node nn : getNodesFrom(g, n)) DFS(nn, g, nodeorder, av, hcs);
		nodeorder.add(n);
		hcs.add(n);
	}

	/************************************************************
	 * A naive implementation of topological sort. This implementation
	 * uses the 'marker' field of a node to maintain a count of the number
	 * of unresolved dependencies. Then there is a double-for loop over
	 * all of the nodes, each time adding a node with 0 unresolved
	 * dependencies to the output list.
	 ************************************************************/
	public static Node[] FindNaive(Graph g) {
		
		int numNodes = g.size();
		int numFinished = 0;
		Node[] ret= new Node[numNodes];
		
		MarkDeps(g);
		while (numFinished < numNodes) {
			for (Node n : g.GetNodes()) {
				if (n.marker == 0) {
					UnmarkDeps(n, g);
					n.marker = -1;
					ret[numFinished] = n;
					numFinished++;
				}
			}
		}
		
		return ret;
	}
	
	// use each node's marker to count how many nodes depend on it
	private static void MarkDeps(Graph g) {
		
		for (Node n: g.GetNodes()) {
			for (Node next: g.GetEdges(n))
				next.marker++;
		}
	}
	
	// reduce dependency count for all dependencies of a specific node
	private static void UnmarkDeps(Node n, Graph g) {
		
		for (Node next: g.GetEdges(n))
			next.marker--;
	}
	
	
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		String file1;
		System.out.printf("Enter <dependencies file> <algorithm>, ([n]aive, [t]opological sort).\n");
		System.out.printf("(e.g: deps/small n)\n");
		file1 = s.next();

		// read in dependencies
		Graph g = InputGraph(file1);

		String algo = s.next();
		Node[] result = {};

		switch (algo.charAt(0)) {
		case 'n':
			result = FindNaive(g);
			break;
		case 't':
			result = FindTopo(g);
			break;
		default:
			System.out.println("Invalid algorithm");
			System.exit(0);
			break;
		}

		s.close();

		System.out.printf("Order of files: ");
		for (int i = 0; i < result.length; i++)
			System.out.println(result[i].toString());
	}


	// reads in the graph from a specific file formatted with lines like:
	// FILENAME 1 2 10
	// This means that the file FILENAME depends on files on lines 1, 2,
	// and 10 of the file to be completed first.
	// This reads through the input file twice, first to get names for the
	// nodes and next to set up edges in the correct direction
	private static Graph InputGraph(String file1) {
		
		Graph g = new Graph();
		try (Scanner f = new Scanner(new File(file1))) {
			int i = 0;
			while(f.hasNextLine()) { // each file listing
				String line = f.nextLine();
				Scanner lineScan = new Scanner(line);
				Node n = new Node(i, lineScan.next());
				lineScan.close();
				g.AddNode(n);
				i++;
			}
		} catch (IOException e) {
			System.err.println("Cannot open file " + file1 + ". Exiting.");
			System.exit(0);
		}
		
		try (Scanner f = new Scanner(new File(file1))) {
			int i = 0;
			while(f.hasNextLine()) { // each file listing
				String line = f.nextLine();
				Scanner lineScan = new Scanner(line);
				lineScan.next();	// skip over file name
				while (lineScan.hasNextInt())	// for each dependency
					g.GetNodes()[lineScan.nextInt()].AddEdge(i);
				lineScan.close();
				i++;
			}
		} catch (IOException e) {
			System.err.println("Cannot open file " + file1 + ". Exiting.");
			System.exit(0);
		}
		
		return g;
	}

	private static Node[] getNodesFrom(Graph g, Node n){
		return Arrays.stream(g.GetNodes()).filter(nn -> IntStream.of(nn.GetEdges()).boxed().collect(Collectors.toList()).contains(n.GetID())).collect(Collectors.toCollection(ArrayList::new)).toArray(new Node[]{});
	}

}
