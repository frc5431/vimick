package org.usfirst.frc.team5431.vimick;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class NodeDisplay extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int index = 0;

	private class DragListener implements MouseMotionListener, MouseListener, KeyListener {

		private double mouseStartX, mouseStartY;

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
			mouseStartX = e.getX();
			mouseStartY = e.getY();
		}

		@Override
		public void mouseReleased(final MouseEvent e) {

		}
		
		@Override
		public void keyPressed(KeyEvent arg0) {
			System.out.println("DELETE");
			if(arg0.getID() == KeyEvent.VK_BACK_SPACE || arg0.getID() == KeyEvent.VK_DELETE) {
				System.out.println("DELETE");
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			
		}

		@Override
		public void mouseDragged(final MouseEvent e) {
			System.out.println(mouseStartY + " " + e.getYOnScreen());
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
	private boolean selected = false;
	
	public NodeDisplay(final Node no) {
		this.node = no;
		final DragListener drag = new DragListener();
		addMouseListener(drag);
		addMouseMotionListener(drag);
		addKeyListener(drag);
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

		if (selected) {
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
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
