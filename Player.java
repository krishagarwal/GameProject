import java.awt.*;
import javax.swing.*;

public class Player
{
	String name, team;
	int posX, posY, health;
	Image front, back, left, right, costume;

	public Player(int posX, int posY, String name, String team)
	{
		this.posX = posX;
		this.posY = posY;
		this.team = team;
		this.name = name;
		front = new ImageIcon(team + "_front.png").getImage();
		back = new ImageIcon(team + "_back.png").getImage();
		left = new ImageIcon(team + "_left.png").getImage();
		right = new ImageIcon(team + "_right.png").getImage();
		costume = back;
		if (team.equals("blue"))
			costume = front;
		health = 100;
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
		health -= 5;
	}

	public void moveLeft()
	{
		posX -= 2;
		costume = left;
	}

	public void moveRight()
	{
		posX += 2;
		costume = right;
	}

	public void moveUp()
	{
		posY -= 2;
		costume = back;
	}

	public void moveDown()
	{
		posY += 2;
		costume = front;
	}

	public void revive(int newPosX)
	{
		posY = ServerConstants.FRAME_SIZE - ServerConstants.PLAYER_SIZE;
		if (team.equals("blue"))
			posY = ServerConstants.PLAYER_SIZE;
		posX = newPosX;
		health = 100;
	}

	public void draw(Graphics g, int refX, int refY)
	{
		g.setColor(new Color(255, 132, 132, 80));
		int posX = this.posX + 300 - refX;
		int posY = this.posY + 300 - refY;
		if (team.equals("blue"))
			g.setColor(new Color(147, 197, 255, 80));
		// g.fillRect(posX - ServerConstants.PLAYER_SIZE / 2, posY - ServerConstants.PLAYER_SIZE / 2, ServerConstants.PLAYER_SIZE, ServerConstants.PLAYER_SIZE);
		g.drawImage(costume, posX - ServerConstants.PLAYER_SIZE / 2, posY - ServerConstants.PLAYER_SIZE / 2, ServerConstants.PLAYER_SIZE, ServerConstants.PLAYER_SIZE, null);
		g.setColor(new Color(235 - (int)(health * 1.5), 35 + 2 * health, (int)(35 + health * 0.7)));
		g.fillRect(posX - ServerConstants.PLAYER_SIZE / 2, posY - ServerConstants.PLAYER_SIZE / 2 - 25, (int)(ServerConstants.PLAYER_SIZE / 100.0 * health), 5);
		g.setColor(Color.BLACK);
		String displayName = name.substring(0, name.indexOf(ServerConstants.NAME_SEPERATOR));
		g.drawString(displayName, posX - (int)(g.getFontMetrics().getStringBounds(displayName, g).getWidth()) / 2, 
			posY - ServerConstants.PLAYER_SIZE / 2 - 5);
		// g.drawRect(posX - ServerConstants.PLAYER_SIZE / 2, posY - ServerConstants.PLAYER_SIZE / 2, ServerConstants.PLAYER_SIZE, ServerConstants.PLAYER_SIZE);
		g.drawRect(posX - ServerConstants.PLAYER_SIZE / 2, posY - ServerConstants.PLAYER_SIZE / 2 - 25, ServerConstants.PLAYER_SIZE, 5);
	}
}