import javax.swing.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.util.Scanner;
import java.io.PrintWriter;

public class Server implements Runnable
{
	static ServerSocket serverSocket;
	static ArrayList<Server> clients;
	static ConcurrentHashMap<String, Player> players;
	static ConcurrentHashMap<String, Bullet> bullets;
	static Timer waitTimer, bulletTimer;
	static int count;
	static boolean gamePlaying;
	static int redCount = 0, blueCount = 0;

	private Socket socket;
	private Scanner in;
	private PrintWriter out;
	public static void main(String[] args)
	{
		System.out.println("Waiting for ip address...");
		System.out.println("Connect clients using this IP address: " + getLocalHost());

		count = 0;
		gamePlaying = false;
		players = new ConcurrentHashMap<String, Player>();
		bullets = new ConcurrentHashMap<String, Bullet>();
		waitTimer = new Timer(1000, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				sendToAll(ServerConstants.WAIT_BEFORE_PLAY + count);
				count--;
				if (count >= 0)
					return;
				waitTimer.stop();
				sendToAll(ServerConstants.READY_TO_PLAY);
				gamePlaying = true;
				if (redCount < blueCount)
					new Bot("red");
				else if (blueCount < redCount)
					new Bot("blue");
			}
		});

		bulletTimer = new Timer(10, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				for (String name : bullets.keySet())
				{
					Bullet curr = bullets.get(name);
					curr.update();
					sendToAll(ServerConstants.UPDATE_BULLET + name);
					Player nearest = getNearestOpponent(curr.posX, curr.posY, curr.team);
					if (nearest == null)
						return;
					if (nearest.getDistanceTo(curr.posX, curr.posY) < ServerConstants.PLAYER_SIZE / 2 * Math.sqrt(2))
					{
						stopBullet(name);
						nearest.decreaseHealth();
						sendToAll(ServerConstants.DECREASE_PLAYER_HEALTH + nearest.name);
						if (nearest.health == 0)
						{
							int newPosX = (int)(Math.random() * (ServerConstants.FRAME_SIZE - ServerConstants.PLAYER_SIZE * 3) + ServerConstants.PLAYER_SIZE * 1.5);
							sendToAll(ServerConstants.REVIVE_PLAYER + nearest.name + '\0' + newPosX);
							nearest.revive(newPosX);
						}
					}
					else if (curr.posX > 1000 || curr.posX < 0 || curr.posY > 1000 || curr.posY < 0)
						stopBullet(name);
				}
			}
		});
		bulletTimer.start();

		serverSocket = null;
		try
		{
			serverSocket = new ServerSocket(ServerConstants.PORT_NUMBER);
			acceptClients();
		}
		catch (IOException e)
		{
			System.err.println("Could not listen on port: " + ServerConstants.PORT_NUMBER);
			System.exit(1);
		}
	}

	public static void acceptClients()
	{
		clients = new ArrayList<Server>();
		while (true)
		{
			try
			{
				Socket socket = serverSocket.accept();
				Server client = new Server(socket);
				Thread thread = new Thread(client);
				thread.start();
				clients.add(client);
				if (clients.size() == 1)
				{
					waitTimer.start();
					count = ServerConstants.WAIT_TIME;
				}
				if (gamePlaying)
					client.getWriter().println(ServerConstants.GAME_IN_SESSION);
			}
			catch (IOException e)
			{
				System.err.println("Accept failed on: " + ServerConstants.PORT_NUMBER);
			}
		}
	}

	public Server(Socket socket)
	{
		this.socket = socket;

		try
		{
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new Scanner(socket.getInputStream());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public void run()
	{
		int rand = (int)(Math.random() * 2);
		if (!gamePlaying && (blueCount < redCount || (blueCount == redCount && rand == 0)))
		{
			out.println(ServerConstants.SET_TEAM + "blue");
			blueCount++;
		}
		else if (!gamePlaying && (redCount < blueCount || (blueCount == redCount && rand == 1)))
		{
			out.println(ServerConstants.SET_TEAM + "red");
			redCount++;
		}
		while (socket.isConnected())
		{
			if (in.hasNext())
			{
				String input = in.nextLine();
				if (input.startsWith(ServerConstants.MOVE_PLAYER_UP))
					players.get(input.substring(ServerConstants.MOVE_PLAYER_UP.length())).moveUp();
				else if (input.startsWith(ServerConstants.MOVE_PLAYER_DOWN))
					players.get(input.substring(ServerConstants.MOVE_PLAYER_DOWN.length())).moveDown();
				else if (input.startsWith(ServerConstants.MOVE_PLAYER_LEFT))
					players.get(input.substring(ServerConstants.MOVE_PLAYER_LEFT.length())).moveLeft();
				else if (input.startsWith(ServerConstants.MOVE_PLAYER_RIGHT))
					players.get(input.substring(ServerConstants.MOVE_PLAYER_RIGHT.length())).moveRight();
				else if (input.startsWith(ServerConstants.CREATE_BULLET))
					addBulletLog(input);
				else if (input.startsWith(ServerConstants.DELETE_PLAYER))
					players.remove(input.substring(ServerConstants.DELETE_PLAYER.length()));
				else if (input.startsWith(ServerConstants.ADD_PLAYER))
					players.put(input.substring(ServerConstants.ADD_PLAYER.length(), input.indexOf('\0')), 
						Player.getNewPlayer(input.substring(ServerConstants.ADD_PLAYER.length())));
				sendToAll(input);
			}
		}
	}

	public PrintWriter getWriter()
	{
		return out;
	}

	public static void sendToAll(String input)
	{
		for (Server client : clients)
			client.getWriter().println(input);
	}

	public static Player getNearestOpponent(int x, int y, String team)
	{
		Player nearest = null;
		for (Player curr : players.values())
		{
			if (!curr.team.equals(team) && (nearest == null || curr.getDistanceTo(x, y) < nearest.getDistanceTo(x, y)))
				nearest = curr;
		}
		return nearest;
	}

	public static void addBulletLog(String input)
	{
		String name = input.substring(ServerConstants.CREATE_BULLET.length(), input.indexOf('\0'));
		Bullet toAdd = Bullet.getNewBullet(input.substring(input.indexOf('\0') + 1));
		bullets.put(name, toAdd);
	}

	public static void stopBullet(String name)
	{
		bullets.remove(name);
		sendToAll(ServerConstants.TERMINATE_BULLET + name);
	}

	public static String getLocalHost()
	{
		String ip = "";
		try
		{
			ip = InetAddress.getLocalHost().toString();
			ip = ip.substring(ip.indexOf('/') + 1);
		}
		catch(UnknownHostException e)
		{
			e.printStackTrace();
		}
		return ip;
	}
}