import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class WaitPanel extends JPanel
{
	String text = "";

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		setBackground(Color.ORANGE);
		// g.setColor(Color.GRAY);
		// g.drawRect(200, 250, 200, 100);
		g.setColor(Color.WHITE);
		g.getFontMetrics().getStringBounds(Client.playerName, g).getWidth();
		g.setFont(new Font("Serif", Font.PLAIN, 50));
		g.drawString(text, (int)(300 - g.getFontMetrics().getStringBounds(text, g).getWidth() / 2), 325);
	}

	public void setText(String message)
	{
		text = message;
		repaint();
	}
}