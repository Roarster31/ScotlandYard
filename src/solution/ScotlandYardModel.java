package solution;

import scotlandyard.*;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

public class ScotlandYardModel extends ScotlandYard {

	private static final Colour MR_X_COLOUR = Colour.Black;

	private final List<Boolean> mRounds;
	private final ExtendedGraph mGraph;
	private final Map<Colour, PlayerHolder> mPlayerMap;
	private final List<Spectator> mSpectators;
	private final ArrayList<Colour> colourList;
	private final int mNumberOfDetectives;

	private Colour mCurrentPlayerColour = MR_X_COLOUR;
	private int mCurrentRound = 0;

	public ScotlandYardModel(int numberOfDetectives, List<Boolean> rounds, String graphFileName) throws IOException {
        super(numberOfDetectives, rounds, graphFileName);
		mRounds = rounds;
        final URL resource = getClass().getClassLoader().getResource(graphFileName);
        final String filename = URLDecoder.decode(resource.getFile());
        System.out.println("filename = " + filename);
        mGraph = new ExtendedGraph(new ScotlandYardGraphReader().readGraph(filename));
		mNumberOfDetectives = numberOfDetectives;
		mPlayerMap = new HashMap<Colour, PlayerHolder>();
		mSpectators = new ArrayList<Spectator>();
		colourList = new ArrayList<Colour>();

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
			// Add the ticket to MrX's stash if it wasn't Mr X who played
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
    public  List<Move> validMoves(Colour player) {
		final PlayerHolder playerHolder = mPlayerMap.get(player);

		int playerPos = playerHolder.getRealPosition();
        List<Edge<Integer, Route>> edges = mGraph.getConnectedEdges(new Node<Integer>(playerPos));

		List<Move> validMoves = new ArrayList<Move>();


		for(Edge<Integer, Route> firstEdge : edges){

			Integer firstNodePos = null;
			if(firstEdge.source() == playerPos){
				firstNodePos = firstEdge.target();
			}else if(firstEdge.target() == playerPos){
				firstNodePos = firstEdge.source();
			}

			MoveTicket firstMove;

			final Ticket firstTicket = Ticket.fromRoute(firstEdge.data());
			if(!detectiveOnNode(firstNodePos) && playerHolder.hasEnoughTickets(firstTicket)) {
				firstMove = new MoveTicket(player, firstNodePos, firstTicket);
				validMoves.add(firstMove);
			}else{
				continue;
			}


			//if we're dealing with Mr X
			if(player == MR_X_COLOUR){

				//then he has the possibility to use a secret move
				MoveTicket firstSecretMove = new MoveTicket(player, firstNodePos, Ticket.SecretMove);
				if(playerHolder.hasEnoughTickets(Ticket.SecretMove)) {
					validMoves.add(firstSecretMove);
				}

				//if he has double moves then we need to add those too
				if(playerHolder.hasEnoughTickets(Ticket.DoubleMove)) {

					List<Edge<Integer, Route>> secondaryEdges = mGraph.getConnectedEdges(new Node<Integer>(firstNodePos));

					for (Edge<Integer, Route> secondaryEdge : secondaryEdges) {

						Integer secondaryNode = null;
						if (secondaryEdge.source().equals(firstNodePos)) {
							secondaryNode = secondaryEdge.target();
						} else if (secondaryEdge.target().equals(firstNodePos)) {
							secondaryNode = secondaryEdge.source();
						}

						final Ticket secondTicket = Ticket.fromRoute(secondaryEdge.data());
						final MoveTicket secondMove = new MoveTicket(player, secondaryNode, secondTicket);
						MoveTicket secondSecretMove = new MoveTicket(player, secondaryNode, Ticket.SecretMove);

						if (firstMove.target == 2 && secondMove.target == 5) {
							player.name();
						}

						//so long as there isn't a detective on the chosen node
						if (!detectiveOnNode(secondaryNode)) {

							//and so long as Mr X has enough tickets
							if (playerHolder.hasEnoughTickets(playerHolder, firstTicket, secondTicket)) {
								validMoves.add(new MoveDouble(player, firstMove, secondMove));
							}

							if (playerHolder.hasEnoughTickets(playerHolder, firstTicket, Ticket.SecretMove)) {
								validMoves.add(new MoveDouble(player, firstMove, secondSecretMove));
							}

							if (playerHolder.hasEnoughTickets(playerHolder, Ticket.SecretMove, secondTicket)) {
								validMoves.add(new MoveDouble(player, firstSecretMove, secondMove));
							}

							if (playerHolder.hasEnoughTickets(playerHolder, Ticket.SecretMove, Ticket.SecretMove)) {
								validMoves.add(new MoveDouble(player, firstSecretMove, secondSecretMove));
							}
						}
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
				winningPlayers.addAll(mPlayerMap.keySet());
				winningPlayers.remove(MR_X_COLOUR);
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
			return playerHolder.getVisiblePosition();
		}else{
			return -1;
		}
    }
	// Get Map of Player Holders
	public Map<Colour, PlayerHolder> getPlayerHolders(){
		return mPlayerMap;
	}
	public void setPlayerName(Colour key, String name){
		PlayerHolder oldPlayer = mPlayerMap.get(key);
		PlayerHolder newPlayer = new PlayerHolder(oldPlayer.getPlayer(), key, oldPlayer.getRealPosition(), oldPlayer.getTickets());
		newPlayer.setName(name);
		// Replace
		mPlayerMap.remove(key);
		mPlayerMap.put(key, newPlayer);

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

	private boolean areAllDetectivesStuck(){
		for(Colour currentColour : mPlayerMap.keySet()){

			if(currentColour != MR_X_COLOUR) {
				if (canMove(currentColour)) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean canMove(Colour colour) {
		List<Move> moves = validMoves(colour);
		return moves.size() > 0 && !(moves.get(0) instanceof MovePass);
	}

	@Override
    public boolean isGameOver() {
        return isReady() && (areAllTurnsCompleted() || !canMove(MR_X_COLOUR) || areAllDetectivesStuck() || isDetectiveOnMrX());
    }

    private boolean isDetectiveOnMrX() {
        int mrXPos = mPlayerMap.get(MR_X_COLOUR).getRealPosition();
        for(Colour currentColour : mPlayerMap.keySet()){

            if(currentColour != MR_X_COLOUR) {
                if(getPlayerLocation(currentColour) == mrXPos){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean areAllTurnsCompleted() {
		return mCurrentRound >= (mRounds.size()-1)  && mCurrentPlayerColour == MR_X_COLOUR;
	}

	private boolean isMrXWinner(){
		return (areAllTurnsCompleted() || areAllDetectivesStuck()) && !isDetectiveOnMrX();
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
	return mCurrentRound;
    }

    @Override
    public List<Boolean> getRounds() {
        return mRounds;
    }
}
