package simulator.factories;

import org.json.JSONObject;
import simulator.control.MassEqualStates;
import simulator.control.StateComparator;

public class MassEqualStatesBuilder extends Builder<StateComparator>{

	public MassEqualStatesBuilder() {
		this.typeTag="mes";
		this.desc= "Mass comparator";
	}
	
	//Se devuelve un objeto de tipo MassEqualStates
	protected StateComparator createTheInstance(JSONObject js) {
		return new MassEqualStates();
	}
	
	//Devolución de un JSON con información del body y los atributos de este
	protected JSONObject createData() {
		JSONObject basicbody = new JSONObject();
		basicbody.put("id", "this is the body id");	
		basicbody.put("p", "Body's position vector");
		basicbody.put("v", "Body's velocity vector");
		basicbody.put("m", "Body's mass");
		basicbody.put("freq", "Frecuency that mass goes down");
		basicbody.put("factor", "Decreasing mass factor");
		return basicbody;
	}
}