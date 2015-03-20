package solution.views;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by rory on 10/03/15.
 */
@Deprecated
public class PlayerCountLayout extends JPanel {

    PlayerCountListener mListener;

    public interface PlayerCountListener {
        public void onPlayerCountDecided(final int count);
    }
    public PlayerCountLayout (final int minPlayers, final int maxPlayers) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        final int arraySize = maxPlayers - minPlayers + 1;
        String[] values = new String[arraySize];

        for(int i=0; i<arraySize; i++){
            values[i] = String.valueOf(minPlayers+i);
        }

        SpinnerListModel valueModel = new SpinnerListModel(values);
        final JSpinner spinner = new JSpinner(valueModel);

        JLabel spinnerLabel = new JLabel();
        spinnerLabel.setText("No. of players: ");

        JPanel spinnerLayout = new JPanel();
        spinnerLayout.setLayout(new BoxLayout(spinnerLayout, BoxLayout.X_AXIS));

        spinnerLayout.add(spinnerLabel);
        spinnerLayout.add(spinner);


        JButton doneButton = new JButton();
        doneButton.setText("Done");
        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(mListener != null){
                    mListener.onPlayerCountDecided(Integer.valueOf((String)spinner.getValue()));
                }
            }
        });

        add(spinnerLayout);
        add(doneButton);
    }

    public void setListener(PlayerCountListener listener){
        mListener = listener;
    }
}
