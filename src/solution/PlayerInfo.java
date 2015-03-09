package solution;

import scotlandyard.Colour;

/**
 * Created by benallen on 04/03/15.
 */
public class PlayerInfo {
    private String name;
    protected Colour colour;
    private int startingLocation;

    public void setColour(Colour colour) {
        this.colour = colour;
    }
    public void setStartingLocation(int startingLocation) {
        this.startingLocation = startingLocation;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }
    public Colour getColour() {
        return colour;
    }
    public int getStartingLocation(){
        return startingLocation;
    }
}
