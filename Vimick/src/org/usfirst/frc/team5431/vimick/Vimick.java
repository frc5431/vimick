package org.usfirst.frc.team5431.vimick;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Vimick {

	private static final VimickFrame frame = new VimickFrame();

	public static void main(final String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		frame.setVisible(true);
	}

	public static VimickFrame getFrame() {
		return frame;
	}
}
