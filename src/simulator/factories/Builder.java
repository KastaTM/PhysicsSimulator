package simulator.factories;

import org.json.JSONObject;

public abstract class Builder<T> {

	protected String typeTag;
	protected String desc;
	
	//Si la informacion proporcionada es correcta crea un objeto de tipo T
	public T createInstance(JSONObject info) {
		T gen = null;
		
		if(typeTag != null && typeTag.equals(info.getString("type"))) {
			gen = createTheInstance(info.getJSONObject(("data")));
		}
		return gen;
	}

	protected abstract T createTheInstance(JSONObject js);

	//Devuelve un objeto JSON que sirve de plantilla para el correspondiente constructor
	public JSONObject getBuilderInfo(){
		JSONObject info = new JSONObject();
		info.put("type", typeTag);
		info.put("data", createData());
		info.put("desc", desc);
		return info;
	}
	
	protected JSONObject createData() {
		return new JSONObject();
	}
	
}