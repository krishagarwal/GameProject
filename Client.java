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
	private String name;
	HashMap<String, Object[]> all;
	int posX, posY;
	Color c;
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
		c = new Color((int)(Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256));

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
			g.setColor(Color.BLACK);
			g.drawString(playerName, (Integer)(info[0]) + 25 - (int)(g.getFontMetrics().getStringBounds(playerName, g).getWidth()) / 2, (Integer)(info[1]) - 5);
			g.setColor((Color)(info[2]));
			g.fillRect((Integer)(info[0]), (Integer)(info[1]), 50, 50);
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
		out.println(name + '\0' + posX + '\0' + posY + '\0' + c.getRed() + '\0' + c.getGreen() + '\0' + c.getBlue());
	}

	class ServerThread implements Runnable
	{
		public void run()
		{
			try
			{
				out = new PrintWriter(socket.getOutputStream(), true);
				serverIn = new Scanner(socket.getInputStream());
				out.println(ServerConstants.ADD_CHARACTER + name + '\0' + posX + '\0' + posY + '\0' + c.getRed() + '\0' + c.getGreen() + '\0' + c.getBlue());
				
				while (!socket.isClosed())
				{
					if (serverIn.hasNext())
					{
						String input = serverIn.nextLine();

						if (input.equals(ServerConstants.GAME_IN_SESSION))
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
						}
						else if (input.startsWith(ServerConstants.DELETE_CHARACTER))
							all.remove(input.substring(ServerConstants.DELETE_CHARACTER.length()));
						else
						{
							if (input.startsWith(ServerConstants.ADD_CHARACTER))
							{
								input = input.substring(1);
								if (!waiting)
									out.println(name + '\0' + posX + '\0' + posY + '\0' + c.getRed() + '\0' + c.getGreen() + '\0' + c.getBlue());
							}
							String inName = input.substring(0, input.indexOf('\0'));
							input = input.substring(input.indexOf('\0') + 1);
							Integer x = Integer.parseInt(input.substring(0, input.indexOf('\0')));
							input = input.substring(input.indexOf('\0') + 1);
							Integer y = Integer.parseInt(input.substring(0, input.indexOf('\0')));
							input = input.substring(input.indexOf('\0') + 1);
							Integer r = Integer.parseInt(input.substring(0, input.indexOf('\0')));
							input = input.substring(input.indexOf('\0') + 1);
							Integer g = Integer.parseInt(input.substring(0, input.indexOf('\0')));
							input = input.substring(input.indexOf('\0') + 1);
							Integer b = Integer.parseInt(input);
							all.put(inName, new Object[] {x, y, new Color(r, g, b)});
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