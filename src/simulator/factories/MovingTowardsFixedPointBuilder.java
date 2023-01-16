package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.misc.Vector2D;
import simulator.model.ForceLaws;
import simulator.model.MovingTowardsFixedPoint;

public class MovingTowardsFixedPointBuilder extends Builder <ForceLaws> {
	public MovingTowardsFixedPointBuilder() {
		this.typeTag = "mtfp";
		this.desc = "Moving towards fixed point";
	}
	
	//Se devuelve un objeto de tipo MovingTowardsFixedPoint con los parámetros obtenidos del JSON en caso de tener clave "G"
	protected ForceLaws createTheInstance(JSONObject js) {
		Double _g=9.81;
		Vector2D _c = new Vector2D();
		JSONArray array = new JSONArray();
		if(js.has("g")){
			_g=js.getDouble("g");
		}
		if(js.has("c")) {
			array=js.getJSONArray("c");
			_c = new Vector2D(array.getDouble(0), array.getDouble(1));
		}
		return new MovingTowardsFixedPoint(_c, _g);
	}
	
	//Devolución de un JSON con información del MovingTowardsFixedPoint y los atributos de este
	protected JSONObject createData() {
		JSONObject moving = new JSONObject();
		moving.put("c", "the point towards which bodies move (a json list of 2 numbers, e.g., [100.0,50.0])");
		moving.put("g", "the length of the acceleration vector (a number)");
		return moving;
	}
	
}