package tm.lib.engine;

import org.eclipse.swt.graphics.Point;

public class DPoint {
	public double x;
	public double y;
	
	public DPoint() {
		x = 0;
		y = 0;
	}
	
	public DPoint(double value_x, double value_y) {
		x = value_x;
		y = value_y;
	}
	
	public DPoint(DPoint other) {
		x = other.x;
		y = other.y;
	}
	
	public void set(DPoint other) {
		x = other.x;
		y = other.y;
	}
	
	public DPoint multiply(double v) {
		return new DPoint(x * v, y * v);
	}
	
	public DPoint div(double v) {
		return new DPoint(x / v, y / v);
	}
	
	public DPoint plus(DPoint other) {
		return new DPoint(x + other.x, y + other.y);
	}
	
	public DPoint minus(DPoint other) {
		return new DPoint(x - other.x, y - other.y);
	}	
	
	public Point to_point() {
		return new Point((int) x, (int) y);
	}
	
	public double dist(DPoint other) {
		return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
	}
	
	public double norm() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
}
