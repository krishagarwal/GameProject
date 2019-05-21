/*
Krish Agarwal
5.12.19
ServerConstants.java
*/

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

// This class stores a variety of constants used throughout
// all the other programs. These constants are stored here
// to allow consistency between programs so that hard-coding
// is not used. The most important constants are the first two
// sections of constants, which are numerical sequences
// used to identify the intent of messages sent between Server
// and Client programs. The third section of constants is used
// as a separator in some messages, the fourth section of
// constants is used for numerical constants, and the last section
// of constants is used as codes for the game modes. All the String
// constants use ASCII codes for unprintable characters so that
// they are not confused by input Strings, such as the name.
public final class ServerConstants
{
	// For my reference: checked against using equals(), order
	// does not matter between, but should appear at top
	public static final String GAME_IN_SESSION = 		"\1";
	
	// checked against using startsWith(), must appear last to first
	public static final String READY_TO_PLAY = 			"\1\1";
	public static final String ADD_PLAYER = 			"\1\1\1";
	public static final String DELETE_PLAYER = 			"\1\1\1\1";
	public static final String WAIT_BEFORE_PLAY = 		"\1\1\1\1\1";
	public static final String SET_TEAM = 				"\1\1\1\1\1\1";
	public static final String CREATE_BULLET = 			"\1\1\1\1\1\1\1";
	public static final String TERMINATE_BULLET = 		"\1\1\1\1\1\1\1\1";
	public static final String MOVE_PLAYER_RIGHT = 		"\1\1\1\1\1\1\1\1\1";
	public static final String MOVE_PLAYER_LEFT = 		"\1\1\1\1\1\1\1\1\1\1";
	public static final String MOVE_PLAYER_DOWN = 		"\1\1\1\1\1\1\1\1\1\1\1";
	public static final String MOVE_PLAYER_UP = 		"\1\1\1\1\1\1\1\1\1\1\1\1";
	public static final String UPDATE_BULLET = 			"\1\1\1\1\1\1\1\1\1\1\1\1\1";
	public static final String DECREASE_PLAYER_HEALTH = "\1\1\1\1\1\1\1\1\1\1\1\1\1\1";
	public static final String REVIVE_PLAYER = 			"\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1";
	public static final String WIN = 					"\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1";
	public static final String FLAG_TAKEN = 			"\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1";
	public static final String MOVE_GUN =	 			"\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1";
	public static final String NEW_WAVE =	 			"\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1";
	public static final String CREATE_SHRAPNEL = 		"\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1";
	public static final String BLOW_UP = 				"\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1";
	public static final String SEND_MESSAGE = 			"\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1";
	public static final String ADD_LIFE = 				"\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1\1";
	
	// For my reference: used as a separator, must have special
	// character
	public static final String NAME_SEPERATOR = "\2";
	
	// For my reference: constant numerical fields
	public static final int PORT_NUMBER = 4444;
	public static final int WAIT_TIME = 5;
	public static final int BULLET_SIZE = 8;
	public static final int FRAME_SIZE = 600;
	public static final int BOARD_SIZE = 1350;
	public static final int BOARD_FRAGMENTS = 27;
	public static final int FRAGMENT_SIZE = BOARD_SIZE / BOARD_FRAGMENTS;
	public static final int HEALTH = 100;
	public static final int HEALTH_DECREASE = 5;
	public static final int MOVE_LENGTH = 5;

	// For my reference: game modes
	public static final int CAPTURE_THE_FLAG = 1;
	public static final int RED_VS_BLUE = 2;
	public static final int COLLABORATIVE = 3;

	public static final char RED = '\3';
	public static final char BLUE = '\4';
	public static final char NONE_BLUE = '\5';
	public static final char NONE_RED = '\6';

	public static void playClip(String fileName)
	{
		try
		{
			Clip audio = AudioSystem.getClip();
			audio.open(AudioSystem.getAudioInputStream(new File(fileName)));
			audio.start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static String ip = "";

	// This method is used to display a message String on the
	// screen using a JOptionPane. This method is used in the Server
	// class to display the IP address to connect to.
	public static void showMessage(JFrame parent, String title, String content)
	{
		JOptionPane.showMessageDialog(parent, content, title, JOptionPane.INFORMATION_MESSAGE);
	}

	// This method returns the IP address of the current computer
	// a program is being run on. It is used in the Server class
	// when displaying the IP address.
	public static String getLocalHost(JFrame parent, String error)
	{
		if (!ip.equals(""))
			return ip;
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

	// This method is used to display a String error message. It is
	// used in the getLocalHost() method if that method encounters
	// an error.
	public static void showErrorMessage(JFrame parent, String title, String content)
	{
		JOptionPane.showMessageDialog(parent, content, title, JOptionPane.ERROR_MESSAGE);
	}

	public static String regulateName(String in)
	{
		return regulateName(in, 10);
	}

	public static String regulateName(String in, int amt)
	{
		if (in.length() > amt)
			in = in.substring(0, amt - 3) + "...";
		return in;
	}
}