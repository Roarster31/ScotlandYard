package solution.views;

import scotlandyard.Colour;
import scotlandyard.Ticket;
import solution.Constants;
import solution.helpers.ColourHelper;
import solution.helpers.TicketHelper;
import solution.interfaces.GameControllerInterface;
import sun.invoke.empty.Empty;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * Created by benallen on 10/03/15.
 */
@Deprecated
public class PlayerInfoColumn extends JPanel {
    private int maxTicketNumber = 0;
    private Colour currentPlayer;
    private GameControllerInterface mController;
    public PlayerInfoColumn(Colour currentPlayer, GameControllerInterface controllerInterface, int playerNumber){

        mController = controllerInterface;
        this.currentPlayer = currentPlayer;
        // Check to see whether the passed player equals the current player
        setupCols(currentPlayer, controllerInterface.getPlayerTickets(currentPlayer), playerNumber);
    }

    private void setupCols(Colour currentPlayer, Map<Ticket, Integer> playerTickets, int playerNumber) {
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(170,106));
        setMaximumSize(new Dimension(170, 106));
        setMinimumSize(new Dimension(170,106));
        setSize(new Dimension(170, 106));

        Box vertView = Box.createVerticalBox();
        JPanel tickets = new JPanel();

        tickets.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NORTH;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridy = 0;

        JPanel textOverlay = new JPanel();
        textOverlay.setOpaque(false);
        textOverlay.setLayout(new BoxLayout(textOverlay, BoxLayout.X_AXIS));

        // Load in the font
        InputStream is = getClass().getClassLoader().getResourceAsStream("ui" + File.separator + "snellroundhand.ttf");
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Font playerNameFont = font.deriveFont(22f);

        // Player name
        int playerNumberVisible = playerNumber + 1;
        JLabel playerName = new JLabel("Player " + playerNumberVisible);
        playerName.setFont(playerNameFont);
        if(playerNumber % 2 == 0) {
            playerName.setBorder(new EmptyBorder(12, 10, 0, 0));
        } else {
            playerName.setBorder(new EmptyBorder(12, 5, 0, 0));
        }
        textOverlay.add(playerName);
        textOverlay.setBorder(new EtchedBorder());

        JLabel background = new JLabel();
        URL resource;
        if(playerNumber % 2 == 0) {
             resource = getClass().getClassLoader().getResource("ui" + File.separator + "playerholderA.png");
        } else {
             resource = getClass().getClassLoader().getResource("ui" + File.separator + "playerholderB.png");
        }


        gbc.gridwidth = 100;
        gbc.weightx = 100;
        gbc.weighty = 10;


        // Max limit of tickets
        if(currentPlayer == Constants.MR_X_COLOUR){
            maxTicketNumber = 3;
        } else {
            maxTicketNumber = 3;
        }

        // Get players remaining tickets
        Ticket[] ticketTypes = {Ticket.Bus, Ticket.Underground, Ticket.Taxi, Ticket.DoubleMove, Ticket.SecretMove};

        // Loop through all tickets
        for (int i = 0; i < maxTicketNumber; i++) {
            String numOfTickets = String.valueOf(playerTickets.get(ticketTypes[i]));
            if(!numOfTickets.equals("0")) {
                // Create Panel
                Box horzView = Box.createHorizontalBox();
                JPanel ticketNums = new JPanel();
                ticketNums.setOpaque(false);

                // Add the number of tickets
                JLabel ticketNumbersReaming = new JLabel(numOfTickets, SwingConstants.CENTER);
                ticketNumbersReaming.setForeground(Color.BLACK);
                ticketNumbersReaming.setFont(font.deriveFont(32.0f));
                if(ticketTypes[i] == Ticket.Bus){
                    ticketNumbersReaming.setBorder(new EmptyBorder(0,0,0,0));
                    textOverlay.add(ticketNumbersReaming);
                } else {

                }
//                ticketNums.add(ticketNumbersReaming, BorderLayout.CENTER);
//                ticketNums.setBorder(new EmptyBorder(15, 0, 0, 0));
//                horzView.add(ticketNums);


//                vertView.add(horzView, BorderLayout.CENTER);
            }
        }
        gbc.weighty = 95;
        gbc.gridy = 0;
        tickets.add(vertView, BorderLayout.CENTER);
        //textOverlay.add(tickets, gbc);


        add(textOverlay, gbc);


        ImageIcon bgIcon = new ImageIcon(resource);
        background.setIcon(bgIcon);
        background.setOpaque(false);
        add(background, gbc);


        setOpaque(false);
    }
}
