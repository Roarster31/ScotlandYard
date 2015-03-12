package solution.Models;

import scotlandyard.Colour;
import scotlandyard.Move;
import scotlandyard.Ticket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rory on 12/03/15.
 */
public class SaveData {

    HashMap<Colour, Integer> startPositions;
    HashMap<Colour, Map<Ticket, Integer>> tickets;
    String graphName;
    Move[] movesList;
    List<Boolean> rounds;
    List<Colour> colourList;

    public SaveData(Move[] moves, List<Boolean> rounds, List<Colour> colourList, String graphName, HashMap<Colour, Map<Ticket, Integer>> tickets, HashMap<Colour, Integer> startPositions){
        movesList = moves;
        this.rounds = rounds;
        this.colourList = colourList;
        this.graphName = graphName;
        this.tickets = tickets;
        this.startPositions = startPositions;
    }



    public Move[] getMovesList() {
        return movesList;
    }

    public void setMovesList(Move[] movesList) {
        this.movesList = movesList;
    }

    public List<Boolean> getRounds() {
        return rounds;
    }

    public void setRounds(List<Boolean> rounds) {
        this.rounds = rounds;
    }

    public List<Colour> getColourList() {
        return colourList;
    }

    public void setColourList(List<Colour> colourList) {
        this.colourList = colourList;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }

    public HashMap<Colour, Map<Ticket, Integer>> getTickets() {
        return tickets;
    }

    public void setTickets(HashMap<Colour, Map<Ticket, Integer>> tickets) {
        this.tickets = tickets;
    }

    public HashMap<Colour, Integer> getStartPositions() {
        return startPositions;
    }

    public void setStartPositions(HashMap<Colour, Integer> startPositions) {
        this.startPositions = startPositions;
    }

    @Override
    public String toString() {
        return "SaveData{" +
                "graphName='" + graphName + '\'' +
                ", movesList=" + Arrays.toString(movesList) +
                ", rounds=" + rounds +
                ", colourList=" + colourList +
                '}';
    }
}
