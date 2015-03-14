package solution.development;

import java.awt.geom.GeneralPath;

/**
 * Created by rory on 05/03/15.
 */
public class PathEdge {
	private PathNode mPathNode1;
	private PathNode mPathNode2;
	public PathEdge(final PathNode pathNode1, final PathNode pathNode2) {
		this.mPathNode1 = pathNode1;
		this.mPathNode2 = pathNode2;
	}
	public PathNode getPathNode1() {
		return mPathNode1;
	}
	public void setPathNode1(final PathNode pathNode1) {
		this.mPathNode1 = pathNode1;
	}
	public PathNode getPathNode2() {
		return mPathNode2;
	}
	public void setPathNode2(final PathNode pathNode2) {
		this.mPathNode2 = pathNode2;
	}

	public void notifyNodeMove(final int draggingNode, final int x, final int y) {
		if(mPathNode1.getId() == draggingNode){
			mPathNode1.updatePosition(x, y);
		}else if(mPathNode2.getId() == draggingNode){
			mPathNode2.updatePosition(x,y);
		}
	}

	public PathEdge split(PathNode node){
		PathEdge newEdge = new PathEdge(node, mPathNode2);
		this.mPathNode2 = node;
		return newEdge;
	}

	public GeneralPath getPath () {
		final GeneralPath mPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 2);
		mPath.moveTo(mPathNode1.getShape().getCenterX(), mPathNode1.getShape().getCenterY());
		mPath.lineTo(mPathNode2.getShape().getCenterX(), mPathNode2.getShape().getCenterY());
		return mPath;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PathEdge edge = (PathEdge) o;

        if (mPathNode1 != null ? !mPathNode1.equals(edge.mPathNode1) : edge.mPathNode1 != null) return false;
        if (mPathNode2 != null ? !mPathNode2.equals(edge.mPathNode2) : edge.mPathNode2 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mPathNode1 != null ? mPathNode1.hashCode() : 0;
        result = 31 * result + (mPathNode2 != null ? mPathNode2.hashCode() : 0);
        return result;
    }
}
