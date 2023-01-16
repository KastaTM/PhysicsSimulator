package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.misc.Vector2D;
import simulator.model.Body;
import simulator.model.MassLossingBody;

public class MassLossingBodyBuilder extends Builder <Body>{

	public MassLossingBodyBuilder() {
		this.typeTag = "mlb";
		this.desc = "Mass losing body";
	}

	//Se devuelve un objeto de tipo MassLossingBody con los parámetros obtenidos del JSON
	protected Body createTheInstance(JSONObject js) {
		String idBody=js.getString("id");
		double mass=js.getDouble("m");
		JSONArray a = js.getJSONArray("p");
		Vector2D position = new Vector2D (a.getDouble(0), a.getDouble(1));
		a = js.getJSONArray("v");
		Vector2D vel = new Vector2D (a.getDouble(0), a.getDouble(1));
		double lossFreq = js.getDouble("freq");
		double lossFact=js.getDouble("factor");
		
		return new MassLossingBody(idBody, vel, position, mass, lossFact, lossFreq);
	}
	
	//Devolución de un JSON con información del masslosingbody y los atributos de este
	protected JSONObject createData() {
		JSONObject massLosingBody = new JSONObject();
		massLosingBody.put("id", "Identifier");
		massLosingBody.put("v", "Velocity of body");
		massLosingBody.put("p","Position of the body");
		massLosingBody.put("m", "Mass of the body");
		massLosingBody.put("freq", "Time frequency where the body loses mass");
		massLosingBody.put("factor","Mass loss factor");
		return massLosingBody;
	}
		
}