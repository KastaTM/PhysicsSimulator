package simulator.control;

import org.json.JSONArray;
import org.json.JSONObject;

public class MassEqualStates implements StateComparator{

	//Metodo para obtener si dos estados son iguales comparando claves time, id y mass
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
					if(!b3.getString("id").equals(b4.getString("id")) || (b3.getDouble("mass") != b4.getDouble("mass"))){
						areEqual=false;
					}
				}
			}
		}
		return areEqual;
	}
}