package solution.views;

import solution.Constants;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.adapters.GameUIAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * Created by rory on 09/03/15.
 */
public class MainFrame extends JFrame implements ActionListener {

    private static final String COMMAND_LOAD = "load";
    private static final String COMMAND_SAVE = "save";
    private PlayerCountLayout playerCountLayout;
    private final GameControllerInterface mControllerInterface;
    private GameLayout mGameLayout;


    public MainFrame(final GameControllerInterface controllerInterface) {

        mControllerInterface = controllerInterface;
        controllerInterface.addUpdateListener(new GameAdapter());

        JPanel mainFrame = (JPanel)getGlassPane();

        // Add in some end game views
        GameOverView endOfGame = new GameOverView(mControllerInterface);
        endOfGame.setPreferredSize(new Dimension(1000,800));
        endOfGame.setOpaque(false);
        endOfGame.setVisible(false);
        mainFrame.add(endOfGame);

        // Show the frame
        mainFrame.setVisible(true);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        playerCountLayout = new PlayerCountLayout(Constants.MIN_PLAYERS, Constants.MAX_PLAYERS);
        playerCountLayout.setListener(new PlayerCountLayout.PlayerCountListener() {
            @Override
            public void onPlayerCountDecided(int count) {
                controllerInterface.notifyAllPlayersAdded(count);
            }
        });
        getContentPane().add(playerCountLayout);

        createMenu();

        pack();
        setVisible(true);

    }

    private void createMenu() {
        //Where the GUI is created:
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;

        menuBar = new JMenuBar();

        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);

        menuItem = new JMenuItem("Load game",
                KeyEvent.VK_L);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_L, InputEvent.ALT_MASK));
        menuItem.setActionCommand(COMMAND_LOAD);
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem("Save game",
                KeyEvent.VK_S);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, InputEvent.ALT_MASK));
        menuItem.setActionCommand(COMMAND_SAVE);
        menuItem.addActionListener(this);
        menu.add(menuItem);

        setJMenuBar(menuBar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final JFileChooser fc = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir"));
        fc.setCurrentDirectory(workingDirectory);
        if(COMMAND_LOAD.equals(e.getActionCommand())){
            int returnVal = fc.showOpenDialog(MainFrame.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                System.out.println("Opening: " + file.getName() + ".");


                //Custom button text
                Object[] options = {"Yes, please",
                        "Nope"};
                int response = JOptionPane.showOptionDialog(MainFrame.this,
                        "Would you like to replay the saved game?",
                        "Replay",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);

                mControllerInterface.loadGame(file, response == JOptionPane.YES_OPTION);

            } else {
                System.out.println("Open command cancelled by user.");
            }
        }else if(COMMAND_SAVE.equals(e.getActionCommand())){
            int returnVal = fc.showSaveDialog(MainFrame.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                System.out.println("Saving: " + file.getName() + ".");

                mControllerInterface.saveGame(file);
            } else {
                System.out.println("Save command cancelled by user.");
            }
        }
    }

    class GameAdapter extends GameUIAdapter {
        @Override
        public void showGameInterface() {
            if(mGameLayout != null){
                remove(mGameLayout);
                mGameLayout = null;
            }else{
                setSize(new Dimension(1000, 800));
            }
            if(playerCountLayout != null){
                // Remove the player counter
                remove(playerCountLayout);
                playerCountLayout = null;
            }



            mGameLayout = new GameLayout(mControllerInterface);

            // Add the new the game in
            add(mGameLayout);




        }
    }

}
