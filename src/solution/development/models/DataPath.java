package solution.development.models;

import solution.development.MapCanvas;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

/**
 * This is the model representation of a path between two DataPositions
 * both ids represent associated DataPosition ids, unordered
 * path is the collection of coordinates that form the path
 */

public class DataPath {
    public int id1;
    public int id2;
    public int[] pathXCoords;
    public int[] pathYCoords;

    private transient int movingCoordIndex = -1;

    public DataPath(int id1, int id2) {
        this.id1 = id1;
        this.id2 = id2;
    }
    
    public Path2D getPath(){
        Path2D path = new Path2D.Double(Path2D.WIND_EVEN_ODD, pathXCoords.length);
        for (int i = 0; i < pathXCoords.length; i++) {
            if (i == 0) {
                path.moveTo(pathXCoords[i], pathYCoords[i]);
            } else {
                path.lineTo(pathXCoords[i], pathYCoords[i]);
            }
        }
        return path;
    }
    
    public void onSelected(int x, int y) {
        for (int i = 0; i < pathXCoords.length; i++) {
            Rectangle2D.Double rect = new Rectangle2D.Double(pathXCoords[i] - MapCanvas.EDIT_POINT_CIRC_SIZE / 2, pathYCoords[i] - MapCanvas.EDIT_POINT_CIRC_SIZE / 2, MapCanvas.EDIT_POINT_CIRC_SIZE, MapCanvas.EDIT_POINT_CIRC_SIZE);
            if(rect.contains(x,y)){
                movingCoordIndex = i;
                return;
            }
        }

        PathIterator iterator = getPath().getPathIterator(null);

        float[] prevCoords = null;

        movingCoordIndex = 0;

        while(!iterator.isDone()){

            float[] curCoords = new float[2];

            iterator.currentSegment(curCoords);

            if(prevCoords != null){
                Path2D path = new Path2D.Double(Path2D.WIND_EVEN_ODD,2);
                path.moveTo(prevCoords[0],prevCoords[1]);
                path.lineTo(curCoords[0], curCoords[1]);
                if(new BasicStroke(3f).createStrokedShape(path).contains(x,y)){
                    break;
                }
            }

            prevCoords = curCoords;

            movingCoordIndex++;
            iterator.next();
        }

        int[] newXCoords = new int[pathXCoords.length+1];
        int[] newYCoords = new int[pathYCoords.length+1];

        for (int i = 0; i < pathXCoords.length + 1; i++) {
            if(i == movingCoordIndex){
                newXCoords[i] = x;
                newYCoords[i] = y;
            }else if(i < movingCoordIndex){
                newXCoords[i] = pathXCoords[i];
                newYCoords[i] = pathYCoords[i];
            }else{
                newXCoords[i] = pathXCoords[i-1];
                newYCoords[i] = pathYCoords[i-1];
            }
        }

        pathXCoords = newXCoords;
        pathYCoords = newYCoords;

    }

    public void onPointDrag(int x, int y) {
        pathXCoords[movingCoordIndex] = x;
        pathYCoords[movingCoordIndex] = y;
    }

    public void onDragStop() {
        movingCoordIndex = -1;
    }
}
