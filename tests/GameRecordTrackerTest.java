import junit.framework.TestCase;
import scotlandyard.Colour;
import scotlandyard.Move;
import scotlandyard.Player;
import solution.Models.GameRecordTracker;
import solution.Models.ScotlandYardModel;
import solution.helpers.ColourHelper;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class GameRecordTrackerTest extends TestCase {

    public void testNoReplay() throws Exception {

        GameRecordTracker tracker = new GameRecordTracker();

        ScotlandYardModel game = (ScotlandYardModel) TestHelper.getGame(1);


        TestHelper.addMrxToGame(game, TestHelper.getDoubleMovePlayer(), 1);
        TestHelper.addDetectiveToGame(game, TestHelper.getSingleMovePlayer(), Colour.Blue, 3);

        tracker.track(game);

        while (!game.isGameOver()) {
            game.turn();
        }

        tracker.save(new File("testsave"), game);


        GameRecordTracker tracker2 = new GameRecordTracker();

        LoadedPlayer player = new LoadedPlayer();

        ScotlandYardModel loadedGame = tracker2.load(new File("testsave"), player);

        while (tracker2.getCurrentMove() != null) {
            player.setMove(tracker2.getCurrentMove());
            loadedGame.turn();
            tracker2.nextMove();
        }

        assertEquals("Loaded game must be on the same round as the original", game.getRound(), loadedGame.getRound());
        assertEquals("Loaded game must have the same number of rounds as the original", game.getRounds(), loadedGame.getRounds());
        assertEquals("Loaded game must have the same current player as the original", game.getCurrentPlayer(), loadedGame.getCurrentPlayer());
        assertEquals("Loaded game be in the same ready state as the original", game.isReady(), loadedGame.isReady());
        assertEquals("Loaded game be in the same gameover state as the original", game.isGameOver(), loadedGame.isGameOver());
        assertEquals("Loaded game be in the same gameover state as the original", game.isGameOver(), loadedGame.isGameOver());


        for (Colour colour : game.getPlayers()) {
            assertEquals("Loaded game " + ColourHelper.toString(colour) + "'s tickets must be the same as the original", game.getAllPlayerTickets(colour), loadedGame.getAllPlayerTickets(colour));
            assertEquals("Loaded player positions are not the same after save and load", game.getPlayerLocation(colour), loadedGame.getPlayerLocation(colour));
        }


    }

    class LoadedPlayer implements Player {

        private Move move;

        @Override
        public Move notify(int location, List<Move> list) {
            return move;
        }

        public void setMove(Move move) {
            this.move = move;
        }
    }

    public void testReplay() throws Exception {

        GameRecordTracker tracker = new GameRecordTracker();

        ScotlandYardModel game = (ScotlandYardModel) TestHelper.getGame(1);


        TestHelper.addMrxToGame(game, TestHelper.getDoubleMovePlayer(), 1);
        TestHelper.addDetectiveToGame(game, new TestHelper.TestPlayer(), Colour.Blue, 3);

        tracker.track(game);

        HashMap<Integer, HashMap<Colour, Integer>> initialPositionMap = new HashMap<Integer, HashMap<Colour, Integer>>();

        HashMap<Colour, Integer> positions = new HashMap<Colour, Integer>();
        for(Colour colour : game.getPlayers()){
            positions.put(colour, game.getPlayerLocation(colour));
        }

        initialPositionMap.put(game.getRound(), positions);

        while(!game.isGameOver()) {

            game.turn();

            positions = new HashMap<Colour, Integer>();
            for(Colour colour : game.getPlayers()){
                positions.put(colour, game.getPlayerLocation(colour));
            }

            initialPositionMap.put(game.getRound(), positions);

        }

        tracker.save(new File("testsave"), game);


        GameRecordTracker tracker2 = new GameRecordTracker();

        LoadedPlayer player = new LoadedPlayer();

        ScotlandYardModel loadedGame = tracker2.load(new File("testsave"), player);

        HashMap<Integer, HashMap<Colour, Integer>> loadedPositionMap = new HashMap<Integer, HashMap<Colour, Integer>>();

        HashMap<Colour, Integer> currentPositions = new HashMap<Colour, Integer>();
        for(Colour colour : loadedGame.getPlayers()){
            currentPositions.put(colour, loadedGame.getPlayerLocation(colour));
        }

        loadedPositionMap.put(loadedGame.getRound(), currentPositions);

        while (tracker2.getCurrentMove() != null) {
            player.setMove(tracker2.getCurrentMove());
            loadedGame.turn();

            currentPositions = new HashMap<Colour, Integer>();
            for(Colour colour : loadedGame.getPlayers()){
                currentPositions.put(colour, loadedGame.getPlayerLocation(colour));
            }

            loadedPositionMap.put(loadedGame.getRound(), currentPositions);

            tracker2.nextMove();
        }

        assertEquals("Game round positions are incorrect", initialPositionMap, loadedPositionMap);

    }

}