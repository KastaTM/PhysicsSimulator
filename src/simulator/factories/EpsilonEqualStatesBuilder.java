package simulator.factories;

import org.json.JSONObject;
import simulator.control.EpsilonEqualStates;
import simulator.control.StateComparator;

public class EpsilonEqualStatesBuilder extends Builder<StateComparator>{

	public EpsilonEqualStatesBuilder() {
		this.typeTag = "epseq";
		this.desc = "State Epsilon's comparator";
	}
	
	//Se devuelve un objeto de tipo EpsilonEqualStates con los parámetros obtenidos del JSON
	protected StateComparator createTheInstance(JSONObject js) {
		if(!js.has("eps")){
			return new EpsilonEqualStates(0.0);
		}
		else {
			Double _eps = js.getDouble("eps");
			return new EpsilonEqualStates(_eps);
		}
	}
	
	//Devolución de un JSON con información del Epsilon
	protected JSONObject createData() {
		JSONObject epsilonequalstates = new JSONObject();
		epsilonequalstates.put("eps", "Epsilon value");	
		return epsilonequalstates;
	}
	
}