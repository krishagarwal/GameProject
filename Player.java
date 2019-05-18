/*
Krish Agarwal
5.12.19
Player.java
*/

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;

// This class is used to store information about each player
// that is participating in the game. It stores the team, name,
// health, and current position of each Player. It also has methods
// to handle displaying the player.
public class Player
{
	String name, team;
	int posX, posY, health, blinkerCount, livesLeft, currFront, currBack, currLeft, currRight;
	double gunDegree;
	Image[] front, left, back, right;
	Image redFlag, blueFlag, costume, rightGun, leftGun;
	Timer blinker;
	boolean show, hasFlag, dead;

	// This constructor is used to instantiate a Player given
	// the posiition, name, and team. It also presets the health
	// values as well as al of the images used to display the
	// character. 
	public Player(int posX, int posY, String name, String team)
	{
		this.posX = posX;
		this.posY = posY;
		this.team = team;
		this.name = name;
		livesLeft = 3;
		dead = false;
		redFlag = new ImageIcon("red_flag_icon.png").getImage();
		blueFlag = new ImageIcon("blue_flag_icon.png").getImage();
		rightGun = new ImageIcon("gun_right.png").getImage();
		leftGun = new ImageIcon("gun_left.png").getImage();
		health = ServerConstants.HEALTH;
		show = true;
		hasFlag = false;
		gunDegree = blinkerCount = 0;
		currFront = currBack = currLeft = currRight = 0;
		
		front = new Image[4];
		for (int i = 0; i < front.length - 1; i++)
			front[i] = new ImageIcon("front" + (i + 1) + ".png").getImage();
		front[3] = front[1];

		back = new Image[4];
		for (int i = 0; i < back.length - 1; i++)
			back[i] = new ImageIcon("back" + (i + 1) + ".png").getImage();
		back[3] = back[1];

		left = new Image[4];
		for (int i = 0; i < left.length - 1; i++)
			left[i] = new ImageIcon("left" + (i + 1) + ".png").getImage();
		left[3] = left[1];
		
		right = new Image[4];
		for (int i = 0; i < right.length - 1; i++)
			right[i] = new ImageIcon("right" + (i + 1) + ".png").getImage();
		right[3] = right[1];

		costume = front[1];

		blinker = new Timer(250, new ActionListener()
		{
			// This method is used in a Timer to make the
			// player appear as if it is blinking when they revive.
			// This is acheived by toggling the boolean variable "show".
			public void actionPerformed(ActionEvent e)
			{
				show = !show;
				blinkerCount++;
				if (Client.totalPanel != null)
					Client.totalPanel.repaint();
				if (blinkerCount == 8)
					blinker.stop();
			}
		});
	}

	// This method parses the input String received from a Server
	// program so that it can receive a new Player based on the
	// information sent by the Server. Once each value has been
	// parsed, the constructor is run to actually get an instance
	// of this player.
	public static Player getNewPlayer(String input)
	{
		String name = input.substring(0, input.indexOf('\0'));
		input = input.substring(input.indexOf('\0') + 1);
		int posX = Integer.parseInt(input.substring(0, input.indexOf('\0')));
		input = input.substring(input.indexOf('\0') + 1);
		int posY = Integer.parseInt(input.substring(0, input.indexOf('\0')));
		input = input.substring(input.indexOf('\0') + 1);
		return new Player(posX, posY, name, input);
	}
	
	// This method returns the formatted String with all the
	// information about a new player. This method is called when
	// the Client instantiates its Player, and passes this String to
	// the Server program, which will pass this String to all other
	// Clients. The String returned in this method is the String
	// input that is parsed in the getNewPlayer() method.
	public static String toString(int posX, int posY, String name,
		String team)
	{
		return name + '\0' + posX + '\0' + posY + '\0' + team;
	}

	// This method returns the distance between the Player and the
	// given point (x, y) using the distance formula. This method
	// is especially used to find the nearest opponent in the
	// Bot class.
	public double getDistanceTo(int x, int y)
	{
		return Math.sqrt(Math.pow(x - posX, 2) +
			Math.pow(y - posY, 2));
	}

	// This method decreases the health of the Player. This method
	// is used when a Bullet hits the Player.
	public void decreaseHealth()
	{
		health -= ServerConstants.HEALTH_DECREASE;
	}

	// This method moves the player left and also updates the image
	// that is shown to make the player face left. It returns two
	// boolean variables in an array that descibe the game status.
	// These variables are used in the Server program.
	public boolean[] moveLeft()
	{
		posX -= ServerConstants.MOVE_LENGTH;
		costume = left[currLeft % 4];
		currLeft++;
		return new boolean[] {hasFlag(), isWin()};
	}

	// This method moves the player right and also updates the image
	// that is shown to make the player face right. It returns two
	// boolean variables in an array that descibe the game status.
	// These variables are used in the Server program.
	public boolean[] moveRight()
	{
		posX += ServerConstants.MOVE_LENGTH;
		costume = right[currRight % 4];
		currRight++;
		return new boolean[] {hasFlag(), isWin()};	
	}

	// This method moves the player up and also updates the image
	// that is shown to make the player face up. It returns two
	// boolean variables in an array that descibe the game status.
	// These variables are used in the Server program.
	public boolean[] moveUp()
	{
		posY -= ServerConstants.MOVE_LENGTH;
		costume = back[currBack % 4];
		currBack++;
		return new boolean[] {hasFlag(), isWin()};	
	}

	// This method moves the player down and also updates the image
	// that is shown to make the player face down. It returns two
	// boolean variables in an array that descibe the game status.
	// These variables are used in the Server program.
	public boolean[] moveDown()
	{
		posY += ServerConstants.MOVE_LENGTH;
		costume = front[currFront % 4];
		currFront++;
		return new boolean[] {hasFlag(), isWin()};	
	}

	// Whenever the player moves, this method is called to check if
	// the player has the opposing team's flag.
	public boolean hasFlag()
	{
		return Server.gameBoard != null && ((posY / ServerConstants.FRAGMENT_SIZE == 2 && team.equals("red"))
			|| (posY / ServerConstants.FRAGMENT_SIZE == Server.gameBoard.total.length - 3 && team.equals("blue")))
			&& Server.gameBoard.total[posX / ServerConstants.FRAGMENT_SIZE][posY / ServerConstants.FRAGMENT_SIZE] == 'f';
	}

	// Whenever the player moves, this method is called to check if
	// the player crosses to its team's zone while having possesion
	// of the opposition's flag.
	public boolean isWin()
	{
		return Server.gameBoard != null && hasFlag && ((team.equals("red") && 
			(posY - ServerConstants.FRAGMENT_SIZE / 2) / ServerConstants.FRAGMENT_SIZE >= Server.gameBoard.total.length - 4)
			|| (team.equals("blue") && (posY + ServerConstants.FRAGMENT_SIZE / 2) / ServerConstants.FRAGMENT_SIZE <= 3));
	}

	// This method revives the Player by setting the health back
	// to 100 and placing the Player in a new position based on the
	// team. It also decreses the number of lives left.
	// This method is used when the Player is shot and the
	// Player ends up with 0 health, at which point revive is required.
	public void revive(int newPosX, String shooter)
	{
		hasFlag = false;
		if (Client.totalPanel != null && team.equals("red"))
			Client.blueFlagTaken = false;
		else if (Client.totalPanel != null && team.equals("blue"))
			Client.redFlagTaken = false;
		posY = ServerConstants.BOARD_SIZE - ServerConstants.FRAGMENT_SIZE * 2;
		if (team.equals("blue"))
			posY = ServerConstants.FRAGMENT_SIZE * 2;
		posX = newPosX;
		health = ServerConstants.HEALTH;
		livesLeft--;
		if (((Server.gameBoard != null && Server.gameMode != ServerConstants.CAPTURE_THE_FLAG)
			|| (Client.totalPanel != null && Client.gameMode != ServerConstants.CAPTURE_THE_FLAG)) && livesLeft == 0)
		{
			if (Client.totalPanel != null && (Client.playerName.equals(name) || Client.totalPanel.spectating.equals(name)))
				Client.totalPanel.spectating = shooter;
			dead = true;
		}
		blinkerCount = 0;
		blinker.start();
	}

	// This method is used to draw each Player. It draws the shape
	// of the Player, a health bar indicating health above the player,
	// and the Player's name centered above the health bar. This
	// method is called in the paintComponent() of the TotalPanel
	// class, where this method is called on each player in a HashMap
	// of Players stored by the Client.
	public void draw(Graphics g, int refX, int refY)
	{
		if (!show)
			return;
		int posX = this.posX + ServerConstants.FRAME_SIZE / 2 - refX;
		int posY = this.posY + ServerConstants.FRAME_SIZE / 2 - refY;
		g.setColor(Color.BLACK);
		if (hasFlag)
			g.drawLine(posX, posY, posX + 10, posY);
		g.setColor(new Color(255, 132, 132, 80));
		if (team.equals("blue"))
			g.setColor(new Color(147, 197, 255, 80));
		g.drawImage(costume, posX - ServerConstants.FRAGMENT_SIZE / 4, posY - ServerConstants.FRAGMENT_SIZE / 2, 
			ServerConstants.FRAGMENT_SIZE / 2, ServerConstants.FRAGMENT_SIZE, null);
		g.fillOval(posX - ServerConstants.FRAGMENT_SIZE / 2, posY - ServerConstants.FRAGMENT_SIZE / 2, ServerConstants.FRAGMENT_SIZE, ServerConstants.FRAGMENT_SIZE);
		g.setColor(new Color(235 - (int)(health * 1.5), 35 + 2 * health, (int)(35 + health * 0.7)));
		g.fillRect(posX - ServerConstants.FRAGMENT_SIZE / 2, posY - ServerConstants.FRAGMENT_SIZE / 2 - 25,
			(int)(ServerConstants.FRAGMENT_SIZE / (double)(ServerConstants.HEALTH) * health), 5);
		g.setColor(Color.BLACK);
		String displayName = name.substring(0, name.indexOf(ServerConstants.NAME_SEPERATOR));
		g.drawString(displayName, posX - (int)(g.getFontMetrics().getStringBounds(displayName, g).getWidth()) / 2, posY - ServerConstants.FRAGMENT_SIZE / 2 - 5);
		g.drawRect(posX - ServerConstants.FRAGMENT_SIZE / 2, posY - ServerConstants.FRAGMENT_SIZE / 2 - 25, ServerConstants.FRAGMENT_SIZE, 5);
		if (hasFlag && team.equals("red"))
			g.drawImage(blueFlag, posX + 7, posY - 20, 10, 20, null);
		else if (hasFlag && team.equals("blue"))
			g.drawImage(redFlag, posX + 7, posY - 20, 10, 20, null);
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform old = g2d.getTransform();
		if (Math.PI / 2 < gunDegree && gunDegree <= Math.PI * 1.5)
		{
			g2d.rotate(gunDegree - Math.PI, posX, posY);
			g2d.drawImage(leftGun, posX - 25, posY - 7, 25, 15, null);
		}
		else
		{
			g2d.rotate(gunDegree, posX, posY);
			g2d.drawImage(rightGun, posX, posY - 7, 25, 15, null);
		}
		g2d.setTransform(old);
	}
}
