package org.usfirst.frc.team5431.vimick;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Map.Entry;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class NodePropertiesDisplay extends JTable {// jtaitel

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton exportButton = new JButton("Export");

	public NodePropertiesDisplay() {
		setCellSelectionEnabled(true);
		//setPreferredScrollableViewportSize(getPreferredSize());
		setFillsViewportHeight(true);
		setDefaultEditor(Double.class, new DefaultCellEditor(new JTextField()));
	}

	
	
	@Override
	public Dimension getMinimumSize() {
		if (Vimick.getFrame() != null) {
			return new Dimension(getParent().getWidth() - getParent().getHeight(),
					Vimick.getFrame().getHeight());
		} else {
			return super.getPreferredSize();
		}
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
	}
}
