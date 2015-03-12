package solution.views.map;

import scotlandyard.Colour;
import scotlandyard.MoveDouble;
import scotlandyard.MoveTicket;
import scotlandyard.Ticket;
import solution.Constants;
import solution.Models.GraphData;
import solution.ScotlandYardModel;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.adapters.GameUIAdapter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class MapView extends JPanel implements MapNodePopup.PopupInterface {
    private final Color mDrawColour = Color.gray;
    private final GameControllerInterface mControllerInterface;
    private BufferedImage mGraphImage;
    private Map<Integer, Colour> mPlayerLocations;
    private List<MapPosition> mMapPositions;
    private MapNodePopup mMapPopup;
    private MoveTicket firstMove;
    private List<MoveTicket> secondMoves;
    private AffineTransform transform;
    private AffineTransform inverseTransform;
    private Dimension mImageSize;


    public MapView(final GameControllerInterface gameController, final String graphImageMapPath, final GraphData graphData) {
        mControllerInterface = gameController;
        generatePositionList(graphData.getPositionMap());
        mPlayerLocations = new HashMap<Integer, Colour>();
        mControllerInterface.addUpdateListener(new GameAdapter());
        transform = new AffineTransform();
        inverseTransform = new AffineTransform();
        addMouseListener(new GraphMouseListener());
        addMouseMotionListener(new GraphMouseListener());
        setupGraphImage(graphImageMapPath);


        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = getSize();

                transform.setToScale(size.getWidth() / mImageSize.getWidth(), size.getHeight() / mImageSize.getHeight());
                try {
                    inverseTransform = transform.createInverse();
                } catch (NoninvertibleTransformException e1) {
                    e1.printStackTrace();
                }
                repaint();
            }

        });
    }

    private void generatePositionList(Map<Integer, Integer[]> positionMap) {
        mMapPositions = new ArrayList<MapPosition>();

        Iterator it = positionMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            mMapPositions.add(new MapPosition((Integer) pair.getKey(), ((Integer[]) pair.getValue())[0], ((Integer[]) pair.getValue())[1]));
            it.remove();
        }
    }

    private void setupGraphImage(final String graphImageMapPath) {
        try {
            final URL resource = getClass().getClassLoader().getResource(graphImageMapPath);
            mGraphImage = ImageIO.read(new File(resource.toURI()));
            mImageSize = new Dimension(mGraphImage.getWidth(), mGraphImage.getHeight());
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

        g2d.setTransform(transform);

        g2d.drawImage(mGraphImage, null, 0, 0);

        g2d.setColor(mDrawColour);

        for(MapPosition position : mMapPositions){
            position.draw(g2d, mPlayerLocations);
        }

        if(mMapPopup != null){
            mMapPopup.draw(g2d);
        }

    }

    @Override
    public void onTicketSelected(Ticket ticket, int nodeId) {
        final Colour currentPlayer = mControllerInterface.getCurrentPlayer();
        final MoveTicket moveTicket = new MoveTicket(currentPlayer, nodeId, ticket);
        if(secondMoves != null){
            MoveDouble chosenMove = new MoveDouble(currentPlayer, firstMove, moveTicket);
            mControllerInterface.notifyMoveSelected(chosenMove);
        }else {
            if (ticket.equals(Ticket.DoubleMove)) {
                secondMoves = mControllerInterface.getValidSingleMovesAtLocation(currentPlayer, nodeId);
                firstMove = moveTicket;
                for (MapPosition mapPosition : mMapPositions) {
                    if (mapPosition.getId() == nodeId) {
                        mapPosition.setHighlighted(true);
                        break;
                    }
                }

                setValidMoves(secondMoves);
                mMapPopup = null;
            } else {
                mControllerInterface.notifyMoveSelected(moveTicket);
                mMapPopup = null;
            }
        }
        repaint();
    }

    class GraphMouseListener extends MouseAdapter implements MouseMotionListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            Point2D transformedPoint = inverseTransform.transform(new Point2D.Double(e.getX(), e.getY()), null);

            final boolean popupClicked = mMapPopup != null && mMapPopup.onClick((int) transformedPoint.getX(), (int) transformedPoint.getY());
            if (!popupClicked){
                for(MapPosition position : mMapPositions){
                    if(position.notifyMouseClick((int) transformedPoint.getX(), (int) transformedPoint.getY())){
                        final boolean isMrX = mControllerInterface.getCurrentPlayer() == Constants.MR_X_COLOUR;
                        final boolean hasEnoughDoubleMoves = mControllerInterface.getPlayerTickets(Constants.MR_X_COLOUR).get(Ticket.DoubleMove) > 0;
                        final boolean canDoubleMove = isMrX && hasEnoughDoubleMoves && secondMoves == null;
                        mMapPopup = new MapNodePopup(position, getSize(), canDoubleMove, MapView.this);
                    }
                }
            }
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            Point2D transformedPoint = inverseTransform.transform(new Point2D.Double(e.getX(), e.getY()), null);

            boolean positionHovered = false;
            final boolean popupHovered = mMapPopup != null && mMapPopup.onMouseMoved((int) transformedPoint.getX(), (int) transformedPoint.getY());
            if (!popupHovered) {
                for(MapPosition position : mMapPositions){
                    if(position.notifyMouseMove((int) transformedPoint.getX(), (int) transformedPoint.getY())){
                        positionHovered = true;
                    }
                }
            }

            if(!positionHovered && !popupHovered){
                mMapPopup = null;
            }

            repaint();

        }


    }

    class GameAdapter extends GameUIAdapter {
        @Override
        public void onGameModelUpdated(ScotlandYardModel model) {

            if(!model.isGameOver()) {
                mPlayerLocations.clear();
                for (Colour colour : model.getPlayers()) {
                    mPlayerLocations.put(model.getPlayerLocation(colour), colour);
                }
                final Colour currentPlayer = model.getCurrentPlayer();
                List<MoveTicket> firstMoves = mControllerInterface.getValidSingleMovesAtLocation(currentPlayer, model.getRealPlayerLocation(currentPlayer));

                setValidMoves(firstMoves);
            }
            repaint();
        }
    }

    private void setValidMoves(List<MoveTicket> moves) {

        for(MapPosition position : mMapPositions){
            position.resetTickets();
        }

        for(MoveTicket moveTicket : moves){
            for(MapPosition position : mMapPositions){
                if(position.getId() == moveTicket.target){
                    position.addTicket(moveTicket.ticket);
                }
            }
        }
    }



}
