package solution.views;

import com.sun.deploy.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rory on 10/03/15.
 */
@Deprecated
public class PlayerManagementLayout extends JPanel implements PlayerManagementView.PlayerManagementViewListener {

    private static final int REQUIRED_PLAYER_COUNT = 2;
    private final PlayerManagementListener mListener;
    private int playerViewCount;

    public PlayerManagementLayout(final PlayerManagementListener listener) {
        mListener = listener;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel();
        title.setText("Add players");

        JButton doneButton = new JButton();
        doneButton.setText("Done");
        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(validateFields()){

                    ArrayList<String> nameList = new ArrayList<String>();

                    for(int i=0; i<getComponentCount(); i++){
                        Component component = getComponent(i);
                        if(component instanceof PlayerManagementView){
                            PlayerManagementView managementView = (PlayerManagementView) component;
                            final String name = managementView.getTextFieldName();

                            if(name != null && !StringUtils.trimWhitespace(name).equals("")) {
                                nameList.add(name);
                            }
                            listener.onAllPlayersAdded(nameList);
                        }
                    }

                }
            }
        });
        add(doneButton);

        addPlayerView();
    }

    private boolean validateFields() {
        if(playerViewCount > REQUIRED_PLAYER_COUNT){
            return true;
        }else{
            JOptionPane.showMessageDialog(this, "You must add at least "+REQUIRED_PLAYER_COUNT+" players");
            return false;
        }
    }

    private void addPlayerView() {
        PlayerManagementView playerView = new PlayerManagementView(this);
        add(playerView, getComponentCount()-1);
        playerViewCount++;
    }

    @Override
    public void onPlayerAdd() {
        //add another player
        addPlayerView();
        mListener.onPlayersChanged();
    }

    @Override
    public void onPlayerRemove(PlayerManagementView view) {
        remove(view);
        playerViewCount--;
        if (playerViewCount == 0) {
            addPlayerView();
        }
        mListener.onPlayersChanged();
    }

    public interface PlayerManagementListener {
        public void onPlayersChanged();
        public void onAllPlayersAdded(List<String> nameList);
    }
}
