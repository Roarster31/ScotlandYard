package solution.helpers;

import scotlandyard.Move;
import scotlandyard.MoveDouble;
import scotlandyard.MoveTicket;
import scotlandyard.Ticket;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rory on 11/03/15.
 */
public class MoveHelper {

    private final List<Move> mMovesList;
    private ArrayList<Integer> mMoveEndLocationList;

    public MoveHelper (final List<Move> moves){
        mMovesList = moves;
    }

    public ArrayList<Integer> getFinalMovePositions(){
        if(mMoveEndLocationList == null){
            generateMoveEndLocationList();
        }
        return mMoveEndLocationList;
    }

    private void generateMoveEndLocationList() {
        mMoveEndLocationList = new ArrayList<Integer>();
        for(Move move : mMovesList){
            MoveTicket moveTicket = getFinalMove(move);
            if(moveTicket != null){
                mMoveEndLocationList.add(moveTicket.target);
            }
        }
    }

    public static MoveTicket getFinalMove(Move move){
        if(move instanceof MoveTicket){
            return (MoveTicket) move;
        }else if(move instanceof MoveDouble){
            MoveDouble doubleMove = (MoveDouble) move;
            return getFinalMove(doubleMove.moves.get(doubleMove.moves.size()-1));
        }else{
            System.err.println("Need to implement the case for a MovePass");
            return null;
        }
    }

    public void clear() {
        mMovesList.clear();
        if(mMoveEndLocationList != null){
            mMoveEndLocationList.clear();
        }else{
            mMoveEndLocationList = new ArrayList<Integer>();
        }
    }

    public static ArrayList<Ticket> moveListToTicketList(ArrayList<MoveTicket> moves){
        ArrayList<Ticket> tickets = new ArrayList<Ticket>();
        for(MoveTicket moveTicket : moves){
            tickets.add(moveTicket.ticket);
        }
        return tickets;
    }
}
