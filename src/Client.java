import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Scanner;

public class Client
{
	private Socket socket;
	static String playerName;
	String team;
	ConcurrentHashMap<String, Player> players;
	ConcurrentHashMap<String, Bullet> bullets;
	private Scanner serverIn;
	static PrintWriter out;
	private final static String START = "start";
	private final static String WAIT = "wait";
	private final static String GAME = "game";
	JPanel startPanel, waitPanel, parentPanel;
	static GamePanel gamePanel;
	JLabel waitTime;
	JFrame frame;
	boolean waiting;
	static int bulletCount = 0;
	CardLayout cl;

	public Client()
	{
		waiting = false;
		players = new ConcurrentHashMap<String, Player>();
		bullets = new ConcurrentHashMap<String, Bullet>();

		startPanel = new JPanel();
		JButton play = new JButton("Play");
		startPanel.add(play);
		play.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				socket = null;
				try
				{
					String ip = ServerConstants.getFromMessage(frame, "Enter IP Address of Server", "IP (skip if on same machine)",
						ServerConstants.getLocalHost(frame, "You do not seem to be connected to the internet. Reconnect and try again."));
					socket = new Socket(ip, ServerConstants.PORT_NUMBER);
					Thread.sleep(1000);
					Thread server = new Thread(new ServerThread());
					server.start();
				}
				catch (IOException ioe)
				{
					System.err.println("Fatal connection error");
					ioe.printStackTrace();
				}
				catch (InterruptedException ie)
				{
					System.err.println("Fatal connection error");
					ie.printStackTrace();
				}

				cl.show(parentPanel, WAIT);
				frame.addWindowListener(new WindowListener()
				{
					public void windowOpened(WindowEvent e) {}
					public void windowIconified(WindowEvent e) {}
					public void windowDeiconified(WindowEvent e) {}
					public void windowDeactivated(WindowEvent e) {}
					public void windowClosed(WindowEvent e) {}
					public void windowActivated(WindowEvent e) {}

					public void windowClosing(WindowEvent e)
					{
						send(ServerConstants.DELETE_PLAYER + playerName);
					}
				});
			}
		});

		waitPanel = new JPanel(new BorderLayout());
		waitTime = new JLabel("", JLabel.CENTER);
		waitPanel.add(waitTime, BorderLayout.CENTER);

		gamePanel = new GamePanel(players, bullets);

		cl = new CardLayout();
		parentPanel = new JPanel(cl);
		parentPanel.add(startPanel, START);
		parentPanel.add(waitPanel, WAIT);
		parentPanel.add(gamePanel, GAME);
		cl.show(parentPanel, START);

		frame = new JFrame("Game");
		frame.setSize(ServerConstants.FRAME_SIZE, ServerConstants.FRAME_SIZE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(parentPanel);
		frame.setVisible(true);
		
		try
		{
			playerName = ServerConstants.getFromMessage(frame, "Enter your name", "Name", "-") + ServerConstants.NAME_SEPERATOR +
				InetAddress.getLocalHost() + System.currentTimeMillis();
		}
		catch(UnknownHostException uhe)
		{
			uhe.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		new Client();
	}

	public static void send(String message)
	{
		out.println(message);
	}

	class ServerThread implements Runnable
	{
		public void run()
		{
			try
			{
				out = new PrintWriter(socket.getOutputStream(), true);
				serverIn = new Scanner(socket.getInputStream());
				
				while (!socket.isClosed())
				{
					if (serverIn.hasNext())
					{
						String input = serverIn.nextLine();
						if (input.equals(ServerConstants.READY_TO_PLAY))
						{
							cl.show(parentPanel, GAME);
							gamePanel.requestFocus();
							int posY = ServerConstants.BOARD_SIZE - ServerConstants.FRAGMENT_SIZE * 2;
							if (team.equals("blue"))
								posY = ServerConstants.FRAGMENT_SIZE * 2;
							send(ServerConstants.ADD_PLAYER + Player.toString((int)(Math.random() * (ServerConstants.BOARD_SIZE -
								ServerConstants.FRAGMENT_SIZE * 3) + 1.5 * ServerConstants.FRAGMENT_SIZE) / 5 * 5, posY, playerName, team));
						}
						else if (input.equals(ServerConstants.GAME_IN_SESSION))
						{
							waitTime.setText("Game is in session. Please wait for the next game.");
							send(ServerConstants.DELETE_PLAYER + playerName);
							waiting = true;
						}
						else if (input.startsWith(ServerConstants.REVIVE_PLAYER))
							players.get(input.substring(ServerConstants.REVIVE_PLAYER.length(), 
								input.indexOf('\0'))).revive(Integer.parseInt(input.substring(input.indexOf('\0') + 1)));
						else if (input.startsWith(ServerConstants.DECREASE_PLAYER_HEALTH))
							players.get(input.substring(ServerConstants.DECREASE_PLAYER_HEALTH.length())).decreaseHealth();
						else if (!waiting && input.startsWith(ServerConstants.UPDATE_BULLET))
						{
							Bullet curr = bullets.get(input.substring(ServerConstants.UPDATE_BULLET.length()));
							if (curr != null)
								curr.update();
						}
						else if (!waiting && input.startsWith(ServerConstants.MOVE_PLAYER_UP))
							players.get(input.substring(ServerConstants.MOVE_PLAYER_UP.length())).moveUp();
						else if (!waiting && input.startsWith(ServerConstants.MOVE_PLAYER_DOWN))
							players.get(input.substring(ServerConstants.MOVE_PLAYER_DOWN.length())).moveDown();
						else if (!waiting && input.startsWith(ServerConstants.MOVE_PLAYER_LEFT))
							players.get(input.substring(ServerConstants.MOVE_PLAYER_LEFT.length())).moveLeft();
						else if (!waiting && input.startsWith(ServerConstants.MOVE_PLAYER_RIGHT))
							players.get(input.substring(ServerConstants.MOVE_PLAYER_RIGHT.length())).moveRight();
						else if (!waiting && input.startsWith(ServerConstants.TERMINATE_BULLET))
							bullets.remove(input.substring(ServerConstants.TERMINATE_BULLET.length()));
						else if (!waiting && input.startsWith(ServerConstants.CREATE_BULLET))
							bullets.put(input.substring(ServerConstants.CREATE_BULLET.length(), input.indexOf('\0')), 
								Bullet.getNewBullet(input.substring(input.indexOf('\0') + 1)));
						else if (!waiting && input.startsWith(ServerConstants.SET_TEAM))
							team = input.substring(ServerConstants.SET_TEAM.length());
						else if (input.startsWith(ServerConstants.WAIT_BEFORE_PLAY))
							waitTime.setText("Starting in " + Integer.parseInt(input.substring(ServerConstants.WAIT_BEFORE_PLAY.length())));
						else if (!waiting && input.startsWith(ServerConstants.DELETE_PLAYER))
							players.remove(input.substring(ServerConstants.DELETE_PLAYER.length()));
						else if (!waiting && input.startsWith(ServerConstants.ADD_PLAYER))
							players.put(input.substring(ServerConstants.ADD_PLAYER.length(), input.indexOf('\0')), 
								Player.getNewPlayer(input.substring(ServerConstants.ADD_PLAYER.length())));
						gamePanel.repaint();
					}
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}