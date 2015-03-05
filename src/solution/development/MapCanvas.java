package solution.development;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Created by rory on 04/03/15.
 */
public class MapCanvas extends JPanel implements MouseListener, MouseMotionListener {
	private static final double WAYPOINT_SIZE = 4;
	private static double CIRC_SIZE = 10;
	private final BasicStroke stroke;
	ArrayList<PathNode> mNodeList;
	ArrayList<PathEdge> mEdgeList;
	private String mCurrentTool;
	private int mouseX;
	private int mouseY;
	private PathNode draggingNode;
	private PathNode selectedNode;
	private CanvasInterface mInterface;
	private boolean mConnectingNodes;
	public PathNode getDraggingNode() {
		return draggingNode;
	}
	public PathNode getSelectedNode() {
		return selectedNode;
	}
	public interface CanvasInterface {
		public void onNodeSelected(PathNode node);
	}
	public MapCanvas() {
		setOpaque(false);
		mNodeList = new ArrayList<PathNode>();
		mEdgeList = new ArrayList<PathEdge>();

		stroke = new BasicStroke(3.0f);

		addMouseListener(this);
		addMouseMotionListener(this);

	}

	public void setInterface(final CanvasInterface anInterface) {
		mInterface = anInterface;
	}


	private void doDrawing(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;

		g2d.setStroke(stroke);

		g2d.setColor(new Color(250,250,250,200));

		g2d.fill(new Rectangle2D.Double(0,0,getSize().getWidth(), getSize().getHeight()));

		g2d.setColor(Color.RED);

		g2d.setFont(new Font(null, Font.PLAIN, getSize().width/100));

		FontMetrics fm = g2d.getFontMetrics();



		for(PathEdge edge : mEdgeList){

			GeneralPath polyLine = edge.getPath();
			if(HighwayControlUI.EDIT_PATH_TOOL.equals(mCurrentTool) && stroke.createStrokedShape(polyLine).contains(mouseX, mouseY)){
				final BasicStroke tempStroke = new BasicStroke(5.0f);
				g2d.setStroke(tempStroke);
				g2d.draw(polyLine);
				g2d.setStroke(stroke);
			}else{
				g2d.draw(polyLine);
			}
		}

		for(PathNode node : mNodeList){

			if(selectedNode != null && node.getId() == selectedNode.getId() && mConnectingNodes){
				GeneralPath polyLine = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 2);
				polyLine.moveTo(node.getShape().getCenterX(), node.getShape().getCenterY());
				polyLine.lineTo(mouseX, mouseY);
				g2d.draw(polyLine);
			}

			if (selectedNode != null && node.getId() == selectedNode.getId()) {
				g2d.setColor(Color.magenta);
				g2d.fill(node.getShape());
				g2d.setColor(Color.RED);
			}else {
				g2d.fill(node.getShape());
			}


		}

		for(PathNode node : mNodeList){
			final String nodeName = node.getName();
			Rectangle2D r = fm.getStringBounds(nodeName, g2d);
			int x = (int) (node.getShape().getCenterX() - ((int) r.getWidth() / 2) );
			int y = (int) (node.getShape().getCenterY() - ((int) r.getHeight() / 2) + fm.getAscent());
			g2d.setColor(Color.BLACK);
			g.drawString(nodeName, x, y);
			g2d.setColor(Color.RED);
		}
	}

	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		doDrawing(g);
	}

	public void resetActions() {
		mConnectingNodes = false;

		draggingNode = selectedNode = null;
		repaint();
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		if(HighwayControlUI.ADD_EDIT_NODE_TOOL.equals(mCurrentTool)){
			if(selectNode(e.getX(), e.getY()) == null){
				addNode(e.getX(), e.getY());
			}
		}else if(HighwayControlUI.MOVE_ELEMENT_TOOL.equals(mCurrentTool)){
			beginMoveNode(e.getX(), e.getY());
		}else if(HighwayControlUI.CONNECT_NODE_TOOL.equals(mCurrentTool)){
			connectNodes(e.getX(), e.getY());
		}else if(HighwayControlUI.EDIT_PATH_TOOL.equals(mCurrentTool)){
			if(!beginMoveNode(e.getX(), e.getY())){
				addPathWaypoint(e.getX(), e.getY());
			}
		}
	}
	private void connectNodes(final int x, final int y) {

		PathNode firstNode = null;

		for(PathNode node : mNodeList){
			if(selectedNode != null && node.getId() == selectedNode.getId()){
				firstNode = node;
				break;
			}
		}


		if(firstNode == null) {
			selectNode(x, y);
			mConnectingNodes = true;

		}else{

			PathNode secondNode = null;
			for (PathNode node : mNodeList) {
				if (node.getShape().contains(x, y)) {
					secondNode = node;
					break;
				}
			}

			if(secondNode != null) {
				mEdgeList.add(new PathEdge(firstNode, secondNode));
				resetActions();
			}
		}
	}
	private boolean addPathWaypoint(final int x, final int y) {

		for(PathEdge edge : mEdgeList){

			GeneralPath polyLine = edge.getPath();
			if(stroke.createStrokedShape(polyLine).contains(mouseX, mouseY)){
				PathNode waypoint = new PathNode(mNodeList.size()+1, x,y, WAYPOINT_SIZE);
				mNodeList.add(waypoint);
				mEdgeList.add(edge.split(waypoint));

				draggingNode = waypoint;
				return true;
			}
		}
		return false;
	}
	@Override
	public void mouseReleased(final MouseEvent e) {
		if(draggingNode != null){
			endMoveNode(e.getX(), e.getY());
		}
	}
	@Override
	public void mouseEntered(final MouseEvent e) {

	}
	@Override
	public void mouseExited(final MouseEvent e) {

	}
	@Override
	public void mouseClicked(final MouseEvent e) {

	}
	@Override
	public void mouseDragged(final MouseEvent e) {
		if (draggingNode != null) {
			moveNode(e.getX(), e.getY());
		}
	}
	@Override
	public void mouseMoved(final MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();

		if(HighwayControlUI.EDIT_PATH_TOOL.equals(mCurrentTool)){
			repaint();
		}

		if(selectedNode != null){
			repaint();
		}
	}

	private void addNode(int x, int y){
		final PathNode node = new PathNode(mNodeList.size() + 1, x, y, CIRC_SIZE);
		mNodeList.add(node);
		selectedNode = node;

		if(mInterface != null) {
			mInterface.onNodeSelected(node);
		}
		repaint();
	}

	private boolean beginMoveNode(final int x, final int y) {
		for(PathNode node : mNodeList){
			if(node.getShape().contains(x, y)){
				draggingNode = node;
				repaint();
				return true;
			}
		}

		return false;
	}

	private void endMoveNode(final int x, final int y) {
		for(PathNode node : mNodeList){
			if(node.getId() == draggingNode.getId()){
				node.updatePosition(x,y);
				draggingNode = null;
				break;
			}
		}
		repaint();
	}

	private void moveNode(final int x, final int y) {
		for(PathNode node : mNodeList){
			if(node.getId() == draggingNode.getId()){
				node.updatePosition(x, y);

				for(PathEdge edge : mEdgeList){
					edge.notifyNodeMove(draggingNode.getId(), x, y);
				}

				break;
			}
		}
		repaint();
	}

	private PathNode selectNode(final int x, final int y) {

		for (PathNode node : mNodeList) {
			if (node.getShape().contains(x, y)) {
				selectedNode = node;

				if(mInterface != null) {
					mInterface.onNodeSelected(node);
				}
				repaint();
				return node;
			}
		}

		return null;

	}
	public void setCurrentTool(final String currentTool) {
		mCurrentTool = currentTool;
		resetActions();
	}
	public void setData(final MapData mapData) {
		mNodeList = mapData.getPathNodeList();
		mEdgeList = mapData.getPathEdgeList();
		resetActions();
	}
	public void deleteNode(PathNode node){
		mNodeList.remove(node);

		ArrayList<PathNode> dirtyNodeList = new ArrayList<PathNode>();




		if(node.getRadius() == CIRC_SIZE){
			for(PathEdge edge : mEdgeList){
				if(edge.getPathNode1().getId() == node.getId() && edge.getPathNode1().getRadius() == WAYPOINT_SIZE){
					dirtyNodeList.add(edge.getPathNode1());
				}else if(edge.getPathNode2().getId() == node.getId() && edge.getPathNode2().getRadius() == WAYPOINT_SIZE){
					dirtyNodeList.add(edge.getPathNode2());
				}
			}
		}


		ArrayList<PathNode> removedNodeList = new ArrayList<PathNode>();
		removedNodeList.add(node);



		while(dirtyNodeList.size() > 0)
		{
			ArrayList<PathNode> newDirtyNodeList = new ArrayList<PathNode>();

			for(PathNode dirtyNode : dirtyNodeList){
				for(PathEdge edge : mEdgeList){
					if(edge.getPathNode1().getId() == dirtyNode.getId() && edge.getPathNode1().getRadius() == WAYPOINT_SIZE && !dirtyNodeList.contains(edge.getPathNode1())){
						newDirtyNodeList.add(edge.getPathNode1());
					}else if(edge.getPathNode2().getId() == dirtyNode.getId() && edge.getPathNode2().getRadius() == WAYPOINT_SIZE && !dirtyNodeList.contains(edge.getPathNode1())){
						newDirtyNodeList.add(edge.getPathNode2());
					}
				}
			}

			dirtyNodeList = newDirtyNodeList;
		}

		ArrayList<PathNode> newNodeList = new ArrayList<PathNode>();
		ArrayList<PathEdge> newEdgeList = new ArrayList<PathEdge>();

		for (final PathEdge edge : mEdgeList) {
			if(!(removedNodeList.contains(edge.getPathNode1()) || removedNodeList.contains(edge.getPathNode2()))){
				newEdgeList.add(edge);
			}
		}

		for(PathNode oldNode : mNodeList){
			if(!(removedNodeList.contains(oldNode) || removedNodeList.contains(oldNode))){
				newNodeList.add(oldNode);
			}
		}

		mNodeList = newNodeList;
		mEdgeList = newEdgeList;

		repaint();

	}
}
