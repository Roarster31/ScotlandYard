package solution.views;

import solution.interfaces.GameControllerInterface;
import solution.interfaces.adapters.GameUIAdapter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

/**
 * Created by benallen on 12/03/15.
 */
public class MrXFrame extends JPanel {
    GameControllerInterface mControllerInterface;
    public MrXFrame(GameControllerInterface controllerInterface) {
        mControllerInterface = controllerInterface;
        GameAdapter gameAdapter = new GameAdapter();
        controllerInterface.addUpdateListener(gameAdapter);
        createFrame();
        gameAdapter.onGameModelUpdated(controllerInterface);

    }

    private void createFrame() {
        setVisible(false);

//        setMinimumSize(new Dimension(200,100));
//        setPreferredSize(new Dimension(200,100));
//        setMaximumSize(new Dimension(200,100));
//        setSize(new Dimension(200,100));

        GameControllerInterface controllerInterface = mControllerInterface;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 10;
        gbc.weighty = 100;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Create the round counter
        JLabel roundContainer = new JLabel();
        // Load in background
        URL resource = getClass().getClassLoader().getResource("ui" + File.separator + "roundholder.png");
        ImageIcon bgIcon = new ImageIcon(resource);
        roundContainer.setIcon(bgIcon);
        roundContainer.setOpaque(false);

        gbc.gridheight = 30;
        gbc.weighty = 300;
        add(roundContainer, gbc);


        setBackground(Color.BLACK);
        setOpaque(false);
        setVisible(true);

    }

    class GameAdapter extends GameUIAdapter {
        @Override
        public void onGameModelUpdated(GameControllerInterface controllerInterface) {
            removeAll();
            createFrame();
        }
    }


}
