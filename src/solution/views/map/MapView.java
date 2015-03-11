package solution.views.map;

import scotlandyard.Colour;
import scotlandyard.Move;
import scotlandyard.MoveTicket;
import solution.Models.GraphData;
import solution.ScotlandYardModel;
import solution.helpers.MoveHelper;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.adapters.GameUIAdapter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
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
    private Dimension mImageSize;
    private Map<Integer, Colour> mPlayerLocations;
    private List<MapPosition> mMapPositions;
    private MapNodePopup mMapPopup;


    public MapView(final GameControllerInterface gameController, final String graphImageMapPath, final GraphData graphData) {
        mControllerInterface = gameController;
        generatePositionList(graphData.getPositionMap());
        mPlayerLocations = new HashMap<Integer, Colour>();
        mControllerInterface.addUpdateListener(new GameAdapter());
        addMouseListener(new GraphMouseListener());
        addMouseMotionListener(new GraphMouseListener());
        setupGraphImage(graphImageMapPath);
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

        for(MapPosition position : mMapPositions){
            position.draw(g2d, mPlayerLocations);
        }

        if(mMapPopup != null){
            mMapPopup.draw(g2d);
        }

    }

    @Override
    public void onMoveSelected(Move move, int nodeId) {
        mControllerInterface.notifyMoveSelected(move);
        mMapPopup = null;
        repaint();
    }

    class GraphMouseListener extends MouseAdapter implements MouseMotionListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            final boolean popupClicked = mMapPopup != null && mMapPopup.onClick(e.getX(), e.getY());
            if (!popupClicked){
                for(MapPosition position : mMapPositions){
                    if(position.notifyMouseClick(e.getX(), e.getY())){
                        mMapPopup = new MapNodePopup(position, mImageSize, MapView.this);
                    }
                }
            }
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            boolean positionHovered = false;
            final boolean popupHovered = mMapPopup != null && mMapPopup.onMouseMoved(e.getX(), e.getY());
            if (!popupHovered) {
                for(MapPosition position : mMapPositions){
                    if(position.notifyMouseMove(e.getX(),e.getY())){
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

            for(MapPosition position : mMapPositions){
                position.resetMoves();
            }

            if(!model.isGameOver()) {
                mPlayerLocations.clear();
                for (Colour colour : model.getPlayers()) {
                    mPlayerLocations.put(model.getPlayerLocation(colour), colour);
                }
                List<Move> validMoves = model.validMoves(model.getCurrentPlayer());

                for(Move move : validMoves){
                    MoveTicket moveTicket = MoveHelper.getFinalMove(move);
                    for(MapPosition position : mMapPositions){
                        if(position.getId() == moveTicket.target){
                            position.addMove(move);
                        }
                    }
                }
            }
            repaint();
        }
    }

}
