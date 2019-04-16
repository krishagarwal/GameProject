import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.util.Scanner;
import java.io.PrintWriter;

public class Server implements Runnable
{
	static ServerSocket serverSocket;
	static int portNumber = 4444;
	static ArrayList<Server> clients;
	static HashMap<String, Player> players;
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
		players = new HashMap<String, Player>();
		waitTimer = new Timer(1000, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				for (Server client : clients)
					client.getWriter().println(ServerConstants.WAIT_BEFORE_PLAY + count);
				count--;
				if (count >= 0)
					return;
				waitTimer.stop();
				for (Server client : clients)
					client.getWriter().println(ServerConstants.READY_TO_PLAY);
				gamePlaying = true;
				if (redCount < blueCount)
					new Bot("red");
			}
		});


		serverSocket = null;
		try
		{
			serverSocket = new ServerSocket(portNumber);
			acceptClients();
		}
		catch (IOException e)
		{
			System.err.println("Could not listen on port: " + portNumber);
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
				System.err.println("Accept failed on: " + portNumber);
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
				if (input.startsWith(ServerConstants.DELETE_CHARACTER))
					players.remove(input.substring(ServerConstants.DELETE_CHARACTER.length()));
				else if (input.startsWith(ServerConstants.ADD_CHARACTER))
					players.put(input.substring(ServerConstants.ADD_CHARACTER.length(), input.indexOf('\0')), Player.getNewPlayer(input.substring(input.indexOf('\0') + 1)));
				else if (input.startsWith(ServerConstants.UPDATE_CHARACTER))
					players.get(input.substring(ServerConstants.UPDATE_CHARACTER.length(), input.indexOf('\0'))).setPlayer(input.substring(input.indexOf('\0') + 1));
				for (Server client : clients)
					client.getWriter().println(input);
			}
		}
	}

	public PrintWriter getWriter()
	{
		return out;
	}

	public static Player getNearestOpponent(Player ref)
	{
		Player nearest = null;
		for (Player curr : players.values())
		{
			if (!curr.team.equals(ref.team) && (nearest == null || curr.getDistanceTo(ref.posX, ref.posY) < nearest.getDistanceTo(ref.posX, ref.posY)))
				nearest = curr;
		}
		return nearest;
	}
}

class Bot
{
	private Player player;
	private String name;

	public Bot(String team)
	{
		player = new Player(team, (int)(Math.random() * 540 + 10), 500);
		name = "Bot" + ServerConstants.NAME_SEPERATOR;

		for (Server client : Server.clients)
			client.getWriter().println(ServerConstants.ADD_CHARACTER + name + '\0' + player.toString());
		Timer mover = new Timer(85, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Player nearest = Server.getNearestOpponent(player);
				if (nearest == null)
					return;
				if (Math.abs(nearest.posX - player.posX) > Math.abs(nearest.posY - player.posY) && Math.abs(nearest.posX - player.posX) > 100)
					player.posX += 2 * (nearest.posX - player.posX) / Math.abs(nearest.posX - player.posX);
				else if (Math.abs(nearest.posY - player.posY) > 100)
					player.posY += 2 * (nearest.posY - player.posY) / Math.abs(nearest.posY - player.posY);
				else
					return;
				for (Server client : Server.clients)
					client.getWriter().print(ServerConstants.UPDATE_CHARACTER + name + '\0' + player.toString());
			}
		});
		mover.start();
	}
}