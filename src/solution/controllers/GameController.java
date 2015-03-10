package solution.controllers;

import scotlandyard.Move;
import scotlandyard.Player;
import scotlandyard.ScotlandYard;
import solution.ScotlandYardModel;
import solution.views.MainFrame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rory on 10/03/15.
 */
public class GameController implements MainFrame.MainFrameListener, Player{

    private final MainFrame mMainFrame;
    private ScotlandYard model;

    public GameController(MainFrame mainFrame){
        mMainFrame = mainFrame;

        mMainFrame.setMainFrameListener(this);

    }

    @Override
    public void onPlayersAdded(List<String> names) {
        setupModel(names);
        mMainFrame.showGameUI();
    }

    private void setupModel(List<String> names) {
        try {
            model = new ScotlandYardModel(names.size(), getRounds(), "graph.txt");

//            for(String name : names){
//                model.join(this, SetupHelper.getTickets(false))
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Boolean> getRounds() {
        List<Boolean> rounds = new ArrayList<Boolean>();
        rounds.add(false);
        rounds.add(false);
        rounds.add(true);
        rounds.add(false);
        rounds.add(false);
        return rounds;
    }

    @Override
    public Move notify(int location, List<Move> list) {
        return null;
    }
}
