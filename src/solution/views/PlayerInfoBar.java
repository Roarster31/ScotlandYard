package solution.views;

import scotlandyard.Colour;
import solution.interfaces.GameControllerInterface;

import javax.swing.*;
import java.util.List;

/**
 * Created by benallen on 10/03/15.
 */
public class PlayerInfoBar extends JPanel {
    Box horzView;
    public PlayerInfoBar(GameControllerInterface controllerInterface){
        horzView = Box.createHorizontalBox();

        List<Colour> allPlayers = controllerInterface.getPlayerList();
        PlayerInfoColumn[] playerColumns = new PlayerInfoColumn[allPlayers.size()];



        for(int i = 0; i < allPlayers.size(); i++){
            Colour currentPlayer = allPlayers.get(i);
            playerColumns[i] = new PlayerInfoColumn(currentPlayer, controllerInterface);

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
