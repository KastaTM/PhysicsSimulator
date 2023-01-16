package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class ControlPanel extends JPanel implements SimulatorObserver {

	private Controller _ctrl;
	private boolean _stopped;
	private JToolBar toolBar;
	JButton openButton, physicsButton, runButton, stopButton, exitButton;
	JSpinner stepsSpinner;
	JLabel stepsLabel, deltaLabel;
	JTextField deltaText;
	ForceLawsDialog _dialog;
	
	ControlPanel(Controller ctrl) {
		this._ctrl = ctrl;
		_stopped = true;
		initGUI();
		_ctrl.addObserver(this);
	}
	
	ControlPanel() {
		_stopped = true;
		initGUI();
	}
	
	private void initGUI() {
		this.openButton = new JButton(); //Crear boton
		this.openButton.setToolTipText("Open Bodies"); //Si ponemos raton encima da esa info
		this.openButton.setIcon(new ImageIcon("resources/icons/open.png")); //El icono que queremos
		this.openButton.addActionListener(new ActionListener() { //Lo que hace el boton cuando pulsemos
			public void actionPerformed(ActionEvent e) {
				try {
					loadBodies();
				} catch(Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE); //Titulo del dialogo (ERROR) y contenido de este( joptionpane)
				}
			}
		});
		
		this.physicsButton = new JButton(); 
		this.physicsButton.setToolTipText("Choose Force Law"); 
		this.physicsButton.setIcon(new ImageIcon("resources/icons/physics.png"));
		this.physicsButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
					selectForce();
			}
		});
		
		this.runButton = new JButton(); 
		this.runButton.setToolTipText("Run"); 
		this.runButton.setIcon(new ImageIcon("resources/icons/run.png")); 
		this.runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					run();
				}
				catch(Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE); //Titulo del dialogo (ERROR) y contenido de este( joptionpane)
				}
				
			}
		});
		
		this.stopButton = new JButton(); 
		this.stopButton.setToolTipText("Stop"); 
		this.stopButton.setIcon(new ImageIcon("resources/icons/stop.png")); 
		this.stopButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				_stopped = true;
				enableButtons(true);
			}
		});
		
		this.exitButton = new JButton(); 
		this.exitButton.setToolTipText("Exit"); 
		this.exitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
		this.exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				off();
			}
		});
		
		this.stepsSpinner = new JSpinner(new SpinnerNumberModel(150000,1,1000000000,100)); //valor predeterminado, Min max y de cuanto en cuanto avanza 
		this.stepsSpinner.setPreferredSize(new Dimension(80,40)); 
		stepsSpinner.setMaximumSize(new Dimension(80,40));
		this.deltaText = new JTextField("2500.0",5); //Valor predeterminado para empezar y tamaño
		deltaText.setPreferredSize(new Dimension(80,40));
		deltaText.setMaximumSize(new Dimension(80,40));
		this.stepsLabel = new JLabel("Steps:");
		this.deltaLabel = new JLabel("Delta-Time:");
		
		this.toolBar = new JToolBar(); //Barra del controlPanel
		this.toolBar.setLayout(new BoxLayout(this.toolBar, BoxLayout.LINE_AXIS));
		this.toolBar.add(openButton);
		this.toolBar.addSeparator();
		this.toolBar.add(physicsButton);
		this.toolBar.addSeparator();
		this.toolBar.add(runButton);
		this.toolBar.addSeparator();
		this.toolBar.add(stopButton);
		this.toolBar.addSeparator();
		this.toolBar.add(stepsLabel);
		this.toolBar.addSeparator();
		this.toolBar.add(stepsSpinner);
		this.toolBar.addSeparator();
		this.toolBar.add(deltaLabel);
		this.toolBar.addSeparator();
		this.toolBar.add(deltaText);
		this.toolBar.addSeparator();
		this.toolBar.add(Box.createHorizontalGlue());
		this.toolBar.add(exitButton);
		
		this.setLayout(new BorderLayout());
		this.add(this.toolBar);
		
		
	}
	// other private/protected methods
	
	private void run_sim(int n) {
		if ( n>0 && !_stopped ) {
			try {	
				_ctrl.run(1);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE); //Titulo del dialogo (ERROR) y contenido de este( joptionpane)
				_stopped = true;
				enableButtons(true);
				return;
			}	
			SwingUtilities.invokeLater( new Runnable() {
				@Override
				public void run() {
					run_sim(n-1);
				}
			});
		} else {
			_stopped = true;
			enableButtons(true);
		}
	}
	
	private void loadBodies() throws FileNotFoundException {
		JFileChooser load = new JFileChooser();
		load.showOpenDialog(load);
		File archivo = load.getSelectedFile();
		//try illegalargument
		if(archivo != null) {
			InputStream i = new FileInputStream(archivo);
			_ctrl.reset();
			_ctrl.loadBodies(i);
		}
	}
	
	private void run() { //Método para hacer correr simulador
		try {
			_ctrl.setDeltaTime(Double.parseDouble((deltaText.getText())));
			enableButtons(false);
			_stopped = false;
			run_sim((int) stepsSpinner.getValue());
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE); //Titulo del dialogo (ERROR) y contenido de este( joptionpane)
		}
	}
	
	private void enableButtons(boolean activated) { //Metodo para des/habilitar los botones
		openButton.setEnabled(activated);
		physicsButton.setEnabled(activated);
		exitButton.setEnabled(activated);
		runButton.setEnabled(activated);
	}
	
	private void off() { //Método para cerrar simulador
		int close = JOptionPane.showConfirmDialog(null, "¿Seguro que quieres salir?", "Cerrar", JOptionPane.YES_NO_OPTION);
		if(close == 0)
			System.exit(0);
	}
	
	private void selectForce() { //Método para seleccionar fuerza
		if(_dialog == null) {
			_dialog = new ForceLawsDialog((Frame) SwingUtilities.getWindowAncestor(this), _ctrl.getForceLawsInfo());
		}
		int status = _dialog.open();
		if(status == 1) {
			try {
				_ctrl.setForceLaws(_dialog.getJSON());
			}
			catch(Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	// SimulatorObserver methods


	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String fLawsDesc) {
		this.deltaText.setText(Double.toString(dt));
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String fLawsDesc) {
		this.deltaText.setText(Double.toString(dt));
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {}

	@Override
	public void onAdvance(List<Body> bodies, double time) {}

	@Override
	public void onDeltaTimeChanged(double dt) {
		this.deltaText.setText(Double.toString(dt));
	}

	@Override
	public void onForceLawsChanged(String fLawsDesc) {}
		
}