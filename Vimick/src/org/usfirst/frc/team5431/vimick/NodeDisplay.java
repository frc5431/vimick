package org.usfirst.frc.team5431.vimick;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

public class NodeDisplay extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private class DragListener implements MouseMotionListener, MouseListener {

		private double mouseStartX, mouseStartY;
		
		@Override
		public void mouseClicked(final MouseEvent e) {
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
			setBorderColor(Color.BLUE);
			getParent().repaint();
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			setBorderColor(Color.RED);
			getParent().repaint();
		}

		@Override
		public void mousePressed(final MouseEvent e) {
			mouseStartX = e.getX();
			mouseStartY = e.getY();
			
			final Set<Integer> toAdd = new HashSet<>();
			if (!Vimick.getFrame().getSelected().isEmpty() && e.isShiftDown()) {
				int startingIndex = Vimick.getFrame().getSelected().iterator().next();
				if (startingIndex > node.index) {
					for (; startingIndex > node.index; --startingIndex) {
						toAdd.add(startingIndex);
					}
				} else {
					for (; startingIndex < node.index; ++startingIndex) {
						toAdd.add(startingIndex);
					}
				}
			} else {
				toAdd.add(node.index);
			}

			if (e.isControlDown()) {
				Vimick.getFrame().addSelected(toAdd);
			} else {
				Vimick.getFrame().setSelected(toAdd);
			}
			getParent().repaint();
		}

		@Override
		public void mouseReleased(final MouseEvent e) {	
		}

		@Override
		public void mouseDragged(final MouseEvent e) {
			double x = (e.getX() + getX() - mouseStartX) / (double) getParent().getHeight(),
					y = (e.getY() + getY() - mouseStartY) / (double) (getParent().getHeight());
			if(x > 1.0) {
				x = 1.0;
			}else if(x < 0.0) {
				x = 0.0;
			}
			
			if(y > 1.0) {
				y = 1.0;
			}else if(y < 0.0) {
				y = 0.0;
			}
			
			getNode().setX(x);
			getNode().setY(y);
			Vimick.getFrame().repaint();
		}

		@Override
		public void mouseMoved(final MouseEvent e) {

		}

	}

	private final Node node;
	private Color borderColor = Color.RED;
	
	public NodeDisplay(final Node no) {
		this.node = no;
		final DragListener drag = new DragListener();
		addMouseListener(drag);
		addMouseMotionListener(drag);
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(final Color borderColor) {
		this.borderColor = borderColor;
	}

	public Node getNode() {
		return node;
	}

	@Override
	protected void paintComponent(final Graphics g) {
		setBounds((int) (node.getX() * getParent().getWidth()), (int) (node.getY() * getParent().getHeight()), 20, 20);

		if (isSelected()) {
			g.setColor(Color.GREEN);
		} else {
			g.setColor(borderColor);
		}
		g.fillOval(0, 0, getWidth(), getHeight());

		if (node.isHome()) {
			g.setColor(Color.YELLOW);
		} else {
			g.setColor(Color.WHITE);
		}
		g.fillOval(5, 5, getWidth() - 10, getHeight() - 10);
	}

	public boolean isSelected() {
		return Vimick.getFrame().getSelected().contains(node.index);
	}
}
