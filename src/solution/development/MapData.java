package solution.development;

import java.util.ArrayList;

/**
 * Created by rory on 05/03/15.
 */
public class MapData {
	private ArrayList<PathNode> mPathNodeList;
	private ArrayList<PathEdge> mPathEdgeList;
	private int mHighId;

	public MapData(int highId, ArrayList<PathNode> nodeList, ArrayList<PathEdge> edgeList){
		this.mHighId = highId;
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
	public int getHighId() {
		return mHighId;
	}
	public void setHighId(final int highId) {
		mHighId = highId;
	}
}