package simulator.control;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import simulator.factories.Factory;
import simulator.model.Body;
import simulator.model.ForceLaws;
import simulator.model.PhysicsSimulator;
import simulator.model.SimulatorObserver;

public class Controller {

	private PhysicsSimulator _ps;
	private Factory <Body> _bodies;
	private Factory <ForceLaws> _fflaws;
	
	public Controller(PhysicsSimulator ps, Factory<Body> bodies) {
		this._ps = ps;
		this._bodies = bodies;
	}
	
	public Controller(PhysicsSimulator ps, Factory<Body> bodies, Factory<ForceLaws> FLaws) {
		this._ps = ps;
		this._bodies = bodies;
		this._fflaws = FLaws;
	}
	
	//Extracción de los cuerpos de un JSONInput y añadidos posteriormente al simulador
	public void loadBodies(InputStream in) {
		JSONObject jsonInupt = new JSONObject(new JSONTokener(in));
		JSONArray b = jsonInupt.getJSONArray("bodies");
		for(int i = 0; i < b.length(); i++) {
			try{ 													// Salta excepcion en caso de que falle el añadir objeto
			_ps.addBody(_bodies.createInstance(b.getJSONObject(i)));
			}
			catch(IllegalArgumentException iae){
			 System.out.println("Error en el body" + i + "iae.toString()");
			}
		}
	}
	
	//Método que ejecuta el simulador en n pasos y muestra los diferentes estados en out
	public void run(int n, OutputStream out, InputStream expOut, StateComparator cmp) throws Exception { //Se matiene para que funcione en modo de la practica anterior
		JSONObject s1 = null, s2;
		JSONArray statesExpected = null;
		if(expOut != null) {
			JSONObject js = new JSONObject(new JSONTokener(expOut));
			statesExpected = js.getJSONArray("states");
		}
		PrintStream print = new PrintStream(out);
		print.println("{");
		print.println("\"states\": [");
		s1 = _ps.getState(); 									//Estado normal
		if(statesExpected != null) {
			s2 = statesExpected.getJSONObject(0); 				//Salida
			if(!cmp.equal(s1,s2)) {
				throw new Exception("Error en en paso 0" + "\n" + s1.toString() + "\n" + s2.toString());
			}
		}
		print.println(s1);
		for(int i = 1; i <= n; i++) {
			_ps.advance();
			s1 = _ps.getState(); 								//Estado normal
			print.print(",");
			print.println(s1);
			if(statesExpected != null) {
				s2 = statesExpected.getJSONObject(i);
				if(cmp.equal(s1,s2)) {
					print.println(s1.toString());
				}
				else {
					throw new Exception("Error en en paso "+ i +"\n" + s1.toString() + "\n" + s2.toString());
				}
			}
		}
		print.println("]");
		print.println("}");		
	}
	
	//////////////////////////////////////
	//v2
	/////////////////////////////////////
	public void reset(){
		this._ps.reset();
	}
	
	public void setDeltaTime(double dt) {
		this._ps.setDeltaTime(dt);
	}
	
	public void addObserver(SimulatorObserver o) {
		this._ps.addObserver(o);
	}
	
	public void run(int n) {
		for(int i = 0; i < n; ++i) {
			this._ps.advance();
		}
	}
	
	public void setForceLaws(JSONObject info) {
		ForceLaws force = this._fflaws.createInstance(info);
		if(force != null) {
			this._ps.setForceLaws(force);
		}
	}
	
	public List<JSONObject>getForceLawsInfo(){
		return _fflaws.getInfo();
	}
	
}