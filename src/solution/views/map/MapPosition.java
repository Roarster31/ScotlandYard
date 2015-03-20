package solution.views.map;

import scotlandyard.Colour;
import scotlandyard.Ticket;
import solution.development.models.ViewPosition;
import solution.helpers.ColourHelper;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created by rory on 11/03/15.
 */
public class MapPosition {

    public static final int CIRC_RADIUS = 20;
    private static final Color STANDARD_COLOUR = Color.GRAY;
    private static final Color HIGHLIGHT_COLOUR = Color.CYAN;
    private static final Color AVAILABLE_COLOUR = Color.MAGENTA;

    private final Integer positionId;
    private final int x;
    private final int y;
    private final Rectangle2D.Double rect;
    private final Set<Ticket> tickets;
    private boolean hovered;
    private boolean available;
    private boolean highlighted;

    public MapPosition(ViewPosition viewPosition) {
        this.positionId = viewPosition.id;
        this.x = viewPosition.x;
        this.y = viewPosition.y;
        this.tickets = new HashSet<Ticket>(viewPosition.types);
        this.rect = new Rectangle2D.Double(x - CIRC_RADIUS/2,y - CIRC_RADIUS/2,CIRC_RADIUS,CIRC_RADIUS);

    }

    public void draw(final Graphics2D g2d, Map<Integer, Colour> playerLocations){

        int radius = hovered && isAvailable() ? (int) (CIRC_RADIUS * 1.5f) : CIRC_RADIUS;

        if(playerLocations.containsKey(positionId)) {
            g2d.setColor(ColourHelper.toColor(playerLocations.get(positionId)));
            g2d.fillOval(x-radius/2, y-radius/2, radius, radius);
        }else if(highlighted){
            g2d.setColor(HIGHLIGHT_COLOUR);
            g2d.fillOval(x - radius / 2, y - radius / 2, radius, radius);
        }else if(isAvailable()){
            g2d.setColor(AVAILABLE_COLOUR);
            g2d.fillOval(x - radius / 2, y - radius / 2, radius, radius);
        }else{
            g2d.setColor(STANDARD_COLOUR);
            g2d.fillOval(x-radius/2, y-radius/2, radius, radius);
        }

    }

    public boolean notifyMouseMove(int x, int y) {
        hovered = rect.contains(x,y);
        return hovered;
    }

    public boolean notifyMouseClick(int x, int y) {
        return rect.contains(x,y) && isAvailable();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapPosition position = (MapPosition) o;

        if (x != position.x) return false;
        if (y != position.y) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    public int getId() {
        return positionId;
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }
}
