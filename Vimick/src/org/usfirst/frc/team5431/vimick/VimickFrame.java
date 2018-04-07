package org.usfirst.frc.team5431.vimick;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.usfirst.frc.team5431.vimick.Mimick.MimickException;
import org.usfirst.frc.team5431.vimick.Transform.AbsoluteStep;

public class VimickFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final NodeCanvas canvas = new NodeCanvas();
	private final NodePropertiesDisplay properties = new NodePropertiesDisplay();
	private final JMenuBar menubar = new JMenuBar();
	private Transform transform;
	private File currentFile = null;

	private Set<Integer> selected = new HashSet<>();// index of selected nodes

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
			load();
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
			saveAs();
			// transform.toRelative(canvas.getNodes());
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

		getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "open");
		getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "save");
		getRootPane().getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK),
				"save as");
		getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "node up");
		getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "node down");
		getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "node left");
		getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "node right");
		getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "node delete");
		getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), "select all");

		getRootPane().getActionMap().put("open", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				load();

			}

		});

		getRootPane().getActionMap().put("save", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				save();
			}

		});

		getRootPane().getActionMap().put("save as", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				saveAs();
			}

		});
		
		getRootPane().getActionMap().put("node left", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				forAllSelected((node) -> node.setX(node.getX() - 0.01));
				repaint();
			}

		});

		getRootPane().getActionMap().put("node right", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				forAllSelected((node) -> node.setX(node.getX() + 0.01));
				repaint();
			}

		});


		getRootPane().getActionMap().put("node up", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				forAllSelected((node) -> node.setY(node.getY() - 0.01));
				repaint();
			}

		});

		getRootPane().getActionMap().put("node down", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				forAllSelected((node) -> node.setY(node.getY() + 0.01));//y is inversed
				repaint();
			}

		});

		getRootPane().getActionMap().put("node delete", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (!selected.isEmpty()) {
					for (final Integer node : selected) {
						canvas.removeNode(node);
					}
					clearSelected();
					repaint();
				}
			}

		});
		
		getRootPane().getActionMap().put("select all", new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				for(int i = 0; i < canvas.getNodes().size(); ++i) {
					addSelected(i);
				}
				repaint();
			}

		});
	}

	public void save() {
		try {
			final List<AbsoluteStep> steps = transform.toSteps(canvas.getNodes());
			transform.fromRelative(steps);
			transform.save(currentFile);
		} catch (final MimickException | IOException e) {
			e.printStackTrace();
		}
		canvas.flagSave();
		repaint();
	}

	public void saveAs() {
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
	}

	public void load() {
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
					canvas.addNode(node); // new Node(step.x, 1.0 - step.y));
				}

				validate();
				repaint();

				canvas.flagSave();
			} catch (final MimickException e) {
				e.printStackTrace();
			}
		}
	}

	public NodeCanvas getCanvas() {
		return canvas;
	}

	public void addNode(final Node no) {
		canvas.addNode(no);
	}

	public Set<Integer> getSelected() {
		return selected;
	}

	public List<Node> getSelectedNodes() {
		final List<Node> out = new ArrayList<>();
		for (final int select : selected) {
			out.add(canvas.getNodes().get(select).getNode());
		}
		return out;
	}

	public void forAllSelected(final Consumer<Node> cons) {
		selected.forEach((index) -> cons.accept(canvas.getNodes().get(index).getNode()));
	}

	public void addSelected(final int selected) {
		this.selected.add(selected);
		// properties.setModel(new NodeTableModel(getSelectedNode().getNode()));
	}
	
	public void addSelected(final Set<Integer> selected) {
		this.selected.addAll(selected);
	}

	public void setSelected(final int selected) {
		clearSelected();
		addSelected(selected);
	}

	public void setSelected(final Set<Integer> selected) {
		clearSelected();
		addSelected(selected);
	}
	
	public void clearSelected() {
		selected.clear();
		properties.setModel(new DefaultTableModel());
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
