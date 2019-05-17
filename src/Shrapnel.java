/*
Krish Agarwal
5.12.19
Bullet.java
*/

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class Shrapnel extends Bullet
{
	double turnAngle, currAngle;
	Image shrapnel;

	public Shrapnel(double originX, double originY)
	{
		super(originX, originY, 0, 0, "none");
		currAngle = turnAngle = Math.random() * Math.PI / 12 + Math.PI / 12;
		double angle = Math.random() * Math.PI * 2;
		addY = Math.sin(angle) * 6;
		addX = Math.cos(angle) * 6;
		shrapnel = new ImageIcon("../images/shrapnel.png").getImage();
	}

	public String toString()
	{
		return "" + posX + '\0' + posY + '\0' + addX + '\0' + addY;
	}

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

	public void draw(Graphics g, int refX, int refY)
	{
		currAngle += turnAngle;
		int posX = (int)(this.posX + ServerConstants.FRAME_SIZE / 2 - refX);
		int posY = (int)(this.posY + ServerConstants.FRAME_SIZE / 2 - refY);
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform old = g2d.getTransform();
		g2d.rotate(currAngle, posX, posY);
		// g.setColor(Color.BLACK);
		g2d.drawImage(shrapnel, posX - ServerConstants.BULLET_SIZE / 2, posY - ServerConstants.BULLET_SIZE / 2, ServerConstants.BULLET_SIZE, ServerConstants.BULLET_SIZE, null);
		// g.fillOval(posX - ServerConstants.BULLET_SIZE / 2, posY - ServerConstants.BULLET_SIZE / 2, ServerConstants.BULLET_SIZE, ServerConstants.BULLET_SIZE);
		g2d.setTransform(old);
	}
}