package solution.development;

import solution.views.map.MapPath;
import solution.views.map.MapPosition;

import java.awt.geom.Path2D;
import java.util.ArrayList;

/**
 * Created by rory on 09/03/15.
 */
public class CompatibleData {
    private ArrayList<MapPosition> positions;
    private ArrayList<int[]> pathXCoords;
    private ArrayList<int[]> pathYCoords;
    private ArrayList<int[]> pathNodeIds;

    public CompatibleData (MapData mapData){
        positions = new ArrayList<MapPosition>();
        pathXCoords = new ArrayList<int[]>();
        pathYCoords = new ArrayList<int[]>();
        pathNodeIds = new ArrayList<int[]>();
        createFromMapData(mapData);
    }

    private void createFromMapData(MapData mapData) {

        addPathData(mapData.getPathNodeList(), mapData.getPathEdgeList());
        addPathData(mapData.getmBusPathNodeList(), mapData.getmBusPathEdgeList());
        addPathData(mapData.getmUndergroundPathNodeList(), mapData.getmUndergroundPathEdgeList());

    }

    private void addPathData(ArrayList<PathNode> pathNodeList, ArrayList<PathEdge> pathEdgeList) {
        for(PathNode pathNode : pathNodeList){
            if(pathNode.getRadius() == MapCanvas.CIRC_SIZE) {
                //only add the ones we care about
                final int positionId = pathNode.getId();
                positions.add(new MapPosition(Integer.parseInt(pathNode.getName()), pathNode.getX(), pathNode.getY()));

                final ArrayList<ArrayList<int[]>> positionsList = getConnectedPaths(positionId, pathEdgeList);

                for(ArrayList<int[]> positions : positionsList) {
                    int[] xs = new int[positions.size()];
                    int[] ys = new int[positions.size()];
                    int[] ids = new int[positions.size()];

                    for (int i = 0; i < positions.size(); i++) {
                        int[] array = positions.get(i);
                        xs[i] = array[0];
                        ys[i] = array[1];
                        ids[i] = array[2];
                    }

                    pathXCoords.add(xs);
                    pathYCoords.add(ys);
                    pathNodeIds.add(ids);

                }

            }
        }
    }

    private ArrayList<ArrayList<int[]>> getConnectedPaths(int positionId, ArrayList<PathEdge> pathEdges) {

        ArrayList<ArrayList<int[]>> pathPointsList = new ArrayList<ArrayList<int[]>>();

        for(PathEdge edge : pathEdges){

            if(edge.getPathNode1().getId() == positionId){
                pathPointsList.add(findPathFromNode(edge.getPathNode1(), edge.getPathNode2(), pathEdges));
            }else if(edge.getPathNode2().getId() == positionId){
                pathPointsList.add(findPathFromNode(edge.getPathNode2(), edge.getPathNode1(), pathEdges));
            }

        }

        return pathPointsList;
    }

    private ArrayList<int[]> findPathFromNode(PathNode startNode, PathNode secondNode, ArrayList<PathEdge> pathEdges) {
        ArrayList<int[]> pathPoints = new ArrayList<int[]>();


        if(startNode.getRadius() == MapCanvas.CIRC_SIZE) {
            pathPoints.add(new int[]{startNode.getX(), startNode.getY(), Integer.parseInt(startNode.getName())});
        }else{
            pathPoints.add(new int[]{startNode.getX(), startNode.getY(), startNode.getId()});
        }

        if(secondNode.getRadius() != MapCanvas.CIRC_SIZE) {
            for (PathEdge edge : pathEdges) {
                if (edge.getPathNode1().equals(secondNode) && !edge.getPathNode2().equals(startNode)) {
                    pathPoints.addAll(findPathFromNode(edge.getPathNode1(), edge.getPathNode2(), pathEdges));
                } else if (edge.getPathNode2().equals(secondNode) && !edge.getPathNode1().equals(startNode)) {
                    pathPoints.addAll(findPathFromNode(edge.getPathNode2(), edge.getPathNode1(), pathEdges));
                }
            }
        }else{
            pathPoints.add(new int[]{secondNode.getX(), secondNode.getY(), Integer.parseInt(secondNode.getName())});
        }

        return pathPoints;
    }

    public ArrayList<MapPath> getPaths() {
        ArrayList<MapPath> paths = new ArrayList<MapPath>();

        for (int i = 0; i < pathXCoords.size(); i++) {
            int[] xArray = pathXCoords.get(i);
            int[] yArray = pathYCoords.get(i);
            int[] idArray = pathNodeIds.get(i);

            Path2D.Double path = new Path2D.Double(Path2D.WIND_EVEN_ODD, xArray.length);

            for(int j = 0; j < xArray.length; j++){
                if(j == 0){
                    path.moveTo(xArray[j], yArray[j]);
                }else{
                    path.lineTo(xArray[j], yArray[j]);
                }
            }

            paths.add(new MapPath(path, idArray[0], idArray[idArray.length-1]));

        }

        return paths;
    }

    public ArrayList<MapPosition> getPositions() {
        return positions;
    }
}
