package solution.views;

import scotlandyard.*;
import solution.Models.CoordinateData;
import solution.Models.GraphData;
import solution.helpers.ColourHelper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class deals with drawing the underlying Graph and anything on top of
 * it.
 */
public class GraphView extends JPanel implements GraphNodePopup.PopupInterface {
    private static final int CIRC_RADIUS = 20;
    private final ArrayList<CoordinateData> mNodes;
    private final Color mDrawColour;
    private BufferedImage mGraphImage;
    private Dimension mImageSize;
    private GraphViewListener mListener;
    private int mCurrentHoverNode;
    private int mDoubleMoveNode;
    private Map<Integer, Colour> mPlayerLocations;
    private List<Move> mAvailableMoves;
    private List<Move> mDoubleAvailableMoves;
    private List<Integer> mAvailablePositions;
    private List<Integer> mDoubleAvailablePositions;
    private int mCurrentClickNode;
    private GraphNodePopup graphPopup;

    public void setPlayerPosition(Colour colour, int playerLocation) {
        for(Map.Entry<Integer, Colour> entry : mPlayerLocations.entrySet()){
            if(entry.getValue().equals(colour)){
                mPlayerLocations.remove(entry.getKey());
                break;
            }
        }
        mPlayerLocations.put(playerLocation, colour);
    }

    public interface GraphViewListener {


        public void onNodeClicked(int nodeId);
    }
    public GraphView(final String graphImageMapPath, final GraphData graphData) {
        if (graphImageMapPath == null || graphData == null) {
            System.err.println("Do not pass null variables!");
        }
        mNodes = graphData.getNodes();
        mAvailableMoves = new ArrayList<Move>();
        mAvailablePositions = new ArrayList<Integer>();
        mDoubleAvailablePositions = new ArrayList<Integer>();
        mDoubleAvailableMoves = new ArrayList<Move>();
        mPlayerLocations = new HashMap<Integer, Colour>();
        mDrawColour = Color.gray;
        addMouseListener(new GraphMouseListener());
        addMouseMotionListener(new GraphMouseListener());
        setupGraphImage(graphImageMapPath);
    }

    public void setListener(GraphViewListener mListener) {
        this.mListener = mListener;
    }

    public void setAvailableMoves(java.util.List<Move> availableMoves) {
        mAvailableMoves.clear();
        mAvailablePositions.clear();
        mAvailableMoves.addAll(availableMoves);
        for(Move move : mAvailableMoves){
            mAvailablePositions.add(getMovePosition(move));
        }
        repaint();
    }

    private int getMovePosition(final Move move){
        if(move instanceof MoveTicket){
            return ((MoveTicket)move).target;
        }else {
            return -1;
        }
    };

    private void setupGraphImage(final String graphImageMapPath) {
        try {
            final URL resource = getClass().getClassLoader().getResource(graphImageMapPath);
            mGraphImage = ImageIO.read(new File(resource.toURI()));
            mImageSize = new Dimension(mGraphImage.getWidth(), mGraphImage.getHeight());
            setSize(mImageSize);
            setMinimumSize(mImageSize);
            setPreferredSize(mImageSize);
        } catch (IOException ex) {
            //todo handle exception...
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.drawImage(mGraphImage, null, 0, 0);

        g2d.setColor(mDrawColour);


        for(CoordinateData coordinateData : mNodes){
            final boolean isHovered = coordinateData.getId() == mCurrentHoverNode;
            final boolean isAvailable = mAvailablePositions.contains(coordinateData.getId());
            int radius = isHovered && isAvailable ? (int) (CIRC_RADIUS * 1.5f) : CIRC_RADIUS;

            if(mPlayerLocations.containsKey(coordinateData.getId())){
                g2d.setColor(ColourHelper.toColor(mPlayerLocations.get(coordinateData.getId())));
                g2d.fillOval(coordinateData.getX()-radius/2, coordinateData.getY()-radius/2, radius, radius);
                g2d.setColor(mDrawColour);
            }else if(isAvailable){
                g2d.setColor(Color.MAGENTA);
                g2d.fillOval(coordinateData.getX()-radius/2, coordinateData.getY()-radius/2, radius, radius);
                g2d.setColor(mDrawColour);
            }else{
                g2d.fillOval(coordinateData.getX()-radius/2, coordinateData.getY()-radius/2, radius, radius);
            }

            if(mCurrentClickNode == coordinateData.getId()){
                ArrayList<Ticket> tickets = new ArrayList<Ticket>();
                for(Move move : mAvailableMoves){
                    if(move instanceof MoveTicket){
                        MoveTicket ticket = (MoveTicket) move;
                        if(ticket.target == mCurrentClickNode){
                            tickets.add(ticket.ticket);
                        }
                    }
                }
                if(graphPopup == null || graphPopup.getNodeId() != coordinateData.getId()) {
                    graphPopup = new GraphNodePopup(coordinateData.getId(), tickets, this);
                    graphPopup.create(coordinateData.getX(), coordinateData.getY(), mImageSize);
                }
            }

        }

        if(graphPopup != null){
            graphPopup.draw(g2d);
        }

    }

    @Override
    public void onMoveSelected(Ticket ticket, int nodeId) {
//        mDoubleMoveNode = nodeId;
        mListener.onNodeClicked(nodeId);
        mCurrentClickNode = -1;
        graphPopup = null;
        repaint();
    }

    class GraphMouseListener extends MouseAdapter implements MouseMotionListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(graphPopup != null && graphPopup.onClick(e.getX(), e.getY())){
                repaint();
            } else {
                for (CoordinateData coordinateData : mNodes) {
                    if (mAvailablePositions.contains(coordinateData.getId()) && new Rectangle2D.Double(coordinateData.getX() - CIRC_RADIUS, coordinateData.getY() - CIRC_RADIUS, 2 * CIRC_RADIUS, 2 * CIRC_RADIUS).contains(e.getX(), e.getY())) {
                        mCurrentClickNode = coordinateData.getId();
                        repaint();
                        return;
                    }
                }
                mCurrentClickNode = -1;
                graphPopup = null;
                repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if(graphPopup != null && graphPopup.onMouseMoved(e.getX(), e.getY())){
              repaint();
            } else {
                for (CoordinateData coordinateData : mNodes) {
                    if (new Rectangle2D.Double(coordinateData.getX() - CIRC_RADIUS, coordinateData.getY() - CIRC_RADIUS, 2 * CIRC_RADIUS, 2 * CIRC_RADIUS).contains(e.getX(), e.getY())) {
                        mCurrentHoverNode = coordinateData.getId();
                        repaint();
                        return;
                    }
                }
                if (mCurrentHoverNode != -1) {
                    mCurrentHoverNode = -1;
                    mCurrentClickNode = -1;
                    graphPopup = null;
                    repaint();
                }
            }
        }


    }

}
