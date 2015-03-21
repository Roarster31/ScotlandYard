package solution.views;

import scotlandyard.Colour;
import solution.Constants;
import solution.interfaces.GameControllerInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Set;

/**
 * Created by benallen on 12/03/15.
 */
public class GameOverView {
    private int mWidth = 100;
    private int mHeight = 100;
    private String mOutput = "";

    public GameOverView(GameControllerInterface controllerInterface, int width, int height) {
        mWidth = width;
        mHeight = height;

        // Get the winning players and make a label
        Set<Colour> winningPlayers = controllerInterface.getWinningPlayers();
        mOutput = "No-one has won yet";

        // Decide on the text content
        if (winningPlayers == null){

        } else if(winningPlayers.contains(Constants.MR_X_COLOUR)) {
            mOutput = "Mr X Wins!";
        } else if(winningPlayers.size() > 0) {
            mOutput = "The Detectives win!";
        }

    }
    public void draw(Graphics2D g2d){

        // Draw Background
        g2d.setColor(new Color(0,0,0,0.7f));
        g2d.fillRect(mWidth / 4, mHeight / 4, mWidth / 2, mHeight / 2);

        // Draw Text
        g2d.setFont(g2d.getFont().deriveFont(40.0f));
        FontMetrics fm = g2d.getFontMetrics();
        int w = fm.stringWidth(mOutput);
        int w2 = fm.stringWidth("End of Game");
        g2d.setColor(Color.WHITE);
        g2d.drawString("End of Game", (mWidth / 2) - (w2 / 2), (mHeight / 2) - 50);
        g2d.drawString(mOutput, (mWidth / 2) - (w / 2), (mHeight / 2));

    }
}
