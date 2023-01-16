package simulator.model;

import org.json.JSONObject;
import simulator.misc.Vector2D;

public class Body {
	
	protected String id;	//Id del cuerpo
	protected Vector2D v;	//Vector velocidad
	protected Vector2D f;	//Vector fuerza
	protected Vector2D p;	//Vector posicion
	protected double m;		//Masa del cuerpo
	
	
	public Body(String id, Vector2D v, Vector2D p, double m) {
		this.id=id;
		this.v=v;
		this.p=p;
		this.m=m;
		this.f = new Vector2D();
	}
	
	public String getId() {
		return this.id;
	}
	
	public Vector2D getVelocity() {
		return this.v;
	}
	
	public Vector2D getForce() {
		return this.f;
	}
	
	public Vector2D getPosition() {
		return this.p;
	}
	
	public double getMass() {
		return this.m;
	}
	
	//Añadir fuerzas
	void addForce(Vector2D f1) {
		f = f.plus(f1);
	}
	
	//Reseteo de fuerzas
	void resetForce() {				
		f = new Vector2D();
	}
	
	//Movimiento de bodies
	void move(double t) {												
		Vector2D a;
		if(this.m==0) {
			a=new Vector2D();
		}
		else {
			a = f.scale(1.0/m);
		}
		p = p.plus(v.scale(t).plus(a.scale(0.5*t*t)));
		v = v.plus(a.scale(t));
		
	}
	
	//Funcion de equals de bodies
	public boolean equals(Object obj) {										
		Body others;
		if(this == obj) {
			return true;
		}
		else if(obj == null){
			return false;
		}
		else if(this.getClass() != obj.getClass()) {
			return false;
		}
		others = (Body) obj;
		
		if(this.id.equals(others.id)) {
			return true;
		}
		return false;
	}
	
	public JSONObject getState() {
		JSONObject js = new JSONObject();
		js.put("id", id);
		js.put("m", m);
		js.put("p", p.asJSONArray());
		js.put("v",v.asJSONArray());
		js.put("f", f.asJSONArray());
		return js;
	}
	
	public String toString() {
		return getState().toString();
	}

}