package solution.controllers;

import scotlandyard.*;
import solution.Constants;
import solution.Models.GameRecordTracker;
import solution.Models.ScotlandYardModel;
import solution.helpers.ColourHelper;
import solution.helpers.SetupHelper;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.GameUIInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by rory on 10/03/15.
 */
public class GameController implements GameControllerInterface {

    private ScotlandYardModel model;
    private List<GameUIInterface> listeners;
    private int mSelectedNode;
    private MrXHistoryTracker mrXHistoryTracker;
    private UIPlayer uiPlayer;
    private final GameRecordTracker gameRecordTracker;
    private int screenToDisplay = 0; // 0 = intro, 1 = add players, 2 = gameplay, 3 = gameover

    public List<MoveTicket> getMrXHistory() {
        return mrXHistoryTracker.getMoveHistory();
    }

    @Override
    public void manageScreenProgression(int screenKey){
        screenToDisplay = screenKey;

    }
    @Override
    public void saveGame(File fileLocation) {
        try {
            gameRecordTracker.save(fileLocation, model);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean isGameOver(){
        return model.isGameOver();
    }
    @Override
    public Set<Colour> getWinningPlayers() {
        return model.getWinningPlayers();
    }

    @Override
    public void loadGame(File fileLocation, boolean replay) {
        try {
            model = gameRecordTracker.load(fileLocation, uiPlayer, replay);

            model.spectate(mrXHistoryTracker);

            if(replay){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(gameRecordTracker.hasNextMove()){

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            gameRecordTracker.playNextMove();

                            notifyModelUpdated();


                        }
                    }
                }).start();
            }

            //parts of the ui rely on the model being created so this has to
            //come after setupModel
            listeners.get(0).showGameInterface();
            notifyModelUpdated();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Edge<Integer, Route>> getGraphRoutes() {
        return model.getGraph().getEdges();
    }

    @Override
    public int getPlayerFacadePosition(Colour colour) {
        return model.getPlayerLocation(colour);
    }

    public GameController(){
        listeners = new ArrayList<GameUIInterface>();
        mrXHistoryTracker = new MrXHistoryTracker();
        uiPlayer = new UIPlayer();
        gameRecordTracker = new GameRecordTracker();
    }
    public void addUpdateListener(GameUIInterface listener){
        listeners.add(listener);
    }

    private void setupModel(final int playerCount) {
        try {
            model = new ScotlandYardModel(playerCount-1, getRounds(), "graph.txt");

            model.spectate(mrXHistoryTracker);

            for (int i = 0; i < playerCount; i++) {
                //todo do proper location
                final Colour colour = ColourHelper.getColour(i);
                model.join(uiPlayer, colour, new Random().nextInt(190), SetupHelper.getTickets(colour.equals(Constants.MR_X_COLOUR)));
            }

            gameRecordTracker.track(model);
        } catch (IOException e) {
            e.printStackTrace();
        }
        notifyModelUpdated();
    }

    private void notifyModelUpdated() {
        for(GameUIInterface gameInterface : listeners) {
            gameInterface.onGameModelUpdated(this);
        }
    }

    public Colour getCurrentPlayer(){
        return model.getCurrentPlayer();
    }
    public Map<Ticket, Integer> getPlayerTickets(Colour currentPlayer){
        return model.getAllPlayerTickets(currentPlayer);
    }

    @Override
    public List<Colour> getPlayerList() {
        return model.getPlayers();
    }

    @Override
    public List<MoveTicket> getValidSingleMovesAtLocation(Colour currentPlayer, int location) {
        return model.getAvailableSingleMoves(model.getGraph(), currentPlayer, location, model.getAllPlayerTickets(currentPlayer));
    }

    @Override
    public List<MoveTicket> getValidSecondMovesAtLocation(Colour currentPlayer, int location, Ticket firstTicket) {
        Map<Ticket, Integer> playerTickets = new HashMap<Ticket, Integer>(model.getAllPlayerTickets(currentPlayer));
        playerTickets.put(firstTicket, playerTickets.get(firstTicket)-1);
        return model.getAvailableSingleMoves(model.getGraph(), currentPlayer, location, playerTickets);
    }

    @Override
    public int getCurrentPlayerRealPosition() {
        return model.getRealPlayerLocation(model.getCurrentPlayer());
    }

    @Override
    public void notifyAllPlayersAdded(int count) {
        setupModel(count);

        //parts of the ui rely on the model being created so this has to
        //come after setupModel

        for(GameUIInterface uiInterface : listeners){
            uiInterface.showGameInterface();
        }

    }

    @Override
    public void notifyMoveSelected(Move move) {
        uiPlayer.setPendingMove(move);
        model.turn();
        notifyModelUpdated();

        // Is the game over?
        if(isGameOver()){
            System.out.printf("The game is over ):");
        }
    }

    public List<Colour> getPlayers(){
        return model.getPlayers();
    }
    public static List<Boolean> getRounds() {
        List<Boolean> rounds = new ArrayList<Boolean>();
        rounds.add(false);
        rounds.add(false);
        rounds.add(true);
        rounds.add(false);
        rounds.add(false);
        rounds.add(false);
        rounds.add(true);
        rounds.add(false);
        rounds.add(false);
        rounds.add(false);
        rounds.add(true);
        rounds.add(false);
        return rounds;
    }



    class MrXHistoryTracker implements Spectator {

        List<MoveTicket> moveHistory;

        public MrXHistoryTracker () {
            moveHistory = new ArrayList<MoveTicket>();
        }

        @Override
        public void notify(Move move) {
            if(move.colour == Constants.MR_X_COLOUR){
                if(move instanceof MoveTicket){
                    moveHistory.add((MoveTicket) move);
                }else if(move instanceof MoveDouble){
                    for(Move subMove : ((MoveDouble) move).moves){
                        notify(subMove);
                    }
                }
            }
        }

        public List<MoveTicket> getMoveHistory() {
            return moveHistory;
        }

    }

}
