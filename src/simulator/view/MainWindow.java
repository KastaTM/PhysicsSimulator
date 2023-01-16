package simulator.view;

import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import simulator.control.Controller;


public class MainWindow extends JFrame {
	
	Controller _ctrl;
	public MainWindow(Controller ctrl) {
		super("Physics Simulator");
		_ctrl = ctrl;
		initGUI();
	}
	
	private void initGUI() {
		JPanel mainPanel = new JPanel();
		setContentPane(mainPanel);
		mainPanel.setLayout(new BorderLayout());
		ControlPanel controlPanel = new ControlPanel(_ctrl);
		mainPanel.add(controlPanel, BorderLayout.PAGE_START);
		JPanel statusBar = new StatusBar(_ctrl);
		mainPanel.add(statusBar, BorderLayout.PAGE_END);
		JPanel centrePanel = new JPanel();
		centrePanel.setLayout(new BoxLayout(centrePanel, BoxLayout.Y_AXIS));
		centrePanel.add(new BodiesTable(_ctrl));
		centrePanel.add(new Viewer(_ctrl));
		mainPanel.add(centrePanel, BorderLayout.CENTER);
		setVisible(true);
		setLocation(450,200);
		setSize(700,400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
	}
	
}