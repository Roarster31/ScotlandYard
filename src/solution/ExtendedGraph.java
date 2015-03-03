package solution;

import scotlandyard.Edge;
import scotlandyard.Graph;
import scotlandyard.Node;
import scotlandyard.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rory on 03/03/15.
 */
public class ExtendedGraph extends Graph<Integer, Route> {

	public ExtendedGraph (Graph<Integer, Route> graph){
		this.nodes.addAll(graph.getNodes());
		this.edges.addAll(graph.getEdges());
	}

	public List<Node> getNeighbours(Node targetNode){
		List<Node> neighbours = new ArrayList<Node>();
		if(nodes.contains(targetNode)){

			for(Edge edge: edges){
				if(edge.source() == targetNode.data()){
					neighbours.add(new Node(edge.source()));
				}else if(edge.target() == targetNode.data()){
					neighbours.add(new Node(edge.target()));
				}
			}
		}
		return neighbours;
	}

	public List<Edge<Integer, Route>> getConnectedEdges(Node targetNode){
		List<Edge<Integer, Route>> edges = new ArrayList<Edge<Integer, Route>>();

			for(Edge<Integer, Route> edge: this.edges){
				if(edge.source() == targetNode.data()){
					edges.add(edge);
				}else if(edge.target() == targetNode.data()){
					edges.add(edge);
				}
			}
		return edges;
	}
}
