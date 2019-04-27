import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.ConcurrentHashMap;

public class Bullet
{
	int posX, posY, addX, addY;
	String team;

	private Bullet(int x, int y, int xAdd, int yAdd, String team)
	{
		posX = x;
		posY = y;
		addX = xAdd;
		addY = yAdd;
		this.team = team;
	}

	private static Bullet getNewBullet(int fromX, int fromY, int toX, int toY, String team)
	{
		double radians = Math.atan((double)(toY - fromY) / (toX - fromX));
		if (fromX == toX && fromY == toY)
		{
			radians = Math.random() * 2 * Math.PI;
			return new Bullet(fromX, fromY, (int)(Math.cos(radians) * 6), (int)(Math.sin(radians) * 6), team);
		}
		if (fromX == toX)
			return new Bullet(fromX, fromY, 0, Math.abs(toY - fromY) / (toY - fromY) * 6, team);
		return new Bullet(fromX, fromY, (int)(Math.cos(radians) * 6) * Math.abs(toX - fromX) / (toX - fromX), 
			(int)(Math.sin(radians) * 6) * Math.abs(toX - fromX) / (toX - fromX), team);
	}

	public static Bullet getNewBullet(String input)
	{
		int fromX = Integer.parseInt(input.substring(0, input.indexOf('\0')));
		input = input.substring(input.indexOf('\0') + 1);
		int fromY = Integer.parseInt(input.substring(0, input.indexOf('\0')));
		input = input.substring(input.indexOf('\0') + 1);
		int toX = Integer.parseInt(input.substring(0, input.indexOf('\0')));
		input = input.substring(input.indexOf('\0') + 1);
		int toY = Integer.parseInt(input.substring(0, input.indexOf('\0')));
		input = input.substring(input.indexOf('\0') + 1);
		String team = input;
		return getNewBullet(fromX, fromY, toX, toY, team);
	}

	public static String toString(int fromX, int fromY, int toX, int toY, String team)
	{
		return "" + fromX + '\0' + fromY + '\0' + toX + '\0' + toY + '\0' + team;
	}

	public void update()
	{
		posX += addX;
		posY += addY;
	}

	public void draw(Graphics g, int refX, int refY)
	{
		int posX = this.posX + 300 - refX;
		int posY = this.posY + 300 - refY;
		g.setColor(Color.RED);
		if (team.equals("blue"))
			g.setColor(Color.BLUE);
		g.fillOval(posX - ServerConstants.BULLET_SIZE / 2, posY - ServerConstants.BULLET_SIZE / 2, ServerConstants.BULLET_SIZE, ServerConstants.BULLET_SIZE);
	}
}