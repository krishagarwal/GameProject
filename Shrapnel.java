/*
Krish Agarwal
5.12.19
Shrapnel.java
*/

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

// This class is used to store information on each shrapnel fired
// during when a TNT box is blown up during game play. It behaves like
// a Bullet object but it more random and is also drawn differently.
public class Shrapnel extends Bullet
{
	double turnAngle, currAngle;
	Image shrapnel;

	// This constructor instantiates a new Shrapnel object given the
	// point of origin (a TNT box), by randomly deciding the angle at
	// which to travel and with the angle the amount to add to the x
	// and y coordinates to move.
	public Shrapnel(double originX, double originY)
	{
		super(originX, originY, 0, 0, "none");
		currAngle = turnAngle = Math.random() * Math.PI / 12 + Math.PI / 12;
		double angle = Math.random() * Math.PI * 2;
		addY = Math.sin(angle) * 6;
		addX = Math.cos(angle) * 6;
		shrapnel = new ImageIcon("shrapnel.png").getImage();
	}

	// This method returns a String representing this Shrapnel object
	// so that the Server can send this String and this String can
	// be parsed with the Client program using the getNewShrapnel method.
	public String toString()
	{
		return "" + posX + '\0' + posY + '\0' + addX + '\0' + addY;
	}

	// This method takes a String input and parses it into a new Shrapnel
	// object. The input String is determined by the toString() method and
	// is used at the Client level for information received from the Server.
	public static Shrapnel getNewShrapnel(String input)
	{
		double fromX = Double.parseDouble(input.substring(0, input.indexOf('\0')));
		input = input.substring(input.indexOf('\0') + 1);
		double fromY = Double.parseDouble(input.substring(0, input.indexOf('\0')));
		input = input.substring(input.indexOf('\0') + 1);
		double addX = Double.parseDouble(input.substring(0, input.indexOf('\0')));
		input = input.substring(input.indexOf('\0') + 1);
		double addY = Double.parseDouble(input);
		Shrapnel ret = new Shrapnel(fromX, fromY);
		ret.addX = addX;
		ret.addY = addY;
		return ret;
	}

	// This method draws the Shrapnel to the game panel by drawing the 
	// shrapnel image and also rotating it to give the shrapnel the appearance
	// of rotation.
	public void draw(Graphics g, int refX, int refY)
	{
		currAngle += turnAngle;
		int posX = (int)(this.posX + ServerConstants.FRAME_SIZE / 2 - refX);
		int posY = (int)(this.posY + ServerConstants.FRAME_SIZE / 2 - refY);
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform old = g2d.getTransform();
		g2d.rotate(currAngle, posX, posY);
		g2d.drawImage(shrapnel, posX - ServerConstants.BULLET_SIZE / 2, posY - ServerConstants.BULLET_SIZE / 2, ServerConstants.BULLET_SIZE, ServerConstants.BULLET_SIZE, null);
		g2d.setTransform(old);
	}
}