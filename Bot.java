import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;

public class Bot
{
	private Player player;
	private String name;
	private static int bulletCount = 0;
	String team;

	public Bot(String team)
	{
		this.team = team;
		player = new Player(team, (int)(Math.random() * 540 + 10)  / 2 * 2, 500);
		name = "Bot" + ServerConstants.NAME_SEPERATOR;
		
		for (Server client : Server.clients)
			client.getWriter().println(ServerConstants.ADD_CHARACTER + name + '\0' + player.toString());
		Timer mover = new Timer(85, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Player nearest = Server.getNearestOpponent(player);
				if (nearest == null)
					return;
				
				if (bulletCount % 5 == 0)
				{
					for (Server client : Server.clients)
						client.getWriter().println(ServerConstants.CREATE_BULLET + (name + bulletCount) + '\0' + Bullet.toString(player.posX + 25, player.posY + 25, nearest.posX + 25, nearest.posY + 25, team));
				}
				bulletCount++;

				if (Math.abs(nearest.posX - player.posX) > Math.abs(nearest.posY - player.posY) && Math.abs(nearest.posX - player.posX) > 100)
					player.posX += 2 * (nearest.posX - player.posX) / Math.abs(nearest.posX - player.posX);
				else if (Math.abs(nearest.posY - player.posY) > 100)
					player.posY += 2 * (nearest.posY - player.posY) / Math.abs(nearest.posY - player.posY);
				else
					return;
				for (Server client : Server.clients)
					client.getWriter().println(ServerConstants.UPDATE_CHARACTER + name + '\0' + player.toString());
			}
		});
		mover.start();
	}
}