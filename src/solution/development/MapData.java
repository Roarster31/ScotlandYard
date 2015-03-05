package solution.development;

import java.util.ArrayList;

/**
 * Created by rory on 05/03/15.
 */
public class MapData {
	private ArrayList<PathNode> mPathNodeList;
	private ArrayList<PathEdge> mPathEdgeList;

	public MapData(ArrayList<PathNode> nodeList, ArrayList<PathEdge> edgeList){
		this.mPathNodeList = nodeList;
		this.mPathEdgeList = edgeList;
	}

	public ArrayList<PathNode> getPathNodeList() {
		return mPathNodeList;
	}
	public void setPathNodeList(final ArrayList<PathNode> pathNodeList) {
		this.mPathNodeList = pathNodeList;
	}
	public ArrayList<PathEdge> getPathEdgeList() {
		return mPathEdgeList;
	}
	public void setPathEdgeList(final ArrayList<PathEdge> pathEdgeList) {
		this.mPathEdgeList = pathEdgeList;
	}
}