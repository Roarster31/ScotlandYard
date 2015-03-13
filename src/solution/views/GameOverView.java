package solution.views;

import scotlandyard.Colour;
import solution.interfaces.GameControllerInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Set;

/**
 * Created by benallen on 12/03/15.
 */
public class GameOverView extends JPanel {
    private GameControllerInterface mController;
    public GameOverView(GameControllerInterface controllerInterface) {

        // Create some containers
        JPanel container = new JPanel();
        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new GridBagLayout());

        // Make the Grid layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridwidth = gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        mController = controllerInterface;

        // Style the title label
        JLabel title = new JLabel("End of game", JLabel.CENTER);
        title.setVerticalTextPosition(JLabel.CENTER);
        title.setHorizontalTextPosition(JLabel.CENTER);
        title.setFont(title.getFont().deriveFont(32.0f));
        title.setForeground(Color.WHITE);

        // Get the winning players and make a label
        Set<Colour> winningPlayers = mController.getWinningPlayers();
        JLabel whoWins = new JLabel("No-one has won yet", JLabel.CENTER);

        // Decide on the text content
        if (winningPlayers == null){

        } else if(winningPlayers.size() == 1) {
            whoWins.setText("Mr X Wins the game!");
        } else if(winningPlayers.size() > 0) {
            whoWins.setText("The Detectives win the game!");
        }
        // Style the who wins text
        whoWins.setVerticalTextPosition(JLabel.CENTER);
        whoWins.setHorizontalTextPosition(JLabel.CENTER);
        whoWins.setFont(title.getFont().deriveFont(32.0f));
        whoWins.setForeground(Color.WHITE);

        //Add in the text containers
        centerContainer.add(title,gbc);
        gbc.gridy = 1;
        centerContainer.add(whoWins,gbc);

        // Style the centered container
        centerContainer.setBounds(0, 0, 200, 200);
        centerContainer.setOpaque(false);
        centerContainer.setBorder(new EmptyBorder(20,20,20,20));

        // Style the outer container
        container.add(centerContainer);
        container.setBackground(new Color(0, 0, 0, 0.7f) );

        // Add in the final container
        add(container);
    }
}
