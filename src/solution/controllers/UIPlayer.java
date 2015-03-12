package solution.controllers;

import scotlandyard.Move;
import scotlandyard.Player;

import java.util.List;

/**
 * Created by rory on 11/03/15.
 */
public class UIPlayer implements Player {
    private Move mPendingMove;

    public void setPendingMove(Move pendingMove) {
        this.mPendingMove = pendingMove;
    }

    @Override
    public Move notify(int location, List<Move> list) {
        for(Move move : list){
            if(move.equals(mPendingMove)){
                return move;
            }
        }
        System.err.println("Something's gone wrong (with null move)");
        return null;
    }
}
