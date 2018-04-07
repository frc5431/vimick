package org.usfirst.frc.team5431.vimick;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class NodeCanvas extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final List<NodeDisplay> nodes = new ArrayList<>();
	private boolean hasChanged = false;
	private Rectangle selectionBox = new Rectangle();

	private class DragListener implements MouseListener, MouseMotionListener {
		
		@Override
		public void mouseDragged(final MouseEvent e) {
			selectionBox.width = e.getX() - selectionBox.x;
			selectionBox.height = e.getY() - selectionBox.y;
			repaint();
		}

		@Override
		public void mouseMoved(final MouseEvent e) {
		}

		@Override
		public void mouseClicked(final MouseEvent e) {
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
		}

		@Override
		public void mouseExited(final MouseEvent e) {
		}

		@Override
		public void mousePressed(final MouseEvent e) {
			selectionBox = new Rectangle(e.getX(), e.getY(), 0, 0);
			Vimick.getFrame().clearSelected();
			getParent().repaint();
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			selectionBox = new Rectangle(0, 0, 0, 0);
			repaint();
		}

	}

	public NodeCanvas() {
		setSize(700, 700);

		final DragListener drag = new DragListener();
		addMouseListener(drag);
		addMouseMotionListener(drag);
	}

	@Override
	public Dimension getPreferredSize() {
		if (Vimick.getFrame() != null) {
			return new Dimension(Vimick.getFrame().getHeight(), Vimick.getFrame().getHeight());
		} else {
			return super.getPreferredSize();
		}
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);

		try {
			g.drawImage(ImageIO.read(new File("field.png")), 0, 0, getWidth(), getHeight(), null);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		NodeDisplay lastNode = null;
		for (final NodeDisplay node : nodes) {
			final boolean isLastSelected = lastNode != null && lastNode.isSelected();
			final boolean isSelected = node.isSelected();
			if (isSelected || isLastSelected) {
				if (isLastSelected && isSelected) {
					g.setColor(Color.GREEN);
				} else {
					g.setColor(Color.YELLOW);
				}
			} else {
				g.setColor(Color.RED);
			}
			if (lastNode != null) {
				g.drawLine(lastNode.getX() + (lastNode.getWidth() / 2), lastNode.getY() + (lastNode.getHeight() / 2),
						node.getX() + (node.getWidth() / 2), node.getY() + (node.getHeight() / 2));
			}
			lastNode = node;
		}

		final Rectangle toDraw = new Rectangle(selectionBox.x, selectionBox.y, selectionBox.width, selectionBox.height);
		if(toDraw.width < 0) {
			toDraw.x = selectionBox.x - Math.abs(toDraw.width);
			toDraw.width = Math.abs(toDraw.width);
		}
		if(toDraw.height < 0) {
			toDraw.y = selectionBox.y - Math.abs(toDraw.height);
			toDraw.height = Math.abs(toDraw.height);
		}
		
		g.setColor(new Color(100, 100,255,128));
		g.fillRect(toDraw.x, toDraw.y, toDraw.width, toDraw.height);
		g.setColor(Color.BLUE);
		g.drawRect(toDraw.x, toDraw.y, toDraw.width, toDraw.height);

		final File currentFile = Vimick.getFrame().getCurrentFile();
		if (currentFile == null) {
			Vimick.getFrame().setTitle("Vimick");
		} else {
			Vimick.getFrame().setTitle(currentFile.getName() + " | Vimick" + (hasChanged ? " *" : ""));
		}
	}

	public void flagChange() {
		hasChanged = true;
	}

	public void flagSave() {
		hasChanged = false;
	}

	public List<NodeDisplay> getNodes() {
		return nodes;
	}

	public void clearNodes() {
		nodes.clear();
		removeAll();
		flagChange();
	}

	public void addNode(final Node no) {
		final NodeDisplay dis = new NodeDisplay(no);
		final int index = nodes.size();
		no.index = index;
		nodes.add(dis);
		add(dis);
		flagChange();
	}

	public void removeNode(final int index) {
		nodes.remove(index);
		remove(index);
		getParent().repaint();
		flagChange();
	}
}
