package solution.views;

import com.sun.deploy.util.StringUtils;
import scotlandyard.Colour;
import solution.ModelUpdateListener;
import solution.Models.GraphData;
import solution.ScotlandYardModel;
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

    public GameLayout () {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        graphView = new GraphView("map.jpg", new GraphData("pos.txt", GraphData.DataFormat.STANDARD));
        add(graphView);

        statusLabel = new JLabel("");
        add(statusLabel);

    }

    @Override
    public void onWaitingOnPlayer(ScotlandYardModel model) {

        if(!model.isGameOver()) {
            for (Colour colour : model.getPlayers()) {
                graphView.setPlayerPosition(colour, model.getPlayerLocation(colour));
            }
            graphView.setAvailablePositions(model.validMoves(model.getCurrentPlayer()));
            statusLabel.setText("It is " + ColourHelper.toString(model.getCurrentPlayer()) + "'s turn");
        }else{
            List<String> winningPlayers = new ArrayList<String>();
            for(Colour winningColour : model.getWinningPlayers()){
                winningPlayers.add(ColourHelper.toString(winningColour));
            }
            graphView.setAvailablePositions(null);
            statusLabel.setText(("Gameover! "+ StringUtils.join(winningPlayers,", ")+" won!"));
        }
    }

    public void setGameListener(GraphView.GraphViewListener listener) {
        graphView.setListener(listener);
    }
}
