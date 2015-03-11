package solution.views;

import solution.Constants;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.adapters.GameUIAdapter;

import javax.swing.*;

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
            mGameLayout = new GameLayout(mControllerInterface);

            remove(playerCountLayout);
            add(mGameLayout);
            pack();
        }
    }

}
