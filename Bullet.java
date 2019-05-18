/*
Krish Agarwal
5.12.19
Bullet.java
*/

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

import javax.swing.*;
import java.util.concurrent.ConcurrentHashMap;

// This class is used to store information on each bullet fired
// during game play. It stores the position and the slope of the
// line that the bullet follows.
public class Bullet
{
	double posX, posY, addX, addY, degree;
	int fromX, fromY;
	String team;

	// This constructor instantiates a Bullet starting from (x, y)
	// and adding xAdd and yAdd to update the position.
	protected Bullet(double x, double y, double xAdd, double yAdd, String team)
	{
		addX = xAdd;
		addY = yAdd;
		posX = x + addX * 25 / 6;
		posY = y + addY * 25 / 6;
		fromX = (int)posX;
		fromY = (int)posY;
		degree = Math.atan((double)addY / addX);
		if (addX < 0)
			degree += Math.PI;
		this.team = team;
		// if (Client.totalPanel != null)
		// 	ServerConstants.playClip("gun_shot.wav", 1 - (Client.players.get(Client.playerName).getDistanceTo((int)posX, (int)posY)));
	}

	// This method returns a Bullet based on the starting coordinates
	// and the coordinates of the mouse press when a bullet is shot.
	// It calls the Bullet constructor when the xAdd and yAdd have
	// been calculated.
	private static Bullet getNewBullet(int fromX, int fromY, int toX, int toY, String team)
	{
		double radians = Math.atan((double)(toY - fromY) / (toX - fromX));
		if (fromX == toX && fromY == toY)
		{
			radians = Math.random() * 2 * Math.PI;
			return new Bullet(fromX, fromY, Math.cos(radians) * 6, Math.sin(radians) * 6, team);
		}
		if (fromX == toX)
			return new Bullet(fromX, fromY, 0, Math.abs(toY - fromY) / (toY - fromY) * 6, team);
		return new Bullet(fromX, fromY, Math.cos(radians) * 6 * Math.abs(toX - fromX) / (toX - fromX),
			Math.sin(radians) * 6 * Math.abs(toX - fromX) / (toX - fromX), team);
	}

	// This method takes the String input sent to all Clients when
	// a bullet is fired and retrieves individual values from the
	// String to call the other getNewBullet() so a Bullet can be
	// instantiated.
	public static Bullet getNewBullet(String input)
	{
		int fromX = Integer.parseInt(input.substring(0, input.indexOf('\0')));
		input = input.substring(input.indexOf('\0') + 1);
		int fromY = Integer.parseInt(input.substring(0, input.indexOf('\0')));
		input = input.substring(input.indexOf('\0') + 1);
		int toX = Integer.parseInt(input.substring(0, input.indexOf('\0')));
		input = input.substring(input.indexOf('\0') + 1);
		int toY = Integer.parseInt(input.substring(0, input.indexOf('\0')));
		input = input.substring(input.indexOf('\0') + 1);
		String team = input;
		return getNewBullet(fromX, fromY, toX, toY, team);
	}

	// This method returns the String that should be sent to each
	// Client to inform the Clients to make a new Bullet (on each
	// Client program, the getNewBullet(String input) method parses
	// this input to make a Bullet with the same information)
	public static String toString(int fromX, int fromY, int toX, int toY, String team)
	{
		return "" + fromX + '\0' + fromY + '\0' + toX + '\0' + toY + '\0' + team;
	}

	// This method increments the x and y position of the Bullet
	// so the Bullet appears to be moving.
	public void update()
	{
		posX += addX;
		posY += addY;
	}

	// This method is called inside TotalPanel's paintComponent()
	// so that each Bullet can be drawn on the screen.
	public void draw(Graphics g, int refX, int refY)
	{
		int posX = (int)(this.posX + ServerConstants.FRAME_SIZE / 2 - refX);
		int posY = (int)(this.posY + ServerConstants.FRAME_SIZE / 2 - refY);
		g.setColor(Color.RED);
		if (team.equals("blue"))
			g.setColor(Color.BLUE);
		g.fillOval(posX - ServerConstants.BULLET_SIZE / 2, posY - ServerConstants.BULLET_SIZE / 2, ServerConstants.BULLET_SIZE, ServerConstants.BULLET_SIZE);
	}
}