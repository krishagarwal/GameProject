import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientThread extends Server implements Runnable
{
	private Socket socket;
	private Scanner in;
	private PrintWriter out;

	public ClientThread(Socket socket)
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

	@Override
	public void run()
	{
		while (socket.isConnected())
		{
			if (in.hasNext())
			{
				String input = in.nextLine();
				for (ClientThread client : clients)
					client.getWriter().println(input);
			}
		}
	}

	public PrintWriter getWriter()
	{
		return out;
	}
}