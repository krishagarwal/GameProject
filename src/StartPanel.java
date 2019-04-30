import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class StartPanel extends JPanel implements MouseListener
{
	public StartPanel()
	{
		setFocusable(true);
		addMouseListener(this);
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
		g.drawString("Play",
			(int)(300 - g.getFontMetrics().getStringBounds("Play", g).getWidth() / 2), 325);
	}

	public void mousePressed(MouseEvent e)
	{
		if (e.getX() >= 200 && e.getX() <= 400 && e.getY() >= 250 && e.getY() <= 350)
		{
			Client.cl.show(Client.parentPanel, Client.SERVER_IN);
			Client.serverInPanel.requestFocus();
		}
	}
	
	public void mouseReleased(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}