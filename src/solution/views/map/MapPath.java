package solution.views.map;

import scotlandyard.MoveTicket;
import scotlandyard.Route;
import scotlandyard.Ticket;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;

/**
 * Created by rory on 14/03/15.
 */
public class MapPath {

    private final int length;
    private Stacked2DPath path;
    private int nodeId1;
    private int nodeId2;
    private boolean pathAvailable;
    private boolean hovered;
    private ArrayList<Ticket> tickets;

    public MapPath(Path2D path2D, int length, int nodeId1, int nodeId2) {
        this.path = new Stacked2DPath(path2D,1);
        this.nodeId1 = nodeId1;
        this.nodeId2 = nodeId2;
        this.length = length;

        tickets = new ArrayList<Ticket>();

    }


    public void draw(Graphics2D g2d) {

//        if(tickets.contains(Ticket.Underground)){
//            g2d.setStroke(new BasicStroke(8f));
//            g2d.setColor(Color.RED);
//            g2d.draw(path);
//        }
//
//        if(tickets.contains(Ticket.Bus)){
//            g2d.setStroke(new BasicStroke(6f));
//            g2d.setColor(Color.GREEN);
//            g2d.draw(path);
//        }
//
//        if(tickets.contains(Ticket.Taxi)){
//            g2d.setStroke(new BasicStroke(4f));
//            g2d.setColor(Color.ORANGE);
//            g2d.draw(path);
//        }


    }

    public void draw(Graphics2D g2d, Ticket ticket) {
//        if(tickets.contains(ticket)){
//            if(ticket.equals(Ticket.Taxi)) {
//                g2d.setStroke(new BasicStroke(3f));
//                g2d.setColor(Color.ORANGE);
//            } else if(ticket.equals(Ticket.Bus)) {
//                g2d.setStroke(new BasicStroke(6f));
//                g2d.setColor(Color.GREEN);
//            } else if (ticket.equals(Ticket.Underground)) {
//                g2d.setStroke(new BasicStroke(8f));
//                g2d.setColor(Color.RED);
//            }
//        }
        path.draw(g2d);
    }

    public boolean setAvailable(int startNode, MoveTicket move) {
        if (startNode == nodeId1 || startNode == nodeId2) {
            System.out.println();
        }
        boolean available = (nodeId1 == move.target && startNode == nodeId2) || (nodeId1 == startNode && move.target == nodeId2);

        if (available) {
            pathAvailable = available;
        }
        return available;
    }

    public void resetAvailability() {

        pathAvailable = false;
    }

    public void notifyPositionHovered(MapPosition position) {
        hovered = position != null && hasNode(position.getId());
    }

    public boolean isAvailable() {
        return pathAvailable;
    }

    public boolean hasNode(int nodeId) {
        return nodeId == nodeId1 || nodeId == nodeId2;
    }

    public Path2D getPath2D() {
        return path.getmPath();
    }

    public int getStartingNode() {
        return nodeId1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapPath mapPath = (MapPath) o;

        if (nodeId1 != mapPath.nodeId1) return false;
        if (nodeId2 != mapPath.nodeId2) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = nodeId1;
        result = 31 * result + nodeId2;
        return result;
    }

    public boolean isHighlighted() {
        return isAvailable() && hovered;
    }

    public void addRoute(Route route) {
        tickets.add(Ticket.fromRoute(route));
    }

    public int getLength() {
        return length;
    }
}
