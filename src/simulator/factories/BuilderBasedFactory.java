package simulator.factories;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class BuilderBasedFactory<T> implements Factory<T>{

	private List<Builder<T>> builders;
	
	public BuilderBasedFactory (List<Builder<T>> builders) {
		this.builders = builders;
	}

	public T createInstance(JSONObject js) {
		if(js != null) {
			for(Builder <T> b: this.builders) {
				T o = b.createInstance(js);
				if(o != null) {
					return o;
				}
			}
		}
		throw new IllegalArgumentException("Error JSON incorrecto");
	}

	public List<JSONObject> getInfo() {
		List <JSONObject> listJs = new ArrayList<JSONObject>();
		JSONObject js;
		
		for(Builder <T> b: this.builders) {
			js = b.getBuilderInfo();
			listJs.add(js);
		}
		return listJs;
	}
}