package org.usfirst.frc.team5431.vimick;

public class Vimick {

	private static final VimickFrame frame = new VimickFrame();

	public static void main(final String[] args) {
		frame.setVisible(true);
	}

	public static VimickFrame getFrame() {
		return frame;
	}
}
