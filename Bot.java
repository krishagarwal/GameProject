import java.awt.event.*;
import javax.swing.*;

public class Bot
{
	private String name;
	private static int bulletCount = 0;

	public Bot(String team)
	{
		int posX = (int)(Math.random() * (1000 - ServerConstants.PLAYER_SIZE * 3) + ServerConstants.PLAYER_SIZE * 1.5);
		int posY = 1000 - ServerConstants.PLAYER_SIZE;
		if (team.equals("blue"))
			posY = ServerConstants.PLAYER_SIZE;
		name = "Bot" + ServerConstants.NAME_SEPERATOR;
		Server.players.put(name, new Player(posX, posY, name, team));
		Server.sendToAll(ServerConstants.ADD_PLAYER + Player.toString(posX, posY, name, team));
		Timer mover = new Timer(85, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Player player = Server.players.get(name);
				Player nearest = Server.getNearestOpponent(player.posX, player.posY, player.team);
				if (nearest == null)
					return;
				
				if (bulletCount % 5 == 6)
				{
					String input = (name + bulletCount) + '\0' + 
						Bullet.toString(player.posX, player.posY, nearest.posX, nearest.posY, team);
					Server.sendToAll(ServerConstants.CREATE_BULLET + input);
					Server.addBulletLog(ServerConstants.CREATE_BULLET + input);
				}
				bulletCount++;

				if (Math.abs(nearest.posX - player.posX) > Math.abs(nearest.posY - player.posY) && Math.abs(nearest.posX - player.posX) > 100)
				{
					player.posX += 2 * Math.abs(nearest.posX - player.posX) / (nearest.posX - player.posX);
					if (nearest.posX - player.posX < 0)
						Server.sendToAll(ServerConstants.MOVE_PLAYER_LEFT + name);
					else
						Server.sendToAll(ServerConstants.MOVE_PLAYER_RIGHT + name);
				}
				else if (Math.abs(nearest.posY - player.posY) > 100)
				{
					player.posY += 2 * Math.abs(nearest.posY - player.posY) / (nearest.posY - player.posY);
					if (nearest.posY - player.posY < 0)
						Server.sendToAll(ServerConstants.MOVE_PLAYER_UP + name);
					else
						Server.sendToAll(ServerConstants.MOVE_PLAYER_DOWN + name);
				}
			}
		});
		mover.start();
	}
}