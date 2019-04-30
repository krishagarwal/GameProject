import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class ServerThread implements Runnable
{
	public void run()
	{
		try
		{
			Client.out = new PrintWriter(Client.socket.getOutputStream(), true);
			Client.serverIn = new Scanner(Client.socket.getInputStream());
			
			while (!Client.socket.isClosed())
			{
				if (Client.serverIn.hasNext())
				{
					String input = Client.serverIn.nextLine();
					if (input.equals(ServerConstants.READY_TO_PLAY))
					{
						Client.cl.show(Client.parentPanel, Client.GAME);
						Client.gamePanel.requestFocus();
						int posY = ServerConstants.BOARD_SIZE - ServerConstants.FRAGMENT_SIZE * 2;
						if (Client.team.equals("blue"))
							posY = ServerConstants.FRAGMENT_SIZE * 2;
						Client.send(ServerConstants.ADD_PLAYER + Player.toString((int)(Math.random() * (ServerConstants.BOARD_SIZE -
							ServerConstants.FRAGMENT_SIZE * 3) + 1.5 * ServerConstants.FRAGMENT_SIZE) / 5 * 5, posY, Client.playerName, Client.team));
					}
					else if (input.equals(ServerConstants.GAME_IN_SESSION))
					{
						((WaitPanel)(Client.waitPanel)).setText("Game is in session. Please wait for the next game.");
						Client.send(ServerConstants.DELETE_PLAYER + Client.playerName);
						Client.waiting = true;
					}
					else if (input.startsWith(ServerConstants.REVIVE_PLAYER))
						Client.players.get(input.substring(ServerConstants.REVIVE_PLAYER.length(), 
							input.indexOf('\0'))).revive(Integer.parseInt(input.substring(input.indexOf('\0') + 1)));
					else if (input.startsWith(ServerConstants.DECREASE_PLAYER_HEALTH))
						Client.players.get(input.substring(ServerConstants.DECREASE_PLAYER_HEALTH.length())).decreaseHealth();
					else if (!Client.waiting && input.startsWith(ServerConstants.UPDATE_BULLET))
					{
						Bullet curr = Client.bullets.get(input.substring(ServerConstants.UPDATE_BULLET.length()));
						if (curr != null)
							curr.update();
					}
					else if (!Client.waiting && input.startsWith(ServerConstants.MOVE_PLAYER_UP))
						Client.players.get(input.substring(ServerConstants.MOVE_PLAYER_UP.length())).moveUp();
					else if (!Client.waiting && input.startsWith(ServerConstants.MOVE_PLAYER_DOWN))
						Client.players.get(input.substring(ServerConstants.MOVE_PLAYER_DOWN.length())).moveDown();
					else if (!Client.waiting && input.startsWith(ServerConstants.MOVE_PLAYER_LEFT))
						Client.players.get(input.substring(ServerConstants.MOVE_PLAYER_LEFT.length())).moveLeft();
					else if (!Client.waiting && input.startsWith(ServerConstants.MOVE_PLAYER_RIGHT))
						Client.players.get(input.substring(ServerConstants.MOVE_PLAYER_RIGHT.length())).moveRight();
					else if (!Client.waiting && input.startsWith(ServerConstants.TERMINATE_BULLET))
						Client.bullets.remove(input.substring(ServerConstants.TERMINATE_BULLET.length()));
					else if (!Client.waiting && input.startsWith(ServerConstants.CREATE_BULLET))
						Client.bullets.put(input.substring(ServerConstants.CREATE_BULLET.length(), input.indexOf('\0')), 
							Bullet.getNewBullet(input.substring(input.indexOf('\0') + 1)));
					else if (!Client.waiting && input.startsWith(ServerConstants.SET_TEAM))
						Client.team = input.substring(ServerConstants.SET_TEAM.length());
					else if (input.startsWith(ServerConstants.WAIT_BEFORE_PLAY))
						((WaitPanel)(Client.waitPanel)).setText("Starting in " + Integer.parseInt(input.substring(ServerConstants.WAIT_BEFORE_PLAY.length())));
					else if (!Client.waiting && input.startsWith(ServerConstants.DELETE_PLAYER))
						Client.players.remove(input.substring(ServerConstants.DELETE_PLAYER.length()));
					else if (!Client.waiting && input.startsWith(ServerConstants.ADD_PLAYER))
						Client.players.put(input.substring(ServerConstants.ADD_PLAYER.length(), input.indexOf('\0')), 
							Player.getNewPlayer(input.substring(ServerConstants.ADD_PLAYER.length())));
					Client.gamePanel.repaint();
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}