import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class Server
{
	static ServerSocket serverSocket;
	static int portNumber = 4444;
	static ArrayList<ClientThread> clients;
	static Timer waitTimer;
	static int count;
	static boolean gamePlaying;

	public static void main(String[] args)
	{
		count = 0;
		gamePlaying = false;
		waitTimer = new Timer(1000, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				for (ClientThread client : clients)
					client.getWriter().println(ServerConstants.WAIT_BEFORE_PLAY + count);
				count--;
				if (count >= 0)
					return;
				waitTimer.stop();
				for (ClientThread client : clients)
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
		clients = new ArrayList<ClientThread>();
		while (true)
		{
			try
			{
				Socket socket = serverSocket.accept();
				ClientThread client = new ClientThread(socket);
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
}