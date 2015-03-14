package solution.views.map;

import scotlandyard.Colour;
import scotlandyard.MoveDouble;
import scotlandyard.MoveTicket;
import scotlandyard.Ticket;
import solution.Constants;
import solution.Models.GraphData;
import solution.Models.ScotlandYardModel;
import solution.helpers.InterpolatorHelper;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.adapters.GameUIAdapter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapView extends JPanel implements MapNodePopup.PopupInterface {
    private final Color mDrawColour = Color.gray;
    private final GameControllerInterface mControllerInterface;
    private BufferedImage mGraphImage;
    private Map<Integer, Colour> mPlayerLocations;
    private final List<MapPosition> mMapPositions;
    private final ArrayList<MapPath> mMapPaths;
    private MapNodePopup mMapPopup;
    private MoveTicket firstMove;
    private List<MoveTicket> secondMoves;
    private AffineTransform transform;
    private AffineTransform inverseTransform;
    private Dimension mImageSize;
    private float[] animationCoords;


    public MapView(final GameControllerInterface gameController, final String graphImageMapPath, final GraphData graphData) {
        mControllerInterface = gameController;
        mMapPositions = graphData.getPositionList();
        mMapPaths = graphData.getPathList();
        mPlayerLocations = new HashMap<Integer, Colour>();
        mControllerInterface.addUpdateListener(new GameAdapter());
        transform = new AffineTransform();
        inverseTransform = new AffineTransform();
        addMouseListener(new GraphMouseListener());
        addMouseMotionListener(new GraphMouseListener());
        setupGraphImage(graphImageMapPath);

        try {
            mMapPopup = new MapNodePopup(this);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


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

        g2d.setStroke(new BasicStroke(5f));

        for(MapPath mapPath : mMapPaths){
            mapPath.drawBackground(g2d);
        }

        g2d.setStroke(new BasicStroke(3f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));


        for(MapPath mapPath : mMapPaths){
            mapPath.drawIfAvailable(g2d);
        }

        for(MapPath mapPath : mMapPaths){
            mapPath.drawIfHighlighted(g2d);
        }

        for(MapPosition position : mMapPositions){
            position.draw(g2d, mPlayerLocations);
        }

        if(mMapPopup != null){
            mMapPopup.draw(g2d);
        }

        g2d.setColor(Color.GREEN);

        if(animationCoords != null){
            g2d.fillOval((int) animationCoords[0]-5, (int) animationCoords[1]-5,10,10);
        }

    }

    @Override
    public void onTicketSelected(Ticket ticket, final int nodeId) {
        final Colour currentPlayer = mControllerInterface.getCurrentPlayer();
        final MoveTicket moveTicket = new MoveTicket(currentPlayer, nodeId, ticket);
        if(secondMoves != null && firstMove != null){
            MoveDouble chosenMove = new MoveDouble(currentPlayer, firstMove, moveTicket);
//            mControllerInterface.notifyMoveSelected(chosenMove);
//            secondMoves = null;
//            firstMove = null;
        }else {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    Path2D chosenPath = null;

                    for(MapPath path : mMapPaths){
                        if(path.isAvailable() && path.hasNode(nodeId)){
                            if(path.getStartingNode() == nodeId) {
                                chosenPath = InterpolatorHelper.reverse(path.getPath2D());
                            }else{
                                chosenPath = path.getPath2D();
                            }
                            break;
                        }
                    }


                    if(chosenPath != null){

                        Path2D interpolatedPath = InterpolatorHelper.interpolate(chosenPath, 5f);

                        PathIterator iterator = interpolatedPath.getPathIterator(null, 0.1f);

                        while(!iterator.isDone()){

                            animationCoords = new float[6];
                            iterator.currentSegment(animationCoords);
                            iterator.next();
                            repaint();

                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        animationCoords = null;
                        mControllerInterface.notifyMoveSelected(moveTicket);


                    }


                }
            }).start();

//            mControllerInterface.notifyMoveSelected(moveTicket);
        }

        mMapPopup.reset();
        repaint();
    }

    @Override
    public void onDoubleMoveSelected(Ticket ticket, int nodeId) {
        final Colour currentPlayer = mControllerInterface.getCurrentPlayer();
        final MoveTicket moveTicket = new MoveTicket(currentPlayer, nodeId, ticket);

        secondMoves = mControllerInterface.getValidSingleMovesAtLocation(currentPlayer, nodeId);
        firstMove = moveTicket;

        for (MapPosition mapPosition : mMapPositions) {
            if (mapPosition.getId() == nodeId) {
                mapPosition.setHighlighted(true);
                break;
            }
        }

        setValidMoves(secondMoves, nodeId);
        mMapPopup.reset();

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
                        mMapPopup.create(position, getSize(), canDoubleMove);
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
                        for(MapPath path : mMapPaths){
                            path.notifyPositionHovered(position);
                        }
                    }

                }
            }

            if(!positionHovered && !popupHovered){
                mMapPopup.reset();
                for(MapPath path : mMapPaths){
                    path.notifyPositionHovered(null);
                }
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

                setValidMoves(firstMoves, model.getRealPlayerLocation(currentPlayer));
            }
            repaint();
        }
    }

    private void setValidMoves(List<MoveTicket> moves, int currentPosition) {

        for(MapPosition position : mMapPositions){
            position.resetTickets();
        }

        for(MapPath mapPath : mMapPaths){
            mapPath.resetAvailability();
        }

        for(MoveTicket moveTicket : moves){
            for(MapPosition position : mMapPositions){
                if(position.getId() == moveTicket.target){
                    position.addTicket(moveTicket.ticket);
                }
            }

            for(MapPath mapPath : mMapPaths){
                mapPath.setAvailable(currentPosition, moveTicket);
            }
        }
    }



}
