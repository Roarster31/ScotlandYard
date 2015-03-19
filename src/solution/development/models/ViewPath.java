package solution.development.models;

import scotlandyard.Ticket;
import solution.views.map.Stacked2DPath;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * This is the view representation of a path between two DataPositions
 * both ids represent associated DataPosition ids, unordered
 * path is the collection of coordinates that form the path
 * types is a list of the types of route that pass through this path
 */
public class ViewPath {
    public int id1;
    public int id2;
    private Path2D path2D;
    public Stacked2DPath path;
    public ArrayList<Ticket> types;

    public ViewPath(DataPath dataPath) {
        this.id1 = dataPath.id1;
        this.id2 = dataPath.id2;
        path2D = new Path2D.Double(Path2D.WIND_EVEN_ODD, dataPath.pathXCoords.length);

        for (int i = 0; i < dataPath.pathXCoords.length; i++) {
            if(i == 0){
                path2D.moveTo(dataPath.pathXCoords[i],dataPath.pathYCoords[i]);
            }else{
                path2D.lineTo(dataPath.pathXCoords[i], dataPath.pathYCoords[i]);
            }
        }


        this.types = new ArrayList<Ticket>();

    }

    public void setTypes(ArrayList<Ticket> ticketTypes) {
        types = new ArrayList<Ticket>(new LinkedHashSet<Ticket>(ticketTypes));
        this.path = new Stacked2DPath(path2D,types);
    }
}
