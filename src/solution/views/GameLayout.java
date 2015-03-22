package solution.views;

import com.sun.deploy.util.StringUtils;
import scotlandyard.Colour;
import solution.Models.MapData;
import solution.helpers.ColourHelper;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.adapters.GameUIAdapter;
import solution.views.map.MapView;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rory on 10/03/15.
 */
public class GameLayout extends JPanel {
    private MapView mapView;
    private SideBarView sbView;
    private GameControllerInterface mControllerInterface;
    GridBagConstraints gbc = new GridBagConstraints();
    public GameLayout(GameControllerInterface controllerInterface, PlayerInfoBar.PlayerInfoBarListener listener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        mControllerInterface = controllerInterface;

        setOpaque(false);
        GameAdapter gameAdapter = new GameAdapter();
        controllerInterface.addUpdateListener(gameAdapter);

        PlayerInfoBar playerInfoBar = new PlayerInfoBar(controllerInterface);
        playerInfoBar.setListener(listener);
        // Set to a percentage layout
        setLayout(new GridBagLayout());

        GridBagConstraints gbcInside = new GridBagConstraints();

        // Setup the grid
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Set up map Panel
        JPanel mapViewContainer = new JPanel();
        mapViewContainer.setLayout(new BorderLayout());
        mapViewContainer.setOpaque(false);
        mapViewContainer.setBorder(new EmptyBorder(20,20,20,20));
        // Load in the map view
        mapView = new MapView(controllerInterface, "pirate_map.png", new MapData("custom_data", MapData.DataFormat.CUSTOM));
        mapView.setBorder(new EmptyBorder(20,20,20,20));

        // Set Dimensions
        mapView.setPreferredSize(new Dimension(800, 600));
        mapView.setMinimumSize(new Dimension(800,600));


        // Load in the map
        mapViewContainer.add(mapView);

        // Setup top view container
        JPanel subLayout = new JPanel();
        subLayout.setLayout(new GridBagLayout());
        subLayout.setOpaque(false);

        sbView = new SideBarView(controllerInterface);

        gbcInside.gridy = gbcInside.gridx = 0;
        gbcInside.weightx = 70;
        gbcInside.weighty = 100;
        subLayout.add(mapViewContainer, gbcInside);

        gbcInside.gridx = 1;
        gbcInside.weightx = 30;
        subLayout.add(sbView, gbcInside);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 100;
        gbc.weighty = 90;

        add(subLayout, gbc);
        gbc.gridy = 1;
        gbc.weighty = 10;
        add(playerInfoBar, gbc);

        gameAdapter.onGameModelUpdated(controllerInterface);

    }

    class GameAdapter extends GameUIAdapter {
        @Override
        public void onGameModelUpdated(GameControllerInterface controllerInterface) {
            System.out.println("Change has been made\n\n\n");
            if(!controllerInterface.isGameOver()) {
                System.out.println("It is " + ColourHelper.toString(controllerInterface.getCurrentPlayer()) + "'s turn");
            }else{
                List<String> winningPlayers = new ArrayList<String>();
                for(Colour winningColour : controllerInterface.getWinningPlayers()){
                    winningPlayers.add(ColourHelper.toString(winningColour));
                }
                System.out.println("Gameover! " + StringUtils.join(winningPlayers, ", ") + " won!");
                mapView.showGameOverView();

            }
        }
    }

}
