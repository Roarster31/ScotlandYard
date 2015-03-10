package solution.views;

import com.sun.deploy.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by rory on 10/03/15.
 */
public class PlayerManagementView extends JPanel {

    private final JTextField mNameTextField;
    private final JLabel mNameTextLabel;
    private boolean playerAdded;

    public String getTextFieldName() {
        return mNameTextLabel.getText();
    }

    public interface PlayerManagementViewListener {
        public void onPlayerAdd();
        public void onPlayerRemove(PlayerManagementView view);
    }

    public PlayerManagementView (final PlayerManagementViewListener listener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        final JPanel nameDataFrame = new JPanel();
        nameDataFrame.setLayout(new BoxLayout(nameDataFrame, BoxLayout.X_AXIS));

        final JLabel nameLabel = new JLabel();
        nameLabel.setText("Name:");

        mNameTextField = new JTextField(1);
        mNameTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                mNameTextField.setBackground(Color.WHITE);
            }
        });

        mNameTextLabel = new JLabel();
        mNameTextLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));


        final JButton actionButton = new JButton();
        actionButton.setText("Add");
        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!playerAdded) {
                    if (validationsPass()) {
                        mNameTextLabel.setText(mNameTextField.getText());
                        mNameTextLabel.setVisible(true);
                        mNameTextField.setVisible(false);
                        playerAdded = true;
                        listener.onPlayerAdd();
                        actionButton.setText("Remove");
                    }
                }else{
                    listener.onPlayerRemove(PlayerManagementView.this);
                }
            }
        });

        nameDataFrame.add(nameLabel);
        nameDataFrame.add(mNameTextField);
        nameDataFrame.add(mNameTextLabel);

        add(nameDataFrame);
        add(actionButton);
    }

    private boolean validationsPass() {
        final boolean longEnough = StringUtils.trimWhitespace(mNameTextField.getText()).length() > 0;
        if(!longEnough){
            mNameTextField.setBackground(Color.MAGENTA);
            return false;
        }else {
            return true;
        }
    }
}
