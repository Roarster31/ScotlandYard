package solution.views.map;

import scotlandyard.MoveTicket;
import scotlandyard.Ticket;
import solution.development.models.ViewPath;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rory on 14/03/15.
 */
public class MapPath {

    private final Stacked2DPath path;
    private final int nodeId1;
    private final int nodeId2;
    private final Set<Ticket> tickets;
    private boolean available;
    private boolean hovered;

    public MapPath(ViewPath viewPath) {
        this.path = viewPath.path;
        this.nodeId1 = viewPath.id1;
        this.nodeId2 = viewPath.id2;
        this.tickets = new HashSet<Ticket>(viewPath.types);
    }


    public void draw(Graphics2D g2d) {
        path.draw(g2d);
    }


    public boolean setAvailable(int startNode, MoveTicket move) {

        boolean available = (nodeId1 == move.target && startNode == nodeId2) || (nodeId1 == startNode && move.target == nodeId2);

        if (available) {
            this.available = available;
        }
        return available;
    }

    public void resetAvailability() {
        available = false;
    }

    public void notifyPositionHovered(MapPosition position) {
        hovered = position != null && hasNode(position.getId());
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean hasNode(int nodeId) {
        return nodeId == nodeId1 || nodeId == nodeId2;
    }

    public Path2D getPath() {
        return path.getPath();
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


}
