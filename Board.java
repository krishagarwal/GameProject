/*
Krish Agarwal
5.12.19
Board.java
*/

import java.awt.*;
import javax.swing.*;

// This class stores the layout of the map that is displayed during
// game play. It stores this information in a large 2D char array.
public class Board
{
	char[][] total;
	Image wall, tnt, redFlag, blueFlag, bush;
	
	// This constructor is used to instantiate a new Board object. A
	// 2D character array is stored to define the layour of the board.
	// Each character in the array represents a 50x50 region in the
	// map (of size 1350). In the array, 'o' represents open space
	// while 't' represents a tower, meaning players cannot move
	// through it. Also, 'f' represents a flag for capture the flag.
	public Board()
	{
		wall = new ImageIcon("wall.png").getImage();
		tnt = new ImageIcon("tnt.png").getImage();
		redFlag = new ImageIcon("red_flag.png").getImage();
		blueFlag = new ImageIcon("blue_flag.png").getImage();
		bush = new ImageIcon("bush.png").getImage();
		resetBoard();
	}
	
	// This method handles drawing the board during the game. It takes
	// the starting and ending rows/columns so it knows the reference
	// frame to draw, and draws that part of the board. This method
	// is called in TotalPanel's paintComponent.
	public void drawBoard(Graphics g, int rowStart, int rowEnd, int colStart, int colEnd, int refX, int refY)
	{
		for (int i = 0; i < rowEnd - rowStart; i++)
		{
			for (int j = 0; j < colEnd - colStart; j++)
			{
				g.setColor(new Color(40, 130, 75));
				if (i + rowStart < 0 || j + colStart < 0 || i + rowStart >= ServerConstants.BOARD_FRAGMENTS || j + colStart >= ServerConstants.BOARD_FRAGMENTS)
				{
					g.fillRect(j * ServerConstants.FRAGMENT_SIZE + refX, i * ServerConstants.FRAGMENT_SIZE + refY, ServerConstants.FRAGMENT_SIZE, ServerConstants.FRAGMENT_SIZE);
					g.drawImage(bush, j * ServerConstants.FRAGMENT_SIZE + refX, i * ServerConstants.FRAGMENT_SIZE + refY,
						ServerConstants.FRAGMENT_SIZE, ServerConstants.FRAGMENT_SIZE, null);
				}
				else
				{
					char c = total[i + rowStart][j + colStart];
					g.setColor(new Color(255, 215, 100));
					if (1 <= i + rowStart && i + rowStart <= 3 && 1 <= j + colStart && j + colStart <= ServerConstants.BOARD_FRAGMENTS - 2)
						g.setColor(new Color(165, 230, 255));
					else if (ServerConstants.BOARD_FRAGMENTS - 4 <= i + rowStart && i + rowStart <= ServerConstants.BOARD_FRAGMENTS - 2 && 1 <= j + colStart
						&& j + colStart <= ServerConstants.BOARD_FRAGMENTS - 2)
						g.setColor(new Color(255, 195, 195));
					g.fillRect(j * ServerConstants.FRAGMENT_SIZE + refX, i * ServerConstants.FRAGMENT_SIZE + refY, ServerConstants.FRAGMENT_SIZE, ServerConstants.FRAGMENT_SIZE);
					if (c == 't')
						g.drawImage(wall, j * ServerConstants.FRAGMENT_SIZE + refX, i * ServerConstants.FRAGMENT_SIZE + refY, ServerConstants.FRAGMENT_SIZE,
							ServerConstants.FRAGMENT_SIZE, null);
					else if (c == 'T')
						g.drawImage(tnt, j * ServerConstants.FRAGMENT_SIZE + refX, i * ServerConstants.FRAGMENT_SIZE + refY, ServerConstants.FRAGMENT_SIZE,
							ServerConstants.FRAGMENT_SIZE, null);
					else if (Client.gameMode == ServerConstants.CAPTURE_THE_FLAG && !Client.blueFlagTaken && c == 'f' && i + rowStart == 2)
						g.drawImage(blueFlag, j * ServerConstants.FRAGMENT_SIZE + refX, i * ServerConstants.FRAGMENT_SIZE + refY, ServerConstants.FRAGMENT_SIZE,
							ServerConstants.FRAGMENT_SIZE, null);
					else if (Client.gameMode == ServerConstants.CAPTURE_THE_FLAG && !Client.redFlagTaken && c == 'f' && i + rowStart == total.length - 3)
						g.drawImage(redFlag, j * ServerConstants.FRAGMENT_SIZE + refX, i * ServerConstants.FRAGMENT_SIZE + refY, ServerConstants.FRAGMENT_SIZE,
							ServerConstants.FRAGMENT_SIZE, null);
				}
			}
		}
	}

	// This method is used to check if there is a tower barrier below
	// the given (x, y) coordinate in the board. It is used in the
	// Server class to handle movement logic.
	public boolean isBelow(int x, int y)
	{
		if (y % ServerConstants.FRAGMENT_SIZE != ServerConstants.FRAGMENT_SIZE / 2)
			return false;
		else if (x % ServerConstants.FRAGMENT_SIZE == ServerConstants.FRAGMENT_SIZE / 2)
			return ("" + total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE]).toLowerCase().equals("t");
		else if (x % ServerConstants.FRAGMENT_SIZE < ServerConstants.FRAGMENT_SIZE / 2)
			return ("" + total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE]
				+ total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE - 1]).toLowerCase().contains("t");
		return ("" + total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE]
			+ total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE + 1]).toLowerCase().contains("t");
	}

	// This method is used to check if there is a tower barrier above
	// the given (x, y) coordinate in the board. It is used in the
	// Server class to handle movement logic.
	public boolean isAbove(int x, int y)
	{
		if (y % ServerConstants.FRAGMENT_SIZE != ServerConstants.FRAGMENT_SIZE / 2)
			return false;
		if (x % ServerConstants.FRAGMENT_SIZE == ServerConstants.FRAGMENT_SIZE / 2)
			return ("" + total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE]).toLowerCase().equals("t");
		else if (x % ServerConstants.FRAGMENT_SIZE < ServerConstants.FRAGMENT_SIZE / 2)
			return ("" + total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE]
				+ total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE - 1]).toLowerCase().contains("t");
		return ("" + total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE]
			+ total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE + 1]).toLowerCase().contains("t");
	}

	// This method is used to check if there is a tower barrier at the
	// left ofthe given (x, y) coordinate in the board. It is used in
	// the Server class to handle movement logic.
	public boolean isLeft(int x, int y)
	{
		if (x % ServerConstants.FRAGMENT_SIZE != ServerConstants.FRAGMENT_SIZE / 2)
			return false;
		if (y % ServerConstants.FRAGMENT_SIZE == ServerConstants.FRAGMENT_SIZE / 2)
			return ("" + total[y / ServerConstants.FRAGMENT_SIZE][x / ServerConstants.FRAGMENT_SIZE - 1]).toLowerCase().equals("t");
		else if (y % ServerConstants.FRAGMENT_SIZE < ServerConstants.FRAGMENT_SIZE / 2)
			return ("" + total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE - 1]
				+ total[y / ServerConstants.FRAGMENT_SIZE][x / ServerConstants.FRAGMENT_SIZE - 1]).toLowerCase().contains("t");
		return ("" + total[y / ServerConstants.FRAGMENT_SIZE][x / ServerConstants.FRAGMENT_SIZE - 1]	
			+ total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE - 1]).toLowerCase().contains("t");
	}
	
	// This method is used to check if there is a tower barrier at the
	// right ofthe given (x, y) coordinate in the board. It is used in
	// the Server class to handle movement logic.
	public boolean isRight(int x, int y)
	{
		if (x % ServerConstants.FRAGMENT_SIZE !=	ServerConstants.FRAGMENT_SIZE / 2)
			return false;
		if (y % ServerConstants.FRAGMENT_SIZE == ServerConstants.FRAGMENT_SIZE / 2)
			return ("" + total[y / ServerConstants.FRAGMENT_SIZE][x / ServerConstants.FRAGMENT_SIZE + 1]).toLowerCase().equals("t");
		else if (y % ServerConstants.FRAGMENT_SIZE < ServerConstants.FRAGMENT_SIZE / 2)
			return ("" + total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE + 1]
				+ total[y / ServerConstants.FRAGMENT_SIZE][x / ServerConstants.FRAGMENT_SIZE + 1]).toLowerCase().contains("t");
		return ("" + total[y / ServerConstants.FRAGMENT_SIZE][x / ServerConstants.FRAGMENT_SIZE + 1]
			+ total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE + 1]).toLowerCase().contains("t");
	}

	// This method resets the board by resetting the whole character array
	// that deterines the layout of the board.
	public void resetBoard()
	{
		total = new char[][] {
			{'t', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o',	'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 'o', 'f', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 'o', 'o', 'o', 'o', 't', 't', 'T', 't', 'o', 'o', 't', 'T', 't', 't', 'o', 'o', 't', 'T', 'T', 't', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 'o', 'o', 'o', 'o', 't', 'T', 't', 't', 'o', 'o', 'T', 't', 'T', 't', 'o', 'o', 't', 'T', 't', 't', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 'T', 't', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't', 'T', 't', 't', 'o', 'o', 't', 't', 'T', 't', 'o', 'o', 't', 'T', 't', 't'},
			{'t', 't', 't', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't', 't', 't', 'T', 'o', 'o', 't', 'T', 't', 't', 'o', 'o', 't', 't', 'T', 't'},
			{'t', 't', 'T', 't', 't', 't', 'o', 'o', 'T', 't', 't', 'T', 't', 'o', 'o', 'o', 'o', 't', 'T', 'T', 't', 'o', 'o', 'T', 'T', 't', 't'},
			{'t', 't', 't', 't', 't', 't', 'o', 'o', 't', 'T', 't', 't', 't', 'o', 'o', 'o', 'o', 't', 'T', 't', 't', 'o', 'o', 't', 't', 'T', 't'},
			{'t', 'T', 't', 'T', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'T', 't', 't', 't'},
			{'t', 't', 't', 't', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't', 'T', 't', 't'},
			{'t', 't', 'T', 't', 't', 't', 'o', 'o', 't', 't', 't', 'T', 't', 'o', 'o', 'o', 'o', 't', 't', 'T', 'T', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 't', 't', 't', 'T', 't', 'o', 'o', 'T', 't', 'T', 't', 't', 'o', 'o', 'o', 'o', 'T', 'T', 't', 't', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't', 'T', 't', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't', 'T', 't', 't'},
			{'t', 'T', 't', 'T', 't', 'T', 't', 't', 't', 'T', 't', 't', 'T', 'o', 'o', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 'T', 't', 't'},
			{'t', 't', 'T', 't', 't', 't', 't', 'T', 't', 't', 't', 'T', 't', 'o', 'o', 'o', 'o', 't', 't', 'T', 't', 'o', 'o', 't', 'T', 't', 't'},
			{'t', 't', 't', 'T', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 'T', 't', 't', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 't', 'T', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 'T', 'T', 't', 'o', 'o', 't', 'T', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 'T', 't', 'T', 'T', 'o',	'o', 't', 'T', 'T', 't', 'o', 'o', 'T', 'T', 't', 'T', 'o', 'o', 'T', 't', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 't', 't', 'T', 't', 'o', 'o', 't', 'T', 't', 't', 'o', 'o', 't', 't', 'T', 'T', 'o', 'o', 't', 'T', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'f', 'o', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't'}
		};
	}
}