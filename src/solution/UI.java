package solution;

import scotlandyard.Colour;
import scotlandyard.Move;
import scotlandyard.Ticket;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

/**
 * Created by benallen on 04/03/15.
 */
public class UI {
    private JFrame window;
    GameLogic gl;
    public void init(){
        window = new JFrame();
        window.setDefaultCloseOperation(window.EXIT_ON_CLOSE);
        window.setLocationByPlatform(true);
        window.setTitle("Scotland Yard");
        window.setSize(1018, 809);
        gl = new GameLogic();

    }
    public void hideScreen(){
        window.setVisible(false);
    }
    public void showScreen(){
        window.setVisible(true);
    }
    public void showGame() {
    }

    public void showMenuScreen() {
        System.out.printf("Showing Menu Screen");
        JPanel menuScreen = new JPanel();
        Box segment = Box.createHorizontalBox();

        // Add each player
        final Box[] playerDetails = new Box[6];
        JLabel[] playerLbl = new JLabel[6];
        JTextField[] nameTxt = new JTextField[6];
        JTextField[] colourTxt = new JTextField[6];
        JButton[] beginBtn = new JButton[6];
        Border[] freeSpaceOf = new Border[6];
        for (int i = 0; i < 6; i++) {
            freeSpaceOf[i] = BorderFactory.createEmptyBorder(10,10,10,10);
            playerDetails[i] = Box.createVerticalBox();
            // Add the label into the middle element
            if(i == 3){
                JLabel queryLbl = new JLabel();
                queryLbl.setText("Please Enter the players:");
                playerDetails[i].add(queryLbl);
            }
            // Create a labels, textfield and button
            playerLbl[i] = new JLabel();
            playerLbl[i].setText("Player " + (i + 1) + ":");
            playerDetails[i].add(playerLbl[i]);
            colourTxt[i] = new JTextField("");
            colourTxt[i].setSize(300, 50);
            playerDetails[i].add(colourTxt[i]);
            nameTxt[i] = new JTextField("");
            nameTxt[i].setSize(300, 50);
            playerDetails[i].add(nameTxt[i]);
            beginBtn[i] = new JButton();
            beginBtn[i].setText("+ Add");
            beginBtn[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    gl.joinPlayer("player name", "black");
                }
            });
            if(i == 3) {
                JButton submitBtn = new JButton();
                submitBtn.setText("Submit");
                beginBtn[i].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        gl.initGame();
                    }
                });
            }
            playerDetails[i].add(beginBtn[i]);
            playerDetails[i].setBorder(freeSpaceOf[i]);
            segment.add(playerDetails[i]);
        }

        menuScreen.add(segment);

        window.add(menuScreen);


    }

    public void showMapScreen() {
    }

    public void showPlayersValidMoves(Colour playerKey, List<Move> validMoves) {
    }

    public void showPlayerCards(Colour playerKey, Map<Ticket, Integer> playerTickets) {
    }

    public void showPlayerNotifForTurn(Colour currentPlayer) {
    }
}
