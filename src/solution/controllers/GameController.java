package solution.controllers;

import scotlandyard.*;
import solution.ModelUpdateListener;
import solution.ScotlandYardModel;
import solution.helpers.ColourHelper;
import solution.helpers.SetupHelper;
import solution.views.GraphView;
import solution.views.MainFrame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rory on 10/03/15.
 */
public class GameController implements MainFrame.MainFrameListener, Player, GraphView.GraphViewListener{

    private final MainFrame mMainFrame;
    private ScotlandYardModel model;
    private List<ModelUpdateListener> listeners;
    private int mSelectedNode;

    public GameController(MainFrame mainFrame){
        mMainFrame = mainFrame;
        listeners = new ArrayList<ModelUpdateListener>();
        mMainFrame.setMainFrameListener(this);
        addModelUpdateListener(mMainFrame.getGameLayout());
        mMainFrame.getGameLayout().setGameListener(this);
    }

    public void addModelUpdateListener(ModelUpdateListener listener){
        listeners.add(listener);
    }

    @Override
    public void onPlayersAdded(final int count) {
        setupModel(count);
        mMainFrame.showGameUI();
    }

    private void setupModel(final int playerCount) {
        try {
            model = new ScotlandYardModel(playerCount-1, getRounds(), "graph.txt");

            for (int i = 0; i < playerCount; i++) {
                //todo do proper location
                final Colour colour = ColourHelper.getColour(i);
                model.join(this, colour, new Random().nextInt(190), SetupHelper.getTickets(colour.equals(Colour.Black)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        notifyUpdateListeners();
    }

    private void notifyUpdateListeners() {
        for(ModelUpdateListener listener : listeners){
            listener.onWaitingOnPlayer(model);
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
        for(Move move : list){
            if(move instanceof MoveTicket){
                MoveTicket moveTicket = (MoveTicket) move;
                if(moveTicket.target == mSelectedNode){
                    return move;
                }
            }else if(move instanceof MoveDouble){
                MoveDouble doubleMove = (MoveDouble) move;
                Move finalMove = doubleMove.moves.get(doubleMove.moves.size()-1);
                if(finalMove instanceof MoveTicket){
                    MoveTicket moveTicket = (MoveTicket) finalMove;
                    if(moveTicket.target == mSelectedNode){
                        return move;
                    }
                }

            }
        }
        return null;
    }

    @Override
    public void onNodeClicked(int nodeId) {
        mSelectedNode = nodeId;
        boolean nodeFound = false;
        for(Move move : model.validMoves(model.getCurrentPlayer())){
            if(move instanceof MoveTicket){
                MoveTicket ticket = (MoveTicket) move;
                if(ticket.target == nodeId){
                    nodeFound = true;
                    break;
                }
            }else if(move instanceof MoveDouble){
                MoveDouble doubleMove = (MoveDouble) move;
                Move finalMove = doubleMove.moves.get(doubleMove.moves.size()-1);
                if(finalMove instanceof MoveTicket){
                    MoveTicket ticket = (MoveTicket) finalMove;
                    if(ticket.target == nodeId){
                        nodeFound = true;
                        break;
                    }
                }

            }
        }
        if(!nodeFound){
            return;
        }
        System.out.println("mSelectedNode = " + mSelectedNode);
        model.turn();
        notifyUpdateListeners();
    }
}
