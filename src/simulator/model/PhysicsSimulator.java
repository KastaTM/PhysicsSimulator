package simulator.model;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class PhysicsSimulator {
	private double time = 0.0, dt;
	private ForceLaws law;
	private List<Body> bodies;
	private List<SimulatorObserver> observer;
	//array para las notificaciones de solo lectura
	
	public PhysicsSimulator(double time, ForceLaws force) {
		bodies = new ArrayList<Body>();
		observer = new ArrayList<SimulatorObserver>();
		setDeltaTime(time);
		setForceLaws(force);
		reset();
		//_bodiesUnmodifiable = Collections.unmodifiableList(bodies);
	}	
	
	//Aplicador de un paso de simulación
	public void advance() {
		for(Body b1: bodies) {
			b1.resetForce();
		}
		law.apply(bodies);
		for(Body b1: bodies) {
			b1.move(dt);
		}
		time += dt;
		for(SimulatorObserver o : this.observer) {
			o.onAdvance(this.bodies, this.time);
		}
	}
	
	//Añade un cuerpo B al simulador
	public void addBody(Body b) {
		if(!bodies.contains(b)) {
			bodies.add(b);
			for(SimulatorObserver o : this.observer) {
				o.onBodyAdded(this.bodies, b);
			}
		}
		else
			throw new IllegalArgumentException();
	}
	
	//Representación de un estado del simulador
	public JSONObject getState() {
		JSONObject js = new JSONObject();
		JSONArray array = new JSONArray();
		js.put("time", time);
		js.put("bodies", array);
		for(Body b1: bodies) {
			array.put(b1.getState());
		}
		return js;
	}
	
	//Resetear simulador
	public void reset() {
		this.time = 0.0;
		this.bodies = new ArrayList<Body>();
		for(SimulatorObserver o : this.observer) {
			o.onReset(this.bodies, this.time, this.dt, this.law.toString());
		}
	}
	
	//Añadir un nuevo observador
	public void addObserver(SimulatorObserver o) {
		if(!this.observer.contains(o)) {
			this.observer.add(o);
			o.onRegister(this.bodies, this.time, this.dt, this.law.toString());
		}
	}
	
	public String toString() {
		return getState().toString();
	}
	
	public double getTime() {
		return time;
	}
	public void setTime(double time) {
		this.time = time;
	}
	
	public void setDeltaTime(double dt) {
		if(dt <= 0.0) {
			throw new IllegalArgumentException();
		}
		this.dt =dt;
		for(SimulatorObserver o : this.observer) {
			o.onDeltaTimeChanged(this.dt);
		}
	}
	
	public void setForceLaws(ForceLaws forceLaws) {
		if(forceLaws == null) {
			throw new IllegalArgumentException();
		}
		this.law = forceLaws;
		for(SimulatorObserver o : this.observer) {
			o.onForceLawsChanged(this.law.toString());
		}
	}
	
}