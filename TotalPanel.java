/*
Krish Agarwal
5.12.19
TotalPanel.java
*/

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.*;

// This class is the JPanel class used to display all of the game.
// It does not store separate JPanels for different screens, rather
// displays them all reative to a variable determining the current
// screen to display to allow the capability of sliding the screen
// instead of a sudden change using CardLayout.
public class TotalPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener
{
	public static int chatX, viewX, viewY, origX, origY, posX, posY, movePosX, movePosY, showCount;
	String waitText, winningTeam, winner, spectating, showText, sendText;
	public Board gameBoard;
	Timer screenMoverLeft, screenMoverRight, screenMoverDown, screenMoverUp, moverUp, moverDown, moverLeft, moverRight, posMover, textShower, chatMoverLeft, chatMoverRight;
	boolean loading, showWinner, won, showEnter, showHoverPlay, hasEnteredName, showHoverMainMenu;
	CopyOnWriteArrayList<String> messages, deathLog;
	private Color red, blue;
	Image heart, play, playHover, rvb, ctf, collab, leftArr, rightArr, mainMenu, mainMenuHover;

	// This constructor initializes the JPanel. The layout is set to
	// null because all components are drawn in paintComponent(). It
	// adds the required listeners and also initializes some field
	// variables.
	public TotalPanel()
	{
		setLayout(null);
		showText = spectating = waitText = winningTeam = winner = "";
		requestFocus();
		addMouseListener(this);
		addKeyListener(this);
		addMouseMotionListener(this);
		posX = posY = ServerConstants.FRAME_SIZE / 2;
		origX = viewX = ServerConstants.FRAME_SIZE;
		origY = viewY = 2 * ServerConstants.FRAME_SIZE;
		gameBoard = new Board();
		showHoverMainMenu = hasEnteredName = showHoverPlay = loading = won = false;
		showEnter = true;
		heart = new ImageIcon("heart.png").getImage();
		rvb = new ImageIcon("red_vs_blue.png").getImage();
		ctf = new ImageIcon("capture_the_flag.png").getImage();
		collab = new ImageIcon("collaborative.png").getImage();
		leftArr = new ImageIcon("left_arrow.png").getImage();
		rightArr = new ImageIcon("right_arrow.png").getImage();
		
		// This method shows some text at the top of the game panel
		// for only about 1 second and then stops displaying the text
		textShower = new Timer(1000, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				showCount++;
				if (showCount > 2)
				{
					showCount = 0;
					showText = "";
					textShower.stop();
				}
				repaint();
			}
		});

		chatMoverLeft = new Timer(30, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				chatX -= 10;
				if (chatX == -170)
					chatMoverLeft.stop();
			}
		});

		chatMoverRight = new Timer(30, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				chatX += 10;
				if (chatX == 10)
					chatMoverRight.stop();
			}
		});
		
		// This method is used to send information to the Server
		// that the player is moving up. It is placed in a Timer
		// so that when the up arrow key is held down, the
		// movement is smooth.
		moverUp = new Timer(30, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Client.send(ServerConstants.MOVE_PLAYER_UP + Client.playerName);
			}
		});
		
		// This method is used to send information to the Server
		// that the player is moving down. It is placed in a Timer
		// so that when the down arrow key is held down, the
		// movement is smooth.
		moverDown = new Timer(30, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Client.send(ServerConstants.MOVE_PLAYER_DOWN + Client.playerName);
			}
		});

		// This method is used to send information to the Server
		// that the player is moving left. It is placed in a Timer
		// so that when the left arrow key is held down, the
		// movement is smooth.
		moverLeft = new Timer(30, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Client.send(ServerConstants.MOVE_PLAYER_LEFT + Client.playerName);
			}
		});

		// This method is used to send information to the Server
		// that the player is moving right. It is placed in a Timer
		// so that when the right arrow key is held down, the
		// movement is smooth.
		moverRight = new Timer(30, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Client.send(ServerConstants.MOVE_PLAYER_RIGHT + Client.playerName);
			}
		});

		// This method is used to move the screen right in a sliding
		// motion when switching between different "panels".
		screenMoverRight = getScreenMover(new Runnable()
		{
			public void run()
			{
				viewX += 20;
			}
		}, 600, 0);

		// This method is used to move the screen left in a sliding
		// motion when switching between different "panels".
		screenMoverLeft = getScreenMover(new Runnable()
		{
			public void run()
			{
				viewX -= 20;
			}
		}, 600, 0);

		// This method is used to move the screen up in a sliding
		// motion when switching between different "panels".
		screenMoverUp = getScreenMover(new Runnable()
		{
			public void run()
			{
				viewY -= 20;
			}
		}, 0, 600);

		// This method is used to move the screen down in a sliding
		// motion when switching between different "panels".
		screenMoverDown = getScreenMover(new Runnable()
		{
			public void run()
			{
				viewY += 20;
			}
		}, 0, 600);

		// This method is used to move the game board towards the
		// center of the board when a win situation has occured as
		// an animation before displaying the results of the game.
		

		posMover = new Timer(25, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				posX += movePosX;
				posY += movePosY;
				if (posX == ServerConstants.BOARD_SIZE / 2 || posY == ServerConstants.BOARD_SIZE / 2)
				{
					posMover.stop();
					showWinner = true;
				}
				repaint();
			}
		});

		red = new Color(245, 0, 0);
		blue = new Color(0, 140, 245);
		play = new ImageIcon("play.png").getImage();
		playHover = new ImageIcon("play_hover.png").getImage();
		mainMenu = new ImageIcon("main_menu.png").getImage();
		mainMenuHover = new ImageIcon("main_menu_hover.png").getImage();

		chatX = 10;
		sendText = "You: ";
		messages = new CopyOnWriteArrayList<String>();
		deathLog = new CopyOnWriteArrayList<String>();
	}

	// This method is used to draw the components on the screen. It
	// draws all different "panels" at the same time so that when
	// a switch is needed between different "panels", the movement is
	// a sliding motion.
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// For my reference: GamePanel
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		Player me = Client.players.get(Client.playerName), spectatingPlayer = Client.players.get(spectating);
		boolean drawHearts = false, showSpectating = false;
		if (me != null && !me.dead)
		{
			posX = me.posX;
			posY = me.posY;
			drawHearts = true;
		}
		else if (spectatingPlayer != null)
		{
			posX = spectatingPlayer.posX;
			posY = spectatingPlayer.posY;
			showSpectating = true;
		}
		
		int refX = (posX - ServerConstants.FRAME_SIZE / 2) % ServerConstants.FRAGMENT_SIZE * -1
			- ServerConstants.FRAGMENT_SIZE + ServerConstants.FRAME_SIZE - viewX;
		int refY = (posY - ServerConstants.FRAME_SIZE / 2) % ServerConstants.FRAGMENT_SIZE * -1
			- ServerConstants.FRAGMENT_SIZE - viewY;
		gameBoard.drawBoard(g, (posY - ServerConstants.FRAME_SIZE / 2) / ServerConstants.FRAGMENT_SIZE - 1, 
			(posY - ServerConstants.FRAME_SIZE / 2) / ServerConstants.FRAGMENT_SIZE + 16, 
			(posX - ServerConstants.FRAME_SIZE / 2) / ServerConstants.FRAGMENT_SIZE - 1, 
			(posX - ServerConstants.FRAME_SIZE / 2) / ServerConstants.FRAGMENT_SIZE + 16, refX, refY);

		g.setColor(new Color(125, 125, 125, 100));
		if (posMover.isRunning());
		else if (Client.gameMode == ServerConstants.CAPTURE_THE_FLAG)
		{
			g.fillRoundRect(225, 5, 190, 50, 15, 15);
			g.drawImage(ctf, 230, 10, 180, 40, null);
		}
		else
			g.fillRoundRect(235, 5, 170, 50, 15, 15);
		if (posMover.isRunning());
		else if (Client.gameMode == ServerConstants.RED_VS_BLUE)
			g.drawImage(rvb, 240, 10, 160, 40, null);
		else if (Client.gameMode == ServerConstants.COLLABORATIVE)
			g.drawImage(collab, 240, 10, 160, 40, null);
		
		for (Player curr : Client.players.values())
		{
			if (!curr.dead)
				curr.draw(g, posX + ServerConstants.FRAME_SIZE - viewX, posY - viewY);
		}
		for (Bullet bullet : Client.bullets.values())
			bullet.draw(g, posX + ServerConstants.FRAME_SIZE - viewX, posY - viewY);		
		if (showWinner && Client.gameMode == ServerConstants.CAPTURE_THE_FLAG)
		{
			g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 25));
			g.setColor(new Color(125, 125, 125, 220));
			g.fillRect(10 + ServerConstants.FRAME_SIZE - viewX, 10 - viewY, 580, 560);
			// g.setColor(Color.DARK_GRAY);
			// g.fillRect(185 + ServerConstants.FRAME_SIZE - viewX, 300 - viewY, 230, 60);
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
			g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
			if (showHoverMainMenu)
				g.drawImage(mainMenuHover, 185, 310, 230, 60, null);
			g.drawImage(mainMenu, 185 + ServerConstants.FRAME_SIZE - viewX, 300 - viewY, 230, 60, null);
		}
		else if (showWinner)
		{
			g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 25));
			g.setColor(new Color(125, 125, 125, 220));
			g.fillRoundRect(10 + ServerConstants.FRAME_SIZE - viewX, 10 - viewY, 580, 560, 15, 15);
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
			int secondWidth = (int)(g.getFontMetrics().getStringBounds("\"" + winner + "\"" + " killed the last " + losingTeam, g).getWidth());
			g.setColor(Color.BLACK);
			g.drawString("\"" + winner + "\"" + " killed the last " + losingTeam, 298 - secondWidth / 2 + ServerConstants.FRAME_SIZE - viewX, 202 - viewY);
			g.setColor(nameColor);
			g.drawString("\"" + winner + "\"", 300 - secondWidth / 2 + ServerConstants.FRAME_SIZE - viewX, 200 - viewY);
			g.setColor(Color.WHITE);
			g.drawString(" killed the last ", 300 - secondWidth / 2
				+ (int)(g.getFontMetrics().getStringBounds("\"" + winner + "\"", g).getWidth()) + ServerConstants.FRAME_SIZE - viewX, 200 - viewY);
			g.setColor(losingColor);
			g.drawString(losingTeam, 300 - secondWidth / 2
				+ (int)(g.getFontMetrics().getStringBounds("\"" + winner + "\"" + " killed the last ", g).getWidth()) + ServerConstants.FRAME_SIZE - viewX, 200 - viewY);
			g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
			if (showHoverMainMenu)
				g.drawImage(mainMenuHover, 185, 310, 230, 60, null);
			g.drawImage(mainMenu, 185 + ServerConstants.FRAME_SIZE - viewX, 300 - viewY, 230, 60, null);
		}
		else if (!posMover.isRunning() && drawHearts && Client.gameMode != ServerConstants.CAPTURE_THE_FLAG)
		{
			if (me.livesLeft >= 1)
				g.drawImage(heart, 540 + ServerConstants.FRAME_SIZE - viewX, 10 - viewY, 50, 50, null);
			if (me.livesLeft >= 2)
				g.drawImage(heart, 480 + ServerConstants.FRAME_SIZE - viewX, 10 - viewY, 50, 50, null);
			if (me.livesLeft >= 3)
				g.drawImage(heart, 420 + ServerConstants.FRAME_SIZE - viewX, 10 - viewY, 50, 50, null);
		}
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
		g.setColor(new Color(125, 125, 125, 100));
		if (!posMover.isRunning() && showSpectating)
		{	
			String display = "Spectating \"" + ServerConstants.regulateName(spectating.substring(0, spectating.indexOf(ServerConstants.NAME_SEPERATOR))) + "\"";
			int displayWidth = (int)(g.getFontMetrics().getStringBounds(display, g).getWidth());
			g.fillRoundRect(590 - displayWidth - 20, 60, displayWidth + 20, 25, 15, 15);
			g.setColor(Color.WHITE);
			g.drawString(display, 580 - displayWidth, 80);
			g.setColor(new Color(125, 125, 125, 100));
		}
		if (!posMover.isRunning() && !showText.equals(""))
		{
			int showWidth = (int)(g.getFontMetrics().getStringBounds(showText, g).getWidth());
			g.fillRoundRect(300 - showWidth / 2 - 10, 545, showWidth + 20, 25, 15, 15);
			g.setColor(Color.WHITE);
			g.drawString(showText, 300 - showWidth / 2, 565);
		}
		if (!showWinner && !posMover.isRunning())
		{
			g.setColor(new Color(125, 125, 125, 220));
			g.fillRoundRect(chatX, 10, 210, 560, 15, 15);
			if (chatX == -170 || chatMoverRight.isRunning())
				g.drawImage(rightArr, chatX + 180, 20, 20, 20, null);
			else
				g.drawImage(leftArr, chatX + 180, 20, 20, 20, null);
			g.fillRoundRect(405, 445, 185, 125, 15, 15);
			g.setColor(new Color(200, 200, 200, 150));
			g.fillRoundRect(chatX, 10, 210, 40, 15, 15);
			g.fillRoundRect(405, 445, 185, 40, 15, 15);
			g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
			g.setColor(Color.WHITE);
			g.drawString("Team Chat", chatX + 10, 40);
			g.drawString("Recent Deaths", 415, 475);
			g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
			g.setColor(Color.WHITE);
			displayMessages(g);
			displayDeaths(g);
			g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
		}

		// For my reference: NamePanel
		g.setColor(Color.BLACK);
		String displayName = Client.playerName;
		if (displayName.indexOf(ServerConstants.NAME_SEPERATOR) >= 0)
			displayName = displayName.substring(0, displayName.indexOf(ServerConstants.NAME_SEPERATOR));
		int nameWidth = (int)(g.getFontMetrics().getStringBounds(displayName, g).getWidth());
		g.fillRect(ServerConstants.FRAME_SIZE - viewX, ServerConstants.FRAME_SIZE * 2 - viewY, 600, 600);
		g.setColor(Color.DARK_GRAY);
		g.fillRoundRect(300 - nameWidth / 2 - 10 + ServerConstants.FRAME_SIZE - viewX, 275 + ServerConstants.FRAME_SIZE * 2 - viewY, nameWidth + 20, 50, 15, 15);
		g.setColor(Color.WHITE);
		g.drawString("enter your name:",
			(int)(300 - g.getFontMetrics().getStringBounds("enter your name:", g).getWidth() / 2)
			+ ServerConstants.FRAME_SIZE - viewX, 265 + ServerConstants.FRAME_SIZE * 2 - viewY);
		g.drawString(displayName, 300 - nameWidth / 2 + ServerConstants.FRAME_SIZE - viewX, 310 + ServerConstants.FRAME_SIZE * 2 - viewY);

		// For my reference: StartPanel
		g.setColor(Color.BLACK);
		g.fillRect(ServerConstants.FRAME_SIZE - viewX, ServerConstants.FRAME_SIZE - viewY, 600, 600);
		if (showHoverPlay)
			g.drawImage(playHover, 250 + ServerConstants.FRAME_SIZE - viewX, 285 + ServerConstants.FRAME_SIZE - viewY, 100, 50, null);
		g.drawImage(play, 250 + ServerConstants.FRAME_SIZE - viewX, 275 + ServerConstants.FRAME_SIZE - viewY, 100, 50, null);

		// For my reference: ServerInputPanel
		g.setColor(Color.BLACK);
		int ipWidth = (int)(g.getFontMetrics().getStringBounds(Client.ip, g).getWidth());
		g.fillRect(ServerConstants.FRAME_SIZE * 2 - viewX, ServerConstants.FRAME_SIZE - viewY, 600, 600);
		g.setColor(Color.DARK_GRAY);
		g.fillRoundRect(290 - ipWidth / 2 + ServerConstants.FRAME_SIZE * 2 - viewX, 275 + ServerConstants.FRAME_SIZE - viewY, ipWidth + 20, 50, 15, 15);
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
		
		if (((viewX == ServerConstants.FRAME_SIZE && viewY == ServerConstants.FRAME_SIZE * 2)
			|| (viewX == ServerConstants.FRAME_SIZE * 2 && viewY == ServerConstants.FRAME_SIZE)) && showEnter)
		{
			g.setFont(new Font(Font.MONOSPACED, Font.ITALIC, 10));
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
			if (evt.getKeyCode() == KeyEvent.VK_ENTER && !hasEnteredName)
			{
				hasEnteredName = true;
				Client.playerName += ServerConstants.NAME_SEPERATOR +
					ServerConstants.getLocalHost(Client.frame, "not connected to internet") + System.currentTimeMillis();
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
			if (evt.getKeyCode() == KeyEvent.VK_ENTER && !Client.startedConnection && Client.connect())
				moveUp();
			else if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE && Client.ip.length() > 0)
			{
				Client.ip = Client.ip.substring(0, Client.ip.length() - 1);
				repaint();
			}
		}
		else if (Client.playing && viewX == ServerConstants.FRAME_SIZE && viewY == 0 && allAreStopped())
		{
			int e = evt.getKeyCode();
			if (e == KeyEvent.VK_UP && !Client.players.get(Client.playerName).dead)
				moverUp.start();
			else if (e == KeyEvent.VK_DOWN && !Client.players.get(Client.playerName).dead)
				moverDown.start();
			else if (e == KeyEvent.VK_LEFT && !Client.players.get(Client.playerName).dead)
				moverLeft.start();
			else if (e == KeyEvent.VK_RIGHT && !Client.players.get(Client.playerName).dead)
				moverRight.start();
			else if (e == KeyEvent.VK_BACK_SPACE && sendText.length() > 5)
			{
				sendText = sendText.substring(0, sendText.length() - 1);
				repaint();
			}
			else if (chatX == 10 && e == KeyEvent.VK_ENTER && sendText.length() > 5)
			{
				Client.send(ServerConstants.SEND_MESSAGE + Client.players.get(Client.playerName).team + '\0'
					+ ServerConstants.regulateName(Client.playerName.substring(0, Client.playerName.indexOf(ServerConstants.NAME_SEPERATOR))) + sendText.substring(3));
				sendText = "You: ";
			}
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
		if (viewX == ServerConstants.FRAME_SIZE && viewY == ServerConstants.FRAME_SIZE * 2 && Client.playerName.length() < 40)
		{
			Client.playerName += evt.getKeyChar();
			repaint();
		}
		else if (viewX == ServerConstants.FRAME_SIZE * 2 && viewY == ServerConstants.FRAME_SIZE)
		{
			Client.ip += evt.getKeyChar();
			repaint();
		}
		else if (viewX == ServerConstants.FRAME_SIZE && viewY == 0 && chatX == 10)
		{
			sendText += evt.getKeyChar();
			repaint();
		}
	}

	// This method is part of the MouseListener. Whenever the mouse is
	// pressed, it checks which "panel" is currently being displayed
	// and then responds appropriately.
	public void mousePressed(MouseEvent e)
	{
		requestFocus();
		if (viewX == ServerConstants.FRAME_SIZE && viewY == ServerConstants.FRAME_SIZE && e.getX() >= 250 && e.getX() <= 350 && e.getY() >= 275 && e.getY() <= 325)
			moveRight();
		else if (viewX == ServerConstants.FRAME_SIZE && viewY == 0)
		{
			if (showWinner && e.getX() >= 185 && e.getX() <= 415 && e.getY() >= 300 && e.getY() <= 360)
				moveDown();
			else if (!Client.playing)
				return;
			if (chatX == 10 && e.getX() >= 190 && e.getX() <= 210 && e.getY() >= 20 && e.getY() <= 40)
				chatMoverLeft.start();
			else if (chatX == -170 && e.getX() >= 10 && e.getX() <= 30 && e.getY() >= 20 && e.getY() <= 40)
				chatMoverRight.start();
			else
			{
				Player player = Client.players.get(Client.playerName);
				if (player == null || player.dead)
					return;
				Client.send(ServerConstants.CREATE_BULLET + (Client.playerName + ServerConstants.NAME_SEPERATOR + Client.bulletCount) + '\0' + 
					Bullet.toString(player.posX, player.posY, e.getX() - ServerConstants.FRAME_SIZE / 2 + player.posX,
						e.getY() - ServerConstants.FRAME_SIZE / 2 + player.posY, player.team));
				Client.bulletCount++;
			}
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

	public void mouseDragged(MouseEvent e) {}

	public void mouseMoved(MouseEvent e)
	{
		if (viewX == ServerConstants.FRAME_SIZE && viewY == 0)
		{
			if (showWinner)
			{
				if (e.getX() >= 185 && e.getX() <= 415 && e.getY() >= 300 && e.getY() <= 360)
					showHoverMainMenu = true;
				else
					showHoverMainMenu = false;
				repaint();
				return;
			}
			Player me = Client.players.get(Client.playerName);
			if (me == null)
				return;
			int y = e.getY() - ServerConstants.FRAME_SIZE / 2, x = e.getX() - ServerConstants.FRAME_SIZE / 2;
			double deg = Math.atan((double)y / x);
			if (x < 0)
				deg += Math.PI;
			Client.send(ServerConstants.MOVE_GUN + Client.playerName + '\0' + deg);
		}
		else if (viewX == ServerConstants.FRAME_SIZE && viewY == ServerConstants.FRAME_SIZE)
		{
			if (e.getX() >= 250 && e.getX() <= 350 && e.getY() >= 275 && e.getY() <= 325)
				showHoverPlay = true;
			else
				showHoverPlay = false;
			repaint();
		}
	}

	// This method is used to set the text of the wait "panel"
	// for when the countdown is occuring in the Server program.
	public void setWaitText(String message)
	{
		waitText = message;
		repaint();
	}

	// This method starts the Timer to move the screen right
	// to switch between "panels" smoothly.
	public void moveRight()
	{
		showHoverPlay = false;
		if (allScreenMoversStopped())
			screenMoverRight.start();
	}

	// This method starts the Timer to move the screen left
	// to switch between "panels" smoothly.
	public void moveLeft()
	{
		if (allScreenMoversStopped())
			screenMoverLeft.start();
	}

	// This method starts the Timer to move the screen down
	// to switch between "panels" smoothly.
	public void moveDown()
	{
		showHoverMainMenu = false;
		if (allScreenMoversStopped())
			screenMoverDown.start();
	}

	// This method starts the Timer to move the screen up
	// to switch between "panels" smoothly.
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
			// This method runs the given Runnable class method run()
			// and checks when to stop the Timer. This is used for the
			// screen movers and is here to reduce code.
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
					winner = winningTeam = "";
					if (viewX == ServerConstants.FRAME_SIZE && viewY == ServerConstants.FRAME_SIZE)
					{
						gameBoard.resetBoard();
						messages.clear();
						deathLog.clear();
					}
					Client.blueFlagTaken = Client.redFlagTaken = showWinner = won = false;
					origX = viewX;
					origY = viewY;
				}
			}
		});
	}

	// This takes a text input and shows the given text at the top of the game
	// panel for a few seconds by starting a timer.
	public void displayText(String text)
	{
		textShower.stop();
		showText = text;
		textShower.start();
	}

	public void addNewMessage(String msg)
	{
		if (!Client.players.get(Client.playerName).team.equals(msg.substring(0, msg.indexOf('\0'))))
			return;
		messages.add(msg.substring(msg.indexOf('\0') + 1));
		repaint();
	}

	public void displayMessages(Graphics g)
	{
		int y = 580;
		y = displayMessage(sendText, chatX + 10, y, g, true) - 5;
		for (int i = messages.size() - 1; i >= 0; i--)
		{
			y = displayMessage(messages.get(i), chatX + 10, y, g, false) - 5;
			if (y < 85)
				i = -1;
		}
	}

	public int displayMessage(String line, int x, int y, Graphics g, boolean isYou)
	{
		ArrayList<String> currLine = new ArrayList<String>();
		while(line.length() > 30)
		{
			int index = line.lastIndexOf(' ', 30);
			if (index < 0)
				index = 30;
			currLine.add(line.substring(0, index + 1));
			line = line.substring(index + 1);
		}
		currLine.add(line);

		int currY = y;
		for (int i = 1; i <= currLine.size(); i++)
		{
			currY -= 15;
			if (isYou)
			{
				g.setColor(new Color(200, 200, 200, 150));
				g.fillRoundRect(chatX, y - i * 15 - 12, 210, 17, 15, 15);
				g.setColor(Color.WHITE);
			}
			g.drawString(currLine.get(currLine.size() - i), x, y - i * 15);
			if (currY < 85)
				i = currLine.size() + 1;
		}
		return currY;
	}

	public void displayDeaths(Graphics g)
	{
		int y = 560;
		for (int i = deathLog.size() - 1; i >= deathLog.size() - 5 && i >= 0; i--)
			y = displayDeath(g, 580, y, deathLog.get(i));
	}

	public int displayDeath(Graphics g, int x, int y, String death)
	{
		Color first = blue, second = red;
		if (death.charAt(0) == ServerConstants.NONE_BLUE)
		{
			first = new Color(0, 0, 0, 0);
			second = blue;
		}
		if (death.charAt(0) == ServerConstants.NONE_RED)
		{
			first = new Color(0, 0, 0, 0);
			second = red;
		}
		else if (death.charAt(0) == ServerConstants.RED)
		{
			first = red;
			second = blue;
		}
		String killer = "\"" + death.substring(1, death.indexOf('\0')) + "\"", killed = "\"" + death.substring(death.indexOf('\0') + 1) + "\"";
		int startX = x - (int)(g.getFontMetrics().getStringBounds(killer + " killed " + killed, g).getWidth());
		int killerLength = (int)(g.getFontMetrics().getStringBounds(killer, g).getWidth()), sepLength = (int)(g.getFontMetrics().getStringBounds(" killed ", g).getWidth());
		g.setColor(new Color(first.getRed(), first.getGreen(), first.getBlue(), 80));
		g.fillRoundRect(startX, y - 10, killerLength, 10, 15, 15);
		g.setColor(new Color(second.getRed(), second.getGreen(), second.getBlue(), 80));
		g.fillRoundRect(startX + killerLength + sepLength, y - 10, (int)(g.getFontMetrics().getStringBounds(killed, g).getWidth()), 10, 15, 15);
		g.setColor(Color.WHITE);
		g.drawString(killer, startX, y);
		g.drawString(" killed ", startX + killerLength, y);
		g.drawString(killed, startX + killerLength + sepLength, y);
		return y - 15;
	}
}