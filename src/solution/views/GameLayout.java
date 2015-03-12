package solution.views;

import com.sun.deploy.util.StringUtils;
import scotlandyard.Colour;
import solution.Models.GraphData;
import solution.ScotlandYardModel;
import solution.helpers.ColourHelper;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.adapters.GameUIAdapter;
import solution.views.map.MapView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rory on 10/03/15.
 */
public class GameLayout extends JPanel {

    private final JLabel statusLabel;
    private final MapView mapView;
    private final CurrentPlayerIndicator playerIndicator;
    private final PlayerInfoBar playerInfoBar;

    public GameLayout(GameControllerInterface controllerInterface) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        GridBagConstraints gbcInside = new GridBagConstraints();

        gbc.gridwidth = gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbcInside.gridwidth = gbcInside.gridheight = 1;
        gbcInside.fill = GridBagConstraints.BOTH;
        gbcInside.anchor = GridBagConstraints.NORTHWEST;

        controllerInterface.addUpdateListener(new GameAdapter());
        JPanel subLayout = new JPanel();
        subLayout.setLayout(new BoxLayout(subLayout, BoxLayout.X_AXIS));


        MrXFrame mrXHistoryPanel = new MrXFrame(controllerInterface);
        mrXHistoryPanel.setLayout(new BoxLayout(mrXHistoryPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(mrXHistoryPanel);

        mapView = new MapView(controllerInterface, "map.jpg", new GraphData("pos.txt", GraphData.DataFormat.STANDARD));
        playerIndicator = new CurrentPlayerIndicator();
        playerInfoBar = new PlayerInfoBar(controllerInterface);
        statusLabel = new JLabel("");

        gbcInside.gridx = 0;
        gbcInside.gridy = 0;
        gbcInside.weightx = 20;
        gbcInside.weighty = 100;
        subLayout.add(mapView, gbcInside);

        gbcInside.gridx = 1;
        gbcInside.gridy = 0;
        gbcInside.weightx = 80;
        gbcInside.weighty = 100;
        subLayout.add(scrollPane, gbcInside);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 100;
        gbc.weighty = 80;
        add(subLayout, gbc);


        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 100;
        gbc.weighty = 20;
        add(playerInfoBar, gbc);
        //add(statusLabel);

    }

    class GameAdapter extends GameUIAdapter {
        @Override
        public void onGameModelUpdated(ScotlandYardModel model) {
            playerIndicator.setColours(model.getPlayers());
            playerIndicator.setSelectedColour(model.getCurrentPlayer());
            if(!model.isGameOver()) {
                statusLabel.setText("It is " + ColourHelper.toString(model.getCurrentPlayer()) + "'s turn");
            }else{
                List<String> winningPlayers = new ArrayList<String>();
                for(Colour winningColour : model.getWinningPlayers()){
                    winningPlayers.add(ColourHelper.toString(winningColour));
                }
                statusLabel.setText(("Gameover! " + StringUtils.join(winningPlayers, ", ")+" won!"));
            }
        }
    }

}
