package solution.Models;

import com.google.gson.*;
import com.sun.deploy.util.StringUtils;
import scotlandyard.*;
import solution.Constants;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.*;

/**
 * Created by rory on 12/03/15.
 */
public class GameRecordTracker implements Spectator {

    private final ArrayList<Move> mMoveList;
    private final HashMap<Colour, Integer> startPositions;

    private int mCurrentPosInQueue;
    private Move[] mQueuedMoves;
    private ScotlandYardModel mModel;
    private int mIgnoreMoveCount;

    public GameRecordTracker(){
        mMoveList = new ArrayList<Move>();
        startPositions = new HashMap<Colour, Integer>();
    }

    /**
     * be sure to call this after all players have been added to the model
     * but before any moves have been made
     */
    public void track(ScotlandYardModel model) {
        mModel = model;
        model.spectate(this);

        startPositions.clear();
        for(Colour colour : model.getPlayers()){
            startPositions.put(colour, model.getRealPlayerLocation(colour));
        }
    }

    public void save(File fileLocation, ScotlandYardModel model) throws FileNotFoundException, UnsupportedEncodingException {

        Move[] array = new Move[mMoveList.size()];

        for (int i = 0; i < mMoveList.size(); i++) {
            array[i] = mMoveList.get(i);
        }

        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(Move.class, new MoveClassAdapter());

        HashMap<Colour, Map<Ticket, Integer>> tickets = new HashMap<Colour, Map<Ticket, Integer>>();

        for(Colour colour : model.getPlayers()){

            tickets.put(colour, model.getAllPlayerTickets(colour));
        }

        PrintWriter writer = new PrintWriter(fileLocation, "UTF-8");
        writer.write(gson.create().toJson(new SaveData(array, model.getRounds(), model.getPlayers(), model.getGraphName(), tickets, startPositions)));
        writer.close();

        System.out.println("saved");

    }

    public ScotlandYardModel load(File fileLocation, Player uiPlayer) throws IOException {

        String input = StringUtils.join(Files.readAllLines(fileLocation.toPath()), "");


        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(Move.class, new MoveClassAdapter());

        SaveData loadData = gson.create().fromJson(input, SaveData.class);

        mCurrentPosInQueue = 0;
        mQueuedMoves = loadData.getMovesList();
        System.out.println("loaded");


        HashMap<Colour, Map<Ticket, Integer>> startingTickets = rebuildTickets(loadData.getMovesList(), loadData.getTickets());



        ScotlandYardModel model = null;
        try {
            model = new ScotlandYardModel(loadData.getColourList().size()-1, loadData.getRounds(), loadData.getGraphName());
        } catch (IOException e) {
            System.err.println("make sure '"+loadData.getGraphName()+"' is available to load");
            e.printStackTrace();
        }


        for(Colour colour : loadData.getColourList()){
            model.join(uiPlayer, colour, loadData.getStartPositions().get(colour), startingTickets.get(colour));
        }

        track(model);



        return model;

    }

    /**
     * returns a map of tickets and their count values as should have originally been added to the model
     */
    private HashMap<Colour, Map<Ticket, Integer>> rebuildTickets(Move[] moveList, HashMap<Colour, Map<Ticket, Integer>> startingTickets) {
        HashMap<Colour, Map<Ticket, Integer>> out = new HashMap<Colour, Map<Ticket, Integer>>(startingTickets);
        //we need to get the starting values for the tickets now, yay...

        //go through each move, and work out the tickets used to complete it
        for(Move move : moveList){

            Map<Ticket, Integer> ticketsMap = out.get(move.colour);
            final HashMap<Ticket, Integer> moveTickets = getMoveTickets(move);
            Iterator it = moveTickets.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();

                final Ticket ticket = (Ticket) pair.getKey();
                final Integer value = (Integer) pair.getValue();
                ticketsMap.put(ticket, ticketsMap.get(ticket) + value);

                if(move.colour != Constants.MR_X_COLOUR) {
                    //if it's not MrX we're dealing with we need to take the tickets off him
                    Map<Ticket, Integer> mrXTicketMap = out.get(Constants.MR_X_COLOUR);
                    mrXTicketMap.put(ticket, mrXTicketMap.get(ticket)-value);
                }

                it.remove(); // avoids a ConcurrentModificationException
            }


        }
        return out;
    }

    /**
     * returns the total number of tickets required to complete a move
     */
    private HashMap<Ticket, Integer> getMoveTickets(Move move){
        HashMap<Ticket, Integer> tickets = new HashMap<Ticket, Integer>();
        if(move instanceof MoveTicket){
            tickets.put(((MoveTicket) move).ticket, 1);
        }else if(move instanceof MoveDouble){
            tickets.put(Ticket.DoubleMove, 1);
            for(Move innerMove : ((MoveDouble)move).moves){
                Iterator it = getMoveTickets(innerMove).entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();

                    Ticket ticket = (Ticket) pair.getKey();
                    Integer value = (Integer) pair.getValue();

                    if(tickets.containsKey(ticket)){
                        tickets.put(ticket, tickets.get(ticket)+value);
                    }else{
                        tickets.put(ticket,value);
                    }

                    it.remove(); // avoids a ConcurrentModificationException
                }
            }
        }
        return tickets;
    }


    public Move getCurrentMove(){

        if(mQueuedMoves != null && mCurrentPosInQueue < mQueuedMoves.length){
            Move move = mQueuedMoves[mCurrentPosInQueue];
            return move;
        }
        return null;
    }

    public void nextMove(){
        mCurrentPosInQueue++;
    }


    @Override
    public void notify(Move move) {

        if(mIgnoreMoveCount > 0){
            mIgnoreMoveCount--;
        }else {
            if (move instanceof MoveTicket && move.colour == Constants.MR_X_COLOUR) {
                MoveTicket moveTicket = (MoveTicket) move;
                mMoveList.add(new MoveTicket(moveTicket.colour, mModel.getRealPlayerLocation(Constants.MR_X_COLOUR), moveTicket.ticket));
            } else {
                mMoveList.add(move);
            }

            if (move instanceof MoveDouble) {
                mIgnoreMoveCount = 2;
            }
        }
    }

    public class MoveClassAdapter  implements JsonSerializer<Move>, JsonDeserializer<Move> {
        @Override
        public JsonElement serialize(Move src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
            result.add("properties", context.serialize(src, src.getClass()));
            return result;
        }


        @Override
        public Move deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            JsonElement element = jsonObject.get("properties");

            try {
                String thepackage = "scotlandyard.";
                return context.deserialize(element, Class.forName(thepackage + type));
            } catch (ClassNotFoundException cnfe) {
                throw new JsonParseException("Unknown element type: " + type, cnfe);
            }
        }
    }
}
