package solution.helpers;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

/**
 * Created by rory on 14/03/15.
 */
public class PathInterpolator {

    private Path2D path;
    private ArrayList<Segment> segments;
    private int currentSegmentIndex = 0;

    public PathInterpolator(Path2D path2D){
        this.path = path2D;
    }

    public PathInterpolator reverse(){

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

        path = newPath;

        return this;
    }

    public PathInterpolator interpolate(float step){
        PathIterator iterator = path.getPathIterator(null);

        ArrayList<Float> xs = new ArrayList<Float>();
        ArrayList<Float> ys = new ArrayList<Float>();
        ArrayList<Float> rs = new ArrayList<Float>();

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
                float rotation = (float) Math.atan2(dy,dx);
                rs.add(rotation);
            }else {
                float newX = curX - newDx;
                float newY = curY - newDy;

                xs.add(i + 1, newX);
                ys.add(i + 1, newY);
            }
        }

        rs.add(0,rs.get(0));

        segments = new ArrayList<Segment>();

        path = new Path2D.Double(Path2D.WIND_EVEN_ODD, xs.size());

        for (int i1 = 0; i1 < xs.size(); i1++) {
            float x = xs.get(i1);
            float y = ys.get(i1);
            float r = rs.get(i1);

            if(i1 == 0){
                path.moveTo(x,y);
            }else{
                path.lineTo(x,y);
            }

            segments.add(new Segment(x,y,r));

        }


        return this;

    }

    public void nextSegment(){
        currentSegmentIndex++;
    }
    public Segment getCurrentSegment() {
        return segments != null ? segments.get(currentSegmentIndex) : null;
    }

    public boolean isDone() {
        return segments == null || currentSegmentIndex > segments.size()-1;
    }

    public class Segment {

        private final float x;
        private final float y;
        private final float rotation;

        public Segment(float x, float y, float rotation) {
            this.x = x;
            this.y = y;
            this.rotation = rotation;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getRotation() {
            return rotation;
        }
    }

    public Path2D getPath() {
        return path;
    }
}
