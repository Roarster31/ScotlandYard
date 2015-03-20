package solution.development;

import java.util.ArrayList;

/**
 * Created by rory on 05/03/15.
 */
@Deprecated
public class MapData {
	private ArrayList<PathNode> mPathNodeList;
	private ArrayList<PathEdge> mPathEdgeList;
    private ArrayList<PathNode> mBusPathNodeList;
    private ArrayList<PathEdge> mBusPathEdgeList;
    private ArrayList<PathNode> mUndergroundPathNodeList;
    private ArrayList<PathEdge> mUndergroundPathEdgeList;
	private int mHighId;

	public MapData(int highId, ArrayList<PathNode> nodeList, ArrayList<PathEdge> edgeList, ArrayList<PathNode> busNodeList, ArrayList<PathEdge> busEdgeList, ArrayList<PathNode> undergroundNodeList, ArrayList<PathEdge> undergroundEdgeList){
		this.mHighId = highId;
		this.mPathNodeList = nodeList;
		this.mPathEdgeList = edgeList;
        this.mBusPathNodeList = busNodeList;
        this.mBusPathEdgeList = busEdgeList;
        this.mUndergroundPathNodeList = undergroundNodeList;
        this.mUndergroundPathEdgeList = undergroundEdgeList;
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

    public ArrayList<PathNode> getmPathNodeList() {
        return mPathNodeList;
    }

    public void setmPathNodeList(ArrayList<PathNode> mPathNodeList) {
        this.mPathNodeList = mPathNodeList;
    }

    public ArrayList<PathEdge> getmPathEdgeList() {
        return mPathEdgeList;
    }

    public void setmPathEdgeList(ArrayList<PathEdge> mPathEdgeList) {
        this.mPathEdgeList = mPathEdgeList;
    }

    public ArrayList<PathNode> getmBusPathNodeList() {
        return mBusPathNodeList;
    }

    public void setmBusPathNodeList(ArrayList<PathNode> mBusPathNodeList) {
        this.mBusPathNodeList = mBusPathNodeList;
    }

    public ArrayList<PathEdge> getmBusPathEdgeList() {
        return mBusPathEdgeList;
    }

    public void setmBusPathEdgeList(ArrayList<PathEdge> mBusPathEdgeList) {
        this.mBusPathEdgeList = mBusPathEdgeList;
    }

    public ArrayList<PathNode> getmUndergroundPathNodeList() {
        return mUndergroundPathNodeList;
    }

    public void setmUndergroundPathNodeList(ArrayList<PathNode> mUndergroundPathNodeList) {
        this.mUndergroundPathNodeList = mUndergroundPathNodeList;
    }

    public ArrayList<PathEdge> getmUndergroundPathEdgeList() {
        return mUndergroundPathEdgeList;
    }

    public void setmUndergroundPathEdgeList(ArrayList<PathEdge> mUndergroundPathEdgeList) {
        this.mUndergroundPathEdgeList = mUndergroundPathEdgeList;
    }
}