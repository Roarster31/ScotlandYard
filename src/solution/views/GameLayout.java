package solution.views;

import solution.Models.GraphData;

import javax.swing.*;

/**
 * Created by rory on 10/03/15.
 */
public class GameLayout extends JPanel {

    public GameLayout () {
        add(new GraphView("map.jpg", new GraphData("pos.txt", GraphData.DataFormat.STANDARD)));
    }
}
