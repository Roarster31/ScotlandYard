package solution.views;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by rory on 09/03/15.
 */
public class MainFrame extends JFrame {

    private final PlayerManagementLayout mManagementFrame;
    MainFrameListener mainFrameListener;
    private GameLayout mGameLayout;

    public void showGameUI() {
        remove(mManagementFrame);
        mGameLayout = new GameLayout();
        add(mGameLayout);
        pack();
    }

    public interface MainFrameListener {
        public void onPlayersAdded(List<String> names);
    }

    public MainFrame () {
        //here we can add our layouts

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

//        GraphView graphView = new GraphView("map.jpg", new GraphData("pos.txt", GraphData.DataFormat.STANDARD));
//
//        graphView.setMainFrameListener(new GraphView.GraphViewListener() {
//            @Override
//            public void onNodeClicked(int nodeId) {
//                System.out.println("clicked on node with id "+nodeId);
//            }
//        });
//
//        ArrayList<Integer> availablePositions = new ArrayList<Integer>();
//
//        availablePositions.add(127);
//        availablePositions.add(115);
//        availablePositions.add(126);
//        availablePositions.add(133);
//        availablePositions.add(134);
//        availablePositions.add(116);
//
//        graphView.setAvailablePositions(availablePositions);
//
//        getContentPane().add(graphView, BorderLayout.CENTER);

        mManagementFrame = new PlayerManagementLayout(new PlayerManagementLayout.PlayerManagementListener() {
            @Override
            public void onPlayersChanged() {
                pack();
            }

            @Override
            public void onAllPlayersAdded(List<String> nameList) {
                mainFrameListener.onPlayersAdded(nameList);
            }
        });

        getContentPane().add(mManagementFrame, BorderLayout.CENTER);

        pack();

        setVisible(true);
    }

    public void setMainFrameListener(final MainFrameListener mainFrameListener){
        this.mainFrameListener = mainFrameListener;
    }
}
