package simulator.control;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.misc.Vector2D;

public class EpsilonEqualStates implements StateComparator{

	private double _eps;
	
	public EpsilonEqualStates(double eps) {
		this._eps = eps;
	}
	
	//Metodo para obtener si dos estados son iguales
	public boolean equal(JSONObject s1, JSONObject s2) {
		boolean areEqual=true;
		if(s1.getDouble("time")==s2.getDouble("time")) {
			areEqual=false;
		}
		else {
			JSONArray b1 = s1.getJSONArray("bodies"), b2 = s2.getJSONArray("bodies");
			if(b1.length() != b2.length()) {
				areEqual = false;
			}
			else {
				for(int i = 0; i < b1.length() && areEqual; i++) {
					JSONObject b3 = b1.getJSONObject(i), b4 = b2.getJSONObject(i);
					//Posición
					JSONArray a1 = b3.getJSONArray("p");
					Vector2D p1 = new Vector2D (a1.getDouble(0), a1.getDouble(1));
					JSONArray a2 = b4.getJSONArray("p");
					Vector2D p2 = new Vector2D (a2.getDouble(0), a2.getDouble(1));
					//Velocidad
					JSONArray a3 = b3.getJSONArray("v");
					Vector2D v1 = new Vector2D (a3.getDouble(0), a3.getDouble(1));
					JSONArray a4 = b4.getJSONArray("v");
					Vector2D v2 = new Vector2D (a4.getDouble(0), a4.getDouble(1));
					//Fuerza
					JSONArray a5 = b3.getJSONArray("f");
					Vector2D f1 = new Vector2D (a5.getDouble(0), a5.getDouble(1));
					JSONArray a6 = b4.getJSONArray("f");
					Vector2D f2 = new Vector2D (a6.getDouble(0), a6.getDouble(1));
					if(!b3.getString("id").equals(b4.getString("id"))&& (Math.abs(b3.getDouble("mass")- b4.getDouble("mass")) > this._eps)){
						areEqual=false;
					}
					else if(p1.distanceTo(p2) > this._eps || (v1.distanceTo(v2) > this._eps) || (f1.distanceTo(f2)> this._eps)) {
						areEqual=false;
					}
				}
			}
		}
		return areEqual;
	}
}