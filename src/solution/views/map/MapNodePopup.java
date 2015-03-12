package solution.views.map;

import scotlandyard.Ticket;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rory on 10/03/15.
 */
public class MapNodePopup {

    private static final int BUTTON_SIZE = 50;
    private static final int TRIANGLE_SIZE = 10;
    private static final int BUTTON_PADDING = 10;
    private static final int BUTTON_CORNER_RADIUS = 15;
    private final MapPosition mapPosition;
    private final PopupInterface mInterface;
    ArrayList<Ticket> fullTicketList = new ArrayList<Ticket>() {{
        add(Ticket.Bus);
        add(Ticket.DoubleMove);
        add(Ticket.SecretMove);
        add(Ticket.Taxi);
        add(Ticket.Underground);
    }};
    Set<Ticket> ticketList = new HashSet<Ticket>();
    private Rectangle2D.Double mainRect;
    private ArrayList<Rectangle2D> mTicketRectList;
    private Polygon mTrianglePolygon;
    private Rectangle2D mHoveredTicketRect;
    public MapNodePopup(MapPosition mapPosition, final Dimension canvasSize, boolean doubleMove, PopupInterface popupInterface) {
        this.mapPosition = mapPosition;
        ticketList = mapPosition.getTickets();
        if (doubleMove) {
            ticketList.add(Ticket.DoubleMove);
        }
        mInterface = popupInterface;
        create(mapPosition.getX(), mapPosition.getY(), canvasSize);
    }

    private void create(final int x, final int y, final Dimension canvasSize) {


        Rectangle2D rect = getStandardRect();

        final int width = BUTTON_PADDING + fullTicketList.size() * (BUTTON_SIZE + BUTTON_PADDING);
        final int height = BUTTON_SIZE + 2 * BUTTON_PADDING;

        int xPosition;
        int yPosition;
        int triangleX1;
        int triangleX2;
        int triangleY1;
        int triangleY2;

        if (x < rect.getWidth() / 2) {
            //to right
            xPosition = x + TRIANGLE_SIZE;
            yPosition = y - height / 2;
            triangleX1 = x + TRIANGLE_SIZE;
            triangleX2 = x + TRIANGLE_SIZE;
            triangleY1 = y + TRIANGLE_SIZE;
            triangleY2 = y - TRIANGLE_SIZE;
        } else if (canvasSize.width - x < rect.getWidth() / 2) {
            //to left
            xPosition = x - width - TRIANGLE_SIZE;
            yPosition = y - height / 2;
            triangleX1 = x - TRIANGLE_SIZE;
            triangleX2 = x - TRIANGLE_SIZE;
            triangleY1 = y + TRIANGLE_SIZE;
            triangleY2 = y - TRIANGLE_SIZE;
        } else if (y > rect.getHeight()) {
            //above
            xPosition = x - width / 2;
            yPosition = y - height - TRIANGLE_SIZE;
            triangleX1 = x + TRIANGLE_SIZE;
            triangleX2 = x - TRIANGLE_SIZE;
            triangleY1 = y - TRIANGLE_SIZE;
            triangleY2 = y - TRIANGLE_SIZE;
        } else {
            //below
            xPosition = x - width / 2;
            yPosition = y + TRIANGLE_SIZE;
            triangleX1 = x + TRIANGLE_SIZE;
            triangleX2 = x - TRIANGLE_SIZE;
            triangleY1 = y + TRIANGLE_SIZE;
            triangleY2 = y + TRIANGLE_SIZE;
        }

        mainRect = new Rectangle2D.Double(xPosition, yPosition, width, height);

        mTicketRectList = new ArrayList<Rectangle2D>();

        for (int i = 0; i < fullTicketList.size(); i++) {
            mTicketRectList.add(new Rectangle2D.Double(xPosition + BUTTON_PADDING + i * (BUTTON_SIZE + BUTTON_PADDING), yPosition + BUTTON_PADDING, BUTTON_SIZE, BUTTON_SIZE));
        }

        mTrianglePolygon = new Polygon(new int[]{x, triangleX1, triangleX2}, new int[]{y, triangleY1, triangleY2}, 3);

    }

    public void draw(final Graphics2D g2d) {
        Color initialColour = g2d.getColor();

        g2d.setColor(Color.BLACK);
        FontMetrics fm = g2d.getFontMetrics();


        g2d.fillRoundRect((int) mainRect.getX(), (int) mainRect.getY(), (int) mainRect.getWidth(), (int) mainRect.getHeight(), BUTTON_CORNER_RADIUS, BUTTON_CORNER_RADIUS);

        g2d.setStroke(new BasicStroke(2f));

        for (int i = 0; i < fullTicketList.size(); i++) {
            if (!ticketList.contains(fullTicketList.get(i))) {
                g2d.setColor(new Color(19, 133, 33, 120));
            } else {
                g2d.setColor(new Color(38, 255, 0, 120));
            }
            g2d.fillRoundRect((int) mTicketRectList.get(i).getX(), (int) mTicketRectList.get(i).getY(), (int) mTicketRectList.get(i).getWidth(), (int) mTicketRectList.get(i).getHeight(), BUTTON_CORNER_RADIUS, BUTTON_CORNER_RADIUS);

            if (mHoveredTicketRect == mTicketRectList.get(i)) {
                g2d.setColor(new Color(0, 232, 58, 205));
                g2d.drawRoundRect((int) mTicketRectList.get(i).getX(), (int) mTicketRectList.get(i).getY(), (int) mTicketRectList.get(i).getWidth(), (int) mTicketRectList.get(i).getHeight(), BUTTON_CORNER_RADIUS, BUTTON_CORNER_RADIUS);
            }

            String ticketName = String.valueOf(fullTicketList.get(i).name().toUpperCase().charAt(0));


            Rectangle2D r = fm.getStringBounds(ticketName, g2d);
            int textX = (int) (mainRect.getX() + BUTTON_PADDING + BUTTON_SIZE / 2 + i * (BUTTON_SIZE + BUTTON_PADDING) - ((int) r.getWidth() / 2));
            int textY = (int) (mainRect.getY() + mainRect.getHeight() / 2 - ((int) r.getHeight() / 2) + fm.getAscent());
            g2d.setColor(Color.BLACK);
            g2d.drawString(ticketName, textX, textY);
        }

        g2d.setColor(Color.BLACK);


        g2d.fillPolygon(mTrianglePolygon);


        g2d.setColor(initialColour);
    }

    private Rectangle2D getStandardRect() {

        final int width = BUTTON_PADDING + fullTicketList.size() * (BUTTON_SIZE + BUTTON_PADDING);
        final int height = BUTTON_SIZE + 2 * BUTTON_PADDING;

        return new Rectangle2D.Double(0, 0, width, height);
    }

    public boolean onClick(final int x, final int y) {
        if (mainRect.contains(x, y)) {
            for (int i = 0; i < mTicketRectList.size(); i++) {
                Rectangle2D rect = mTicketRectList.get(i);
                if (rect.contains(x, y)) {
                    Ticket ticket = fullTicketList.get(i);
                    if (mapPosition.getTickets().contains(ticket)) {
                        mInterface.onTicketSelected(ticket, mapPosition.getId());
                        return true;
                    }
                    break;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean onMouseMoved(int x, int y) {
        if (mainRect.contains(x, y)) {
            for (Rectangle2D rect : mTicketRectList) {
                if (rect.contains(x, y)) {
                    mHoveredTicketRect = rect;
                    break;
                }
            }
            return true;
        } else {
            return false;
        }
    }


    public interface PopupInterface {
        public void onTicketSelected(final Ticket ticket, final int nodeId);
    }

}
