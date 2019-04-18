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
	ConcurrentHashMap<String, Player> players;
	ConcurrentHashMap<String, Bullet> bullets;
	Player player;
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
		players = new ConcurrentHashMap<String, Player>();
		bullets = new ConcurrentHashMap<String, Bullet>();
		player = new Player();
		
		frame = new JFrame();
		frame.setSize(ServerConstants.FRAME_SIZE, ServerConstants.FRAME_SIZE);
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
						out.println(ServerConstants.DELETE_PLAYER + name);
					}
				});

				try
				{
					name = nameEnter.getText() + ServerConstants.NAME_SEPERATOR + InetAddress.getLocalHost() + System.currentTimeMillis();
					socket = new Socket("192.168.1.168", ServerConstants.PORT_NUMBER);  // windows: 192.168.1.2, mac: 192.168.1.168
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
		for (String playerName : players.keySet())
			players.get(playerName).draw(g, playerName.substring(0, playerName.indexOf(ServerConstants.NAME_SEPERATOR)));
		for (Bullet bullet : bullets.values())
			bullet.draw(g);
	}

	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}

	public void keyPressed(KeyEvent evt)
	{
		int e = evt.getKeyCode();
		if (e == KeyEvent.VK_UP)
			player.posY -= 2;
		else if (e == KeyEvent.VK_DOWN)
			player.posY += 2;
		else if (e == KeyEvent.VK_LEFT)
			player.posX -= 2;
		else if (e == KeyEvent.VK_RIGHT)
			player.posX += 2;
		out.println(ServerConstants.UPDATE_PLAYER + name + '\0' + player.toString());
	}

	
	public void mousePressed(MouseEvent e)
	{
		out.println(ServerConstants.CREATE_BULLET + (name + bulletCount) + '\0' + Bullet.toString(player.posX, player.posY, e.getX(), e.getY(), player.team));
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
							out.println(ServerConstants.ADD_PLAYER + name + '\0' + player.toString());
						}
						else if (input.equals(ServerConstants.GAME_IN_SESSION))
						{
							waitTime.setText("Game is in session. Please wait for the next game.");
							out.println(ServerConstants.DELETE_PLAYER + name);
							waiting = true;
						}
						else if (!waiting && input.startsWith(ServerConstants.UPDATE_BULLET))
							bullets.get(input.substring(ServerConstants.UPDATE_BULLET.length())).update();
						else if (!waiting && input.startsWith(ServerConstants.UPDATE_PLAYER))
							players.get(input.substring(ServerConstants.UPDATE_PLAYER.length(), input.indexOf('\0'))).setPlayer(input.substring(input.indexOf('\0') + 1));
						else if (!waiting && input.startsWith(ServerConstants.TERMINATE_BULLET))
							bullets.remove(input.substring(ServerConstants.TERMINATE_BULLET.length()));
						else if (!waiting && input.startsWith(ServerConstants.CREATE_BULLET))
							bullets.put(input.substring(ServerConstants.CREATE_BULLET.length(), input.indexOf('\0')), Bullet.getNewBullet(input.substring(input.indexOf('\0') + 1)));
						else if (!waiting && input.startsWith(ServerConstants.SET_TEAM))
						{
							player.team = input.substring(ServerConstants.SET_TEAM.length());
							player.posX = (int)(Math.random() * (ServerConstants.PLAYER_SIZE - ServerConstants.PLAYER_SIZE * 3) + ServerConstants.PLAYER_SIZE * 1.5) / 2 * 2;
							if (player.team.equals("blue"))
								player.posY = ServerConstants.PLAYER_SIZE;
							else
								player.posY = ServerConstants.FRAME_SIZE - ServerConstants.PLAYER_SIZE;
						}
						else if (input.startsWith(ServerConstants.WAIT_BEFORE_PLAY))
							waitTime.setText("Starting in " + Integer.parseInt(input.substring(ServerConstants.WAIT_BEFORE_PLAY.length())));
						else if (!waiting && input.startsWith(ServerConstants.DELETE_PLAYER))
							players.remove(input.substring(ServerConstants.DELETE_PLAYER.length()));
						else if (!waiting && input.startsWith(ServerConstants.ADD_PLAYER))
						{
							out.println(ServerConstants.UPDATE_PLAYER + name + '\0' + player.toString());
							players.put(input.substring(ServerConstants.ADD_PLAYER.length(), input.indexOf('\0')), Player.getNewPlayer(input.substring(input.indexOf('\0') + 1)));
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