package solution;

import scotlandyard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphHelper {

    public static List<Edge<Integer, Route>> getConnectedEdges(Graph<Integer, Route> graph, Node node){
        List<Edge<Integer, Route>> edges = new ArrayList<Edge<Integer, Route>>();

        for(Edge<Integer, Route> edge: graph.getEdges()){
            if(edge.source().equals(node.data())){
                edges.add(edge);
            }else if(edge.target().equals(node.data())){
                edges.add(edge);
            }
        }
        return edges;
    }

    public static List<Edge<Integer, Route>> filterAvailableRoutes(List<Edge<Integer, Route>> edges, Map<Ticket, Integer> tickets){

        List<Edge<Integer, Route>> output = new ArrayList<Edge<Integer, Route>>();

        for(Edge<Integer, Route> edge : edges){
            Ticket requiredTicket = Ticket.fromRoute(edge.data());
            if(tickets.containsKey(requiredTicket) && tickets.get(requiredTicket) > 0){
                output.add(edge);
            }
        }

        return output;
    }
}
