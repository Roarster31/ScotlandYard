package solution.views;

import scotlandyard.Colour;
import scotlandyard.Ticket;
import solution.helpers.ColourHelper;
import solution.helpers.TicketHelper;
import solution.interfaces.GameControllerInterface;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;

/**
 * Created by benallen on 10/03/15.
 */
public class PlayerInfoColumn extends JPanel {
    private int maxTicketNumber = 0;
    private Colour currentPlayer;

    public PlayerInfoColumn(Colour currentPlayer, GameControllerInterface controllerInterface){


        this.currentPlayer = currentPlayer;
        // Check to see whether the passed player equals the current player
        if(controllerInterface.getCurrentPlayer() == currentPlayer) {
            //setupExtendedCols(currentPlayer, controllerInterface.getPlayerTickets(controllerInterface.getCurrentPlayer()));
        } else {
            //setupCols(currentPlayer, controllerInterface.getPlayerTickets(controllerInterface.getCurrentPlayer()));
        }
        setupCols(currentPlayer, controllerInterface.getPlayerTickets(currentPlayer));
    }

    private void setupCols(Colour currentPlayer, Map<Ticket, Integer> playerTickets) {
        setLayout(new GridBagLayout());
        Box vertView = Box.createVerticalBox();
        JPanel tickets = new JPanel();
        tickets.setOpaque(false);
        JPanel colour = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridy = 0;
        gbc.gridwidth = 100;
        gbc.weightx = 100;
        gbc.weighty = 5;
        colour.setBackground(ColourHelper.toColor(currentPlayer));
        colour.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
        colour.setOpaque(true);

        add(colour, gbc);

        // Max limit of tickets
        if(currentPlayer == Colour.Black){
            maxTicketNumber = 5;
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
                ticketNumbersReaming.setForeground(Color.LIGHT_GRAY);
                ticketNumbersReaming.setFont(ticketNumbersReaming.getFont().deriveFont(16.0f));
                ticketNums.add(ticketNumbersReaming, BorderLayout.CENTER);
                ticketNums.setBorder(new EmptyBorder(15, 0, 0, 0));
                horzView.add(ticketNums);

                // Add the ticket type
                JLabel ticketName = new JLabel("", SwingConstants.CENTER);
                ticketName.setIcon(TicketHelper.ticketToImg(ticketTypes[i]));

                // Add padding
                ticketName.setBorder(new EmptyBorder(5, 5, 5, 5));
                horzView.add(ticketName, BorderLayout.CENTER);

                vertView.add(horzView, BorderLayout.CENTER);
            }
        }
        gbc.weighty = 95;
        gbc.gridy = 1;
        tickets.add(vertView, BorderLayout.CENTER);
        add(tickets, gbc);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Retains the previous state
        Paint oldPaint = g2.getPaint();

        // Fills the circle with solid blue color
        g2.setColor(new Color(0x2c2c2c));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Adds shadows at the top
        Paint p;
        p = new GradientPaint(0, 0, new Color(0.0f, 0.0f, 0.0f, 0.4f),
                0, getHeight(), new Color(0.0f, 0.0f, 0.0f, 0.0f));
        g2.setPaint(p);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Adds highlights at the bottom 
        p = new GradientPaint(0, 0, new Color(1.0f, 1.0f, 1.0f, 0.0f),
                0, getHeight(), new Color(1.0f, 1.0f, 1.0f, 0.4f));
        g2.setPaint(p);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Adds oval inner highlight at the bottom
        p = new RadialGradientPaint(new Point2D.Double(getWidth() / 2.0,
                getHeight() * 1.5), getWidth() / 2.3f,
                new Point2D.Double(getWidth() / 2.0, getHeight() * 1.75 + 6),
                new float[] { 0.0f, 0.8f },
                new Color[] { new Color(81, 81, 81, 255),
                        new Color(40, 40, 40, 0) },
                RadialGradientPaint.CycleMethod.NO_CYCLE,
                RadialGradientPaint.ColorSpaceType.SRGB,
                AffineTransform.getScaleInstance(1.0, 0.5));
        g2.setPaint(p);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Restores the previous state
        g2.setPaint(oldPaint);
    }

//    private void setupExtendedCols(Colour currentPlayer, Map<Ticket, Integer> playerTickets){
//        Box horzViewMain = Box.createHorizontalBox();
//        JLabel colour = new JLabel(" HELLO ");
//        colour.setBackground(ColourHelper.toColor(currentPlayer));
//        colour.setOpaque(true);
//
//        horzViewMain.add(colour);
//
//        // Max limit of tickets
//        if(isMrX){
//            maxTicketNumber = 5;
//        } else {
//            maxTicketNumber = 3;
//        }
//
//        // Get players remaining tickets
//        Ticket[] ticketTypes = {Ticket.Bus, Ticket.Underground, Ticket.Taxi, Ticket.DoubleMove, Ticket.SecretMove};
//        String[] ticketNames = {"B", "U", "T", "DM", "SM"};
//        String[] ticketImgNames = {"bus.png", "underground.png", "taxi.png" , "doublemove.png", "secretmove.png"};
//        // Loop through all tickets
//        for (int i = 0; i < maxTicketNumber; i++) {
//            Box horzView = Box.createHorizontalBox();
//
//            // Add the number of tickets
//            String numOfTickets = String.valueOf(playerTickets.get(ticketTypes[i]));
//            JLabel ticketNumbersReaming = new JLabel(numOfTickets + ":");
//            horzView.add(ticketNumbersReaming);
//
//            // Add the ticket type
//            JLabel ticketName = new JLabel(ticketNames[i]);
//            horzView.add(ticketName);
//
//            horzViewMain.add(horzView);
//
//        }
//
//        add(horzViewMain);
//    }


}
