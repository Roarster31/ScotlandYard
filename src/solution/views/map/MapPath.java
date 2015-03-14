package solution.views.map;

import scotlandyard.MoveTicket;

import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Created by rory on 14/03/15.
 */
public class MapPath {

    private Path2D path;
    private int nodeId1;
    private int nodeId2;
    private boolean pathAvailable;
    private boolean hovered;

    public MapPath (Path2D path2D, int nodeId1, int nodeId2){
        this.path = path2D;
        this.nodeId1 = nodeId1;
        this.nodeId2 = nodeId2;
    }

    public void drawBackground(Graphics2D g2d){
        g2d.setColor(new Color(255, 255, 255, 255));
        g2d.draw(path);
    }

    public void drawIfAvailable(Graphics2D g2d){
        if(pathAvailable) {
            if(hovered) {
                g2d.setColor(Color.MAGENTA);
            }else{
                g2d.setColor(new Color(255, 0, 20, 255));
            }
            g2d.draw(path);
        }
    }

    public void notifyAvailableNode(int currentPosition, MoveTicket move){
        if(currentPosition == nodeId1 || currentPosition == nodeId2 ){
            System.out.println();
        }
        boolean available = (nodeId1 == move.target && currentPosition == nodeId2) || (nodeId1 == currentPosition && move.target == nodeId2);

        if(available){
            pathAvailable = available;
        }
    }

    public void resetAvailability() {

        pathAvailable = false;
    }

    public void notifyPositionHovered(MapPosition position) {
        hovered = position != null && (position.getId() == nodeId1 || position.getId() == nodeId2);
    }
}
