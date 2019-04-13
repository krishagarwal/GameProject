import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class Client extends JPanel implements KeyListener
{
	private Socket socket;
	private String name, color;
	HashMap<String, Object[]> all;
	int posX, posY;
	private Scanner serverIn;
	private PrintWriter out;
	Client outer;
	JPanel waitPanel;
	JLabel waitTime;
	JFrame frame;
	boolean waiting;

	public Client()
	{
		waiting = false;
		outer = this;
		all = new HashMap<String, Object[]>();
		
		frame = new JFrame();
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		posX = 275;
		posY = 275;

		JPanel start = new JPanel();
		JTextField nameEnter = new JTextField();
		JButton play = new JButton("Play");
		nameEnter.setPreferredSize(new Dimension(100, 20));
		start.add(nameEnter);
		start.add(play);

		frame.setContentPane(start);
		frame.setVisible(true);

		waitPanel = new JPanel(new BorderLayout());
		waitTime = new JLabel("", JLabel.CENTER);
		waitPanel.add(waitTime, BorderLayout.CENTER);

		play.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				socket = null;
				frame.setContentPane(waitPanel);
				frame.validate();
				frame.repaint();

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
						out.println(ServerConstants.DELETE_CHARACTER + name);
					}
				});

				int portNumber = 4444;
				try
				{
					name = nameEnter.getText() + ServerConstants.NAME_SEPERATOR + InetAddress.getLocalHost() + System.currentTimeMillis();
					socket = new Socket("192.168.1.168", portNumber);
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
			}
		});
	}
	public static void main(String[] args)
	{
		new Client();
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		for (String playerName : all.keySet())
		{
			Object[] info = all.get(playerName);
			playerName = playerName.substring(0, playerName.indexOf(ServerConstants.NAME_SEPERATOR));
			if (info[2].equals("blue"))
				g.setColor(Color.BLUE);
			else
				g.setColor(Color.RED);
			g.fillRect((Integer)(info[0]), (Integer)(info[1]), 50, 50);
			g.setColor(Color.BLACK);
			g.drawString(playerName, (Integer)(info[0]) + 25 - (int)(g.getFontMetrics().getStringBounds(playerName, g).getWidth()) / 2, (Integer)(info[1]) - 5);
			g.drawRect((Integer)(info[0]), (Integer)(info[1]), 50, 50);
		}
	}

	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}

	public void keyPressed(KeyEvent evt)
	{
		int e = evt.getKeyCode();
		if (e == KeyEvent.VK_UP)
			posY -= 2;
		else if (e == KeyEvent.VK_DOWN)
			posY += 2;
		else if (e == KeyEvent.VK_LEFT)
			posX -= 2;
		else if (e == KeyEvent.VK_RIGHT)
			posX += 2;
		out.println(name + '\0' + posX + '\0' + posY + '\0' + color);
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

						if (input.startsWith(ServerConstants.SET_COLOR))
							color = input.substring(ServerConstants.SET_COLOR.length());
						else if (input.equals(ServerConstants.GAME_IN_SESSION))
						{
							waitTime.setText("Game is in session. Please wait for the next game.");
							out.println(ServerConstants.DELETE_CHARACTER + name);
							waiting = true;
						}
						else if (input.startsWith(ServerConstants.WAIT_BEFORE_PLAY))
							waitTime.setText("Starting in " + Integer.parseInt(input.substring(ServerConstants.WAIT_BEFORE_PLAY.length())));
						else if (input.equals(ServerConstants.READY_TO_PLAY))
						{
							frame.setContentPane(outer);
							frame.validate();
							frame.repaint();
							outer.requestFocus();
							outer.addKeyListener(outer);
							out.println(ServerConstants.ADD_CHARACTER + name + '\0' + posX + '\0' + posY + '\0' + color);
						}
						else if (input.startsWith(ServerConstants.DELETE_CHARACTER))
							all.remove(input.substring(ServerConstants.DELETE_CHARACTER.length()));
						else if (!waiting)
						{
							if (input.startsWith(ServerConstants.ADD_CHARACTER))
							{
								input = input.substring(1);
								out.println(name + '\0' + posX + '\0' + posY + '\0' + color);
							}
							String inName = input.substring(0, input.indexOf('\0'));
							input = input.substring(input.indexOf('\0') + 1);
							Integer pX = Integer.parseInt(input.substring(0, input.indexOf('\0')));
							input = input.substring(input.indexOf('\0') + 1);
							Integer pY = Integer.parseInt(input.substring(0, input.indexOf('\0')));
							input = input.substring(input.indexOf('\0') + 1);
							String pColor = input;
							all.put(inName, new Object[] {pX, pY, pColor});
						}
						outer.repaint();
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