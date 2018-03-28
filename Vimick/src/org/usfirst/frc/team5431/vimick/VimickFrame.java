package org.usfirst.frc.team5431.vimick;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

public class VimickFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final NodeCanvas canvas = new NodeCanvas();
	private final NodePropertiesDisplay properties = new NodePropertiesDisplay();

	private int selected = -1;// index of selected node

	public VimickFrame() {
		super("Vimick");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(1000, 700);

		setLayout(new FlowLayout(FlowLayout.LEADING,0, 0));
		
		add(canvas);
		add(properties);

		canvas.addNode(new Node(0.5, 0.5));
		canvas.addNode(new Node(0.6, 0.5));
		canvas.addNode(new Node(0.65, 0.6));
	}

	public NodeCanvas getCanvas() {
		return canvas;
	}

	public void addNode(final Node no) {
		canvas.addNode(no);
	}

	public int getSelected() {
		return selected;
	}

	public NodeDisplay getSelectedNode() {
		if (selected < 0) {
			return null;
		} else {
			return canvas.getNodes().get(selected);
		}
	}

	public void setSelected(int selected) {
		if (this.selected >= 0) {
			getSelectedNode().setSelected(false);
		}
		this.selected = selected;
		if (this.selected >= 0) {
			getSelectedNode().setSelected(true);
			properties.setModel(new NodeTableModel(getSelectedNode().getNode()));
		}else {
			properties.setModel(new DefaultTableModel());
		}
	}
}
