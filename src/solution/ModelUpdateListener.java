package solution;

import scotlandyard.MoveTicket;

import java.util.List;

/**
 * Created by rory on 10/03/15.
 */
public interface ModelUpdateListener {
    public void onWaitingOnPlayer(ScotlandYardModel model, List<MoveTicket> mrXMoves);
}
