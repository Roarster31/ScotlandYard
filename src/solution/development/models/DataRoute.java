package solution.development.models;

import scotlandyard.Route;
import scotlandyard.Ticket;

import java.util.ArrayList;

/**
 * This is the model representation of a route along 1 or more DataPaths
 * waypointIdList is the list of DataPosition ids the DataRoute passes through
 * type represents the type of route this is
 */
public class DataRoute {
    public ArrayList<Integer> waypointIdList;
    public Ticket type;

    public DataRoute(Integer source, Integer target, Route route) {
        waypointIdList = new ArrayList<Integer>();

        waypointIdList.add(source);
        waypointIdList.add(target);


        type = Ticket.fromRoute(route);
    }
}
