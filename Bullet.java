import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.JPanel;

public class Bullet
{
	int posX, posY;
	Timer mover;
	String team;

	private Bullet(int x, int y, int xAdd, int yAdd, String team, JPanel ref, boolean start)
	{
		posX = x;
		posY = y;
		this.team = team;
		mover = new Timer(10, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				posX += xAdd;
				posY += yAdd;
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
		return "" + (fromX - 5) + '\0' + (fromY - 5) + '\0' + (toX - 5) + '\0' + (toY - 5) + '\0' + team;
	}
}