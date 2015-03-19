package solution.development.models;

import scotlandyard.Ticket;
import solution.development.ShortestPathHelper;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * This is the view representation of a route along 1 or more DataPaths
 * both ids represent associated DataPosition ids, unordered
 * path describes the entire collection of coordinates from id1 to id2 (or vice versa)
 **/
public class ViewRoute {
    public int id1;
    public int id2;
    public Path2D path;
    public Ticket type;
    public ArrayList<DataPosition> positionList;

    public ViewRoute(DataRoute dataRoute, ArrayList<DataPosition> dataPositions, ArrayList<DataPath> dataPaths) {

        this.type = dataRoute.type;

        this.id1 = dataRoute.waypointIdList.get(0);
        this.id2 = dataRoute.waypointIdList.get(dataRoute.waypointIdList.size()-1);

        LinkedHashSet<DataPosition> pathSet = new LinkedHashSet<DataPosition>();

        for (int i = 0; i < dataRoute.waypointIdList.size()-1; i++) {
            ArrayList<DataPosition> list = ShortestPathHelper.shortestPath(dataRoute.waypointIdList.get(i), dataRoute.waypointIdList.get(i + 1), dataPositions, dataPaths);
            for (int j = list.size()-1; j >= 0; j--) {
                DataPosition position = list.get(j);
                pathSet.add(position);
            }
        }

        positionList = new ArrayList<DataPosition>(pathSet);

        this.path = new Path2D.Double(Path2D.WIND_EVEN_ODD, positionList.size());

        for (int i = 0; i < positionList.size(); i++) {
            if(i == 0){
                path.moveTo(positionList.get(i).x, positionList.get(i).y);
            }else{
                path.lineTo(positionList.get(i).x, positionList.get(i).y);
            }
        }
    }
}
