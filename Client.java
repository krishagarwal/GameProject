/*
Krish Agarwal
5.12.19
Client.java
*/

import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Scanner;

// This class is the program run by all Clients who want to play 
// the game. This class handles the JFrame, sending/receiving
// information from the Server, and drawing game elements on
// the screen.
public class Client
{
	static Socket socket;
	static String playerName, ip, team;
	static ConcurrentHashMap<String, Player> players;
	static ConcurrentHashMap<String, Bullet> bullets;
	static Scanner serverIn;
	static PrintWriter out;
	static TotalPanel totalPanel;
	static JFrame frame;
	static boolean waiting, playing, blueFlagTaken, redFlagTaken, startedConnection;
	static int bulletCount = 0, gameMode = 0;
	static WindowListener frameListener;

	// This constructor is used to instantiate a Client
	// player by defining some static field variables, making
	// a new JFrame, and displaying the JPanel on the JFrame
	public Client()
	{
		ServerConstants.getLocalHost(null, "Not connected to internet");
		startedConnection = redFlagTaken = blueFlagTaken = waiting = playing = false;
		players = new ConcurrentHashMap<String, Player>();
		bullets = new ConcurrentHashMap<String, Bullet>();
		playerName = ip = team = "";
		totalPanel = new TotalPanel();
		
		frame = new JFrame("Area 51");
		frame.setSize(ServerConstants.FRAME_SIZE, ServerConstants.FRAME_SIZE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(totalPanel);
		frame.setResizable(false);
		frame.setVisible(true);

		frameListener = new WindowListener()
		{
			// This method is part of the WindowListener but
			// is not used.
			public void windowOpened(WindowEvent e) {}
			
			// This method is part of the WindowListener but
			// is not used.
			public void windowIconified(WindowEvent e) {}
			
			// This method is part of the WindowListener but
			// is not used.
			public void windowDeiconified(WindowEvent e) {}
			
			// This method is part of the WindowListener but
			// is not used.
			public void windowDeactivated(WindowEvent e) {}
			
			// This method is part of the WindowListener but
			// is not used.
			public void windowClosed(WindowEvent e) {}
			
			// This method is part of the WindowListener but
			// is not used.
			public void windowActivated(WindowEvent e) {}

			// This method is run when the Window is closing
			// to notify the Server to delete the player.
			public void windowClosing(WindowEvent e)
			{
				if (out != null)
				{
					send(ServerConstants.DELETE_PLAYER + Client.playerName);
					send(ServerConstants.REMOVE_CLIENT);
				}
			}
		};
	}

	// This method is the first method run when the Client program
	// is run. The method instantiates a Client object to run the
	// Client constructor.
	public static void main(String[] args)
	{
		new Client();
	}

	// This method is used to send the given String message to the
	// Server program using the PrintWriter object out.
	public static void send(String message)
	{
		out.println(message);
	}

	// This method is used to clear the game so that after a win
	// situation, a Client can play again without having to rerun
	// the program.
	public static void clearGame()
	{
		try
		{
			socket.close();
		}
		catch(IOException ioe)
		{
			ServerConstants.showErrorMessage(frame, "Error", "Could not close connection.\nTry again later.");
			System.exit(2);
		}
		ip = team = "";
		bulletCount = 0;
		players.clear();
		bullets.clear();
		if (!waiting)
		{
			TotalPanel.movePosX = Math.abs(ServerConstants.BOARD_SIZE / 2 - TotalPanel.posX) / (ServerConstants.BOARD_SIZE / 2 - TotalPanel.posX) * 5;
			TotalPanel.movePosY = Math.abs(ServerConstants.BOARD_SIZE / 2 - TotalPanel.posY) / (ServerConstants.BOARD_SIZE / 2 - TotalPanel.posY) * 5;
			totalPanel.posMover.start();
			frame.removeWindowListener(frameListener);
		}
		totalPanel.messages.clear();
		totalPanel.deathLog.clear();
		totalPanel.sendText = "You: ";
		startedConnection = false;
	}

	// This method handles connecting the Client program to the
	// Server program by assuming that the static "ip" variable
	// has been preset by any program attempting to connect.
	public static boolean connect()
	{
		startedConnection = true;
		Client.socket = null;
		try
		{
			if (ip.equals(""))
				ip += ServerConstants.getLocalHost(frame, "You do not seem to be connected to the internet.\nReconnect and try again.");
			socket = new Socket(ip, ServerConstants.PORT_NUMBER);
			Thread.sleep(1000);
			Thread server = new Thread(new ServerThread());
			server.start();
		}
		catch (IOException ioe)
		{
			ServerConstants.showErrorMessage(frame, "Error", "Unreachable IP address.\nEnter a different IP.");
			startedConnection = false;
			return false;
		}
		catch (InterruptedException ie)
		{
			ServerConstants.showErrorMessage(frame, "Error", "Unreachable IP address.\nEnter a different IP.");
			startedConnection = false;
			return false;
		}
		frame.addWindowListener(frameListener);
		return true;
	}
}