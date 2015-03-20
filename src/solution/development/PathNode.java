package solution.development;

import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;

/**
 * Created by rory on 05/03/15.
 */
@Deprecated
public class PathNode {
	private int id;
	private int x;
	private int y;
	private double radius;
	private String name;
	public int getId() {
		return id;
	}
	public void setId(final int id) {
		this.id = id;
	}
	public int getX() {
		return x;
	}
	public void setX(final int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(final int y) {
		this.y = y;
	}
	public double getRadius() {
		return radius;
	}
	public void setRadius(final double radius) {
		this.radius = radius;
	}

	public PathNode(int id, int x, int y, double radius){
		this.id = id;
		this.x = x;
		this.y = y;
		this.radius = radius;
	}
	public String getName() {
		if(name != null){
			return name;
		}else {
			return "";
		}
	}

	public RectangularShape getShape(){
		return new Ellipse2D.Double(x-radius, y-radius, radius*2, radius*2);
	}

	public void updatePosition(int x,int y){
		this.x = x;
		this.y = y;
	}

	public void setName(final String name) {
		this.name = name;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PathNode pathNode = (PathNode) o;

        if (id != pathNode.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
