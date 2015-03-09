package solution;

import scotlandyard.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by benallen on 04/03/15.
 */
public class GameLogic {
    private ScotlandYardModel mGame;
    private List<Boolean> mRounds;
    private List<PlayerInfo> playersDetails;

    int[] mrXTicketNumbers = { 4, 3, 3, 2, 5 };
    int[] detectiveTicketNumbers = { 11, 8, 4, 0, 0 };
    Ticket[] tickets = { Ticket.Taxi, Ticket.Bus,
            Ticket.Underground, Ticket.DoubleMove,
            Ticket.SecretMove };
    private boolean gameRunning;
    private int lastFpsTime;
    private int fps;
    public GameLogic(){

//        UI.showMenuScreen();
//
//        // Add players
//        joinPlayer("Ben", "black");
//        joinPlayer("Steve" , "Red");
//        joinPlayer("Rory","yellow");
//
//        UI.showMapScreen();
//
//        // Let them choose their starting point
//        chooseStartingPoint(Colour.Black, 11);
//        chooseStartingPoint(Colour.Red, 14);
//        chooseStartingPoint(Colour.Yellow, 18);
//
//        // Then init the game
//        initGame();
//        UI.showGame();
//        List<Move> moves;
//        UI.showPlayerCards(Colour.Black, getPlayerTickets(Colour.Black));
//
//        // Loop here:
//        while(true) {
//            // Show the players current moves
//            UI.showPlayersValidMoves(mGame.getCurrentPlayer(), getValidMoves(Colour.Black));
//
//            // Ask the player to enter a move
//            UI.showPlayerNotifForTurn(mGame.getCurrentPlayer());
//
//
//
//
//            // MrxPlays a move
//
//            // Next player
//            mGame.nextPlayer();
//        }
    }
    public void main(String[] args) {
        UI sc = new UI();
        sc.init();
        sc.showMenuScreen();
        sc.showScreen();
    }
    public void gameLoop()
    {
        long lastLoopTime = System.nanoTime();
        final int TARGET_FPS = 60;
        final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

        // keep looping round til the game ends
        while (gameRunning)
        {
            // work out how long its been since the last update, this
            // will be used to calculate how far the entities should
            // move this loop
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            lastLoopTime = now;
            double delta = updateLength / ((double)OPTIMAL_TIME);

            // update the frame counter
            lastFpsTime += updateLength;
            fps++;

            // update our FPS counter if a second has passed since
            // we last recorded
            if (lastFpsTime >= 1000000000)
            {
                System.out.println("(FPS: "+fps+")");
                lastFpsTime = 0;
                fps = 0;
            }

            // update the game logic
            //doGameUpdates(delta);

            // we want each frame to take 10 milliseconds, to do this
            // we've recorded when we started the frame. We add 10 milliseconds
            // to this and then factor in the current time to give
            // us our final value to wait for
            // remember this is in ms, whereas our lastLoopTime etc. vars are in ns.
            try{Thread.sleep( (lastLoopTime-System.nanoTime() + OPTIMAL_TIME)/1000000 );} catch (InterruptedException e) {
                e.printStackTrace();
            } ;
        }
    }

    // Called to begin the game after all players have been added in
    public void initGame(){
        if(playersDetails.size() > 0) {

            // Add in some rounds
            mRounds = Arrays.asList(false, false, true, false);

            // Try and create the game
            try {
                mGame = new ScotlandYardModel(playersDetails.size() - 1, mRounds, "graph.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Add in all the players
            for (PlayerInfo playerInfo : playersDetails) {
                Player player = new TestPlayer();
                if (playerInfo.getColour() == Colour.Black) {
                    mGame.join(player, Colour.Black, playerInfo.getStartingLocation(), getTickets(true));
                    mGame.setPlayerName(Colour.Black, playerInfo.getName());
                } else {
                    mGame.join(player, playerInfo.getColour(), playerInfo.getStartingLocation(), getTickets(false));
                    mGame.setPlayerName(playerInfo.getColour(), playerInfo.getName());
                }
            }
        } else {
            System.out.println("There needs to be atleast one player before the game can be initated. Run GameLogic.joinPlayer()");
        }

    }
    // Called when a player is added
    public void joinPlayer(String name, String colour){
        String col = colour.toLowerCase();
        if(name != "" && col != "") {
            PlayerInfo newPlayer = new PlayerInfo();
            newPlayer.setName(name);

            // Set colour
            if (col.equals("black")) {
                newPlayer.setColour(Colour.Black);
            } else if (col.equals("red")) {
                newPlayer.setColour(Colour.Red);
            } else if (col.equals("green")) {
                newPlayer.setColour(Colour.Green);
            } else if (col.equals("blue")) {
                newPlayer.setColour(Colour.Blue);
            } else if (col.equals("white")) {
                newPlayer.setColour(Colour.White);
            } else if (col.equals("yellow")) {
                newPlayer.setColour(Colour.Yellow);
            }
            // Add in player
            playersDetails.add(newPlayer);

        } else {
            System.out.println("Details are missing to add player need a name:'"+name+"' (String) and colour:'"+colour+"' (String)");
        }

    }
    // Called when a player picks the starting location
    public void chooseStartingPoint(Colour playerColour, int location){
        if(playersDetails.size() > 0) {
            for (PlayerInfo player : playersDetails) {
                if (player.getColour() == playerColour) {
                    PlayerInfo tempPlayer = player;
                    tempPlayer.setStartingLocation(location);
                    playersDetails.remove(player);
                    playersDetails.add(tempPlayer);
                }
            }
        } else {
            System.out.println("You need to run GameLogic.joinPlayer() at least once before you can run this command");
        }
    }
    public void playAMove(int target, String ticketType){
        MoveTicket theMove = null;
        if(ticketType.equals("taxi")){
            theMove = new MoveTicket(mGame.getCurrentPlayer(), target, Ticket.Taxi);
        } else if (ticketType.equals("underground")){
            theMove = new MoveTicket(mGame.getCurrentPlayer(), target, Ticket.Underground);
        } else if(ticketType.equals("secret")){
            theMove = new MoveTicket(mGame.getCurrentPlayer(), target, Ticket.SecretMove);
        } else if(ticketType.equals("doublemove")){
            theMove = new MoveTicket(mGame.getCurrentPlayer(), target, Ticket.DoubleMove);
        } else if(ticketType.equals("Bus")){
            theMove = new MoveTicket(mGame.getCurrentPlayer(), target, Ticket.Bus);
        }
        mGame.play(theMove);
        // Player has finished turn
        mGame.turn();
    }
    // Create the rounds
    private void setRounds(List<Boolean> rounds){
        mRounds = rounds;
    }

    // Get a players playerHolder where all there details are stored
    private PlayerHolder getPlayerHolder(Colour playerColour){
        return mGame.getPlayerHolders().get(playerColour);
    }
    // Get the tickets of a player
    private Map<Ticket, Integer> getPlayerTickets(Colour key){
        PlayerHolder temp = getPlayerHolder(key);
        return temp.getTickets();
    }
    private List<Move> getValidMoves(Colour player){
        return mGame.validMoves(player);
    }
    private Map<Ticket, Integer> getTickets(boolean mrX)
    {

        Map<Ticket, Integer> tickets = new HashMap<Ticket, Integer>();
        for (int i = 0; i < this.tickets.length; i++) {
            if(mrX)
                tickets.put(this.tickets[i], mrXTicketNumbers[i]);
            else
                tickets.put(this.tickets[i], detectiveTicketNumbers[i]);
        }
        return tickets;
    }

    private class TestPlayer implements Player {
        public List<Move> moves;
        public int location;

        public Move notify(int location, List<Move> moves) {
            this.moves = moves;
            this.location = location;
            if(moves.size() > 0)
                return this.moves.get(0);
            return null;
        }
    }
}
