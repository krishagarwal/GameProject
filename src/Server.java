/*
Krish Agarwal
5.10.19
Server.java
*/

import javax.swing.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.util.Scanner;
import java.io.PrintWriter;

// This class is used as a Server program to connect to all Client
// programs so that the Clients can communicate between each other.
// This class also handles some parts of the game logic, such as
// terminating Bullets when they hit Players. In addition, an istance
// of this class is made whenever a Client needs to connect, as this
// class stores information about how to communicate with the Client
// program.
public class Server implements Runnable
{
	static ServerSocket serverSocket;
	static CopyOnWriteArrayList<Server> clients;
	static ConcurrentHashMap<String, Player> players;
	static ConcurrentHashMap<String, Bullet> bullets;
	static Timer waitTimer, bulletTimer;
	static int count, gameMode;
	static boolean gamePlaying;
	static int redCount = 0, blueCount = 0;
	private Socket socket;
	private Scanner in;
	private PrintWriter out;
	static Board gameBoard;

	// This is the first method run when the Server program is run.
	// It first prints out the IP address of the computer the Server
	// program is being run on so that this IP address can be
	// inputted into the Client program. It instantiates static field
	// variables, establishes the method of connectivity used to
	// connect Clients to the Server, and runs the acceptClients()
	// method to establish a connection to Client programs that are
	// being run.
	public static void main(String[] args)
	{
		gameMode = ServerConstants.CAPTURE_THE_FLAG;
		Thread message = new Thread(new Runnable()
		{
			// This is a method used to display the IP address that
			// Clients should connect to. It runs separate from the
			// rest of the Server program so that the message can be
			// displayed even while the Server performs certain tasks.
			public void run()
			{
				ServerConstants.showMessage(null, "Play with your friends!", "Connect clients using this IP address: " +
					ServerConstants.getLocalHost(null, "The server is not connected to the internet. Please reconnect and try again."));
			}
		});
		message.start();

		count = 0;
		gamePlaying = false;
		players = new ConcurrentHashMap<String, Player>();
		bullets = new ConcurrentHashMap<String, Bullet>();
		gameBoard = new Board();
		waitTimer = new Timer(1000, new ActionListener()
		{
			// This method is used to count down until the 
			// start of the game. This countdown is required to
			// allow all the Clients some buffer time to join the
			// game before before the game starts.
			public void actionPerformed(ActionEvent e)
			{
				sendToAll(ServerConstants.WAIT_BEFORE_PLAY + count);
				count--;
				if (count >= 0)
					return;
				waitTimer.stop();
				sendToAll(ServerConstants.READY_TO_PLAY + gameMode);
				bulletTimer.start();
				gamePlaying = true;
				if (redCount < blueCount)
					new Bot("red");
				else if (blueCount < redCount)
					new Bot("blue");
			}
		});

		bulletTimer = new Timer(10, new ActionListener()
		{
			// This method is used in a Timer to update all the
			// bullets that are currently in play.
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
					if (nearest.getDistanceTo(curr.posX, curr.posY) < ServerConstants.FRAGMENT_SIZE / 2 * Math.sqrt(2))
					{
						stopBullet(name);
						nearest.decreaseHealth();
						sendToAll(ServerConstants.DECREASE_PLAYER_HEALTH + nearest.name);
						if (nearest.health == 0)
						{
							int newPosX = (int)(Math.random() * (ServerConstants.BOARD_SIZE - ServerConstants.FRAGMENT_SIZE * 3)
								+ 1.5 * ServerConstants.FRAGMENT_SIZE) / 5 * 5;
							sendToAll(ServerConstants.REVIVE_PLAYER + nearest.name + '\0' + newPosX + '\0' + (name.substring(0, name.lastIndexOf(ServerConstants.NAME_SEPERATOR))));
							nearest.revive(newPosX);
						}
					}
					else if (gameBoard.total[curr.posY / ServerConstants.FRAGMENT_SIZE][curr.posX / ServerConstants.FRAGMENT_SIZE] == 't')
						stopBullet(name);
				}
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
			System.err.println("Could not listen on port: "
				+ ServerConstants.PORT_NUMBER);
			System.exit(1);
		}
	}

	// This method runs during the rest of the program to continually
	// accept Client programs that are being run. It establishes a
	// new connection whenever a new Client program is run and makes
	// an instance of the Server class for communication purposes.
	// This is then added to a list of Servers so that communication
	// can occur between all Client programs.
	public static void acceptClients()
	{
		clients = new CopyOnWriteArrayList<Server>();
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

	// This constructor is used to instantiate a new Server object,
	// which stores details on how to communcate with the Server
	// program and the current Client. It uses an object of the Socket
	// class (which is included with Java), which stores information on
	// how to communicate with the current Client in the form of
	// storing a PrintWriter for sending messages to the Client and
	// storing a Scanner for receiving messages from the Client.
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

	// This method is a method run for each Client program separately
	// from the Server program (meaning on different threads) so that
	// messages can be received/sent to and from different Clients in
	// real time. The method first determines the team of the current
	// Player and sends this information to the Client program. Then,
	// during the game this method waits for any messages received
	// from the Client program. Each message is started with a
	// sequence of characters indicating intent of the message, and
	// so when a message is received, the specific sequence of
	// characters is identifies and the program responds appropriately
	// (hence the long if/else structure). After the response, the
	// message is relayed to all other Client programs so that the
	// other Client programs can also respond to the message
	// appropriately on their side.
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
				boolean sendInfo = true;
				boolean[] gameStatus = new boolean[] {false, false};
				String input = in.nextLine();
				if (input.startsWith(ServerConstants.MOVE_PLAYER_UP))
				{
					Player curr = players.get(input.substring(ServerConstants.MOVE_PLAYER_UP.length()));
					if (!gameBoard.isAbove(curr.posX, curr.posY))
						gameStatus = curr.moveUp();
					else
						sendInfo = false;
				}
				else if (input.startsWith(ServerConstants.MOVE_PLAYER_DOWN))
				{
					Player curr = players.get(input.substring(ServerConstants.MOVE_PLAYER_DOWN.length()));
					if (!gameBoard.isBelow(curr.posX, curr.posY))
						gameStatus = curr.moveDown();
					else
						sendInfo = false;
				}
				else if (input.startsWith(ServerConstants.MOVE_PLAYER_LEFT))
				{
					Player curr = players.get(input.substring(ServerConstants.MOVE_PLAYER_LEFT.length()));
					if (!gameBoard.isLeft(curr.posX, curr.posY))
						gameStatus = curr.moveLeft();
					else
						sendInfo = false;
				}
				else if (input.startsWith(ServerConstants.MOVE_PLAYER_RIGHT))
				{
					Player curr = players.get(input.substring(ServerConstants.MOVE_PLAYER_RIGHT.length()));
					if (!gameBoard.isRight(curr.posX, curr.posY))
						gameStatus = curr.moveRight();
					else
						sendInfo = false;
				}
				else if (input.startsWith(ServerConstants.CREATE_BULLET))
					addBulletLog(input);
				else if (input.startsWith(ServerConstants.DELETE_PLAYER))
				{
					players.remove(input.substring(ServerConstants.DELETE_PLAYER.length()));
					if (allAreBots())
						clearGame();
				}
				else if (input.startsWith(ServerConstants.ADD_PLAYER))
					players.put(input.substring(ServerConstants.ADD_PLAYER.length(), input.indexOf('\0')), 
						Player.getNewPlayer(input.substring(ServerConstants.ADD_PLAYER.length())));
				if (sendInfo)
					sendToAll(input);
				if (gameStatus[0])
				{
					players.get(input.substring(input.lastIndexOf('\1') + 1)).hasFlag = true;
					sendToAll(ServerConstants.FLAG_TAKEN + input.substring(input.lastIndexOf('\1') + 1));
				}
				if (gameStatus[1])
				{
					sendToAll(ServerConstants.WIN + input.substring(input.lastIndexOf('\1') + 1));
					clearGame();
				}
			}
		}
		socket = null;
		in = null;
		out = null;
	}

	// This method checks if all the players that are currently
	// online are Bots. This is used when a player closes the
	// Client program before finishing the game, in which case
	// the Server program should be freed to allow players to join
	// a new game as they appear online.
	private static boolean allAreBots()
	{
		for (String curr : players.keySet())
		{
			if (curr.substring(curr.lastIndexOf(ServerConstants.NAME_SEPERATOR) + 1).length() > 0)
				return false;
		}
		return true;
	}

	// This method returns the PrintWriter stored by the current
	// Server object. This PrintWriter is used for sending messages
	// to the current Client program, and this method is required
	// because "PrintWriter out" is a private variable. This method
	// is used in the sendToAll() method.
	public PrintWriter getWriter()
	{
		return out;
	}

	// This method is used to send the String input message to all
	// the Clients by iterating over the list of Client connections
	// and using their PrintWriter objects to send the message.
	public static void sendToAll(String input)
	{
		for (Server client : clients)
			client.getWriter().println(input);
	}

	// This method returns the nearest opponent given the position
	// (x, y) and the team to find the opponent for. The method
	// iterates over the list of Players and uses the
	// Player.getDistanceTo() method to decide which opponent is
	// closest to the given position. This method is used in the Bot
	// class for deciding what the Bot should do in the game.
	public static Player getNearestOpponent(int x, int y, String team)
	{
		Player nearest = null;
		for (Player curr : players.values())
		{
			if (!curr.dead && !curr.team.equals(team) && (nearest == null || curr.getDistanceTo(x, y) < nearest.getDistanceTo(x, y)))
				nearest = curr;
		}
		return nearest;
	}

	// This method is used to creating a new Bullet given the input
	// message received by a Client program. It uses the
	// Bullet.getNewBullet() method to parse the input message into
	// a new Bullet, then adds this bullet to the list of Bullets
	// stored by the Server program.
	public static void addBulletLog(String input)
	{
		String name = input.substring(ServerConstants.CREATE_BULLET.length(), input.indexOf('\0'));
		Bullet toAdd = Bullet.getNewBullet(input.substring(input.indexOf('\0') + 1));
		bullets.put(name, toAdd);
	}

	// This method terminates a Bullet whenever it hits a Player.
	// It first removes the Bullet from the list of Bullets stored
	// by the Server program and then sends the message to all
	// Client programs to terminate the bullet.
	public static void stopBullet(String name)
	{
		bullets.remove(name);
		sendToAll(ServerConstants.TERMINATE_BULLET + name);
	}

	// This method is used to clear the current game. This method is
	// used after a win situation has occurred, in which case the Server
	// should clear the game so that a new one can be started whenever
	// Client programs want to connect.
	public static void clearGame()
	{
		for (Server curr : clients)
		{
			try
			{
				curr.socket.close();
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
		bulletTimer.stop();
		bullets.clear();
		players.clear();
		redCount = 0;
		blueCount = 0;
		clients.clear();
		gamePlaying = false;
		Bot.botCount = 0;
	}
}