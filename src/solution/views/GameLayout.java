package solution.views;

import com.sun.deploy.util.StringUtils;
import scotlandyard.Colour;
import scotlandyard.MoveTicket;
import solution.ModelUpdateListener;
import solution.Models.GraphData;
import solution.ScotlandYardModel;
import solution.controllers.GameController;
import solution.helpers.ColourHelper;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rory on 10/03/15.
 */
public class GameLayout extends JPanel implements ModelUpdateListener{

    private final JLabel statusLabel;
    private final GraphView graphView;
    private final CurrentPlayerIndicator playerIndicator;
    private final PlayerInfoBar playerInfoBar;

    public GameLayout () {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JPanel subLayout = new JPanel();
        subLayout.setLayout(new BoxLayout(subLayout, BoxLayout.Y_AXIS));


        JPanel mrXHistoryPanel = new JPanel();
        mrXHistoryPanel.setLayout(new BoxLayout(mrXHistoryPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(mrXHistoryPanel);

        graphView = new GraphView("map.jpg", new GraphData("pos.txt", GraphData.DataFormat.STANDARD));

        playerIndicator = new CurrentPlayerIndicator();

        playerInfoBar = new PlayerInfoBar();

        statusLabel = new JLabel("");

        subLayout.add(graphView);
<<<<<<< HEAD
//        scrollPane.add(scrollPane);
=======

        subLayout.add(playerInfoBar);
>>>>>>> Added Player Bar
        add(subLayout);
        add(scrollPane);
        add(statusLabel);

    }

    @Override
    public void onWaitingOnPlayer(ScotlandYardModel model, List<MoveTicket> mrXMoves) {

        playerIndicator.setColours(model.getPlayers());
        playerIndicator.setSelectedColour(model.getCurrentPlayer());
        if(!model.isGameOver()) {
            for (Colour colour : model.getPlayers()) {
                graphView.setPlayerPosition(colour, model.getPlayerLocation(colour));
            }
            graphView.setAvailableMoves(model.validMoves(model.getCurrentPlayer()));
            statusLabel.setText("It is " + ColourHelper.toString(model.getCurrentPlayer()) + "'s turn");
        }else{
            List<String> winningPlayers = new ArrayList<String>();
            for(Colour winningColour : model.getWinningPlayers()){
                winningPlayers.add(ColourHelper.toString(winningColour));
            }
            graphView.setAvailableMoves(null);
            statusLabel.setText(("Gameover! " + StringUtils.join(winningPlayers,", ")+" won!"));
        }
    }

    public void setGameListener(GraphView.GraphViewListener listener) {
        graphView.setListener(listener);

    }

    public void setGameController(GameController gc) {
        playerInfoBar.setGameControllerListener(gc);
    }
}
