package solution.views;

import scotlandyard.Colour;
import scotlandyard.Ticket;
import solution.helpers.ColourHelper;
import solution.interfaces.GameControllerInterface;

import javax.swing.*;
import java.util.Map;

/**
 * Created by benallen on 10/03/15.
 */
public class PlayerInfoColumn extends JPanel {
    private Box vertView;
    private boolean isMrX;
    private int maxTicketNumber = 0;
    private Colour currentPlayer;
    public PlayerInfoColumn(Colour currentPlayer, GameControllerInterface controllerInterface){
        vertView = Box.createVerticalBox();
        this.currentPlayer = currentPlayer;
        // Check to see whether the passed player equals the current player
        if(controllerInterface.getCurrentPlayer() == currentPlayer) {
            //setupExtendedCols(currentPlayer);
        } else {
            //setupCols(currentPlayer);
        }
        setupCols(currentPlayer, controllerInterface.getPlayerTickets(controllerInterface.getCurrentPlayer()));
    }

    private void setupCols(Colour currentPlayer, Map<Ticket, Integer> playerTickets) {
        JLabel colour = new JLabel(" hello ");
        colour.setBackground(ColourHelper.toColor(currentPlayer));
        colour.setOpaque(true);
        //colour.setMinimumSize(new Dimension(300,200));

        vertView.add(colour);

        // Max limit of tickets
        if(isMrX){
            maxTicketNumber = 5;
        } else {
            maxTicketNumber = 3;
        }

        // Get players remaining tickets
        Ticket[] ticketTypes = {Ticket.Bus, Ticket.Underground, Ticket.Taxi, Ticket.DoubleMove, Ticket.SecretMove};
        String[] ticketNames = {"B", "U", "T", "DM", "SM"};

        // Loop through all tickets
        for (int i = 0; i < maxTicketNumber; i++) {
            Box horzView = Box.createHorizontalBox();

            // Add the number of tickets
            String numOfTickets = String.valueOf(playerTickets.get(ticketTypes[i]));
            JLabel ticketNumbersReaming = new JLabel(numOfTickets + ":");
            horzView.add(ticketNumbersReaming);

            // Add the ticket type
            JLabel ticketName = new JLabel(ticketNames[i]);
            horzView.add(ticketName);

            vertView.add(horzView);

        }

        add(vertView);
    }

    private void setupExtendedCols(Colour currentPlayer, Map<Ticket, Integer> playerTickets){
        Box horzViewMain = Box.createHorizontalBox();
        JLabel colour = new JLabel(" HELLO ");
        colour.setBackground(ColourHelper.toColor(currentPlayer));
        colour.setOpaque(true);
        colour.setSize(200,40);

        horzViewMain.add(colour);

        // Max limit of tickets
        if(isMrX){
            maxTicketNumber = 5;
        } else {
            maxTicketNumber = 3;
        }

        // Get players remaining tickets
        Ticket[] ticketTypes = {Ticket.Bus, Ticket.Underground, Ticket.Taxi, Ticket.DoubleMove, Ticket.SecretMove};
        String[] ticketNames = {"B", "U", "T", "DM", "SM"};

        // Loop through all tickets
        for (int i = 0; i < maxTicketNumber; i++) {
            Box horzView = Box.createHorizontalBox();

            // Add the number of tickets
            String numOfTickets = String.valueOf(playerTickets.get(ticketTypes[i]));
            JLabel ticketNumbersReaming = new JLabel(numOfTickets + ":");
            horzView.add(ticketNumbersReaming);

            // Add the ticket type
            JLabel ticketName = new JLabel(ticketNames[i]);
            horzView.add(ticketName);

            horzViewMain.add(horzView);

        }

        add(vertView);
    }

    public void setMrX(boolean isMrX) {
        this.isMrX = isMrX;
    }
}
