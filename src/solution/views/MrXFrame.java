package solution.views;

import scotlandyard.Colour;
import scotlandyard.MoveTicket;
import solution.ScotlandYardModel;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.adapters.GameUIAdapter;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by benallen on 12/03/15.
 */
public class MrXFrame extends JPanel {
    GameControllerInterface mControllerInterface;
    public MrXFrame(GameControllerInterface controllerInterface) {
        mControllerInterface = controllerInterface;
        controllerInterface.addUpdateListener(new GameAdapter());
        createFrame();
    }

    private void createFrame() {
        GameControllerInterface controllerInterface = mControllerInterface;
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);
        gbc.gridwidth = gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weighty = 10;
        gbc.gridx = 0;

        List<MoveTicket> moveHistory = controllerInterface.getMrXHistory();
        for(int i = 0; i < moveHistory.size(); i++){
            MoveTicket currentMove = moveHistory.get(i);
            JPanel panelDisplay = new JPanel();
            panelDisplay.setBackground(Color.BLACK);

            String moveLocation = String.valueOf(currentMove.target);
            JLabel moveDetails = new JLabel("Moves: "  + moveLocation);
            moveDetails.setForeground(Color.WHITE);
            panelDisplay.add(moveDetails);
            gbc.gridy = i;
            add(panelDisplay, gbc);
        }
    }

    class GameAdapter extends GameUIAdapter {
        @Override
        public void onGameModelUpdated(ScotlandYardModel model) {
            removeAll();
            createFrame();
            System.out.printf("Hello®");

        }
    }


}
