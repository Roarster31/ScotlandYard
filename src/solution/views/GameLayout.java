package solution.views;

import com.sun.deploy.util.StringUtils;
import scotlandyard.Colour;
import solution.Models.MapData;
import solution.Models.ScotlandYardModel;
import solution.helpers.ColourHelper;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.adapters.GameUIAdapter;
import solution.views.map.MapView;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rory on 10/03/15.
 */
public class GameLayout extends JPanel {
    private MapView mapView;
    public GameLayout(GameControllerInterface controllerInterface, PlayerInfoBar.PlayerInfoBarListener listener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


        setOpaque(false);
        controllerInterface.addUpdateListener(new GameAdapter());

        PlayerInfoBar playerInfoBar = new PlayerInfoBar(controllerInterface);
        playerInfoBar.setListener(listener);
        // Set to a percentage layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
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

        // I DONT WANT TO DO THIS BUT I HAVE TOO
        mapView.setPreferredSize(new Dimension(800,600));
        mapView.setMinimumSize(new Dimension(800,600));


        // Load in the map
        mapViewContainer.add(mapView);

        // Setup top view container
        JPanel subLayout = new JPanel();
        subLayout.setLayout(new GridBagLayout());
        subLayout.setOpaque(false);

        // Set up mr xs history panel
        MrXFrame mrXHistoryPanel = new MrXFrame(controllerInterface);
        mrXHistoryPanel.setLayout(new BoxLayout(mrXHistoryPanel, BoxLayout.Y_AXIS));

        SideBarView sbView = new SideBarView(controllerInterface);


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
    }

    class GameAdapter extends GameUIAdapter {
        @Override
        public void onGameModelUpdated(ScotlandYardModel model) {
            System.out.println("Change has been made");
            if(!model.isGameOver()) {
                System.out.println("It is " + ColourHelper.toString(model.getCurrentPlayer()) + "'s turn");
            }else{
                List<String> winningPlayers = new ArrayList<String>();
                for(Colour winningColour : model.getWinningPlayers()){
                    winningPlayers.add(ColourHelper.toString(winningColour));
                }
                System.out.println("Gameover! " + StringUtils.join(winningPlayers, ", ") + " won!");
            }
        }
    }

}
