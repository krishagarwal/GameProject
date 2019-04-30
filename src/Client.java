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
	static Socket socket;
	static String playerName, ip, team;
	final static String NAME = "name";
	final static String START = "start";
	final static String SERVER_IN = "server";
	final static String WAIT = "wait";
	final static String GAME = "game";
	static ConcurrentHashMap<String, Player> players;
	static ConcurrentHashMap<String, Bullet> bullets;
	static Scanner serverIn;
	static PrintWriter out;
	static JPanel namePanel, startPanel, serverInPanel, waitPanel, parentPanel, gamePanel;
	JLabel waitTime;
	static JFrame frame;
	static boolean waiting;
	static int bulletCount = 0;
	static CardLayout cl;

	public Client()
	{
		waiting = false;
		players = new ConcurrentHashMap<String, Player>();
		bullets = new ConcurrentHashMap<String, Bullet>();
		playerName = ip = "";

		namePanel = new NamePanel();
		startPanel = new StartPanel();
		serverInPanel = new ServerInputPanel();
		waitPanel = new WaitPanel();
		gamePanel = new GamePanel(players, bullets);

		cl = new CardLayout();
		parentPanel = new JPanel(cl);
		parentPanel.add(namePanel, NAME);
		parentPanel.add(startPanel, START);
		parentPanel.add(serverInPanel, SERVER_IN);
		parentPanel.add(waitPanel, WAIT);
		parentPanel.add(gamePanel, GAME);
		cl.show(parentPanel, NAME);
		namePanel.requestFocus();

		frame = new JFrame("Game");
		frame.setSize(ServerConstants.FRAME_SIZE, ServerConstants.FRAME_SIZE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(parentPanel);
		frame.setVisible(true);
	}

	public static void main(String[] args)
	{
		new Client();
	}

	public static void send(String message)
	{
		out.println(message);
	}
}