import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.ConcurrentHashMap;

public class Bullet
{
	int posX, posY, addX, addY;
	Timer mover;
	String team;

	private Bullet(int x, int y, int xAdd, int yAdd, String team, JPanel ref, boolean start)
	{
		posX = x;
		posY = y;
		addX = xAdd;
		addY = yAdd;
		this.team = team;
		mover = new Timer(10, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				posX += addX;
				posY += addY;
				ref.repaint();
			}
		});
		if (start)
			mover.start();
	}

	private static Bullet getNewBullet(int fromX, int fromY, int toX, int toY, String team, JPanel ref, boolean start)
	{
		double radians = Math.atan((double)(toY - fromY) / (toX - fromX));
		if (fromX == toX && fromY == toY)
		{
			radians = Math.random() * 2 * Math.PI;
			return new Bullet(fromX, fromY, (int)(Math.cos(radians) * 6), (int)(Math.sin(radians) * 6), team, ref, start);
		}
		if (fromX == toX)
			return new Bullet(fromX, fromY, 0, Math.abs(toY - fromY) / (toY - fromY) * 6, team, ref, start);
		return new Bullet(fromX, fromY, (int)(Math.cos(radians) * 6) * Math.abs(toX - fromX) / (toX - fromX), (int)(Math.sin(radians) * 6) * Math.abs(toX - fromX) / (toX - fromX), team, ref, start);
	}

	public static Bullet getNewBullet(String input, JPanel ref, boolean start)
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
		return getNewBullet(fromX, fromY, toX, toY, team, ref, start);
	}

	public static String toString(int fromX, int fromY, int toX, int toY, String team)
	{
		return "" + fromX + '\0' + fromY + '\0' + toX + '\0' + toY + '\0' + team;
	}

	public void remove(String name, ConcurrentHashMap<String, Bullet> bullets)
	{
		mover.stop();
		bullets.remove(name);
	}

	public void draw(Graphics g)
	{
		g.setColor(Color.RED);
		if (team.equals("blue"))
			g.setColor(Color.BLUE);
		g.fillOval(posX - ServerConstants.BULLET_SIZE / 2, posY - ServerConstants.BULLET_SIZE / 2, ServerConstants.BULLET_SIZE, ServerConstants.BULLET_SIZE);
	}
}