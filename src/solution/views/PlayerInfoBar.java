package solution.views;

import scotlandyard.Colour;
import solution.controllers.GameController;

import javax.swing.*;
import java.util.List;

/**
 * Created by benallen on 10/03/15.
 */
public class PlayerInfoBar extends JPanel {
    Box horzView;
    GameController gameControllerListener;
    public PlayerInfoBar(){
        horzView = Box.createHorizontalBox();

    }

    public void setGameControllerListener(GameController gameControllerListener) {
        this.gameControllerListener = gameControllerListener;

        setupColumns(gameControllerListener);
    }

    private void setupColumns(GameController gameControllerListener) {
        List<Colour> allPlayers = gameControllerListener.getPlayers();
        PlayerInfoColumn[] playerColumns = new PlayerInfoColumn[allPlayers.size()];



        for(int i = 0; i < allPlayers.size(); i++){
            Colour currentPlayer = allPlayers.get(i);
            playerColumns[i] = new PlayerInfoColumn(currentPlayer);
            playerColumns[i].setGameControllerListener(gameControllerListener);

            if(currentPlayer == Colour.Black) {
                playerColumns[i].setMrX(true);
            } else {
                playerColumns[i].setMrX(false);
            }

            horzView.add(playerColumns[i]);
        }


        add(horzView);
    }
}
