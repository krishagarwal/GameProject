import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Scanner;

public class Client extends JPanel implements KeyListener, MouseListener
{
	private Socket socket;
	private String name;
	ConcurrentHashMap<String, Player> allPlayers;
	ConcurrentHashMap<String, Bullet> allBullets;
	Player current;
	private Scanner serverIn;
	PrintWriter out;
	Client outer;
	JPanel waitPanel;
	JLabel waitTime;
	JFrame frame;
	boolean waiting;

	static int bulletCount = 0;

	public Client()
	{
		waiting = false;
		outer = this;
		allPlayers = new ConcurrentHashMap<String, Player>();
		allBullets = new ConcurrentHashMap<String, Bullet>();
		current = new Player();
		
		frame = new JFrame();
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
		for (String playerName : allPlayers.keySet())
		{
			Player curr = allPlayers.get(playerName);
			playerName = playerName.substring(0, playerName.indexOf(ServerConstants.NAME_SEPERATOR));
			if (curr.team.equals("blue"))
				g.setColor(new Color(147, 197, 255, 80));
			else
				g.setColor(new Color(255, 132, 132, 80));
			g.fillRect(curr.posX, curr.posY, 50, 50);
			g.setColor(Color.BLACK);
			g.drawString(playerName, curr.posX + 25 - (int)(g.getFontMetrics().getStringBounds(playerName, g).getWidth()) / 2, curr.posY - 5);
			g.drawRect(curr.posX, curr.posY, 50, 50);
		}

		g.setColor(Color.RED);
		for (Bullet bullet : allBullets.values())
			g.fillOval(bullet.posX, bullet.posY, 10, 10);
	}

	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}

	public void keyPressed(KeyEvent evt)
	{
		int e = evt.getKeyCode();
		if (e == KeyEvent.VK_UP)
			current.posY -= 2;
		else if (e == KeyEvent.VK_DOWN)
			current.posY += 2;
		else if (e == KeyEvent.VK_LEFT)
			current.posX -= 2;
		else if (e == KeyEvent.VK_RIGHT)
			current.posX += 2;
		out.println(ServerConstants.UPDATE_CHARACTER + name + '\0' + current.toString());
	}

	
	public void mousePressed(MouseEvent e)
	{
		out.println(ServerConstants.CREATE_BULLET + (name + bulletCount) + '\0' + Bullet.toString(current.posX + 25, current.posY + 25, e.getX(), e.getY()));
		bulletCount++;
	}

	public void mouseClicked(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	
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
						if (input.equals(ServerConstants.READY_TO_PLAY))
						{
							frame.setContentPane(outer);
							frame.validate();
							frame.repaint();
							outer.requestFocus();
							outer.addKeyListener(outer);
							outer.addMouseListener(outer);
							out.println(ServerConstants.ADD_CHARACTER + name + '\0' + current.toString());
						}
						else if (input.equals(ServerConstants.GAME_IN_SESSION))
						{
							waitTime.setText("Game is in session. Please wait for the next game.");
							out.println(ServerConstants.DELETE_CHARACTER + name);
							waiting = true;
						}
						else if (!waiting && input.startsWith(ServerConstants.UPDATE_CHARACTER))
							allPlayers.get(input.substring(ServerConstants.UPDATE_CHARACTER.length(), input.indexOf('\0'))).setPlayer(input.substring(input.indexOf('\0') + 1));
						else if (input.startsWith(ServerConstants.CREATE_BULLET))
							allBullets.put(input.substring(ServerConstants.CREATE_BULLET.length(), input.indexOf('\0')), Bullet.getNewBullet(input.substring(input.indexOf('\0') + 1), outer, true));
						else if (input.startsWith(ServerConstants.SET_TEAM))
						{
							current.team = input.substring(ServerConstants.SET_TEAM.length());
							current.posX = (int)(Math.random() * 540 + 10);
							if (current.team.equals("blue"))
								current.posY = 50;
							else
								current.posY = 500;
						}
						else if (input.startsWith(ServerConstants.WAIT_BEFORE_PLAY))
							waitTime.setText("Starting in " + Integer.parseInt(input.substring(ServerConstants.WAIT_BEFORE_PLAY.length())));
						else if (input.startsWith(ServerConstants.DELETE_CHARACTER))
							allPlayers.remove(input.substring(ServerConstants.DELETE_CHARACTER.length()));
						else if (!waiting && input.startsWith(ServerConstants.ADD_CHARACTER))
						{
							out.println(ServerConstants.UPDATE_CHARACTER + name + '\0' + current.toString());
							allPlayers.put(input.substring(ServerConstants.ADD_CHARACTER.length(), input.indexOf('\0')), Player.getNewPlayer(input.substring(input.indexOf('\0') + 1)));
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