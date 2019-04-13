import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
				System.out.println("Accept failed on: " + portNumber);
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
		if (blueCount <= redCount)
		{
			out.println(ServerConstants.SET_COLOR + "blue");
			blueCount++;
		}
		else
		{
			out.println(ServerConstants.SET_COLOR + "red");
			redCount++;
		}
		while (socket.isConnected())
		{
			if (in.hasNext())
			{
				String input = in.nextLine();
				for (Server client : clients)
					client.getWriter().println(input);
			}
		}
	}

	public PrintWriter getWriter()
	{
		return out;
	}
}