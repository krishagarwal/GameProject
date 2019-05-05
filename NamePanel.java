import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;

import javax.swing.*;

public class NamePanel extends JPanel implements KeyListener
{
	public NamePanel()
	{
		setFocusable(true);
		addKeyListener(this);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		setBackground(Color.ORANGE);
		g.setColor(Color.GRAY);
		g.drawRect(200, 250, 200, 100);
		g.setColor(Color.WHITE);
		g.getFontMetrics().getStringBounds(Client.playerName, g).getWidth();
		g.setFont(new Font("Serif", Font.PLAIN, 20));
		g.drawString("Enter your name:",
			(int)(300 - g.getFontMetrics().getStringBounds("Enter your name:", g).getWidth() / 2), 240);
		g.drawString(Client.playerName,
			(int)(300 - g.getFontMetrics().getStringBounds(Client.playerName, g).getWidth() / 2), 325);
	}

	public void keyPressed(KeyEvent evt)
	{
		if (evt.getKeyCode() == KeyEvent.VK_ENTER)
		{
			Client.playerName += ServerConstants.NAME_SEPERATOR +
				ServerConstants.getLocalHost(Client.frame, "Not connected to internet") + System.currentTimeMillis();
			Client.cl.show(Client.parentPanel, Client.START);
			Client.startPanel.requestFocus();
		}
	}

	public void keyReleased(KeyEvent evt) {}
	public void keyTyped(KeyEvent evt)
	{
		Client.playerName += evt.getKeyChar();
		repaint();
	}
}