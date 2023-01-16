package simulator.view;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class BodiesTableModel extends AbstractTableModel implements SimulatorObserver {
	
	private List<Body> _bodies;
	private String[] colNames = {"Id", "Mass", "Position", "Velocity", "Force"};
	
	BodiesTableModel(Controller ctrl) {
		_bodies = new ArrayList<>();
		ctrl.addObserver(this);
	}
	@Override
	public int getRowCount() {
		return _bodies == null ? 0 : _bodies.size();
	}
	
	@Override
	public int getColumnCount() {
		return colNames.length;
	}
	@Override
	public String getColumnName(int column) {
		return colNames[column];
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object s = null;
		switch (columnIndex) {
		case 0:
			s = _bodies.get(rowIndex).getId();
			break;
		case 1:
			s = _bodies.get(rowIndex).getMass();
			break;
		case 2:
			s = _bodies.get(rowIndex).getPosition().toString();
			break;
		case 3:
			s = _bodies.get(rowIndex).getVelocity().toString();
			break;
		case 4:
			s = _bodies.get(rowIndex).getForce().toString();
			break;
		}
		return s;
	}
	
	private void update() {
		fireTableDataChanged();
	}
	
	public void setBodiesList(List <Body> bodies) {
		this._bodies = bodies;
		update();
	}
	
	public boolean isCellEditable() {
		return false;
	}
	
	// SimulatorObserver methods
	
	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String fLawsDesc) {
		this._bodies = bodies;
		update();
	}
	
	@Override
	public void onReset(List<Body> bodies, double time, double dt, String fLawsDesc) {
		this._bodies = bodies;
		update();
	}
	
	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		this._bodies = bodies;
		update();
	}
	
	@Override
	public void onAdvance(List<Body> bodies, double time) {
		this._bodies = bodies;
		update();
		
	}
	@Override
	public void onDeltaTimeChanged(double dt) {}
	
	@Override
	public void onForceLawsChanged(String fLawsDesc) {}
	
}