/*
Krish Agarwal
5.12.19
Bot.java
*/

import java.awt.event.*;
import javax.swing.*;

// This class is used to store information on a Bot's location.
// Bot's are used when an uneven number of players are online
// during a game, meaning that an extra player is required to split
// two teams evenly. This class uses its own logic to play the game.
public class Bot
{
	private String name;
	private int bulletCount = 0;
	static int botCount = 0;
	private Timer mover;

	// This constructor is used to make a new Bot. It uses the given
	// team to position itself in the game, and also notifies all
	// Client programs that a Bot is present (although is is handled
	// as though the bot is a real player)
	public Bot(String team)
	{
		botCount++;
		int posX = (int)(Math.random() * (ServerConstants.BOARD_SIZE - ServerConstants.FRAGMENT_SIZE * 3) + 1.5 * ServerConstants.FRAGMENT_SIZE) / 5 * 5;
		int posY = ServerConstants.BOARD_SIZE - ServerConstants.FRAGMENT_SIZE * 2;
		if (team.equals("blue"))
			posY = ServerConstants.FRAGMENT_SIZE * 2;
		name = "Bot" + botCount + ServerConstants.NAME_SEPERATOR;
		Server.players.put(name, new Player(posX, posY, name, team));
		Server.sendToAll(ServerConstants.ADD_PLAYER + Player.toString(posX, posY, name, team));
		mover = new Timer(45, new ActionListener()
		{
			// This method is run once every 25 milliseconds (using a
			// Timer). This method handles the logic that the Bot
			// follows to play the game. It first locates the nearest
			// opponent. About every half a second, the Bot shoots a
			// bullet towards the nearest opponent. The Bot is
			// constantly trying to get within 100 pixels of the
			// nearest opponent, and when it reaches point, it stops,
			// but continues shooting.
			public void actionPerformed(ActionEvent e)
			{
				Player player = Server.players.get(name);
				if (player == null || player.dead)
				{
					mover.stop();
					return;
				}
				Player nearest = Server.getNearestOpponent(player.posX, player.posY, player.team);
				if (nearest == null)
					return;
				
				int y = nearest.posY - player.posY, x = nearest.posX - player.posX;
				double deg = Math.atan((double)y / x);
				if (x < 0)
					deg += Math.PI;
				Server.sendToAll(ServerConstants.MOVE_GUN + name + '\0' + deg);
				
				if (bulletCount % 10 == 0)
				{
					String input = (name + ServerConstants.NAME_SEPERATOR + bulletCount) + '\0' + 
						Bullet.toString(player.posX, player.posY, nearest.posX, nearest.posY, team);
					Server.sendToAll(ServerConstants.CREATE_BULLET + input);
					Server.addBulletLog(ServerConstants.CREATE_BULLET + input);
				}
				bulletCount++;

				boolean vertical = Math.abs(nearest.posY - player.posY) > 100;
				boolean horizontal = Math.abs(nearest.posX - player.posX) > 100;
				boolean[] gameStatus = {false, false};
				if ((horizontal && canMoveHorizontally()) || (!canMoveVertically() && vertical))
				{
					if (nearest.posX - player.posX < 0 && !Server.gameBoard.isLeft(player.posX, player.posY))
					{
						Server.sendToAll(ServerConstants.MOVE_PLAYER_LEFT + name);
						gameStatus = player.moveLeft();
						Server.checkHearts(player);
					}
					else if (!Server.gameBoard.isRight(player.posX, player.posY))
					{
						Server.sendToAll(ServerConstants.MOVE_PLAYER_RIGHT + name);
						gameStatus = player.moveRight();
						Server.checkHearts(player);
					}
				}
				else if ((vertical && canMoveVertically()) || (!canMoveHorizontally() && horizontal))
				{
					if (nearest.posY - player.posY < 0 && !Server.gameBoard.isAbove(player.posX, player.posY))
					{
						Server.sendToAll(ServerConstants.MOVE_PLAYER_UP + name);
						gameStatus = player.moveUp();
						Server.checkHearts(player);
					}
					else if (!Server.gameBoard.isBelow(player.posX, player.posY))
					{
						Server.sendToAll(ServerConstants.MOVE_PLAYER_DOWN + name);
						gameStatus = player.moveDown();
						Server.checkHearts(player);
					}
				}
				if (Server.gameMode == ServerConstants.CAPTURE_THE_FLAG && gameStatus[0])
				{
					player.hasFlag = true;
					Server.sendToAll(ServerConstants.FLAG_TAKEN + player.name);
				}
				if (Server.gameMode == ServerConstants.CAPTURE_THE_FLAG && gameStatus[1])
				{
					Server.sendToAll(ServerConstants.WIN + player.name);
					Server.clearGame();
				}
			}
		});
		mover.start();
	}

	// This method determines whether or not the bot can move
	// vertically based on whether or not there is a wall in the way
	// based on the Server's game board.
	public boolean canMoveVertically()
	{
		Player me = Server.players.get(name);
		return Server.gameBoard != null && !Server.gameBoard.isAbove(me.posX, me.posY) && !Server.gameBoard.isBelow(me.posX, me.posY);
	}

	// This method determines whether or not the bot can move
	// horizontally based on whether or not there is a wall in the way
	// based on the Server's game board.
	public boolean canMoveHorizontally()
	{
		Player me = Server.players.get(name);
		return Server.gameBoard != null && !Server.gameBoard.isLeft(me.posX, me.posY) && !Server.gameBoard.isRight(me.posX, me.posY);
	}
}