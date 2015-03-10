package solution.Models;

public class CoordinateData {
    private int id;
    private int x;
    private int y;

    public CoordinateData(final int id, final int x, final int y){
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}