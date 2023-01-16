package simulator.model;

import java.util.List;
import simulator.misc.Vector2D;

public class NewtonUniversalGravitation implements ForceLaws{

	private double G; //Constante gravitacion universal
	
	public NewtonUniversalGravitation(double _G) {
		this.G = _G;
	}

	//Apply de la fuerza
	public void apply(List<Body> bodies) {
		Vector2D force;
		for(int i = 0; i < bodies.size(); i++) {
			for(int j = 0; j < bodies.size(); j++) {
				Body b1 = bodies.get(i);
				Body b2 = bodies.get(j);
				if(!b1.equals(b2)) {
					force = ForceA_B(b1, b2);
					if(force != null)
						b1.addForce(force);
				}
			}
		}
	}
	
	//Fuerza de la ley
	private Vector2D ForceA_B(Body A, Body B) {
		Vector2D Fij, aux, aux2, aux3;
		double d, fij;
		
		aux = B.getPosition().minus(A.getPosition());	// |d1 - d2|
		aux2 = aux; 									// copia
		aux3 = aux.direction(); 						// Vector unitario dij = |d1 - d2|
		d = Math.pow(aux2.magnitude(), 2);   			// |d1 - d2|* |d1 - d2| comprobar que no sea 0
		fij = G * A.getMass() * B.getMass() / d; 		// Fuerza en formato double poner getMass()
		Fij=aux3.scale(fij);
		return Fij;
	}
	
	
	public String toString() {
		return "Newton’s Universal Gravitation with G=-"+ this.G;
	}
	
}