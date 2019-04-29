import java.awt.event.*;
import javax.swing.*;

public class Bot
{
	private String name;
	private static int bulletCount = 0;

	public Bot(String team)
	{
		int posX = (int)(Math.random() * (ServerConstants.BOARD_SIZE - ServerConstants.FRAGMENT_SIZE * 3) + 1.5 * ServerConstants.FRAGMENT_SIZE) / 5 * 5;
		int posY = ServerConstants.BOARD_SIZE - ServerConstants.FRAGMENT_SIZE * 2;
		if (team.equals("blue"))
			posY = ServerConstants.FRAGMENT_SIZE * 2;
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
				
				if (bulletCount % 5 == 0)
				{
					String input = (name + bulletCount) + '\0' + 
						Bullet.toString(player.posX, player.posY, nearest.posX, nearest.posY, team);
					Server.sendToAll(ServerConstants.CREATE_BULLET + input);
					Server.addBulletLog(ServerConstants.CREATE_BULLET + input);
				}
				bulletCount++;

				if (Math.abs(nearest.posX - player.posX) > Math.abs(nearest.posY - player.posY) && Math.abs(nearest.posX - player.posX) > 100)
				{
					if (nearest.posX - player.posX < 0 && !Server.gameBoard.isLeft(player.posX, player.posY))
					{
						Server.sendToAll(ServerConstants.MOVE_PLAYER_LEFT + name);
						player.moveLeft();
					}
					else if (!Server.gameBoard.isRight(player.posX, player.posY))
					{
						Server.sendToAll(ServerConstants.MOVE_PLAYER_RIGHT + name);
						player.moveRight();
					}
				}
				else if (Math.abs(nearest.posY - player.posY) > 100)
				{
					if (nearest.posY - player.posY < 0 && !Server.gameBoard.isAbove(player.posX, player.posY))
					{
						Server.sendToAll(ServerConstants.MOVE_PLAYER_UP + name);
						player.moveUp();
					}
					else if (!Server.gameBoard.isBelow(player.posX, player.posY))
					{
						Server.sendToAll(ServerConstants.MOVE_PLAYER_DOWN + name);
						player.moveDown();
					}
				}
			}
		});
		mover.start();
	}
}