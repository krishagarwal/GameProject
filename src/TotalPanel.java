/*
Krish Agarwal
5.10.19
TotalPanel.java
*/

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.Socket;
import javax.swing.*;

// This class is the JPanel class used to display all of the game.
// It does not store separate JPanels for different screens, rather
// displays them all reative to a variable determining the current
// screen to display to allow the capability of sliding the screen
// instead of a sudden change using CardLayout.
public class TotalPanel extends JPanel implements MouseListener, KeyListener
{
	public static int viewX, viewY, origX, origY, posX, posY, movePosX, movePosY;
	String waitText, winningTeam, winner;
	private Board gameBoard;
	Timer screenMoverLeft, screenMoverRight, screenMoverDown, screenMoverUp, moverUp, moverDown, moverLeft, moverRight, posMover;
	boolean loading, showWinner, won;
	private Color red, blue;

	// This constructor initializes the JPanel. The layout is set to
	// null because all components are drawn in paintComponent(). It
	// adds the required listeners and also initializes some field
	// variables.
	public TotalPanel()
	{
		setLayout(null);
		waitText = winningTeam = winner = "";
		setFocusable(true);
		requestFocus();
		addMouseListener(this);
		addKeyListener(this);
		posX = posY = ServerConstants.FRAME_SIZE / 2;
		origX = viewX = ServerConstants.FRAME_SIZE;
		origY = viewY = 2 * ServerConstants.FRAME_SIZE;
		gameBoard = new Board();
		loading = won = false;
		
		// This method is used to send information to the Server
		// that the player is moving up. It is placed in a Timer
		// so that when the up arrow key is held down, the
		// movement is smooth.
		moverUp = new Timer(30, (e) -> Client.send(ServerConstants.MOVE_PLAYER_UP + Client.playerName));
		
		// This method is used to send information to the Server
		// that the player is moving down. It is placed in a Timer
		// so that when the down arrow key is held down, the
		// movement is smooth.
		moverDown = new Timer(30, (e) -> Client.send(ServerConstants.MOVE_PLAYER_DOWN + Client.playerName));

		// This method is used to send information to the Server
		// that the player is moving left. It is placed in a Timer
		// so that when the left arrow key is held down, the
		// movement is smooth.
		moverLeft = new Timer(30, (e) -> Client.send(ServerConstants.MOVE_PLAYER_LEFT + Client.playerName));

		// This method is used to send information to the Server
		// that the player is moving right. It is placed in a Timer
		// so that when the right arrow key is held down, the
		// movement is smooth.
		moverRight = new Timer(30, (e) -> Client.send(ServerConstants.MOVE_PLAYER_RIGHT + Client.playerName));

		// This method is used to move the screen right in a sliding
		// motion when switching between different "panels".
		screenMoverRight = getScreenMover(() -> viewX += 20, 600, 0);

		// This method is used to move the screen left in a sliding
		// motion when switching between different "panels".
		screenMoverLeft = getScreenMover(() -> viewX -= 20, 600, 0);

		// This method is used to move the screen up in a sliding
		// motion when switching between different "panels".
		screenMoverUp = getScreenMover(() -> viewY -= 20, 0, 600);

		// This method is used to move the screen down in a sliding
		// motion when switching between different "panels".
		screenMoverDown = getScreenMover(() -> viewY += 20, 0, 600);

		// This method is used to move the game board towards the
		// center of the board when a win situation has occured as
		// an animation before displaying the results of the game.
		posMover = new Timer(25, (e) ->
		{
			posX += movePosX;
			posY += movePosY;
			if (posX == ServerConstants.BOARD_SIZE / 2 || posY == ServerConstants.BOARD_SIZE / 2)
			{
				posMover.stop();
				showWinner = true;
			}
			repaint();
		});

		red = new Color(245, 0, 0);
		blue = new Color(0, 140, 245);
	}

	// This method is used to draw the components on the screen. It
	// draws all different "panels" at the same time so that when
	// a switch is needed between different "panels", the movement is
	// a sliding motion.
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// For my reference: GamePanel
		g.setFont(new Font("Sans Serif", Font.PLAIN, 12));
		Player me = Client.players.get(Client.playerName);
		if (me != null)
		{
			posX = me.posX;
			posY = me.posY;
		}
		int refX = (posX - ServerConstants.FRAME_SIZE / 2) % ServerConstants.FRAGMENT_SIZE * -1
			- ServerConstants.FRAGMENT_SIZE + ServerConstants.FRAME_SIZE - viewX;
		int refY = (posY - ServerConstants.FRAME_SIZE / 2) % ServerConstants.FRAGMENT_SIZE * -1
			- ServerConstants.FRAGMENT_SIZE - viewY;
		gameBoard.drawBoard(g, (posY - ServerConstants.FRAME_SIZE / 2) / ServerConstants.FRAGMENT_SIZE - 1, 
			(posY - ServerConstants.FRAME_SIZE / 2) / ServerConstants.FRAGMENT_SIZE + 16, 
			(posX - ServerConstants.FRAME_SIZE / 2) / ServerConstants.FRAGMENT_SIZE - 1, 
			(posX - ServerConstants.FRAME_SIZE / 2) / ServerConstants.FRAGMENT_SIZE + 16, refX, refY);
		for (Player curr : Client.players.values())
		{
			if (!curr.dead)
				curr.draw(g, posX + ServerConstants.FRAME_SIZE - viewX, posY - viewY);
		}
		for (Bullet bullet : Client.bullets.values())
			bullet.draw(g, posX + ServerConstants.FRAME_SIZE - viewX, posY - viewY);		
		if (showWinner)
		{
			g.setFont(new Font("Sans Serif", Font.PLAIN, 40));
			g.setColor(new Color(125, 125, 125, 220));
			g.fillRect(10 + ServerConstants.FRAME_SIZE - viewX, 10 - viewY, 580, 560);
			g.setColor(Color.DARK_GRAY);
			g.fillRect(185 + ServerConstants.FRAME_SIZE - viewX, 300 - viewY, 230, 60);
			String topText = "your team won!";
			Color winStatus = blue;
			if (won == false)
			{
				winStatus = red;
				topText = "your team lost!";
			}
			g.setColor(Color.BLACK);
			g.drawString(topText, (int)(300 - g.getFontMetrics().getStringBounds(topText, g).getWidth() / 2) - 2 + ServerConstants.FRAME_SIZE - viewX, 102 - viewY);
			g.setColor(winStatus);
			g.drawString(topText, (int)(300 - g.getFontMetrics().getStringBounds(topText, g).getWidth() / 2) + ServerConstants.FRAME_SIZE - viewX, 100 - viewY);
			Color nameColor = red, losingColor = blue;
			String losingTeam = "blue";
			if (winningTeam.equals("blue"))
			{
				losingTeam = "red";
				nameColor = blue;
				losingColor = red;
			}
			int secondWidth = (int)(g.getFontMetrics().getStringBounds("\"" + winner + "\"" + " got the " + losingTeam + " flag.", g).getWidth());
			g.setColor(Color.BLACK);
			g.drawString("\"" + winner + "\"" + " got the " + losingTeam + " flag.", 298 - secondWidth / 2 + ServerConstants.FRAME_SIZE - viewX, 202 - viewY);
			g.setColor(nameColor);
			g.drawString("\"" + winner + "\"", 300 - secondWidth / 2 + ServerConstants.FRAME_SIZE - viewX, 200 - viewY);
			g.setColor(Color.WHITE);
			g.drawString(" got the ", 300 - secondWidth / 2
				+ (int)(g.getFontMetrics().getStringBounds("\"" + winner + "\"", g).getWidth()) + ServerConstants.FRAME_SIZE - viewX, 200 - viewY);
			g.setColor(losingColor);
			g.drawString(losingTeam, 300 - secondWidth / 2
				+ (int)(g.getFontMetrics().getStringBounds("\"" + winner + "\"" + " got the ", g).getWidth()) + ServerConstants.FRAME_SIZE - viewX, 200 - viewY);
			g.setColor(Color.WHITE);
			g.drawString(" flag.", 300 - secondWidth / 2 + (int)(g.getFontMetrics().getStringBounds("\"" + winner + "\"" + " got the " + losingTeam, g).getWidth())
				+ ServerConstants.FRAME_SIZE - viewX, 200 - viewY);
			g.setFont(new Font("Sans Serif", Font.PLAIN, 20));
			g.drawString("return to main menu", (int)(300 - g.getFontMetrics().getStringBounds("return to main menu", g).getWidth() / 2)
				+ ServerConstants.FRAME_SIZE - viewX, 340 - viewY);
		}
		g.setFont(new Font("Sans Serif", Font.PLAIN, 20));
		
		// For my reference: NamePanel
		g.setColor(Color.BLACK);
		String displayName = Client.playerName;
		if (displayName.indexOf(ServerConstants.NAME_SEPERATOR) >= 0)
			displayName = displayName.substring(0, displayName.indexOf(ServerConstants.NAME_SEPERATOR));
		int nameWidth = (int)(g.getFontMetrics().getStringBounds(displayName, g).getWidth());
		g.fillRect(ServerConstants.FRAME_SIZE - viewX, ServerConstants.FRAME_SIZE * 2 - viewY, 600, 600);
		g.setColor(Color.DARK_GRAY);
		g.fillRect(300 - nameWidth / 2 - 10 + ServerConstants.FRAME_SIZE - viewX, 275 + ServerConstants.FRAME_SIZE * 2 - viewY, nameWidth + 20, 50);
		g.setColor(Color.WHITE);
		g.drawString("enter your name:",
			(int)(300 - g.getFontMetrics().getStringBounds("enter your name:", g).getWidth() / 2)
			+ ServerConstants.FRAME_SIZE - viewX, 265 + ServerConstants.FRAME_SIZE * 2 - viewY);
		g.drawString(displayName, 300 - nameWidth / 2 + ServerConstants.FRAME_SIZE - viewX, 310 + ServerConstants.FRAME_SIZE * 2 - viewY);

		// For my reference: StartPanel
		int playWidth = (int)(g.getFontMetrics().getStringBounds("play", g).getWidth());
		g.setColor(Color.BLACK);
		g.fillRect(ServerConstants.FRAME_SIZE - viewX, ServerConstants.FRAME_SIZE - viewY, 600, 600);
		g.setColor(Color.DARK_GRAY);
		g.fillRect(260 + ServerConstants.FRAME_SIZE - viewX, 270 + ServerConstants.FRAME_SIZE - viewY, 80, 60);
		g.setColor(Color.WHITE);
		g.drawString("play", 300 - playWidth / 2 + ServerConstants.FRAME_SIZE - viewX, 310 + ServerConstants.FRAME_SIZE - viewY);

		// For my reference: ServerInputPanel
		g.setColor(Color.BLACK);
		int ipWidth = (int)(g.getFontMetrics().getStringBounds(Client.ip, g).getWidth());
		g.fillRect(ServerConstants.FRAME_SIZE * 2 - viewX, ServerConstants.FRAME_SIZE - viewY, 600, 600);
		g.setColor(Color.DARK_GRAY);
		g.fillRect(290 - ipWidth / 2 + ServerConstants.FRAME_SIZE * 2 - viewX, 275 + ServerConstants.FRAME_SIZE - viewY, ipWidth + 20, 50);
		g.setColor(Color.WHITE);
		g.drawString("enter the ip of the server (continue if on the same machine):",
			(int)(300 - g.getFontMetrics().getStringBounds("enter the ip of the server (continue if on the same machine):", g).getWidth() / 2)
			+ ServerConstants.FRAME_SIZE * 2 - viewX, 265 + ServerConstants.FRAME_SIZE - viewY);
		g.drawString(Client.ip, 300 - ipWidth / 2 + ServerConstants.FRAME_SIZE * 2 - viewX, 310 + ServerConstants.FRAME_SIZE - viewY);
		
		// For my reference: WaitPanel
		g.setColor(Color.BLACK);
		g.fillRect(ServerConstants.FRAME_SIZE * 2 - viewX, -viewY, 600, 600);
		g.setColor(Color.WHITE);
		g.drawString(waitText, (int)(300 - g.getFontMetrics().getStringBounds(waitText, g).getWidth() / 2)
			+ ServerConstants.FRAME_SIZE * 2 - viewX, 325 - viewY);

		if (loading)
			g.drawString("loading...", (int)(300 - g.getFontMetrics().getStringBounds("loading...", g).getWidth() / 2), 400);
		
		if ((viewX == ServerConstants.FRAME_SIZE && viewY == ServerConstants.FRAME_SIZE * 2)
			|| (viewX == ServerConstants.FRAME_SIZE * 2 && viewY == ServerConstants.FRAME_SIZE))
		{
			g.setFont(new Font("Sans Serif", Font.ITALIC, 15));
			g.drawString("[press enter to continue]", ServerConstants.FRAME_SIZE - 30
				- (int)(g.getFontMetrics().getStringBounds("[press enter to continue]", g).getWidth()), 400);
		}
	}

	// This method is part of the KeyListener. Whenever a key is
	// pressed, it checks which "panel" is currently being displayed
	// and then responds appropriately.
	public void keyPressed(KeyEvent evt)
	{
		if (viewX == ServerConstants.FRAME_SIZE && viewY == ServerConstants.FRAME_SIZE * 2)
		{
			if (evt.getKeyCode() == KeyEvent.VK_ENTER)
			{
				loading = true;
				repaint();
				Client.playerName += ServerConstants.NAME_SEPERATOR +
					ServerConstants.getLocalHost(Client.frame, "not connected to internet") + System.currentTimeMillis();
				loading = false;
				repaint();
				moveUp();
			}
			else if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE && Client.playerName.length() > 0)
			{
				Client.playerName = Client.playerName.substring(0, Client.playerName.length() - 1);
				repaint();
			}
		}
		else if (viewX == ServerConstants.FRAME_SIZE * 2 && viewY == ServerConstants.FRAME_SIZE)
		{
			if (evt.getKeyCode() == KeyEvent.VK_ENTER)
			{
				Client.connect();
				moveUp();
				Client.frame.addWindowListener(new WindowListener()
				{
					// This method is part of the WindowListener but
					// is not used.
					public void windowOpened(WindowEvent e) {}
					
					// This method is part of the WindowListener but
					// is not used.
					public void windowIconified(WindowEvent e) {}
					
					// This method is part of the WindowListener but
					// is not used.
					public void windowDeiconified(WindowEvent e) {}
					
					// This method is part of the WindowListener but
					// is not used.
					public void windowDeactivated(WindowEvent e) {}
					
					// This method is part of the WindowListener but
					// is not used.
					public void windowClosed(WindowEvent e) {}
					
					// This method is part of the WindowListener but
					// is not used.
					public void windowActivated(WindowEvent e) {}

					// This method is run when the Window is closing
					// to notify the Server to delete the player.
					public void windowClosing(WindowEvent e)
					{
						if (Client.out != null)
							Client.send(ServerConstants.DELETE_PLAYER + Client.playerName);
					}
				});
			}
			else if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE && Client.ip.length() > 0)
			{
				Client.ip = Client.ip.substring(0, Client.ip.length() - 1);
				repaint();
			}
		}
		else if (Client.playing && viewX == ServerConstants.FRAME_SIZE && viewY == 0 && allAreStopped())
		{
			int e = evt.getKeyCode();
			if (e == KeyEvent.VK_UP)
				moverUp.start();
			else if (e == KeyEvent.VK_DOWN)
				moverDown.start();
			else if (e == KeyEvent.VK_LEFT)
				moverLeft.start();
			else if (e == KeyEvent.VK_RIGHT)
				moverRight.start();
		}
	}
	
	// This method is part of the KeyListener. Whenever a key is
	// released, it checks which "panel" is currently being displayed
	// and then responds appropriately.
	public void keyReleased(KeyEvent evt)
	{
		if (Client.playing && viewX == ServerConstants.FRAME_SIZE && viewY == 0)
		{
			int e = evt.getKeyCode();
			if (e == KeyEvent.VK_UP)
				moverUp.stop();
			else if (e == KeyEvent.VK_DOWN)
				moverDown.stop();
			else if (e == KeyEvent.VK_LEFT)
				moverLeft.stop();
			else if (e == KeyEvent.VK_RIGHT)
				moverRight.stop();
		}
	}

	// This method is part of the KeyListener. Whenever a key is
	// typed, it checks which "panel" is currently being displayed
	// and then responds appropriately.
	public void keyTyped(KeyEvent evt)
	{
		if (evt.getKeyChar() == '\n' || evt.getKeyChar() == '\b')
			return;
		if (viewX == ServerConstants.FRAME_SIZE && viewY == ServerConstants.FRAME_SIZE * 2)
		{
			Client.playerName += evt.getKeyChar();
			repaint();
		}
		else if (viewX == ServerConstants.FRAME_SIZE * 2 && viewY == ServerConstants.FRAME_SIZE)
		{
			Client.ip += evt.getKeyChar();
			repaint();
		}
	}

	// This method is part of the MouseListener. Whenever the mouse is
	// pressed, it checks which "panel" is currently being displayed
	// and then responds appropriately.
	public void mousePressed(MouseEvent e)
	{
		requestFocus();
		if (viewX == ServerConstants.FRAME_SIZE && viewY == ServerConstants.FRAME_SIZE && e.getX() >= 260 && e.getX() <= 340 && e.getY() >= 270 && e.getY() <= 330)
			moveRight();
		else if (viewX == ServerConstants.FRAME_SIZE && viewY == 0)
		{
			if (Client.playing)
			{
				Player player = Client.players.get(Client.playerName);
				if (player == null || player.dead)
					return;
				Client.send(ServerConstants.CREATE_BULLET + (Client.playerName + ServerConstants.NAME_SEPERATOR + Client.bulletCount) + '\0' + 
					Bullet.toString(player.posX, player.posY, e.getX() - ServerConstants.FRAME_SIZE / 2 + player.posX,
						e.getY() - ServerConstants.FRAME_SIZE / 2 + player.posY, player.team));
				Client.bulletCount++;
			}
			else if (showWinner && e.getX() >= 185 && e.getX() <= 415 && e.getY() >= 300 && e.getY() <= 360)
				moveDown();
		}
	}
	
	// This method is part of the MouseListener but is not used.
	public void mouseReleased(MouseEvent e) {}
	
	// This method is part of the MouseListener but is not used.
	public void mouseClicked(MouseEvent e) {}

	// This method is part of the MouseListener but is not used.
	public void mouseEntered(MouseEvent e) {}

	// This method is part of the MouseListener but is not used.
	public void mouseExited(MouseEvent e) {}

	// This method is used to set the text of the wait "panel"
	// for when the countdown is occuring in the Server program.
	public void setWaitText(String message)
	{
		waitText = message;
		repaint();
	}

	// This method starts the Timer to move the screen to switch
	// between "panels" smoothly.
	public void moveRight()
	{
		if (allScreenMoversStopped())
			screenMoverRight.start();
	}

	public void moveLeft()
	{
		if (allScreenMoversStopped())
			screenMoverLeft.start();
	}

	public void moveDown()
	{
		if (allScreenMoversStopped())
			screenMoverDown.start();
	}

	public void moveUp()
	{
		if (allScreenMoversStopped())
			screenMoverUp.start();
	}

	// This method checks if all the Timers for moving the player
	// are stopped so that when an arrow key is pressed, there is no
	// interference in movement occuring due to 2 Timers running
	// at the same time.
	public boolean allAreStopped()
	{
		return !(moverUp.isRunning() || moverDown.isRunning() || moverLeft.isRunning() || moverRight.isRunning());
	}

	// This method checks that all the screen mover timers are stopped
	// so that two are not being run at the same time.
	public boolean allScreenMoversStopped()
	{
		return !(screenMoverUp.isRunning() || screenMoverDown.isRunning() || screenMoverLeft.isRunning() || screenMoverRight.isRunning());
	}

	// This method stops all the timers that move the current player
	// so that two are not being run at the same time.
	public void stopAll()
	{
		moverUp.stop();
		moverDown.stop();
		moverLeft.stop();
		moverRight.stop();
	}

	// This method returns a screen mover Timer. This method is present
	// because the code for the different screen movers was very similar,
	// so this reduces code.
	private Timer getScreenMover(Runnable runner, int xDifference, int yDifference)
	{
		return new Timer(30, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				runner.run();
				repaint();
				if (Math.abs(origX - viewX) == xDifference && Math.abs(origY - viewY) == yDifference)
				{
					screenMoverDown.stop();
					screenMoverUp.stop();
					screenMoverLeft.stop();
					screenMoverRight.stop();
					posX = posY = ServerConstants.BOARD_SIZE / 2;
					movePosX = movePosY = 0;
					waitText = winner = winningTeam = "";
					Client.blueFlagTaken = Client.redFlagTaken = showWinner = won = false;
					origX = viewX;
					origY = viewY;
				}
			}
		});
	}
}