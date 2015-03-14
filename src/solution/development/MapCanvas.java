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
	public static double CIRC_SIZE = 10;
	private final BasicStroke stroke;
	ArrayList<PathNode> mNodeList;
	ArrayList<PathEdge> mEdgeList;
    ArrayList<PathNode> mBusNodeList;
    ArrayList<PathEdge> mBusEdgeList;
    ArrayList<PathNode> mUndergroundNodeList;
    ArrayList<PathEdge> mUndergroundEdgeList;
	private String mCurrentTool;
	private int mouseX;
	private int mouseY;
	private PathNode draggingNode;
	private PathNode selectedNode;
	private CanvasInterface mInterface;
	private boolean mConnectingNodes;
	public int curHighId = 0;
    private String viewType = HighwayControlUI.VIEW_ALL;

    public PathNode getDraggingNode() {
		return draggingNode;
	}
	public PathNode getSelectedNode() {
		return selectedNode;
	}

    public void setViewType(String mCurrentView) {
        viewType = mCurrentView;
        repaint();
    }

    public interface CanvasInterface {
		public void onNodeSelected(PathNode node);
	}
	public MapCanvas() {
		setOpaque(false);
		mNodeList = new ArrayList<PathNode>();
		mEdgeList = new ArrayList<PathEdge>();
        mBusNodeList = new ArrayList<PathNode>();
        mBusEdgeList = new ArrayList<PathEdge>();
        mUndergroundNodeList = new ArrayList<PathNode>();
        mUndergroundEdgeList = new ArrayList<PathEdge>();

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

		g2d.setColor(new Color(250,250,250, 111));

		g2d.fill(new Rectangle2D.Double(0,0,getSize().getWidth(), getSize().getHeight()));

		g2d.setColor(Color.YELLOW);

		g2d.setFont(new Font(null, Font.PLAIN, getSize().width/100));

		FontMetrics fm = g2d.getFontMetrics();





        if(!viewType.equals(HighwayControlUI.VIEW_ALL)){
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC,0.5f));
        }

        for(PathEdge edge : mEdgeList){

            GeneralPath polyLine = edge.getPath();
            if(HighwayControlUI.EDIT_PATH_TOOL.equals(mCurrentTool) && stroke.createStrokedShape(polyLine).contains(mouseX, mouseY) && viewType.equals(HighwayControlUI.VIEW_ALL)){
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
				g2d.setColor(Color.YELLOW);
			}else {
				g2d.fill(node.getShape());
			}


		}

        if(!viewType.equals(HighwayControlUI.VIEW_ALL)){
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC,1f));

            ArrayList<PathNode> nodeList = mNodeList;
            ArrayList<PathEdge> edgeList = mEdgeList;
            Color color = Color.GREEN;
            if(viewType.equals(HighwayControlUI.VIEW_BUS)){

                nodeList = mBusNodeList;
                edgeList = mBusEdgeList;
            }else{
                color = Color.RED;

                nodeList = mUndergroundNodeList;
                edgeList = mUndergroundEdgeList;
            }

            g2d.setColor(color);

            for(PathEdge edge : edgeList){

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

            for(PathNode node : nodeList){

                    if(selectedNode != null && node.getId() == selectedNode.getId() && mConnectingNodes){
                        GeneralPath polyLine = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 2);
                        polyLine.moveTo(node.getShape().getCenterX(), node.getShape().getCenterY());
                        polyLine.lineTo(mouseX, mouseY);
                        g2d.draw(polyLine);
                    }

                    if (selectedNode != null && node.getId() == selectedNode.getId()) {
                        g2d.setColor(Color.magenta);
                        g2d.fill(node.getShape());
                        g2d.setColor(color);
                    }else {
                        g2d.fill(node.getShape());
                    }


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

		draggingNode = null;
		selectedNode = null;
		repaint();
	}

	@Override
	public void mousePressed(final MouseEvent e) {
        if(viewType.equals(HighwayControlUI.VIEW_ALL)) {
            if (HighwayControlUI.ADD_EDIT_NODE_TOOL.equals(mCurrentTool)) {
                if (selectNode(e.getX(), e.getY()) == null) {
                    addNode(e.getX(), e.getY());
                }
            } else if (HighwayControlUI.MOVE_ELEMENT_TOOL.equals(mCurrentTool)) {
                beginMoveNode(e.getX(), e.getY());
            } else if (HighwayControlUI.CONNECT_NODE_TOOL.equals(mCurrentTool)) {
                connectNodes(e.getX(), e.getY());
            } else if (HighwayControlUI.EDIT_PATH_TOOL.equals(mCurrentTool)) {
                if (!beginMoveNode(e.getX(), e.getY())) {
                    addPathWaypoint(e.getX(), e.getY());
                }
            }
        }else{
            if (HighwayControlUI.ADD_EDIT_NODE_TOOL.equals(mCurrentTool)) {
                if (selectNode(e.getX(), e.getY()) == null) {
                    addNode(e.getX(), e.getY());
                }
            } else if (HighwayControlUI.MOVE_ELEMENT_TOOL.equals(mCurrentTool)) {
                beginMoveNode(e.getX(), e.getY());
            } else if (HighwayControlUI.CONNECT_NODE_TOOL.equals(mCurrentTool)) {
                connectNodes(e.getX(), e.getY());
            } else if (HighwayControlUI.EDIT_PATH_TOOL.equals(mCurrentTool)) {
                if (!beginMoveNode(e.getX(), e.getY())) {
                    addPathWaypoint(e.getX(), e.getY());
                }
            }
        }
	}
	private void connectNodes(final int x, final int y) {

		PathNode firstNode = null;

        ArrayList<PathEdge> edgeList = mEdgeList;
        ArrayList<PathNode> nodeList = mNodeList;

        if(viewType.equals(HighwayControlUI.VIEW_BUS)){
            edgeList = mBusEdgeList;
            nodeList = mBusNodeList;
        }else if(viewType.equals(HighwayControlUI.VIEW_UNDERGROUND)){
            edgeList = mUndergroundEdgeList;
            nodeList = mUndergroundNodeList;
        }

        for(PathNode node : nodeList){
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
			for (PathNode node : nodeList) {
				if (node.getShape().contains(x, y)) {
					secondNode = node;
					break;
				}
			}

			if(secondNode != null) {
				edgeList.add(new PathEdge(firstNode, secondNode));
				resetActions();
			}
		}
	}
	private boolean addPathWaypoint(final int x, final int y) {

        ArrayList<PathEdge> edgeList = mEdgeList;
        ArrayList<PathNode> nodeList = mNodeList;

        if(viewType.equals(HighwayControlUI.VIEW_BUS)){
            edgeList = mBusEdgeList;
            nodeList = mBusNodeList;
        }else if(viewType.equals(HighwayControlUI.VIEW_UNDERGROUND)){
            edgeList = mUndergroundEdgeList;
            nodeList = mUndergroundNodeList;
        }

		for(PathEdge edge : edgeList){

			GeneralPath polyLine = edge.getPath();
			if(stroke.createStrokedShape(polyLine).contains(mouseX, mouseY)){
				PathNode waypoint = new PathNode(curHighId++, x,y, WAYPOINT_SIZE);
                nodeList.add(waypoint);
				edgeList.add(edge.split(waypoint));

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


        if(viewType.equals(HighwayControlUI.VIEW_ALL)) {

            final PathNode node = new PathNode(curHighId++, x, y, CIRC_SIZE);

            mNodeList.add(node);
            selectedNode = node;

            if(mInterface != null) {
                mInterface.onNodeSelected(node);
            }

        }else if(viewType.equals(HighwayControlUI.VIEW_BUS) || viewType.equals(HighwayControlUI.VIEW_UNDERGROUND)){

            PathNode chosenNode = null;
            for(PathNode node : mNodeList){
                if(node.getShape().contains(x, y)){
                    chosenNode = node;
                    break;
                }
            }

            if(chosenNode != null){

                if(viewType.equals(HighwayControlUI.VIEW_BUS)){
                    mBusNodeList.add(chosenNode);
                }else if(viewType.equals(HighwayControlUI.VIEW_UNDERGROUND)){
                    mUndergroundNodeList.add(chosenNode);
                }
            }

        }



		repaint();
	}

	private boolean beginMoveNode(final int x, final int y) {

        ArrayList<PathNode> nodeList = mNodeList;

        if(viewType.equals(HighwayControlUI.VIEW_BUS)){
            nodeList = mBusNodeList;
        }else if(viewType.equals(HighwayControlUI.VIEW_UNDERGROUND)){
            nodeList = mUndergroundNodeList;
        }

		for(PathNode node : nodeList){
			if(node.getShape().contains(x, y)){
				draggingNode = node;
				repaint();
				return true;
			}
		}

		return false;
	}

	private void endMoveNode(final int x, final int y) {

        ArrayList<PathNode> nodeList = mNodeList;

        if(viewType.equals(HighwayControlUI.VIEW_BUS)){
            nodeList = mBusNodeList;
        }else if(viewType.equals(HighwayControlUI.VIEW_UNDERGROUND)){
            nodeList = mUndergroundNodeList;
        }

        int correctedX = x;
        int correctedY = y;

        if(viewType.equals(HighwayControlUI.VIEW_BUS) || viewType.equals(HighwayControlUI.VIEW_UNDERGROUND)) {
            for(PathNode node : mNodeList){
                if(node.getShape().contains(x,y)){
                    correctedX = (int) node.getShape().getCenterX();
                    correctedY = (int) node.getShape().getCenterY();
                    break;
                }
            }


        }

        if(viewType.equals(HighwayControlUI.VIEW_UNDERGROUND)){

            for(PathNode node : mBusNodeList){
                if(node.getShape().contains(x,y)){
                    correctedX = (int) node.getShape().getCenterX();
                    correctedY = (int) node.getShape().getCenterY();
                    break;
                }
            }

        }

		for(PathNode node : nodeList){
			if(node.getId() == draggingNode.getId()){
				node.updatePosition(correctedX,correctedY);
				draggingNode = null;
				break;
			}
		}
		repaint();
	}

	private void moveNode(final int x, final int y) {

        ArrayList<PathEdge> edgeList = mEdgeList;
        ArrayList<PathNode> nodeList = mNodeList;

        if(viewType.equals(HighwayControlUI.VIEW_BUS)){
            edgeList = mBusEdgeList;
            nodeList = mBusNodeList;
        }else if(viewType.equals(HighwayControlUI.VIEW_UNDERGROUND)){
            edgeList = mUndergroundEdgeList;
            nodeList = mUndergroundNodeList;
        }

		for(PathNode node : nodeList){
			if(node.getId() == draggingNode.getId()){
				node.updatePosition(x, y);

				for(PathEdge edge : edgeList){
					edge.notifyNodeMove(draggingNode.getId(), x, y);
				}

				break;
			}
		}
		repaint();
	}

	private PathNode selectNode(final int x, final int y) {

        ArrayList<PathNode> nodeList = mNodeList;

        if(viewType.equals(HighwayControlUI.VIEW_BUS)){
            nodeList = mBusNodeList;
        }else if(viewType.equals(HighwayControlUI.VIEW_UNDERGROUND)){
            nodeList = mUndergroundNodeList;
        }

        for (PathNode node : nodeList) {
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
        mBusNodeList = mapData.getmBusPathNodeList();
        mBusEdgeList = mapData.getmBusPathEdgeList();
        mUndergroundNodeList = mapData.getmUndergroundPathNodeList();
        mUndergroundEdgeList = mapData.getmUndergroundPathEdgeList();
		curHighId = mapData.getHighId();
		resetActions();
	}
	public void deleteNode(PathNode node){



		ArrayList<PathNode> dirtyNodeList = new ArrayList<PathNode>();


        ArrayList<PathEdge> edgeList = mEdgeList;
        ArrayList<PathNode> nodeList = mNodeList;

        if(viewType.equals(HighwayControlUI.VIEW_BUS)){
            edgeList = mBusEdgeList;
            nodeList = mBusNodeList;
        }else if(viewType.equals(HighwayControlUI.VIEW_UNDERGROUND)){
            edgeList = mUndergroundEdgeList;
            nodeList = mUndergroundNodeList;
        }

        nodeList.remove(node);


		if(node.getRadius() == CIRC_SIZE){
            for(PathEdge edge : edgeList){
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
				for(PathEdge edge : edgeList){
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

		for (final PathEdge edge : edgeList) {
			if(!(removedNodeList.contains(edge.getPathNode1()) || removedNodeList.contains(edge.getPathNode2()))){
				newEdgeList.add(edge);
			}
		}

		for(PathNode oldNode : nodeList){
			if(!(removedNodeList.contains(oldNode) || removedNodeList.contains(oldNode))){
				newNodeList.add(oldNode);
			}
		}

        if(viewType.equals(HighwayControlUI.VIEW_ALL)) {
            mNodeList = newNodeList;
            mEdgeList = newEdgeList;
        }else if(viewType.equals(HighwayControlUI.VIEW_BUS)){
            mBusNodeList = newNodeList;
            mBusEdgeList = newEdgeList;
        }else if(viewType.equals(HighwayControlUI.VIEW_UNDERGROUND)){
            mUndergroundNodeList = newNodeList;
            mUndergroundEdgeList = newEdgeList;
        }

        selectedNode = null;

		repaint();

	}
}
