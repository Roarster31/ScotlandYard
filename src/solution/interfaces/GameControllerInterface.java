package solution.interfaces;

import scotlandyard.*;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by rory on 11/03/15.
 */
public interface GameControllerInterface {
    public void addUpdateListener(GameUIInterface listener);
    public void removeUpdateListener(GameUIInterface mListener);

    public Colour getCurrentPlayer();
    public Map<Ticket,Integer> getPlayerTickets(Colour currentPlayer);
    public List<Colour> getPlayerList();
    List<MoveTicket> getValidSingleMovesAtLocation(Colour currentPlayer, int location);
    List<MoveTicket> getValidSecondMovesAtLocation(Colour currentPlayer, int location, Ticket firstTicket);
    public int getCurrentPlayerRealPosition();
    public void notifyAllPlayersAdded(final int count);

    public void notifyMapLoaded();
    public void notifyMoveAnimationFinished();
    public void notifyMoveSelected(final Move move);
    public List<MoveTicket> getMrXHistory();
    public void saveGame(File fileLocation);
    public Set<Colour> getWinningPlayers();

    public void loadGame(File fileLocation, boolean replay);
    public boolean isGameOver();

    public int getPlayerFacadePosition(Colour colour);

}
