package solution.views.map;

import scotlandyard.*;
import solution.Constants;
import solution.Models.GraphData;
import solution.Models.ScotlandYardModel;
import solution.helpers.PathInterpolator;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapView extends JPanel implements MapNodePopup.PopupInterface {
    private final Color mDrawColour = Color.gray;
    private final GameControllerInterface mControllerInterface;
    private final List<MapPosition> mMapPositions;
    private final ArrayList<MapPath> mMapPaths;
    private BufferedImage mGraphImage;
    private Map<Integer, Colour> mPlayerLocations;
    private MapNodePopup mMapPopup;
    private MoveTicket firstMove;
    private List<MoveTicket> secondMoves;
    private AffineTransform transform;
    private AffineTransform inverseTransform;
    private Dimension mImageSize;
    private TransportSprite transportSprite;
    private AnimationWorker animationWorker;


    public MapView(final GameControllerInterface gameController, final String graphImageMapPath, final GraphData graphData) {
        mControllerInterface = gameController;
        mMapPositions = graphData.getPositionList();
        mMapPaths = graphData.getPathList();
        mPlayerLocations = new HashMap<Integer, Colour>();
        mControllerInterface.addUpdateListener(new GameAdapter());
        transform = new AffineTransform();
        inverseTransform = new AffineTransform();
        animationWorker = new AnimationWorker(this);
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

        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));

        for (MapPath mapPath : mMapPaths) {
            mapPath.draw(g2d, Ticket.Underground);
        }
        for (MapPath mapPath : mMapPaths) {
            mapPath.draw(g2d, Ticket.Bus);
        }
        for (MapPath mapPath : mMapPaths) {
            mapPath.draw(g2d, Ticket.Taxi);
        }

//        for (MapPath mapPath : mMapPaths) {
//            if(mapPath.isAvailable()){
//                g2d.setColor(Color.BLACK);
//            }else{
//                g2d.setColor(new Color(209, 161, 134));
//            }
//            mapPath.draw(g2d);
//
//        }

//        Path2D path2D = new Path2D.Double();
//
//        path2D.moveTo(500,500);
//        path2D.lineTo(700, 700);
//        path2D.lineTo(900,00);
//
//        path2D.lineTo(1000,500);
//        path2D.lineTo(900,300);

//        new Stacked2DPath(path2D, 3).draw(g2d);

        for (MapPath mapPath : mMapPaths) {
            if(mapPath.isHighlighted()){
                mapPath.draw(g2d);
            }
        }

        for (MapPosition position : mMapPositions) {
            position.draw(g2d, mPlayerLocations);
        }

        if (mMapPopup != null) {
            mMapPopup.draw(g2d);
        }

        g2d.setColor(Color.GREEN);


        if (transportSprite != null) {
            transportSprite.draw(g2d);
        }

    }

    @Override
    public void onTicketSelected(final Ticket ticket, final int nodeId) {
        final Colour currentPlayer = mControllerInterface.getCurrentPlayer();
        final MoveTicket singleMove = new MoveTicket(currentPlayer, nodeId, ticket);
        if (secondMoves != null && firstMove != null) {


            animateMove(firstMove, singleMove);

        } else {

            animateMove(singleMove, null);

        }

        mMapPopup.reset();
        repaint();
    }

    private void animateMove(final MoveTicket firstMove, final MoveTicket secondMove) {

        PathInterpolator firstMoveInterpolator = null;
        PathInterpolator secondMoveInterpolator = null;


        for (MapPath path : mMapPaths) {
            if (path.hasNode(mControllerInterface.getCurrentPlayerRealPosition()) && path.hasNode(firstMove.target)) {
                firstMoveInterpolator = new PathInterpolator(path.getPath2D());
                if (path.getStartingNode() == firstMove.target) {
                    firstMoveInterpolator.reverse();
                }
                if(secondMove == null || secondMoveInterpolator != null) {
                    break;
                }
            }

            if(secondMove != null) {
                if (path.hasNode(firstMove.target) && path.hasNode(secondMove.target)) {
                    secondMoveInterpolator = new PathInterpolator(path.getPath2D());
                    if (path.getStartingNode() == secondMove.target) {
                        secondMoveInterpolator.reverse();
                    }
                    if (firstMoveInterpolator != null) {
                        break;
                    }
                }
            }

        }

        if (firstMoveInterpolator != null) {

            firstMoveInterpolator.interpolate(2f);

            transportSprite = new TransportSprite(firstMove.ticket);


            final PathInterpolator finalFirstMoveInterpolator = firstMoveInterpolator;
            final PathInterpolator finalSecondMoveInterpolator = secondMoveInterpolator;
            animationWorker.addWork(new AnimationWorker.AnimationInterface() {
                @Override
                public boolean onTick() {


                    transportSprite.setSegment(finalFirstMoveInterpolator.getCurrentSegment());

                    finalFirstMoveInterpolator.nextSegment();

                    return finalFirstMoveInterpolator.isDone();
                }

                @Override
                public void onFinished() {

                    if(finalSecondMoveInterpolator != null){


                        finalSecondMoveInterpolator.interpolate(2f);

                        transportSprite = new TransportSprite(secondMove.ticket);


                        animationWorker.addWork(new AnimationWorker.AnimationInterface() {
                            @Override
                            public boolean onTick() {


                                transportSprite.setSegment(finalSecondMoveInterpolator.getCurrentSegment());

                                finalSecondMoveInterpolator.nextSegment();

                                return finalSecondMoveInterpolator.isDone();
                            }

                            @Override
                            public void onFinished() {

                                transportSprite = null;
                                secondMoves = null;
                                MapView.this.firstMove = null;
                                mControllerInterface.notifyMoveSelected(new MoveDouble(mControllerInterface.getCurrentPlayer(), firstMove, secondMove));
                            }
                        });

                    }else{
                        transportSprite = null;
                        mControllerInterface.notifyMoveSelected(firstMove);
                    }

                }
            });

        }

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

        showValidMoves(secondMoves, nodeId);
        mMapPopup.reset();

        repaint();

    }

    private void showValidMoves(List<MoveTicket> moves, int currentPosition) {

        for (MapPosition position : mMapPositions) {
            position.resetTickets();
        }

        for (MapPath mapPath : mMapPaths) {
            mapPath.resetAvailability();
        }

        for (MoveTicket moveTicket : moves) {
            for (MapPosition position : mMapPositions) {
                if (position.getId() == moveTicket.target) {
                    //if this position is in the move list then add the move type
                    //to it
                    position.addTicket(moveTicket.ticket);
                }
            }

            for (MapPath mapPath : mMapPaths) {
                mapPath.setAvailable(currentPosition, moveTicket);
            }
        }
    }

    class GraphMouseListener extends MouseAdapter implements MouseMotionListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            Point2D transformedPoint = inverseTransform.transform(new Point2D.Double(e.getX(), e.getY()), null);

            final boolean popupClicked = mMapPopup != null && mMapPopup.onClick((int) transformedPoint.getX(), (int) transformedPoint.getY());
            if (!popupClicked) {
                for (MapPosition position : mMapPositions) {
                    if (position.notifyMouseClick((int) transformedPoint.getX(), (int) transformedPoint.getY())) {
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
                for (MapPosition position : mMapPositions) {
                    if (position.notifyMouseMove((int) transformedPoint.getX(), (int) transformedPoint.getY())) {
                        positionHovered = true;
                        for (MapPath path : mMapPaths) {
                            path.notifyPositionHovered(position);
                        }
                    }

                }
            }

            if (!positionHovered && !popupHovered) {
                mMapPopup.reset();
                for (MapPath path : mMapPaths) {
                    path.notifyPositionHovered(null);
                }
            }

            repaint();

        }


    }

    class GameAdapter extends GameUIAdapter {
        @Override
        public void onGameModelUpdated(ScotlandYardModel model) {



            if (!model.isGameOver()) {
                mPlayerLocations.clear();
                for (Colour colour : model.getPlayers()) {
                    mPlayerLocations.put(model.getPlayerLocation(colour), colour);
                }
                final Colour currentPlayer = model.getCurrentPlayer();
                List<MoveTicket> firstMoves = mControllerInterface.getValidSingleMovesAtLocation(currentPlayer, model.getRealPlayerLocation(currentPlayer));

                showValidMoves(firstMoves, model.getRealPlayerLocation(currentPlayer));
            }

                List<Edge<Integer, Route>> edges = mControllerInterface.getGraphRoutes();

                for (MapPath mapPath : mMapPaths) {

                    for(Edge<Integer, Route> edge : edges){
                        if(mapPath.hasNode(edge.source()) && mapPath.hasNode(edge.target())){
                            //weeeee, we have it
                            mapPath.addRoute(edge.data());
                        }
                    }
                }

            repaint();
        }
    }


}
