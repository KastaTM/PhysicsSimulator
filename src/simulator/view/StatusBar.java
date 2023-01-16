package simulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class StatusBar extends JPanel implements SimulatorObserver{

	private JLabel timeLabel, bodiesLabel, forceLabel;
	
	
	StatusBar(Controller c) {
		initGUI();
		c.addObserver(this);
	}
	
	public void initGUI() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setBorder(BorderFactory.createBevelBorder(1));
		
		//Creamos paneles para cada campo
		//Tiempo de ejecución
		JPanel timePanel = new JPanel();
		timePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		timePanel.setPreferredSize(new Dimension(150,20));
		timeLabel = new JLabel("Time: ");
		timePanel.add(timeLabel);
		this.add(timePanel);
		
		//Bodies en el simulador
		JPanel bodiesPanel = new JPanel();
		bodiesPanel.setLayout(new FlowLayout());
		bodiesPanel.setPreferredSize(new Dimension(50,20));
		bodiesLabel = new JLabel("Bodies: ");
		bodiesPanel.add(bodiesLabel);
		this.add(bodiesPanel);
		
		//Ley aplicada
		JPanel forcePanel = new JPanel();
		forcePanel.setLayout(new FlowLayout());
		forcePanel.setPreferredSize(new Dimension(700,20));
		forceLabel = new JLabel("Laws: ");
		forcePanel.add(forceLabel);
		this.add(forcePanel);
	}
	
	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String fLawsDesc) {
		this.timeLabel.setText("Time: "+ time);
		this.bodiesLabel.setText("Bodies: "+ bodies.size());
		this.forceLabel.setText("Laws: " + fLawsDesc);
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String fLawsDesc) {
		this.timeLabel.setText("Time: "+ time);
		this.bodiesLabel.setText("Bodies: "+ bodies.size());
		this.forceLabel.setText("Laws: " + fLawsDesc);
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		this.bodiesLabel.setText("Bodies: "+ bodies.size());
		
	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
		this.timeLabel.setText("Time: "+ time);
		this.bodiesLabel.setText("Bodies: "+ bodies.size());
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
		
	}

	@Override
	public void onForceLawsChanged(String fLawsDesc) {
		this.forceLabel.setText("Laws: " + fLawsDesc);
	}

}
