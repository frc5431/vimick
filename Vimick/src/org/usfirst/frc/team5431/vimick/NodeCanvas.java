package org.usfirst.frc.team5431.vimick;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class NodeCanvas extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final List<NodeDisplay> nodes = new ArrayList<>();

	public NodeCanvas() {
		setSize(700, 700);

		addMouseListener(new MouseListener() {
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
				Vimick.getFrame().setSelected(-1);
				getParent().repaint();
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
			}
		});
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

		g.setColor(Color.RED);
		NodeDisplay lastNode = null;
		for (final NodeDisplay node : nodes) {
			if (lastNode != null) {
				g.drawLine(lastNode.getX() + (lastNode.getWidth() / 2), lastNode.getY() + (lastNode.getHeight() / 2),
						node.getX()  + (node.getWidth() / 2), node.getY()  + (node.getHeight() / 2));
			}
			lastNode = node;
		}
	}

	public List<NodeDisplay> getNodes() {
		return nodes;
	}

	public void addNode(final Node no) {
		final NodeDisplay dis = new NodeDisplay(no);
		nodes.add(dis);
		add(dis);
		final int index = nodes.size() - 1;
		dis.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(final MouseEvent e) {
			}

			@Override
			public void mouseEntered(final MouseEvent e) {
				dis.setBorderColor(Color.BLUE);
				getParent().repaint();
			}

			@Override
			public void mouseExited(final MouseEvent e) {
				dis.setBorderColor(Color.RED);
				getParent().repaint();
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				Vimick.getFrame().setSelected(index);
				getParent().repaint();
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
			}

		});
	}

}
