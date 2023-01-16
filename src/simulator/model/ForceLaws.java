package simulator.model;

import java.util.List;

public abstract interface ForceLaws {
	public void apply(List<Body> bodies);
}