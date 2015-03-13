package solution.views;

import solution.Constants;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.adapters.GameUIAdapter;

import javax.swing.*;
import java.awt.*;

/**
 * Created by rory on 09/03/15.
 */
public class MainFrame extends JFrame {

    private final PlayerCountLayout playerCountLayout;
    private final GameControllerInterface mControllerInterface;
    private GameLayout mGameLayout;


    public MainFrame(final GameControllerInterface controllerInterface) {

        mControllerInterface = controllerInterface;
        controllerInterface.addUpdateListener(new GameAdapter());


        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        playerCountLayout = new PlayerCountLayout(Constants.MIN_PLAYERS, Constants.MAX_PLAYERS);
        playerCountLayout.setListener(new PlayerCountLayout.PlayerCountListener() {
            @Override
            public void onPlayerCountDecided(int count) {
                controllerInterface.notifyAllPlayersAdded(count);
            }
        });
        getContentPane().add(playerCountLayout);

        pack();
        setVisible(true);
    }

    class GameAdapter extends GameUIAdapter {
        @Override
        public void showGameInterface() {
            // Grab the frame
            JPanel mainFrame = (JPanel)getGlassPane();
            setSize(new Dimension(1000,800));

            // Remove the player counter
            remove(playerCountLayout);
            mGameLayout = new GameLayout(mControllerInterface);

            // Add the new the game in
            add(mGameLayout);


            // Add in some end game views
            GameOverView endOfGame = new GameOverView(mControllerInterface);
            endOfGame.setPreferredSize(new Dimension(1000,800));
            endOfGame.setOpaque(false);
            endOfGame.setVisible(false);
            mainFrame.add(endOfGame);

            // Show the frame
            mainFrame.setVisible(true);

        }
    }

}
