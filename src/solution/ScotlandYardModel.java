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

	private static final Colour MR_X_COLOUR = Colour.Black;

	private static final ArrayList<Colour> COLOUR_LIST = new ArrayList<Colour>() {{
		add(Colour.Black);
		add(Colour.Blue);
		add(Colour.Red);
		add(Colour.Green);
		add(Colour.White);
		add(Colour.Yellow);
	}};

	private final List<Boolean> mRounds;
	private final ExtendedGraph mGraph;
	private final TreeMap<Colour, PlayerHolder> mPlayerMap;
	private final List<Spectator> mSpectators;
	private final int mNumberOfDetectives;
	private Colour mCurrentPlayerColour = MR_X_COLOUR;
	private int mCurrentRound = 0;
	public ScotlandYardModel(int numberOfDetectives, List<Boolean> rounds, String graphFileName) throws IOException {
        super(numberOfDetectives, rounds, graphFileName);
		mRounds = rounds;
		mGraph = new ExtendedGraph(new ScotlandYardGraphReader().readGraph(graphFileName));
		mNumberOfDetectives = numberOfDetectives;

		mPlayerMap = new TreeMap<Colour, PlayerHolder>();
		mSpectators = new ArrayList<Spectator>();

    }

	@Override
    protected Move getPlayerMove(Colour colour) {
        return mPlayerMap.get(colour).getMove(validMoves(colour));
    }

    @Override
    protected void nextPlayer() {
		final int pos = COLOUR_LIST.indexOf(mCurrentPlayerColour);
		mCurrentPlayerColour = (pos+1) < COLOUR_LIST.size() ? COLOUR_LIST.get(pos+1) : COLOUR_LIST.get(0);
    }

    @Override
    protected void play(MoveTicket move) {
		System.out.println("ticketmove");
		mPlayerMap.get(move.colour).setCurrentLocation(move.target);

		if(move.colour == MR_X_COLOUR){
			System.out.println("incrementing "+mCurrentRound);
			mCurrentRound++;
		}
		notifySpectators(move);
	}

	@Override
    protected void play(MoveDouble move) {
		notifySpectators(move);

		System.out.println("doublemove");
		for(Move innerMove : move.moves) {
			play(innerMove);
		}
    }

    @Override
    protected void play(MovePass move) {
		notifySpectators(move);
		//we do nothing right now
		System.out.println("passmove");
    }

	private void notifySpectators(final Move move) {
		for(Spectator spectator : mSpectators){
			spectator.notify(move);
		}
	}

    @Override
    protected List<Move> validMoves(Colour player) {
		int playerPos = mPlayerMap.get(player).getRealPosition();
        List<Edge<Integer, Route>> edges = mGraph.getConnectedEdges(new Node<Integer>(playerPos));

		List<Move> validMoves = new ArrayList<Move>();


		for(Edge<Integer, Route> primaryEdge : edges){

			Integer primaryNode = null;
			if(primaryEdge.source() == playerPos){
				primaryNode = primaryEdge.target();
			}else if(primaryEdge.target() == playerPos){
				primaryNode = primaryEdge.source();
			}

			final MoveTicket firstMove = new MoveTicket(player, primaryNode, Ticket.fromRoute(primaryEdge.data()));
			validMoves.add(firstMove);


			//if we're dealing with Mr X, he has double move cards
			if(player == MR_X_COLOUR){


				List<Edge<Integer, Route>> secondaryEdges = mGraph.getConnectedEdges(new Node<Integer>(primaryNode));

				for(Edge<Integer, Route> secondaryEdge : secondaryEdges) {

					Integer secondaryNode = null;
					if (secondaryEdge.source() == primaryNode) {
						secondaryNode = secondaryEdge.target();
					} else if (secondaryEdge.target() == primaryNode) {
						secondaryNode = secondaryEdge.source();
					}

					final MoveTicket secondMove = new MoveTicket(player, secondaryNode, Ticket.fromRoute(primaryEdge.data()));

					validMoves.add(new MoveDouble(player, firstMove, secondMove));
				}


			}

		}



		return validMoves;
    }

    @Override
    public void spectate(Spectator spectator) {
		mSpectators.add(spectator);
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
			if (colour != MR_X_COLOUR || mRounds.get(mCurrentRound)){
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
