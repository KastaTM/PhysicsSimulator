package simulator.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.json.JSONException;
import org.json.JSONObject;

class ForceLawsDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private int _status;
	private ForceTableModel _forceTableModel;
	private List<JSONObject> listJs;
	DefaultComboBoxModel<String> _forceComboModel;
	JComboBox <String> _forceCombo;
	private int indice;
	// This table model stores internally the content of the table. Use
	// getData() to get the content as JSON.
	//
	private class ForceTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		private String[] _header = { "Key", "Value", "Description" };
		String[][] _data;
		private static final int rows = 1;
		private static final int columns = 3;
		
		ForceTableModel() {
			_data = new String[rows][columns];
		}
		
		public void refresh() {
			//_data = new String[getRowCount()][columns];
			JSONObject jo = listJs.get(indice).getJSONObject("data");
			_data = new String[jo.length()][columns];
			int i = 0;
			for(String key : jo.keySet()) {
				_data[i][0] = key;
				_data[i][1] = "";
				_data[i][2] = jo.getString(key);
				++i;
			}
			fireTableStructureChanged();
		}

		@Override
		public String getColumnName(int column) {
			return _header[column];
		}

		@Override
		public int getRowCount() {
			return _data.length;
		}

		@Override
		public int getColumnCount() {
			return _header.length;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return _data[rowIndex][columnIndex];
		}

		@Override
		public void setValueAt(Object o, int rowIndex, int columnIndex) {
			_data[rowIndex][columnIndex] = o.toString();
		}
		
		
		
		// Method getData() returns a String corresponding to a JSON structure
		// with column 1 as keys and column 2 as values.

		// This method return the coIt is important to build it as a string, if
		// we create a corresponding JSONObject and use put(key,value), all values
		// will be added as string. This also means that if users want to add a
		// string value they should add the quotes as well as part of the
		// value (2nd column).
		//
		public String getForceDialog() throws Exception {
			StringBuilder s = new StringBuilder();
			s.append('{');
			s.append('"');
			s.append("type");
			s.append('"');
			s.append(":");
			s.append('"');
			s.append(listJs.get(indice).getString("type"));
			s.append('"');
			s.append(",");
			s.append('"');
			s.append("data");
			s.append('"');
			s.append(":");
			s.append('{');
			
			JSONObject data = listJs.get(indice).getJSONObject("data");
			Set<String> keys = data.keySet();
			int i = 0;
			for(String key: keys) {
				if(hasCharacter(_data[i][1])) {
					throw new Exception ("JSONObject['" + key + "'] is not a number");
				}
				else if(!_data[i][1].isEmpty()) {
					s.append('"');
					s.append(key);
					s.append('"');
					s.append(':');
					s.append(_data[i][1]);
					s.append(',');
				}
				++i;
			}
			
			if(s.length() == ',')
				s.deleteCharAt(s.length() - 1);
			s.append('}');
			s.append('}');
			
			return s.toString();
		}
		
		private boolean hasCharacter(String s) {
			for(int i = 0; i < s.length(); ++i) {
				Character chr = s.charAt(i);
				if(Character.isAlphabetic(chr)) {
					return true;
				}
			}
			return false;
		}
	}


	ForceLawsDialog(Frame parent, List<JSONObject> listaJs) {
		super(parent, true);
		this.listJs = listaJs;
		initGUI();
	}


	private void initGUI() {

		_status = 0;

		setTitle("Force Laws Selection");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);

		// help
		JLabel help = new JLabel("Select a force law and provide values for the paramters in the Value column (default values are used for parametes with no value).");

		help.setAlignmentX(CENTER_ALIGNMENT);
		mainPanel.add(help);

		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		// data table
		_forceTableModel = new ForceTableModel();
		JTable dataTable = new JTable(_forceTableModel) {
			private static final long serialVersionUID = 1L;

			// we override prepareRenderer to resized rows to fit to content
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);
				int rendererWidth = component.getPreferredSize().width;
				TableColumn tableColumn = getColumnModel().getColumn(column);
				tableColumn.setPreferredWidth(
						Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
				return component;
			}
		};
		JScrollPane tabelScroll = new JScrollPane(dataTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mainPanel.add(tabelScroll);

		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		
		//Combo Box
		JPanel comboPanel = new JPanel();
		comboPanel.setLayout(new FlowLayout());
		mainPanel.add(comboPanel);
		JLabel comboLabel = new JLabel("Force Law: ");
		comboPanel.add(comboLabel);
		
		_forceComboModel = new DefaultComboBoxModel<>();
		_forceCombo = new JComboBox<>(_forceComboModel);
		for(JSONObject jo : listJs) {
			_forceComboModel.addElement(jo.getString("desc"));
		}
		this._forceCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				indice = _forceCombo.getSelectedIndex();
				_forceTableModel.refresh();
			}
		});
		comboPanel.add(_forceCombo);

		// bottons
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setAlignmentX(CENTER_ALIGNMENT);

		mainPanel.add(buttonsPanel);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_status = 0;
				ForceLawsDialog.this.setVisible(false);
			}
		});
		buttonsPanel.add(cancelButton);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_status = 1;
				indice = _forceCombo.getSelectedIndex();
				ForceLawsDialog.this.setVisible(false);
			}
		});
		buttonsPanel.add(okButton);

		setPreferredSize(new Dimension(400, 400));

		pack();
		setResizable(false); // change to 'true' if you want to allow resizing
		setVisible(false); // we will show it only whe open is called
	}

	public int open() {

		if (getParent() != null)
			setLocation(//
					getParent().getLocation().x + getParent().getWidth() / 2 - getWidth() / 2, //
					getParent().getLocation().y + getParent().getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
		return _status;
	}

	public JSONObject getJSON() throws JSONException, Exception { 			
		return new JSONObject(_forceTableModel.getForceDialog());
	}

}

	
