package solution.views;

import scotlandyard.Colour;
import scotlandyard.MoveTicket;
import scotlandyard.Ticket;
import solution.ScotlandYardModel;
import solution.helpers.TicketHelper;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.adapters.GameUIAdapter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
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
        setVisible(false);
        GameControllerInterface controllerInterface = mControllerInterface;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 10;
        gbc.weighty = 100;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        setBackground(Color.BLACK);
        Box vertBox = Box.createVerticalBox();


        List<MoveTicket> moveHistory = controllerInterface.getMrXHistory();
        for(int i = 0; i < moveHistory.size(); i++){
            MoveTicket currentMove = moveHistory.get(i);
            JPanel panelDisplay = new JPanel();
            panelDisplay.setOpaque(false);
            panelDisplay.setMaximumSize(new Dimension(300, 80));

            JLabel moveDetails = new JLabel();

            // Find the number in
            moveDetails.setIcon(TicketHelper.ticketToImg(currentMove.ticket));

            moveDetails.setForeground(Color.WHITE);
            panelDisplay.add(moveDetails);
            gbc.gridx = i;
            vertBox.add(panelDisplay, gbc);
        }
        add(vertBox, gbc);
        setVisible(true);
    }

    class GameAdapter extends GameUIAdapter {
        @Override
        public void onGameModelUpdated(ScotlandYardModel model) {
            removeAll();
            createFrame();
        }
    }


}
