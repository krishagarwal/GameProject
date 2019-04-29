import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Player
{
	String name, team;
	int posX, posY, health, blinkerCount;
	Image front, back, left, right, costume;
	Timer blinker;
	boolean show;

	public Player(int posX, int posY, String name, String team)
	{
		this.posX = posX;
		this.posY = posY;
		this.team = team;
		this.name = name;
		front = new ImageIcon("../images/" + team + "_front.png").getImage();
		back = new ImageIcon("../images/" + team + "_back.png").getImage();
		left = new ImageIcon("../images/" + team + "_left.png").getImage();
		right = new ImageIcon("../images/" + team + "_right.png").getImage();
		costume = back;
		if (team.equals("blue"))
			costume = front;
		health = ServerConstants.HEALTH;
		show = true;
		blinkerCount = 0;
		blinker = new Timer(250, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				show = !show;
				blinkerCount++;
				if (Client.gamePanel != null)
					Client.gamePanel.repaint();
				if (blinkerCount == 8)
					blinker.stop();
			}
		});
	}

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

	public static String toString(int posX, int posY, String name, String team)
	{
		return name + '\0' + posX + '\0' + posY + '\0' + team;
	}

	public double getDistanceTo(int x, int y)
	{
		return Math.sqrt(Math.pow(x - posX, 2) + Math.pow(y - posY, 2));
	}

	public void decreaseHealth()
	{
		health -= ServerConstants.HEALTH_DECREASE;
	}

	public void moveLeft()
	{
		posX -= ServerConstants.MOVE_LENGTH;
		costume = left;
	}

	public void moveRight()
	{
		posX += ServerConstants.MOVE_LENGTH;
		costume = right;
	}

	public void moveUp()
	{
		posY -= ServerConstants.MOVE_LENGTH;
		costume = back;
	}

	public void moveDown()
	{
		posY += ServerConstants.MOVE_LENGTH;
		costume = front;
	}

	public void revive(int newPosX)
	{
		posY = ServerConstants.BOARD_SIZE - ServerConstants.FRAGMENT_SIZE * 2;
		if (team.equals("blue"))
			posY = ServerConstants.FRAGMENT_SIZE * 2;
		posX = newPosX;
		health = ServerConstants.HEALTH;
		blinkerCount = 0;
		blinker.start();
	}

	public void draw(Graphics g, int refX, int refY)
	{
		if (!show)
			return;
		g.setColor(new Color(255, 132, 132, 80));
		int posX = this.posX + ServerConstants.FRAME_SIZE / 2 - refX;
		int posY = this.posY + ServerConstants.FRAME_SIZE / 2 - refY;
		if (team.equals("blue"))
			g.setColor(new Color(147, 197, 255, 80));
		g.drawImage(costume, posX - ServerConstants.FRAGMENT_SIZE / 2, posY - ServerConstants.FRAGMENT_SIZE / 2, 
			ServerConstants.FRAGMENT_SIZE, ServerConstants.FRAGMENT_SIZE, null);
		g.setColor(new Color(235 - (int)(health * 1.5), 35 + 2 * health, (int)(35 + health * 0.7)));
		g.fillRect(posX - ServerConstants.FRAGMENT_SIZE / 2, posY - ServerConstants.FRAGMENT_SIZE / 2 - 25,
			(int)(ServerConstants.FRAGMENT_SIZE / (double)(ServerConstants.HEALTH) * health), 5);
		g.setColor(Color.BLACK);
		String displayName = name.substring(0, name.indexOf(ServerConstants.NAME_SEPERATOR));
		g.drawString(displayName, posX - (int)(g.getFontMetrics().getStringBounds(displayName, g).getWidth()) / 2, 
			posY - ServerConstants.FRAGMENT_SIZE / 2 - 5);
		g.drawRect(posX - ServerConstants.FRAGMENT_SIZE / 2, posY - ServerConstants.FRAGMENT_SIZE / 2 - 25, ServerConstants.FRAGMENT_SIZE, 5);
	}
}