import java.awt.*;
import java.awt.event.MouseListener;
import javax.swing.*;

public class StartPanel extends JPanel implements MouseListener
{
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setBackground(Color.ORANGE);
		g.setColor(Color.GRAY);
		g.drawRect(200, 250, 200, 100);
	}
}