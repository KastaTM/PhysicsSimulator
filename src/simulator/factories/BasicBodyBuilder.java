package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.misc.Vector2D;
import simulator.model.Body;

public class BasicBodyBuilder extends Builder <Body>{

	public BasicBodyBuilder() {
		this.typeTag = "basic";
		this.desc = "Default body";
	}
	
	//Se devuelve un objeto de tipo Body con los parámetros obtenidos del JSON
	protected Body createTheInstance(JSONObject js) {
			String idBody = js.getString("id");
			double mass = js.getDouble("m");
			JSONArray a = js.getJSONArray("p"), b = js.getJSONArray("v");
			Vector2D position = new Vector2D (a.getDouble(0), a.getDouble(1));
			Vector2D vel = new Vector2D (b.getDouble(0), b.getDouble(1));
			return new Body(idBody, vel, position, mass);
	}
	
	//Devolución de un JSON con información del body y los atributos de este
	protected JSONObject createData() {
		JSONObject basicbody = new JSONObject();
		basicbody.put("id", "This is the body id");	
		basicbody.put("p", "Body's position vector");
		basicbody.put("v", "Body's velocity vector");
		basicbody.put("m", "Body's mass");
		return basicbody;
	}
}