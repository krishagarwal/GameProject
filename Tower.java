import java.awt.*;

public class Tower
{
	String team;
	int posX, posY, health;

	public Tower(int posX, int posY, String team)
	{
		this.posX = posX;
		this.posY = posY;
		this.team = team;
		health = 100;
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
		health -= 1;
	}

	public void draw(Graphics g)
	{
		g.setColor(new Color(255, 132, 132, 80));
		if (team.equals("blue"))
			g.setColor(new Color(147, 197, 255, 80));
		g.fillRect(posX - ServerConstants.TOWER_SIZE / 2, posY - ServerConstants.TOWER_SIZE / 2, ServerConstants.TOWER_SIZE, ServerConstants.TOWER_SIZE);
		g.setColor(new Color(235 - (int)(health * 1.5), 35 + 2 * health, (int)(35 + health * 0.7)));
		g.fillRect(posX - ServerConstants.TOWER_SIZE / 2, posY - ServerConstants.TOWER_SIZE / 2 - 25, (int)(ServerConstants.TOWER_SIZE / 100.0 * health), 5);
		g.setColor(Color.BLACK);
		g.drawRect(posX - ServerConstants.TOWER_SIZE / 2, posY - ServerConstants.TOWER_SIZE / 2, ServerConstants.TOWER_SIZE, ServerConstants.TOWER_SIZE);
		g.drawRect(posX - ServerConstants.TOWER_SIZE / 2, posY - ServerConstants.TOWER_SIZE / 2 - 25, ServerConstants.TOWER_SIZE, 5);
	}
}