package solution.views;

import solution.Models.GraphData;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by rory on 09/03/15.
 */
public class MainFrame extends JFrame {

    public MainFrame () {
        //here we can add our layouts

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        GraphView graphView = new GraphView("map.jpg", new GraphData("pos.txt", GraphData.DataFormat.STANDARD));

        graphView.setListener(new GraphView.GraphViewListener() {
            @Override
            public void onNodeClicked(int nodeId) {
                System.out.println("clicked on node with id "+nodeId);
            }
        });

        ArrayList<Integer> availablePositions = new ArrayList<Integer>();

        availablePositions.add(127);
        availablePositions.add(115);
        availablePositions.add(126);
        availablePositions.add(133);
        availablePositions.add(134);
        availablePositions.add(116);

        graphView.setAvailablePositions(availablePositions);

        getContentPane().add(graphView, BorderLayout.CENTER);

        pack();

        setVisible(true);
    }
}
