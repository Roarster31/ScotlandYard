package solution.views.map;


import java.awt.*;
import java.util.ArrayList;

/**
 * Created by rory on 15/03/15.
 */
public class AnimationWorker implements Runnable{

    private final Component repaintableComponent;
    private ArrayList<AnimationInterface> callbacks;
    private boolean isRunning;
    private Thread thread;

    public interface AnimationInterface {

        /**
         *
         * @return true if the animation is finished and should be cleared up
         */
        public boolean onTick();
        public void onFinished();
    }
    public AnimationWorker (Component component) {
        repaintableComponent = component;
        callbacks = new ArrayList<AnimationInterface>();
    }

    public void addWork(AnimationInterface callback){
        callbacks.add(callback);
        if(!isRunning){
            startThread();
        }
    }

    private void startThread() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {

        ArrayList<AnimationInterface> dirtyList = new ArrayList<AnimationInterface>();

        while(callbacks.size() > 0){

            isRunning = true;

            dirtyList.clear();

            for(AnimationInterface callback : callbacks){
                if(callback.onTick()){
                    dirtyList.add(callback);
                }
            }

            repaintableComponent.repaint();

            for(AnimationInterface dirtyCallback : dirtyList){
                dirtyCallback.onFinished();
                callbacks.remove(dirtyCallback);
            }

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        callbacks.clear();
        isRunning = false;
    }


}
