package solution;

import scotlandyard.Colour;
import scotlandyard.ScotlandYard;

import javax.swing.*;
import java.util.Set;

/**
 * Created by benallen on 04/03/15.
 */
@Deprecated
public class InterfaceInterrupts {
    public InterfaceInterrupts(){

    }
    public static void checkGameUpdates(ScotlandYard game){
        // Check for end of game
        if(game.isGameOver()){
            endGame(game);
        }
        // Check for enough players
        if (game.getPlayers().size() <= 1){
            notEnoughPlayers();
        }
    }
    private static JPanel notEnoughPlayers(){
        JPanel container = new JPanel();

        JLabel title = new JLabel("Not Enough Players!",JLabel.CENTER);
        title.setVerticalTextPosition(JLabel.CENTER);
        title.setHorizontalTextPosition(JLabel.CENTER);

        JLabel info = new JLabel("You need atleast two players", JLabel.CENTER);

        info.setVerticalTextPosition(JLabel.CENTER);
        info.setHorizontalTextPosition(JLabel.CENTER);
        container.add(title);
        container.add(info);
        return container;
    }
    private static JPanel endGame(ScotlandYard game){
        JPanel container = new JPanel();

        JLabel title = new JLabel("End of game",JLabel.CENTER);
        title.setVerticalTextPosition(JLabel.CENTER);
        title.setHorizontalTextPosition(JLabel.CENTER);

        Set<Colour> winningPlayers = game.getWinningPlayers();
        JLabel whoWins = new JLabel("Noone has won yet", JLabel.CENTER);
        if(winningPlayers.size() == 1) {
            whoWins.setText("Mr X Wins the game!");
        } else {
            whoWins.setText("The Detectives win the game!");
        }
        whoWins.setVerticalTextPosition(JLabel.CENTER);
        whoWins.setHorizontalTextPosition(JLabel.CENTER);
        container.add(title);
        container.add(whoWins);
        return container;
    }
}
