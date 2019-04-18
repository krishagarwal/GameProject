import java.awt.event.*;
import javax.swing.*;

public class Bot
{
	private Player player;
	private String name;
	private static int bulletCount = 0;

	public Bot(String team)
	{
		player = new Player(team, (int)(Math.random() * 540 + 10)  / 2 * 2, 500);
		name = "Bot" + ServerConstants.NAME_SEPERATOR;
		Server.players.put(name, player);
		Server.sendToAll(ServerConstants.ADD_PLAYER + name + '\0' + player.toString());
		Timer mover = new Timer(85, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Player nearest = Server.getNearestOpponent(player.posX, player.posY, player.team);
				if (nearest == null)
					return;
				
				if (bulletCount % 5 == 0)
				{
					Server.sendToAll(ServerConstants.CREATE_BULLET + (name + bulletCount) + '\0' + Bullet.toString(player.posX, player.posY, nearest.posX, nearest.posY, team));
					Server.addBulletLog(ServerConstants.CREATE_BULLET + (name + bulletCount) + '\0' + Bullet.toString(player.posX, player.posY, nearest.posX, nearest.posY, team));
				}
				bulletCount++;

				if (Math.abs(nearest.posX - player.posX) > Math.abs(nearest.posY - player.posY) && Math.abs(nearest.posX - player.posX) > 100)
					player.posX += 2 * (nearest.posX - player.posX) / Math.abs(nearest.posX - player.posX);
				else if (Math.abs(nearest.posY - player.posY) > 100)
					player.posY += 2 * (nearest.posY - player.posY) / Math.abs(nearest.posY - player.posY);
				else
					return;
				Server.sendToAll(ServerConstants.UPDATE_PLAYER + name + '\0' + player.toString());
			}
		});
		mover.start();
	}
}