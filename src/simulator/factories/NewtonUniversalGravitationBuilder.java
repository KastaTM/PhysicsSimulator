package simulator.factories;

import org.json.JSONObject;
import simulator.model.ForceLaws;
import simulator.model.NewtonUniversalGravitation;

public class NewtonUniversalGravitationBuilder extends Builder <ForceLaws> {

	public NewtonUniversalGravitationBuilder() {
		this.typeTag="nlug";
		this.desc="Newton’s law of universal gravitation";
	}
	
	//Se devuelve un objeto de tipo NewtonUniversalGravitation con los parámetros obtenidos del JSON en caso de tener clave "G"
	protected ForceLaws createTheInstance(JSONObject js) {
		if(!js.has("G")){
			return new NewtonUniversalGravitation(6.67E-11);
		}
		else {
			Double _g = js.getDouble("eps");
			return new NewtonUniversalGravitation(_g);
		}
	}
	
	//Devolución de un JSON con información del NewtonUniversalGravitation y los atributos de este
	protected JSONObject createData() {
		JSONObject newton = new JSONObject();
		newton.put("G", "the gravitational constant (a number)");
		return newton;
	}
	
}