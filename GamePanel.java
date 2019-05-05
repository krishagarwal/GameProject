import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ConcurrentHashMap;

public class GamePanel extends JPanel implements MouseListener, KeyListener
{
	ConcurrentHashMap<String, Player> players;
	ConcurrentHashMap<String, Bullet> bullets;
	private Board gameBoard;
	public GamePanel(ConcurrentHashMap<String, Player> inPlayers, ConcurrentHashMap<String, Bullet> inBullets)
	{
		gameBoard = new Board();
		players = inPlayers;
		bullets = inBullets;
		addMouseListener(this);
		addKeyListener(this);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Player me = players.get(Client.playerName);
		if (me == null)
			return;
		int refX = (me.posX - ServerConstants.FRAME_SIZE / 2) % ServerConstants.FRAGMENT_SIZE * -1 - ServerConstants.FRAGMENT_SIZE;
		int refY = (me.posY - ServerConstants.FRAME_SIZE / 2) % ServerConstants.FRAGMENT_SIZE * -1 - ServerConstants.FRAGMENT_SIZE;
		gameBoard.drawBoard(g, (me.posY - ServerConstants.FRAME_SIZE / 2) / ServerConstants.FRAGMENT_SIZE - 1, 
			(me.posY - ServerConstants.FRAME_SIZE / 2) / ServerConstants.FRAGMENT_SIZE + 16, 
			(me.posX - ServerConstants.FRAME_SIZE / 2) / ServerConstants.FRAGMENT_SIZE - 1, 
			(me.posX - ServerConstants.FRAME_SIZE / 2) / ServerConstants.FRAGMENT_SIZE + 16, refX, refY);
		for (Player curr : players.values())
			curr.draw(g, me.posX, me.posY);
		for (Bullet bullet : bullets.values())
			bullet.draw(g, me.posX, me.posY);
	}

	public void keyTyped(KeyEvent evt) {}
	public void keyReleased(KeyEvent evt) {}

	public void keyPressed(KeyEvent evt)
	{
		int e = evt.getKeyCode();
		if (e == KeyEvent.VK_UP)
			Client.send(ServerConstants.MOVE_PLAYER_UP + Client.playerName);
		else if (e == KeyEvent.VK_DOWN)
			Client.send(ServerConstants.MOVE_PLAYER_DOWN + Client.playerName);
		else if (e == KeyEvent.VK_LEFT)
			Client.send(ServerConstants.MOVE_PLAYER_LEFT + Client.playerName);
		else if (e == KeyEvent.VK_RIGHT)
			Client.send(ServerConstants.MOVE_PLAYER_RIGHT + Client.playerName);
	}

	
	public void mousePressed(MouseEvent e)
	{
		requestFocus();
		Player player = players.get(Client.playerName);
		Client.send(ServerConstants.CREATE_BULLET + (Client.playerName + Client.bulletCount) + '\0' + 
			Bullet.toString(player.posX, player.posY, e.getX() - ServerConstants.FRAME_SIZE / 2 + player.posX,
				e.getY() - ServerConstants.FRAME_SIZE / 2 + player.posY, player.team));
		Client.bulletCount++;
	}

	public void mouseClicked(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
}