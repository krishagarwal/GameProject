import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

// This class handles receiving all the information from the Server
// program. It runs runs separate from the GUI program so that it
// can receive information about game play from the Server program
// in real time so the multiplayer capability works.
public class ServerThread implements Runnable
{
	// This method is run separately from the GUI program (on another
	// thread) so that it receives information from the Server during
	// game play. It first establishes a method of communication
	// between this Client program and the Server program using
	// PrintWriter and Scanner (used for transferring information
	// in the form of Strings).vThen, for as long as the game is being
	// played, this method reads information inputted by the Server
	// program. Each message starts with an identifier indicating the
	// purpose of the message, so this method looks for this
	// identifier and responds accordingly (hence the long
	// if/else structure). In the end it requests the JPanel to
	// repaint to display to the changes that occured based on the
	// message that was received from the Server program.
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
						Client.playing = true;
						Client.totalPanel.moveLeft();
						Client.totalPanel.repaint();
						int posY = ServerConstants.BOARD_SIZE - ServerConstants.FRAGMENT_SIZE * 2;
						if (Client.team.equals("blue"))
							posY = ServerConstants.FRAGMENT_SIZE * 2;
						Client.send(ServerConstants.ADD_PLAYER + Player.toString((int)(Math.random() * (ServerConstants.BOARD_SIZE -
							ServerConstants.FRAGMENT_SIZE * 3) + 1.5 * ServerConstants.FRAGMENT_SIZE) / 5 * 5, posY, Client.playerName, Client.team));
					}
					else if (input.equals(ServerConstants.GAME_IN_SESSION))
					{
						Client.totalPanel.setWaitText("Game is in session. Please wait for the next game.");
						Client.send(ServerConstants.DELETE_PLAYER + Client.playerName);
						Client.waiting = true;
					}
					else if (!Client.waiting && input.startsWith(ServerConstants.FLAG_TAKEN))
					{
						Player player = Client.players.get(input.substring(ServerConstants.FLAG_TAKEN.length()));
						player.hasFlag = true;
						if (player.team.equals("red"))
							Client.blueFlagTaken = true;
						else
							Client.redFlagTaken = true;
					}
					else if (input.startsWith(ServerConstants.WIN))
					{
						Client.playing = false;
						String name = input.substring(ServerConstants.WIN.length());
						Client.totalPanel.stopAll();
						Client.totalPanel.winningTeam = Client.players.get(name).team;
						if (Client.totalPanel.winningTeam.equals(Client.players.get(Client.playerName).team))
							Client.totalPanel.won = true;
						Client.totalPanel.winner = name.substring(0, name.indexOf(ServerConstants.NAME_SEPERATOR));
						Client.clearGame();
						if (Client.waiting)
						{
							Client.connect();
							Client.waiting = false;
						}
					}
					else if (!Client.waiting && input.startsWith(ServerConstants.REVIVE_PLAYER))
						Client.players.get(input.substring(ServerConstants.REVIVE_PLAYER.length(), 
							input.indexOf('\0'))).revive(Integer.parseInt(input.substring(input.indexOf('\0') + 1)));
					else if (!Client.waiting && input.startsWith(ServerConstants.DECREASE_PLAYER_HEALTH))
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
					else if (!Client.waiting && input.startsWith(ServerConstants.WAIT_BEFORE_PLAY))
						Client.totalPanel.setWaitText("Starting in " + Integer.parseInt(input.substring(ServerConstants.WAIT_BEFORE_PLAY.length())));
					else if (!Client.waiting && input.startsWith(ServerConstants.DELETE_PLAYER))
						Client.players.remove(input.substring(ServerConstants.DELETE_PLAYER.length()));
					else if (!Client.waiting && input.startsWith(ServerConstants.ADD_PLAYER))
						Client.players.put(input.substring(ServerConstants.ADD_PLAYER.length(), input.indexOf('\0')), 
							Player.getNewPlayer(input.substring(ServerConstants.ADD_PLAYER.length())));
					Client.totalPanel.repaint();
				}
			}
			Client.socket = null;
			Client.serverIn = null;
			Client.out = null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}