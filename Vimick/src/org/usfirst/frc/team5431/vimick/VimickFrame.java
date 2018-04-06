package org.usfirst.frc.team5431.vimick;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import org.usfirst.frc.team5431.vimick.Mimick.MimickException;
import org.usfirst.frc.team5431.vimick.Mimick.Stepper;
import org.usfirst.frc.team5431.vimick.Transform.AbsoluteStep;

public class VimickFrame extends JFrame implements KeyListener {

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
		
		JButton export = new JButton("Export");
		export.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//if(arg0 == ActionEvent.)
				System.out.println("ACTION PERFORMED");
			}
		});
		add(export);

		this.addKeyListener(this);
		this.requestFocus();
		
		Transform t = new Transform(324, 324, 20, 20); //324 inches by 324 inches
		try {
			t.parse("C:\\Users\\AcademyHSRobotics\\Downloads\\LEFT_LEFT_SWITCH.mimic");
			List<AbsoluteStep> steps = t.toAbsolute();
			for(AbsoluteStep step : steps) {
				canvas.addNode(new Node(step.x, 1.0 - step.y));
			}
			
			t.toRelative(steps);
			t.save("C:\\Users\\AcademyHSRobotics\\Downloads\\Relative.mimic");
			/*final List<Stepper> steps = t.getData();
			System.out.println("Total steps: " + steps.size());
			for(int ind = 0; ind < 1; ind++) {
				System.out.print(steps.get(ind).toString());
			}*/
		} catch(MimickException err) {
			err.printStackTrace();
		}
		
		//canvas.addNode(new Node(0.5, 0.5));
		//canvas.addNode(new Node(0.6, 0.5));
		//canvas.addNode(new Node(0.65, 0.6));
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

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
