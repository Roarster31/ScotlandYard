package solution.views;

import com.sun.deploy.util.StringUtils;
import scotlandyard.Colour;
import solution.Models.GraphData;
import solution.ScotlandYardModel;
import solution.helpers.ColourHelper;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.adapters.GameUIAdapter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rory on 10/03/15.
 */
public class GameLayout extends JPanel {

    private final JLabel statusLabel;
    private final GraphView graphView;
    private final CurrentPlayerIndicator playerIndicator;
    private final PlayerInfoBar playerInfoBar;

    public GameLayout(GameControllerInterface controllerInterface) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        controllerInterface.addUpdateListener(new GameAdapter());
        JPanel subLayout = new JPanel();
        subLayout.setLayout(new BoxLayout(subLayout, BoxLayout.Y_AXIS));


        JPanel mrXHistoryPanel = new JPanel();
        mrXHistoryPanel.setLayout(new BoxLayout(mrXHistoryPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(mrXHistoryPanel);

        graphView = new GraphView(controllerInterface, "map.jpg", new GraphData("pos.txt", GraphData.DataFormat.STANDARD));

        playerIndicator = new CurrentPlayerIndicator();

        playerInfoBar = new PlayerInfoBar(controllerInterface);

        statusLabel = new JLabel("");

        subLayout.add(graphView);
        subLayout.add(playerInfoBar);
        add(subLayout);
        add(scrollPane);
        add(statusLabel);

    }

    class GameAdapter extends GameUIAdapter {
        @Override
        public void onGameModelUpdated(ScotlandYardModel model) {
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
                statusLabel.setText(("Gameover! " + StringUtils.join(winningPlayers, ", ")+" won!"));
            }
        }
    }

}
