import java.awt.*;

public class Player
{
	String team;
	int posX, posY;

	public Player(int posX, int posY, String team)
	{
		this.posX = posX;
		this.posY = posY;
		this.team = team;
	}

	public static Player getNewPlayer(String input)
	{
		int posX = Integer.parseInt(input.substring(0, input.indexOf('\0')));
		input = input.substring(input.indexOf('\0') + 1);
		int posY = Integer.parseInt(input.substring(0, input.indexOf('\0')));
		input = input.substring(input.indexOf('\0') + 1);
		return new Player(posX, posY, input);
	}

	public static String toString(int posX, int posY, String team)
	{
		return "" + posX + '\0' + posY + '\0' + team;
	}

	public double getDistanceTo(int x, int y)
	{
		return Math.sqrt(Math.pow(x - posX, 2) + Math.pow(y - posY, 2));
	}

	public void draw(Graphics g, String name)
	{
		g.setColor(new Color(255, 132, 132, 80));
		if (team.equals("blue"))
			g.setColor(new Color(147, 197, 255, 80));
		g.fillRect(posX - ServerConstants.PLAYER_SIZE / 2, posY - ServerConstants.PLAYER_SIZE / 2, ServerConstants.PLAYER_SIZE, ServerConstants.PLAYER_SIZE);
		g.setColor(Color.BLACK);
		g.drawString(name, posX - (int)(g.getFontMetrics().getStringBounds(name, g).getWidth()) / 2, posY - ServerConstants.PLAYER_SIZE / 2 - 5);
		g.drawRect(posX - ServerConstants.PLAYER_SIZE / 2, posY - ServerConstants.PLAYER_SIZE / 2, ServerConstants.PLAYER_SIZE, ServerConstants.PLAYER_SIZE);
	}
}