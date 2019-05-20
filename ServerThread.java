/*
Krish Agarwal
5.12.19
ServerThread.java
*/

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.sound.sampled.Clip;

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
	// in the form of Strings). Then, for as long as the game is being
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
				try
				{
					if (Client.serverIn.hasNextLine())
					{
						String input = Client.serverIn.nextLine();
						if (input.equals(ServerConstants.GAME_IN_SESSION))
						{
							Client.totalPanel.setWaitText("Game is in session. Please wait for the next game.");
							Client.send(ServerConstants.DELETE_PLAYER + Client.playerName);
							Client.waiting = true;
						}
						else if (!Client.waiting && input.startsWith(ServerConstants.ADD_LIFE))
							Client.players.get(input.substring(ServerConstants.ADD_LIFE.length())).livesLeft++;
						else if (!Client.waiting && input.startsWith(ServerConstants.SEND_MESSAGE))
							Client.totalPanel.addNewMessage(input.substring(ServerConstants.SEND_MESSAGE.length()));
						else if (!Client.waiting && input.startsWith(ServerConstants.BLOW_UP))
							Client.totalPanel.gameBoard.total[Integer.parseInt(input.substring(ServerConstants.BLOW_UP.length(), input.indexOf('\0')))]
								[Integer.parseInt(input.substring(input.indexOf('\0') + 1))] = 'o';
						else if (!Client.waiting && input.startsWith(ServerConstants.CREATE_SHRAPNEL))
							Client.bullets.put(input.substring(ServerConstants.CREATE_SHRAPNEL.length(), input.indexOf('\0')), 
								Shrapnel.getNewShrapnel(input.substring(input.indexOf('\0') + 1)));
						else if (!Client.waiting && input.startsWith(ServerConstants.NEW_WAVE))
							Client.totalPanel.displayText("Wave " + input.substring(ServerConstants.NEW_WAVE.length()));
						else if (!Client.waiting && input.startsWith(ServerConstants.MOVE_GUN))
							Client.players.get(input.substring(ServerConstants.MOVE_GUN.length(), input.indexOf('\0'))).gunDegree
								= Double.parseDouble(input.substring(input.indexOf('\0') + 1));
						else if (!Client.waiting && input.startsWith(ServerConstants.FLAG_TAKEN))
						{
							Player player = Client.players.get(input.substring(ServerConstants.FLAG_TAKEN.length()));
							player.hasFlag = true;
							if (player.team.equals("red"))
								Client.blueFlagTaken = true;
							else
								Client.redFlagTaken = true;
						}
						else if (input.startsWith(ServerConstants.WIN) && !input.startsWith(ServerConstants.FLAG_TAKEN))
						{
							if (!Client.waiting)
							{
								Client.playing = false;
								String name = input.substring(ServerConstants.WIN.length());
								Client.totalPanel.stopAll();
								Client.totalPanel.winningTeam = Client.players.get(name).team;
								if (Client.totalPanel.winningTeam.equals(Client.players.get(Client.playerName).team))
									Client.totalPanel.won = true;
								Client.totalPanel.winner = ServerConstants.regulateName(name.substring(0, name.indexOf(ServerConstants.NAME_SEPERATOR)));
							}
							Client.clearGame();
							if (Client.waiting)
							{
								Client.connect();
								Client.waiting = false;
							}
						}
						else if (!Client.waiting && input.startsWith(ServerConstants.REVIVE_PLAYER) && !input.startsWith(ServerConstants.WIN))
						{
							String killed = input.substring(ServerConstants.REVIVE_PLAYER.length(), input.indexOf('\0'));
							String killer = input.substring(input.lastIndexOf('\0') + 1);
							Client.players.get(killed).revive(Integer.parseInt(input.substring(input.indexOf('\0') + 1, input.lastIndexOf('\0'))), killer);
							char start = ServerConstants.BLUE;
							if (Client.players.get(killer).team.equals("red"))
								start = ServerConstants.RED;
							Client.totalPanel.deathLog.add(start + ServerConstants.regulateName(killer.substring(0, killer.indexOf(ServerConstants.NAME_SEPERATOR)), 6)
								+ '\0' + ServerConstants.regulateName(killed.substring(0, killed.indexOf(ServerConstants.NAME_SEPERATOR)), 6));
							System.out.println("\"" + killer.substring(0, killer.indexOf(ServerConstants.NAME_SEPERATOR))
								+ "\" killed \"" + killed.substring(0, killed.indexOf(ServerConstants.NAME_SEPERATOR)) + "\"");
						}
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
							Client.totalPanel.setWaitText("starting in " + Integer.parseInt(input.substring(ServerConstants.WAIT_BEFORE_PLAY.length())));
						else if (!Client.waiting && input.startsWith(ServerConstants.DELETE_PLAYER))
							Client.players.remove(input.substring(ServerConstants.DELETE_PLAYER.length()));
						else if (!Client.waiting && input.startsWith(ServerConstants.ADD_PLAYER))
							Client.players.put(input.substring(ServerConstants.ADD_PLAYER.length(), input.indexOf('\0')), 
								Player.getNewPlayer(input.substring(ServerConstants.ADD_PLAYER.length())));
						else if (input.startsWith(ServerConstants.READY_TO_PLAY) && !input.startsWith(ServerConstants.ADD_PLAYER))
						{
							Client.gameMode = Integer.parseInt(input.substring(ServerConstants.READY_TO_PLAY.length(), input.lastIndexOf('\0')));
							Client.totalPanel.gameBoard.setBoard(input.substring(input.indexOf('\0') + 1));
							Client.playing = true;
							Client.totalPanel.moveLeft();
							int posY = ServerConstants.BOARD_SIZE - ServerConstants.FRAGMENT_SIZE * 2;
							if (Client.team.equals("blue"))
								posY = ServerConstants.FRAGMENT_SIZE * 2;
							Client.send(ServerConstants.ADD_PLAYER + Player.toString((int)(Math.random() * (ServerConstants.BOARD_SIZE -
								ServerConstants.FRAGMENT_SIZE * 3) + 1.5 * ServerConstants.FRAGMENT_SIZE) / 5 * 5, posY, Client.playerName, Client.team));
						}
						Client.totalPanel.repaint();
					}
				}
				catch(Exception e) {}
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