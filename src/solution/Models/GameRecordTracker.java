package solution.Models;

import com.google.gson.*;
import com.sun.deploy.util.StringUtils;
import scotlandyard.*;
import solution.Constants;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rory on 12/03/15.
 */
public class GameRecordTracker implements Spectator {

    private final ArrayList<Move> mMoveList;
    private final HashMap<Colour, Integer> startPositions;
    private SaveData loadData;
    private ScotlandYardModel mModel;
    private int mCurrentMove;

    public void track(ScotlandYardModel model) {
        mModel = model;
        model.spectate(this);

        startPositions.clear();
        for(Colour colour : model.getPlayers()){
            startPositions.put(colour, model.getRealPlayerLocation(colour));
        }
    }

    public interface GameReplayInterface {
        public void onMoveApplied();
    }

    public GameRecordTracker(){
        mMoveList = new ArrayList<Move>();
        startPositions = new HashMap<Colour, Integer>();
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

    public void load(File fileLocation) throws IOException {

        String input = StringUtils.join(Files.readAllLines(fileLocation.toPath()), "");


        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(Move.class, new MoveClassAdapter());

        loadData = gson.create().fromJson(input, SaveData.class);

        System.out.println("loaded");

        System.out.println(loadData);

    }

    /**
     * applies all of the loaddata to a new scotland yard model and returns it
     * @return
     * @param uiPlayer
     */
    public ScotlandYardModel apply(Player uiPlayer, boolean replay){

        HashMap<Colour, Map<Ticket, Integer>> startingTickets = loadData.getTickets();

        //we need to get the starting values for the tickets now, yay...
        for(Move move : loadData.getMovesList()){
            if(move instanceof MoveTicket){
                MoveTicket moveTicket = (MoveTicket) move;
                Map<Ticket, Integer> ticketMap = startingTickets.get(moveTicket.colour);
                ticketMap.put(moveTicket.ticket, ticketMap.get(moveTicket.ticket)+1);
            }
        }

        ScotlandYardModel model = null;
        try {
            model = new ScotlandYardModel(loadData.getColourList().size()-1, loadData.getRounds(), loadData.getGraphName());
        } catch (IOException e) {
            System.err.println("make sure '"+loadData.getGraphName()+"' is available to load");
            e.printStackTrace();
        }


        for(Colour colour : loadData.getColourList()){
            model.join(new PlayerSpoofer(uiPlayer, loadData.getMovesList(), colour), colour, loadData.getStartPositions().get(colour), startingTickets.get(colour));
        }

        track(model);

        if(!replay) {
            while(hasNextMove()){
                playNextMove();
            }
        }

        return model;
    }

    public void playNextMove(){
        mModel.turn();
        mCurrentMove++;
    }

    public boolean hasNextMove(){
        return mCurrentMove < loadData.getMovesList().length;
    }

    class PlayerSpoofer implements Player {

        private final ArrayList<Move> playerMoveList;
        private final Player mRealPlayer;
        private int count = 0;

        public PlayerSpoofer(Player realPlayer, Move[] movesList, Colour colour) {
            mRealPlayer = realPlayer;
            playerMoveList = new ArrayList<Move>();
            for(Move move : movesList){
                if(move.colour.equals(colour)){
                    playerMoveList.add(move);
                }
            }
        }

        @Override
        public Move notify(int location, List<Move> list) {
            if(count < playerMoveList.size()) {
                Move move = playerMoveList.get(count);
                count++;
                return move;
            }else{
                return mRealPlayer.notify(location, list);
            }
        }

    }

    public ArrayList<Move> getPlayerMoveList(final Colour colour){
        ArrayList<Move> out = new ArrayList<Move>();

        for(Move move : mMoveList){
            if(move.colour.equals(colour)){
                out.add(move);
            }
        }

        return out;
    }

    @Override
    public void notify(Move move) {
        if(move instanceof MoveTicket && move.colour == Constants.MR_X_COLOUR){
            MoveTicket moveTicket  = (MoveTicket) move;
            mMoveList.add(new MoveTicket(moveTicket.colour, mModel.getRealPlayerLocation(Constants.MR_X_COLOUR), moveTicket.ticket));
        }else{
            mMoveList.add(move);
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
