import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.*;

public final class ServerConstants
{
	// checked against using equals(), order does not matter between, but should appear at top
	public static final String READY_TO_PLAY = 			"\1";
	public static final String GAME_IN_SESSION =		"\1\1";
	
	// checked against using startsWith(), must appear last to first
	public static final String ADD_PLAYER = 			"\1\1\1";
	public static final String DELETE_PLAYER = 			"\1\1\1\1";
	public static final String WAIT_BEFORE_PLAY = 		"\1\1\1\1\1";
	public static final String SET_TEAM = 				"\1\1\1\1\1\1";
	public static final String CREATE_BULLET = 			"\1\1\1\1\1\1\1";
	public static final String TERMINATE_BULLET = 		"\1\1\1\1\1\1\1\1";
	public static final String MOVE_PLAYER_RIGHT =	 	"\1\1\1\1\1\1\1\1\1";
	public static final String MOVE_PLAYER_LEFT =	 	"\1\1\1\1\1\1\1\1\1\1";
	public static final String MOVE_PLAYER_DOWN = 		"\1\1\1\1\1\1\1\1\1\1\1";
	public static final String MOVE_PLAYER_UP = 		"\1\1\1\1\1\1\1\1\1\1\1\1";
	public static final String UPDATE_BULLET = 			"\1\1\1\1\1\1\1\1\1\1\1\1\1";
	public static final String DECREASE_PLAYER_HEALTH = "\1\1\1\1\1\1\1\1\1\1\1\1\1\1";
	public static final String REVIVE_PLAYER = 			"\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1";
	public static final String DECREASE_TOWER_HEALTH = 	"\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1";
	
	// used as a separator, must have special character
	public static final String NAME_SEPERATOR = "\2";
	
	// constant numerical fields
	public static final int PORT_NUMBER = 4444;
	public static final int WAIT_TIME = 3;
	public static final int BULLET_SIZE = 10;
	public static final int TOWER_SIZE = 75;
	public static final int FRAME_SIZE = 600;
	public static final int BOARD_SIZE = 1350;
	public static final int BOARD_FRAGMENTS = 27;
	public static final int FRAGMENT_SIZE = BOARD_SIZE / BOARD_FRAGMENTS;
	public static final int HEALTH = 100;
	public static final int HEALTH_DECREASE = 5;
	public static final int MOVE_LENGTH = 5;
	public static final int WALL_THICKNESS = 5;

	public static String getFromMessage(JFrame parent, String title, String field, String def)
	{
		JTextField enter = new JTextField();
		JComponent[] components = new JComponent[] {
			new JLabel(field + ":"),
			enter
		};
		int result = JOptionPane.showConfirmDialog(parent, components, title, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION)
			return enter.getText();
		return def;
	}

	public static void showMessage(JFrame parent, String title, String content)
	{
		JOptionPane.showMessageDialog(parent, content, title, JOptionPane.INFORMATION_MESSAGE);
	}

	public static String getLocalHost(JFrame parent, String error)
	{
		String ip = "";
		try
		{
			ip = InetAddress.getLocalHost().toString();
			ip = ip.substring(ip.indexOf('/') + 1);
		}
		catch(UnknownHostException e)
		{
			ServerConstants.showErrorMessage(parent, "Error", error);
			System.exit(2);
		}
		return ip;
	}

	public static void showErrorMessage(JFrame parent, String title, String content)
	{
		JOptionPane.showMessageDialog(parent, content, title, JOptionPane.ERROR_MESSAGE);
	}
}