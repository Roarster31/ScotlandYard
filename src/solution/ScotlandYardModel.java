package solution;

import scotlandyard.Colour;
import scotlandyard.Edge;
import scotlandyard.Move;
import scotlandyard.MoveDouble;
import scotlandyard.MovePass;
import scotlandyard.MoveTicket;
import scotlandyard.Node;
import scotlandyard.Player;
import scotlandyard.Route;
import scotlandyard.ScotlandYard;
import scotlandyard.ScotlandYardGraphReader;
import scotlandyard.Spectator;
import scotlandyard.Ticket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * alright, alright, alright
 * ...
 * this, is confusing, but I think I understand
 * ...
 * it looks like the equivalent to a primary key for players is their colour
 * so we'll use that, maybe in a list
 */
public class ScotlandYardModel extends ScotlandYard {

	private static final Colour MrXColour = Colour.Black;

	private final List<Boolean> mRounds;
	private final ExtendedGraph mGraph;
	private final TreeMap<Colour, PlayerHolder> mPlayerMap;
	private final int mNumberOfDetectives;
	private Colour mCurrentPlayerColour = MrXColour;
	private int mCurrentRound = 0;
	public ScotlandYardModel(int numberOfDetectives, List<Boolean> rounds, String graphFileName) throws IOException {
        super(numberOfDetectives, rounds, graphFileName);
		mRounds = rounds;
		mGraph = new ExtendedGraph(new ScotlandYardGraphReader().readGraph(graphFileName));
		mNumberOfDetectives = numberOfDetectives;

		mPlayerMap = new TreeMap<Colour, PlayerHolder>();

    }

	@Override
    protected Move getPlayerMove(Colour colour) {
        return mPlayerMap.get(colour).getMove(validMoves(colour));
    }

    @Override
    protected void nextPlayer() {
		mCurrentPlayerColour = mPlayerMap.get(ColourHelper.nextColour(mCurrentPlayerColour)).getColour();
    }

    @Override
    protected void play(MoveTicket move) {
		System.out.println("ticketmove");
		mPlayerMap.get(move.colour).setCurrentLocation(move.target);

		if(move.colour == MrXColour){
			System.out.println("incrementing "+mCurrentRound);
			mCurrentRound++;
		}
    }

    @Override
    protected void play(MoveDouble move) {
		System.out.println("doublemove");
		for(Move innerMove : move.moves) {
			play(innerMove);
		}
    }

    @Override
    protected void play(MovePass move) {
		//we do nothing right now
		System.out.println("passmove");
    }

    @Override
    protected List<Move> validMoves(Colour player) {
		int playerPos = mPlayerMap.get(player).getRealPosition();
        List<Edge<Integer, Route>> edges = mGraph.getConnectedEdges(new Node<Integer>(playerPos));

		List<Move> validMoves = new ArrayList<Move>();


		for(Edge<Integer, Route> edge : edges){
			if(edge.source() == playerPos){
				validMoves.add(new MoveTicket(player, edge.target(), Ticket.fromRoute(edge.data())));
			}else if(edge.target() == playerPos){
				validMoves.add(new MoveTicket(player, edge.source(), Ticket.fromRoute(edge.data())));
			}
		}


		return validMoves;
    }

    @Override
    public void spectate(Spectator spectator) {

    }

    @Override
    public boolean join(Player player, Colour colour, int location, Map<Ticket, Integer> tickets) {
		mPlayerMap.put(colour, new PlayerHolder(player, colour, location, tickets));
		//not sure what to return - not in javadocs
        return false;
    }

    @Override
    public List<Colour> getPlayers() {
		List<Colour> colourList = new ArrayList<Colour>();
		colourList.addAll(mPlayerMap.keySet());
		return colourList;
    }

    @Override
    public Set<Colour> getWinningPlayers() {
        return null;
    }

    @Override
    public int getPlayerLocation(Colour colour) {

		PlayerHolder playerHolder = mPlayerMap.get(colour);

		if(playerHolder != null) {
			if (colour != MrXColour || mRounds.get(mCurrentRound)){
				playerHolder.updateVisiblePosition();
			}

			return playerHolder.getVisiblePosition();
		}else{
			return -1;
		}
    }

    @Override
    public int getPlayerTickets(Colour colour, Ticket ticket) {
		PlayerHolder playerHolder = mPlayerMap.get(colour);

		if(playerHolder != null) {
			return playerHolder.getTickets().get(ticket);
		}else{
			return -1;
		}
    }

    @Override
    public boolean isGameOver() {
        return false;
    }

    @Override
    public boolean isReady() {
        return mPlayerMap.size() == mNumberOfDetectives + 1;
    }

    @Override
    public Colour getCurrentPlayer() {
        return mCurrentPlayerColour;
    }

    @Override
    public int getRound() {
		//ok, so if a round is true, then MrX has to show his location at this point
	return mCurrentRound;
    }

    @Override
    public List<Boolean> getRounds() {
        return mRounds;
    }
}
