package simulator.factories;

import org.json.JSONObject;
import simulator.model.ForceLaws;
import simulator.model.NoForce;

public class NoForceBuilder extends Builder <ForceLaws> {
	
	public NoForceBuilder() {
		this.typeTag="ng";
		this.desc="No force";
	}

	//Se devuelve un objeto de tipo NoForce
	protected ForceLaws createTheInstance(JSONObject js) {
		return new NoForce();
	}
	
}