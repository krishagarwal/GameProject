import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.Socket;

import javax.swing.*;

public class ServerInputPanel extends JPanel implements KeyListener
{
	public ServerInputPanel()
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
		g.setFont(new Font("Serif", Font.PLAIN, 20));
		g.drawString("Enter the IP of the Server (continue if on the same machine):",
			(int)(300 - g.getFontMetrics().getStringBounds("Enter the IP of the Server (continue if on the same machine):", g).getWidth() / 2), 240);
		g.drawString(Client.ip,
			(int)(300 - g.getFontMetrics().getStringBounds(Client.ip, g).getWidth() / 2), 325);
	}

	public void keyPressed(KeyEvent evt)
	{
		if (evt.getKeyCode() == KeyEvent.VK_ENTER)
		{
			Client.socket = null;
			try
			{
				Client.ip += ServerConstants.getLocalHost(Client.frame, "You do not seem to be connected to the internet. Reconnect and try again.");
				Client.socket = new Socket(Client.ip, ServerConstants.PORT_NUMBER);
				Thread.sleep(1000);
				Thread server = new Thread(new ServerThread());
				server.start();
			}
			catch (IOException ioe)
			{
				System.err.println("Fatal connection error");
				ioe.printStackTrace();
			}
			catch (InterruptedException ie)
			{
				System.err.println("Fatal connection error");
				ie.printStackTrace();
			}

			Client.cl.show(Client.parentPanel, Client.WAIT);
			Client.frame.addWindowListener(new WindowListener()
			{
				public void windowOpened(WindowEvent e) {}
				public void windowIconified(WindowEvent e) {}
				public void windowDeiconified(WindowEvent e) {}
				public void windowDeactivated(WindowEvent e) {}
				public void windowClosed(WindowEvent e) {}
				public void windowActivated(WindowEvent e) {}

				public void windowClosing(WindowEvent e)
				{
					Client.send(ServerConstants.DELETE_PLAYER + Client.playerName);
				}
			});
		}
	}

	public void keyReleased(KeyEvent evt) {}
	public void keyTyped(KeyEvent evt)
	{
		Client.ip += evt.getKeyChar();
		repaint();
	}
}