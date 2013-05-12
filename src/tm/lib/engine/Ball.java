package tm.lib.engine;

public class Ball {
	public DPoint position;
	public DPoint target;
	public DPoint fake_target;
	public double speed;
	
	public Ball() {
		position = new DPoint(0, 0);
		target = new DPoint(0, 0);
		fake_target = new DPoint(0, 0);
		speed = 0;
	}
}
