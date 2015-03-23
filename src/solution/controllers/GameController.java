package solution.controllers;

import scotlandyard.*;
import solution.Constants;
import solution.Models.GameRecordTracker;
import solution.ScotlandYardModel;
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
    private Set<GameUIInterface> listeners;
    private MrXHistoryTracker mrXHistoryTracker;
    private UIPlayer uiPlayer;
    private GameRecordTracker gameRecordTracker;
    private boolean replayingGame;

    public List<MoveTicket> getMrXHistory() {
        return mrXHistoryTracker.getMoveHistory();
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
            resetGameData();
            model = gameRecordTracker.load(fileLocation, uiPlayer);

            model.spectate(mrXHistoryTracker);

            replayingGame = replay;

            notifyModelUpdated();

            if(!replay) {
                tryNextTrackerMove();
            }

            for(GameUIInterface uiInterface : listeners){
                uiInterface.showGameInterface();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetGameData() {
        mrXHistoryTracker = new MrXHistoryTracker();
        gameRecordTracker = new GameRecordTracker();
        uiPlayer = new UIPlayer();
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
        listeners = new HashSet<GameUIInterface>();
        mrXHistoryTracker = new MrXHistoryTracker();
        gameRecordTracker = new GameRecordTracker();
        uiPlayer = new UIPlayer();
    }
    public void addUpdateListener(GameUIInterface listener){
        listeners.add(listener);
    }

    public void removeUpdateListener(GameUIInterface listener){
        listeners.remove(listener);
    }





    private void setupModel(final int playerCount) {
        try {
            resetGameData();
            model = new ScotlandYardModel(playerCount-1, getRounds(), "graph.txt");

            model.spectate(mrXHistoryTracker);

            ArrayList<Integer> playerLocations = new ArrayList<Integer>();
            int maxNodeNum = model.getGraph().getNodes().size();
            System.out.println("maxNodeNum = " + maxNodeNum);
            Random random = new Random();
            for (int i = 0; i < playerCount; i++) {
                //todo do proper location
                final Colour colour = ColourHelper.getColour(i);
                int location = random.nextInt(maxNodeNum);
                while(playerLocations.contains(location)){
                    location = random.nextInt(maxNodeNum);
                }
                playerLocations.add(location);

                model.join(uiPlayer, colour, location, SetupHelper.getTickets(colour.equals(Constants.MR_X_COLOUR)));
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
        if(currentPlayer == Constants.MR_X_COLOUR){
            System.out.println();
        }

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
    public void notifyMapLoaded() {
        if(gameRecordTracker.getCurrentMove() != null && replayingGame) {
            tryNextTrackerMove();
        }
    }

    private void tryNextTrackerMove() {
        Move currentMove = gameRecordTracker.getCurrentMove();
        if(currentMove != null){
                if(replayingGame){
                    notifyMoveSelected(currentMove);
                }else{
                    uiPlayer.setPendingMove(currentMove);
                    model.turn();
                    gameRecordTracker.nextMove();
                    tryNextTrackerMove();
                }
        }else{
            replayingGame = false;
        }
    }

    @Override
    public void notifyMoveSelected(Move move) {
        uiPlayer.setPendingMove(move);

        if(move instanceof MovePass){
            notifyMoveAnimationFinished();
        }else {
            notifyAnimateMove(move);
        }

    }

    private void notifyAnimateMove(Move move) {
        MoveTicket firstMove = null;
        MoveTicket secondMove = null;

        if(move instanceof MoveTicket){
            firstMove = (MoveTicket) move;
        }else if(move instanceof MoveDouble){
            firstMove = (MoveTicket) ((MoveDouble)move).moves.get(0);
            secondMove = (MoveTicket) ((MoveDouble)move).moves.get(1);
        }

        for(GameUIInterface gameUIInterface : listeners){
            gameUIInterface.animateMove(firstMove, secondMove);
        }
    }

    @Override
    public void notifyMoveAnimationFinished() {

        model.turn();


            notifyModelUpdated();

            // Is the game over?
            if(isGameOver()){
                System.out.printf("The game is over ):");
            }

        if(gameRecordTracker.getCurrentMove() != null && replayingGame){
            gameRecordTracker.nextMove();
            tryNextTrackerMove();
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
                }
            }
        }

        public List<MoveTicket> getMoveHistory() {
            return moveHistory;
        }

    }

}
