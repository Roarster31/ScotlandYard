package solution.helpers;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

/**
 * Created by rory on 14/03/15.
 */
public class InterpolatorHelper {

    public static Path2D reverse(Path2D path){

        PathIterator iterator = path.getPathIterator(null);

        ArrayList<float[]> steps = new ArrayList<float[]>();

        while(!iterator.isDone()){
            float[] coords = new float[2];
            iterator.currentSegment(coords);
            steps.add(coords);
            iterator.next();
        }

        Path2D newPath = new Path2D.Double();

        for (int i = 0; i < steps.size(); i++) {
            float[] coords = steps.get(steps.size()-1-i);

            if(i == 0){
                newPath.moveTo(coords[0], coords[1]);
            }else{
                newPath.lineTo(coords[0], coords[1]);
            }

        }

        return newPath;
    }

    public static Path2D interpolate(Path2D path, float step){
        PathIterator iterator = path.getPathIterator(null);

        ArrayList<Float> xs = new ArrayList<Float>();
        ArrayList<Float> ys = new ArrayList<Float>();

        while(!iterator.isDone()){
            float[] coords = new float[2];
            iterator.currentSegment(coords);
            xs.add(coords[0]);
            ys.add(coords[1]);
            iterator.next();
        }


        int i = 0;
        while(i < xs.size()-1){
            Float curX = xs.get(i + 1);
            Float curY = ys.get(i + 1);
            float dx = curX - xs.get(i);
            float dy = curY - ys.get(i);

            float newDx = 0;
            float newDy = 0;

            if(Math.abs(dy) > Math.abs(dx)){

                if(Math.abs(dy) > step){
                    newDy = Math.signum(dy) * (step);
                    newDx = (newDy / dy) * dx;
                }

            }else{

                if(Math.abs(dx) > step){
                    newDx = Math.signum(dx) * (step);
                    newDy = (newDx / dx) * dy;
                }

            }

            if(newDx == 0){
                i++;
                continue;
            }else {
                float newX = curX - newDx;
                float newY = curY - newDy;

                xs.add(i + 1, newX);
                ys.add(i + 1, newY);
            }
        }

        Path2D newPath = new Path2D.Double();
        for (int j = 0; j < xs.size(); j++) {
            Float x = xs.get(j);
            Float y = ys.get(j);

            if(j == 0){
                newPath.moveTo(x,y);
            }else{
                newPath.lineTo(x,y);
            }

        }

        return newPath;

    }
}
