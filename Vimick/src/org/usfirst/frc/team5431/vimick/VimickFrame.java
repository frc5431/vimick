package org.usfirst.frc.team5431.vimick;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.usfirst.frc.team5431.vimick.Mimick.MimickException;
import org.usfirst.frc.team5431.vimick.Transform.AbsoluteStep;

public class VimickFrame extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final NodeCanvas canvas = new NodeCanvas();
	private final NodePropertiesDisplay properties = new NodePropertiesDisplay();
	private final JMenuBar menubar = new JMenuBar();
	private Transform transform;
	private File currentFile = null;

	private int selected = -1;// index of selected node

	public VimickFrame() {
		super("Vimick");
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(1000, 700);

		setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));

		add(canvas);
		add(properties);

		final JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);

		final JMenuItem loadItem = new JMenuItem("Load");
		loadItem.setMnemonic(KeyEvent.VK_L);
		loadItem.setToolTipText("Load an Mimic file from disc");
		loadItem.addActionListener((final ActionEvent event) -> {
			final JFileChooser fileChooser = new JFileChooser();
			fileChooser.setSelectedFile(currentFile);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setFileFilter(new FileNameExtensionFilter("Mimic File", "mimic"));
			final int returnValue = fileChooser.showOpenDialog(this);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				try {
					currentFile = fileChooser.getSelectedFile();
					transform = new Transform(324, 324, 20, 20); // 324 inches by 324 inches
					transform.parse(currentFile.getAbsolutePath());
										
					final List<AbsoluteStep> steps = transform.toAbsolute();
					canvas.clearNodes();
					final List<Node> nodes = transform.toNodes(steps);
					for (final Node node : nodes) {
						canvas.addNode(node); //new Node(step.x, 1.0 - step.y));
					}

					validate();
					repaint();
				} catch (final MimickException e) {
					e.printStackTrace();
				}
			}
		});
		file.add(loadItem);
		
		final JMenuItem saveItem = new JMenuItem("Save");
		saveItem.setMnemonic(KeyEvent.VK_S);
		saveItem.setToolTipText("Save the current Mimic file");
		saveItem.addActionListener((final ActionEvent event) -> {
			save();
		});
		file.add(saveItem);
		
		final JMenuItem saveAsItem = new JMenuItem("Save As");
		saveAsItem.setMnemonic(KeyEvent.VK_A);
		saveAsItem.setToolTipText("Save as a new Mimic file");
		saveAsItem.addActionListener((final ActionEvent event) -> {
			final JFileChooser fileChooser = new JFileChooser();
			fileChooser.setSelectedFile(currentFile);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
			fileChooser.setFileFilter(new FileNameExtensionFilter("Mimic File", "mimic"));
			final int returnValue = fileChooser.showSaveDialog(this);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				currentFile = fileChooser.getSelectedFile();
				save();
			}
			//transform.toRelative(canvas.getNodes());
		});
		file.add(saveAsItem);

		final JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic(KeyEvent.VK_E);
		exitItem.setToolTipText("Exit application");
		exitItem.addActionListener((final ActionEvent event) -> {
			System.exit(0);
		});

		file.add(exitItem);

		menubar.add(file);

		setJMenuBar(menubar);

		// canvas.addNode(new Node(0.5, 0.5));
		// canvas.addNode(new Node(0.6, 0.5));
		// canvas.addNode(new Node(0.65, 0.6));
	}
	
	public void save() {
		try {
			List<AbsoluteStep> steps = transform.toSteps(canvas.getNodes());
			transform.toRelative(steps);
			transform.save(currentFile);
		} catch (MimickException e) {
			e.printStackTrace();
		}
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
		} else {
			properties.setModel(new DefaultTableModel());
		}
	}

	public Transform getTransform() {
		return transform;
	}

	public void setTransform(final Transform transform) {
		this.transform = transform;
	}

	public File getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(final File currentFile) {
		this.currentFile = currentFile;
	}
}
