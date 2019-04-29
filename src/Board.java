import java.awt.*;
import javax.swing.*;

class Board
{
	char[][] total;
	Image wall;
	
	public Board()
	{
		wall = new ImageIcon("../images/wall.png").getImage();
		total = new char[][] {
			{'t', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 'o', 'o', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 'o', 'o', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 't', 't', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't'},
			{'t', 't', 't', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't'},
			{'t', 't', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 't', 'o', 'o', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't'},
			{'t', 't', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 't', 'o', 'o', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't'},
			{'t', 't', 't', 't', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't', 't', 't', 't'},
			{'t', 't', 't', 't', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't', 't', 't', 't'},
			{'t', 't', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 't', 'o', 'o', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 't', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 't', 'o', 'o', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't', 't', 't', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't', 't', 't', 't'},
			{'t', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 'o', 'o', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't'},
			{'t', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 'o', 'o', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't'},
			{'t', 't', 't', 't', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 't', 't', 't', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't'}
		};
	}
	
	public void drawBoard(Graphics g, int rowStart, int rowEnd, int colStart, int colEnd, int refX, int refY)
	{
		for (int i = 0; i < rowEnd - rowStart; i++)
		{
			for (int j = 0; j < colEnd - colStart; j++)
			{
				g.setColor(new Color(40, 130, 75));
				if (i + rowStart < 0 || j + colStart < 0 || i + rowStart >= ServerConstants.BOARD_FRAGMENTS || j + colStart >= ServerConstants.BOARD_FRAGMENTS)
					g.fillRect(j * ServerConstants.FRAGMENT_SIZE + refX, i * ServerConstants.FRAGMENT_SIZE + refY,
						ServerConstants.FRAGMENT_SIZE, ServerConstants.FRAGMENT_SIZE);
				else
				{
					char c = total[i + rowStart][j + colStart];
					g.setColor(new Color(255, 215, 100));
					if (1 <= i + rowStart && i + rowStart <= 3 && 1 <= j + colStart && j + colStart <= ServerConstants.BOARD_FRAGMENTS - 2)
						g.setColor(new Color(165, 230, 255));
					else if (ServerConstants.BOARD_FRAGMENTS - 4 <= i + rowStart && i + rowStart <= ServerConstants.BOARD_FRAGMENTS - 2 
						&& 1 <= j + colStart && j + colStart <= ServerConstants.BOARD_FRAGMENTS - 2)
						g.setColor(new Color(255, 195, 195));
					g.fillRect(j * ServerConstants.FRAGMENT_SIZE + refX, i * ServerConstants.FRAGMENT_SIZE + refY,
						ServerConstants.FRAGMENT_SIZE, ServerConstants.FRAGMENT_SIZE);
					if (c == 't')
						g.drawImage(wall, j * ServerConstants.FRAGMENT_SIZE + refX, i * ServerConstants.FRAGMENT_SIZE + refY,
							ServerConstants.FRAGMENT_SIZE, ServerConstants.FRAGMENT_SIZE, null);
				}
			}
		}
	}

	public boolean isBelow(int x, int y)
	{
		if (y % ServerConstants.FRAGMENT_SIZE != ServerConstants.FRAGMENT_SIZE / 2)
			return false;
		else if (x % ServerConstants.FRAGMENT_SIZE == ServerConstants.FRAGMENT_SIZE / 2)
			return total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE] == 't';
		else if (x % ServerConstants.FRAGMENT_SIZE < ServerConstants.FRAGMENT_SIZE / 2)
			return ("" + total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE] + 
				total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE - 1]).contains("t");
		return ("" + total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE] + 
			total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE + 1]).contains("t");
	}

	public boolean isAbove(int x, int y)
	{
		if (y % ServerConstants.FRAGMENT_SIZE != ServerConstants.FRAGMENT_SIZE / 2)
			return false;
		if (x % ServerConstants.FRAGMENT_SIZE == ServerConstants.FRAGMENT_SIZE / 2)
			return total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE] == 't';
		else if (x % ServerConstants.FRAGMENT_SIZE < ServerConstants.FRAGMENT_SIZE / 2)
			return ("" + total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE] + 
				total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE - 1]).contains("t");
		return ("" + total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE] + 
			total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE + 1]).contains("t");
	}

	public boolean isLeft(int x, int y)
	{
		if (x % ServerConstants.FRAGMENT_SIZE != ServerConstants.FRAGMENT_SIZE / 2)
			return false;
		if (y % ServerConstants.FRAGMENT_SIZE == ServerConstants.FRAGMENT_SIZE / 2)
			return total[y / ServerConstants.FRAGMENT_SIZE][x / ServerConstants.FRAGMENT_SIZE - 1] == 't';
		else if (y % ServerConstants.FRAGMENT_SIZE < ServerConstants.FRAGMENT_SIZE / 2)
			return ("" + total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE - 1] + 
				total[y / ServerConstants.FRAGMENT_SIZE][x / ServerConstants.FRAGMENT_SIZE - 1]).contains("t");
		return ("" + total[y / ServerConstants.FRAGMENT_SIZE][x / ServerConstants.FRAGMENT_SIZE - 1] + 
			total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE - 1]).contains("t");
	}

	public boolean isRight(int x, int y)
	{
		if (x % ServerConstants.FRAGMENT_SIZE != ServerConstants.FRAGMENT_SIZE / 2)
			return false;
		if (y % ServerConstants.FRAGMENT_SIZE == ServerConstants.FRAGMENT_SIZE / 2)
			return total[y / ServerConstants.FRAGMENT_SIZE][x / ServerConstants.FRAGMENT_SIZE + 1] == 't';
		else if (y % ServerConstants.FRAGMENT_SIZE < ServerConstants.FRAGMENT_SIZE / 2)
			return ("" + total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE + 1] + 
				total[y / ServerConstants.FRAGMENT_SIZE][x / ServerConstants.FRAGMENT_SIZE + 1]).contains("t");
		return ("" + total[y / ServerConstants.FRAGMENT_SIZE][x / ServerConstants.FRAGMENT_SIZE + 1] + 
			total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE + 1]).contains("t");
	}
}