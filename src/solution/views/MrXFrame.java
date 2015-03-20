package solution.views;

import scotlandyard.MoveTicket;
import solution.Models.ScotlandYardModel;
import solution.helpers.TicketHelper;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.adapters.GameUIAdapter;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.net.URL;
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


//        Box vertBox = Box.createVerticalBox();
//
//
//        List<MoveTicket> moveHistory = controllerInterface.getMrXHistory();
//        for(int i = 0; i < moveHistory.size(); i++){
//            MoveTicket currentMove = moveHistory.get(i);
//            JPanel panelDisplay = new JPanel();
//            panelDisplay.setOpaque(false);
//
//            JLabel moveDetails = new JLabel();
//
//            // Find the number in
//            moveDetails.setIcon(TicketHelper.ticketToImg(currentMove.ticket));
//
//            moveDetails.setForeground(Color.WHITE);
//            panelDisplay.add(moveDetails);
//            gbc.gridx = i + 1;
//            vertBox.add(panelDisplay, gbc);
//        }
//        add(vertBox, gbc);
        setBackground(Color.BLACK);
        setOpaque(false);
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
