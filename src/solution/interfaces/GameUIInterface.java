package solution.interfaces;

import scotlandyard.MoveTicket;

/**
 * Created by rory on 11/03/15.
 */
public interface GameUIInterface {
    public void showGameInterface();
    public void onGameModelUpdated(GameControllerInterface controllerInterface);
    public void animateMove(MoveTicket firstMove, MoveTicket secondMove);
}
