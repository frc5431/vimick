package org.usfirst.frc.team5431.vimick;

import java.util.Map;

import javax.swing.table.AbstractTableModel;

public class NodeTableModel extends AbstractTableModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final Node node;
	
	public NodeTableModel(final Node no) {
		this.node = no;
	}
	
	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return node.getProperties().size();
	}

	@Override
	public Object getValueAt(final int row, final int col) {
		final Object[] values = node.getProperties().entrySet().toArray();
		@SuppressWarnings("unchecked")
		final Map.Entry<String, Double> value = (Map.Entry<String, Double>)values[row];
		if(col == 0) {
			return value.getKey();
		}else {
			return value.getValue();
		}
	}

	@Override
	public Class<?> getColumnClass(final int col) {
		if(col == 0) {
			return String.class;
		}else {
			return Double.class;
		}
	}

	@Override
	public void setValueAt(final Object val, final int row, final int col) {
		final Object[] values = node.getProperties().entrySet().toArray();
		@SuppressWarnings("unchecked")
		final Map.Entry<String, Double> value = (Map.Entry<String, Double>)values[row];
		if(col == 0) {
			node.getProperties().put(val.toString(), value.getValue());
			node.getProperties().remove(value.getKey());
		}else {
			value.setValue(Double.parseDouble(val.toString()));
		}
		Vimick.getFrame().repaint();
	}

	@Override
	public int findColumn(final String name) {
		if(name.equals("Key")) {
			return 0;
		}else {
			return 1;
		}
	}

	@Override
	public String getColumnName(final int ind) {
		if(ind == 0) {
			return "Key";
		}else {
			return "Value";
		}
	}

	@Override
	public boolean isCellEditable(final int row, final int col) {
		return col >= 1;
	}

}
