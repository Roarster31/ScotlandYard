package solution.development;

import solution.Models.CoordinateData;

import java.util.ArrayList;

/**
 * Created by rory on 09/03/15.
 */
public class CompatibleData {
    private ArrayList<CoordinateData> nodes;
    private ArrayList<PathData> paths;

    public CompatibleData (MapData mapData){
        nodes = new ArrayList<CoordinateData>();
        paths = new ArrayList<PathData>();
        createFromMapData(mapData);
    }

    private void createFromMapData(MapData mapData) {

        for(PathNode pathNode : mapData.getPathNodeList()){
            if(pathNode.getRadius() == MapCanvas.CIRC_SIZE) {
                //only add the ones we care about
                nodes.add(new CoordinateData(pathNode.getId(), pathNode.getX(), pathNode.getY()));
            }
        }



    }
}
