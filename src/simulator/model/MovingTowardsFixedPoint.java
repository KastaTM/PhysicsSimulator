package simulator.model;

import java.util.List;
import simulator.misc.Vector2D;

public class MovingTowardsFixedPoint implements ForceLaws{

	private double _G;		//Gravedad
	private Vector2D _c;	//Punto
	
	public MovingTowardsFixedPoint(Vector2D c, double G) {
		this._c = c;
		this._G = G;
	}
	
	//Aplly de la ley
	public void apply(List<Body> bodies) {											
		Vector2D force;
		for(Body b1: bodies) {
			force = ForceGravitational(b1);
			b1.addForce(force);
		}
	}
	
	//Fuerza de la ley
	private Vector2D ForceGravitational(Body A) {
		Vector2D force;
		force = _c.minus(A.getPosition()).direction().scale(_G*A.getMass());
		return force;
	}
	
	
	public String toString() {
		return "Moving towards "+this._c+"with constant acceleration -" + this._G;
	}
	
}