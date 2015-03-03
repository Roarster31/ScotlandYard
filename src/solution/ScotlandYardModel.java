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
import java.util.*;

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

	private ArrayList<Colour> colourList = new ArrayList<Colour>();

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
		final int pos = colourList.indexOf(mCurrentPlayerColour);
		mCurrentPlayerColour = (pos+1) < mPlayerMap.size() ? colourList.get(pos+1) : colourList.get(0);
    }

    @Override
    protected void play(MoveTicket move) {
		mPlayerMap.get(move.colour).setCurrentLocation(move.target);

		// Decrease the ticket that has been used by the player
		Map<Ticket, Integer> playerTickets = mPlayerMap.get(move.colour).getTickets();
		playerTickets.put(move.ticket, playerTickets.get(move.ticket) - 1);

		if(move.colour == MR_X_COLOUR){
			mCurrentRound++;
		} else {
			// Add the ticket to MrX stash
			Map<Ticket, Integer> mrXTickets = mPlayerMap.get(MR_X_COLOUR).getTickets();
			mrXTickets.put(move.ticket, mrXTickets.get(move.ticket) + 1);
		}

		if (move.colour == MR_X_COLOUR && !mRounds.get(mCurrentRound)){
			move = new MoveTicket(MR_X_COLOUR, getPlayerLocation(MR_X_COLOUR), move.ticket);
		}
		notifySpectators(move);
	}

	@Override
    protected void play(MoveDouble move) {
		notifySpectators(move);

		for(Move innerMove : move.moves) {
			play(innerMove);
		}
    }

    @Override
    protected void play(MovePass move) {
		notifySpectators(move);
		//we do nothing right now
    }

	private void notifySpectators(final Move move) {
		for(Spectator spectator : mSpectators){
			spectator.notify(move);
		}
	}

    @Override
    protected List<Move> validMoves(Colour player) {
		final PlayerHolder playerHolder = mPlayerMap.get(player);

		int playerPos = playerHolder.getRealPosition();
        List<Edge<Integer, Route>> edges = mGraph.getConnectedEdges(new Node<Integer>(playerPos));

		List<Move> validMoves = new ArrayList<Move>();


		for(Edge<Integer, Route> primaryEdge : edges){

			Integer primaryNode = null;
			if(primaryEdge.source() == playerPos){
				primaryNode = primaryEdge.target();
			}else if(primaryEdge.target() == playerPos){
				primaryNode = primaryEdge.source();
			}

			MoveTicket firstMove = null;

			final Ticket firstTicket = Ticket.fromRoute(primaryEdge.data());
			if(!detectiveOnNode(primaryNode) && playerHolder.hasEnoughTickets(firstTicket)) {
				firstMove = new MoveTicket(player, primaryNode, firstTicket);
				validMoves.add(firstMove);
			}else{
				continue;
			}


			//if we're dealing with Mr X, he has double move cards
			if(player == MR_X_COLOUR && playerHolder.hasEnoughTickets(Ticket.DoubleMove)){

				MoveTicket firstSecretMove = new MoveTicket(player, primaryNode, Ticket.SecretMove);
				if(playerHolder.hasEnoughTickets(Ticket.SecretMove)) {
					validMoves.add(firstSecretMove);
				}

				List<Edge<Integer, Route>> secondaryEdges = mGraph.getConnectedEdges(new Node<Integer>(primaryNode));

				for(Edge<Integer, Route> secondaryEdge : secondaryEdges) {


					Integer secondaryNode = null;
					if (secondaryEdge.source() == primaryNode) {
						secondaryNode = secondaryEdge.target();
					} else if (secondaryEdge.target() == primaryNode) {
						secondaryNode = secondaryEdge.source();
					}




					final Ticket secondTicket = Ticket.fromRoute(secondaryEdge.data());
					final MoveTicket secondMove = new MoveTicket(player, secondaryNode, secondTicket);
					MoveTicket secondSecretMove = new MoveTicket(player, secondaryNode, Ticket.SecretMove);


					if(firstMove.target == 2 && secondMove.target == 5){
						player.name();
					}



					if(!detectiveOnNode(secondaryNode)) {

						if(playerHolder.hasEnoughTickets(playerHolder, firstTicket, secondTicket)) {
							validMoves.add(new MoveDouble(player, firstMove, secondMove));
						}

						if(playerHolder.hasEnoughTickets(playerHolder, firstTicket, Ticket.SecretMove)) {
							validMoves.add(new MoveDouble(player, firstMove, secondSecretMove));
						}

						if(playerHolder.hasEnoughTickets(playerHolder, Ticket.SecretMove, secondTicket)) {
							validMoves.add(new MoveDouble(player, firstSecretMove, secondMove));
						}

						if(playerHolder.hasEnoughTickets(playerHolder, Ticket.SecretMove, Ticket.SecretMove)) {
							validMoves.add(new MoveDouble(player, firstSecretMove, secondSecretMove));
						}

					}else{
						continue;
					}






				}


			}

		}

		// If no possible moves, then return a pass
		if(validMoves.size() == 0 && player != MR_X_COLOUR){
			validMoves.add(new MovePass(player));
		}

		return validMoves;
    }
	/**
	 * returns whether or not there is a detective on the node or not
	 * @param node
	 * @return
	 */
	private boolean detectiveOnNode(Integer node){
		for(Colour colour : mPlayerMap.keySet()){
			PlayerHolder playerHolder = mPlayerMap.get(colour);
			if(playerHolder.getColour() != MR_X_COLOUR && playerHolder.getVisiblePosition() == node){
				return true;
			}
		}
		return false;
	}





    @Override
    public void spectate(Spectator spectator) {
		mSpectators.add(spectator);
    }

    @Override
    public boolean join(Player player, Colour colour, int location, Map<Ticket, Integer> tickets) {
		mPlayerMap.put(colour, new PlayerHolder(player, colour, location, tickets));
		// Add the current player to the colour list
		colourList.add(colour);
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
		Set<Colour> winningPlayers = new HashSet<Colour>();
		if(isGameOver()) {
			if (isMrXWinner()) {
				winningPlayers.add(MR_X_COLOUR);
			} else {
				for (Colour currentColour : mPlayerMap.keySet()) {
					if (currentColour != MR_X_COLOUR) {
						winningPlayers.add(currentColour);
					}
				}

			}
		}
        return winningPlayers;
    }

    @Override
    public int getPlayerLocation(Colour colour) {

		PlayerHolder playerHolder = mPlayerMap.get(colour);

		if(playerHolder != null) {
			if (colour != MR_X_COLOUR || mRounds.get(mCurrentRound)){
				playerHolder.updateVisiblePosition();
			}

			if(colour == MR_X_COLOUR){
				System.out.println();
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
	private boolean isAllDetectivesStuck(){
		for(Colour currentColour : mPlayerMap.keySet()){

			if(currentColour != MR_X_COLOUR) {
				List<Move> moves = validMoves(currentColour);
				if (cannotMove(moves)) {
					continue;
				} else {
					return false;
				}
			}

		}
		return true;
	}

	private boolean cannotMove(List<Move> moves) {
		return moves.size() == 0 || (moves.size() == 1 && moves.get(0) instanceof MovePass);
	}

	@Override
    public boolean isGameOver() {
        return isReady() && (mPlayerMap.size() <= 1 || (mCurrentRound >= (mRounds.size()-1) && mCurrentPlayerColour == MR_X_COLOUR) || cannotMove(validMoves(MR_X_COLOUR)) || isAllDetectivesStuck());
    }

	private boolean isMrXWinner(){
		return mCurrentRound >= mRounds.size() || isAllDetectivesStuck();
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
