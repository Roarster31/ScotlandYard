package solution.views.map;

import scotlandyard.MoveTicket;

import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Created by rory on 14/03/15.
 */
public class MapPath {

    private int pathLength;
    private Path2D path;
    private int nodeId1;
    private int nodeId2;
    private boolean pathAvailable;
    private boolean hovered;

    public MapPath (Path2D path2D, int pathLength, int nodeId1, int nodeId2){
        this.path = path2D;
        this.pathLength = pathLength;
        this.nodeId1 = nodeId1;
        this.nodeId2 = nodeId2;
    }

    public void drawBackground(Graphics2D g2d){
//        g2d.setColor(new Color(223, 223, 223, 255));
//        g2d.draw(path);
    }

    public void drawIfAvailable(Graphics2D g2d){
        if(pathAvailable) {
            g2d.setColor(new Color(0, 0, 0, 255));
            g2d.draw(path);
        }
    }

    public void drawIfHighlighted(Graphics2D g2d) {
        if(pathAvailable && hovered) {
            g2d.setColor(Color.MAGENTA);
            g2d.draw(path);
        }
    }

    public boolean setAvailable(int startNode, MoveTicket move){
        if(startNode == nodeId1 || startNode == nodeId2 ){
            System.out.println();
        }
        boolean available = (nodeId1 == move.target && startNode == nodeId2) || (nodeId1 == startNode && move.target == nodeId2);

        if(available){
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

    public int getPathLength() {
        return pathLength;
    }

    public boolean isAvailable() {
        return pathAvailable;
    }

    public boolean hasNode(int nodeId) {
        return nodeId == nodeId1 || nodeId == nodeId2;
    }

    public Path2D getPath2D() {
        return path;
    }

    public int getStartingNode(){
        return nodeId1;
    }
}
