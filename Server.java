import javax.swing.*;
import java.awt.event.*;
import java.net.ServerSocket;
import java.net.Socket;
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
	static ConcurrentHashMap<String, Timer> bulletTimers;
	static Timer waitTimer;
	static int count;
	static boolean gamePlaying;
	static int redCount = 0;
	static int blueCount = 0;

	private Socket socket;
	private Scanner in;
	private PrintWriter out;
	public static void main(String[] args)
	{
		count = 0;
		gamePlaying = false;
		players = new ConcurrentHashMap<String, Player>();
		bullets = new ConcurrentHashMap<String, Bullet>();
		bulletTimers = new ConcurrentHashMap<String, Timer>();
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
			}
		});


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
		if (!gamePlaying && blueCount <= redCount)
		{
			out.println(ServerConstants.SET_TEAM + "blue");
			blueCount++;
		}
		else if (!gamePlaying)
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
					players.get(input.substring(ServerConstants.MOVE_PLAYER_UP.length())).posY -= 2;
				else if (input.startsWith(ServerConstants.MOVE_PLAYER_DOWN))
					players.get(input.substring(ServerConstants.MOVE_PLAYER_DOWN.length())).posY += 2;
				else if (input.startsWith(ServerConstants.MOVE_PLAYER_LEFT))
					players.get(input.substring(ServerConstants.MOVE_PLAYER_LEFT.length())).posX -= 2;
				else if (input.startsWith(ServerConstants.MOVE_PLAYER_RIGHT))
					players.get(input.substring(ServerConstants.MOVE_PLAYER_RIGHT.length())).posX += 2;
				else if (input.startsWith(ServerConstants.CREATE_BULLET))
					addBulletLog(input);
				else if (input.startsWith(ServerConstants.DELETE_PLAYER))
					players.remove(input.substring(ServerConstants.DELETE_PLAYER.length()));
				else if (input.startsWith(ServerConstants.ADD_PLAYER))
					players.put(input.substring(ServerConstants.ADD_PLAYER.length(), input.indexOf('\0')), Player.getNewPlayer(input.substring(input.indexOf('\0') + 1)));
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
			if (!curr.team.equals(team) && (nearest == null || curr.getDistanceTo(x, x) < nearest.getDistanceTo(nearest.posX, nearest.posY)))
				nearest = curr;
		}
		return nearest;
	}

	public static void addBulletLog(String input)
	{
		String name = input.substring(ServerConstants.CREATE_BULLET.length(), input.indexOf('\0'));
		Bullet toAdd = Bullet.getNewBullet(input.substring(input.indexOf('\0') + 1));
		bullets.put(name, toAdd);
		bulletTimers.put(name, new Timer(10, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				toAdd.update();
				sendToAll(ServerConstants.UPDATE_BULLET + name);
				Player nearest = getNearestOpponent(toAdd.posX, toAdd.posY, toAdd.team);
				if (nearest == null)
					return;
				if (nearest.getDistanceTo(toAdd.posX, toAdd.posY) < ServerConstants.PLAYER_SIZE / 2 * Math.sqrt(2)) {
					bulletTimers.get(name).stop();
					bulletTimers.remove(name);
					sendToAll(ServerConstants.TERMINATE_BULLET + name);
				}
			}
		}));
		bulletTimers.get(name).start();
	}
}