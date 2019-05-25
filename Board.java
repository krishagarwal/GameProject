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
	Image wall, tnt, redFlag, blueFlag, bush, heart, ufo, cactus, spaceship;
	int[][] cacti;
	
	// This constructor is used to instantiate a new Board object. A
	// 2D character array is stored to define the layour of the board.
	// Each character in the array represents a 50x50 region in the
	// map (of size 1350). In the array, 'o' represents open space
	// while 't' represents a tower, meaning players cannot move
	// through it. Also, 'f' represents a flag for capture the flag
	// 'h' represents a heart, and 'T' represents a shrapnel bomb.
	public Board()
	{
		wall = new ImageIcon("wall.png").getImage();
		tnt = new ImageIcon("tnt.png").getImage();
		redFlag = new ImageIcon("red_flag.png").getImage();
		blueFlag = new ImageIcon("blue_flag.png").getImage();
		bush = new ImageIcon("bush.png").getImage();
		heart = new ImageIcon("heart_board.png").getImage();
		ufo = new ImageIcon("ufo.png").getImage();
		cactus = new ImageIcon("cactus.png").getImage();
		spaceship = new ImageIcon("spaceship.png").getImage();
		resetBoard();

		cacti = new int[30][2];

		for (int i = 0; i < cacti.length; i++)
		{
			int num = (int)(Math.random() * 4);
			if (num == 0)
			{
				cacti[i][0] = (int)(Math.random() * 28);
				cacti[i][1] = -1 * (int)(Math.random() * 5 + 2);
			}
			else if (num == 1)
			{
				cacti[i][0] = -1 * (int)(Math.random() * 5 + 2);
				cacti[i][0] = (int)(Math.random() * 28);
			}
			else if (num == 2)
			{
				cacti[i][0] = 27 +  (int)(Math.random() * 5 + 2);
				cacti[i][0] = (int)(Math.random() * 28);
			}
			else
			{
				cacti[i][0] = (int)(Math.random() * 28);
				cacti[i][1] = 27 + (int)(Math.random() * 5 + 2);
			}
		}
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
				g.setColor(new Color(255, 215, 100));
				if (i + rowStart < 0 || j + colStart < 0 || i + rowStart >= ServerConstants.BOARD_FRAGMENTS || j + colStart >= ServerConstants.BOARD_FRAGMENTS)
				{
					g.fillRect(j * ServerConstants.FRAGMENT_SIZE + refX, i * ServerConstants.FRAGMENT_SIZE + refY, ServerConstants.FRAGMENT_SIZE, ServerConstants.FRAGMENT_SIZE);
					if (!(i + rowStart <= -2 || j + colStart <= -2 || i + rowStart >= total.length + 1 || j + colStart >= total.length + 1)
						&& (i + rowStart == -1 || j + colStart == -1 || i + rowStart == total.length || j + colStart == total.length))
						g.drawImage(wall, j * ServerConstants.FRAGMENT_SIZE + refX, i * ServerConstants.FRAGMENT_SIZE + refY, ServerConstants.FRAGMENT_SIZE,
							ServerConstants.FRAGMENT_SIZE, null);
					else if (inArray(i + rowStart, j + colStart, cacti))
						g.drawImage(cactus, j * ServerConstants.FRAGMENT_SIZE + refX, i * ServerConstants.FRAGMENT_SIZE + refY, 50, 50, null);
				}
				else
				{
					char c = total[i + rowStart][j + colStart];
					if (1 <= i + rowStart && i + rowStart <= 3 && 1 <= j + colStart && j + colStart <= ServerConstants.BOARD_FRAGMENTS - 2)
						g.setColor(new Color(165, 230, 255));
					else if (ServerConstants.BOARD_FRAGMENTS - 4 <= i + rowStart && i + rowStart <= ServerConstants.BOARD_FRAGMENTS - 2 && 1 <= j + colStart
						&& j + colStart <= ServerConstants.BOARD_FRAGMENTS - 2)
						g.setColor(new Color(255, 195, 195));
					g.fillRect(j * ServerConstants.FRAGMENT_SIZE + refX, i * ServerConstants.FRAGMENT_SIZE + refY, ServerConstants.FRAGMENT_SIZE, ServerConstants.FRAGMENT_SIZE);
					if (c == 'h' && Client.gameMode != ServerConstants.CAPTURE_THE_FLAG)
						g.drawImage(heart, j * ServerConstants.FRAGMENT_SIZE + refX, i * ServerConstants.FRAGMENT_SIZE + refY, ServerConstants.FRAGMENT_SIZE,
							ServerConstants.FRAGMENT_SIZE, null);
					else if (c == 't')
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

	// This method checks if there is a tower below the given
	// (x, y) coordinate in the board.
	public boolean isBelow(int x, int y)
	{
		return isBelow(x, y, "t");
	}

	// This method checks if there is a tower above the given
	// (x, y) coordinate in the board.
	public boolean isAbove(int x, int y)
	{
		return isAbove(x, y, "t");
	}

	// This method checks if there is a tower left to the given
	// (x, y) coordinate in the board.
	public boolean isLeft(int x, int y)
	{
		return isLeft(x, y, "t");
	}

	// This method checks if there is a tower right to the given
	// (x, y) coordinate in the board.
	public boolean isRight(int x, int y)
	{
		return isRight(x, y, "t");
	}

	// This method is used to return, given that there is the given
	// String (representing a board object) below the given (x, y)
	// coordinate, the position of that object in the board.
	public int[] whereBelow(int x, int y, String s)
	{
		if (x % ServerConstants.FRAGMENT_SIZE == ServerConstants.FRAGMENT_SIZE / 2)
			return new int[] {y / ServerConstants.FRAGMENT_SIZE + 1, x / ServerConstants.FRAGMENT_SIZE};
		else if (x % ServerConstants.FRAGMENT_SIZE < ServerConstants.FRAGMENT_SIZE / 2)
		{
			if (("" + total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE]).toLowerCase().equals(s))
				return new int[] {y / ServerConstants.FRAGMENT_SIZE + 1, x / ServerConstants.FRAGMENT_SIZE};
			return new int[] {y / ServerConstants.FRAGMENT_SIZE + 1, x / ServerConstants.FRAGMENT_SIZE - 1};
		}
		if (("" + total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE]).toLowerCase().equals(s))
			return new int[] {y / ServerConstants.FRAGMENT_SIZE + 1, x / ServerConstants.FRAGMENT_SIZE};
		return new int[] {y / ServerConstants.FRAGMENT_SIZE + 1, x / ServerConstants.FRAGMENT_SIZE + 1};
	}

	// This method is used to return, given that there is the given
	// String (representing a board object) above the given (x, y)
	// coordinate, the position of that object in the board.
	public int[] whereAbove(int x, int y, String s)
	{
		if (x % ServerConstants.FRAGMENT_SIZE == ServerConstants.FRAGMENT_SIZE / 2)
			return new int[] {y / ServerConstants.FRAGMENT_SIZE - 1, x / ServerConstants.FRAGMENT_SIZE};
		else if (x % ServerConstants.FRAGMENT_SIZE < ServerConstants.FRAGMENT_SIZE / 2)
		{
			if (("" + total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE]).toLowerCase().equals(s))
				return new int[] {y / ServerConstants.FRAGMENT_SIZE - 1, x / ServerConstants.FRAGMENT_SIZE};
			return new int[] {y / ServerConstants.FRAGMENT_SIZE - 1, x / ServerConstants.FRAGMENT_SIZE - 1};
		}
		if (("" + total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE]).toLowerCase().equals(s))
			return new int[] {y / ServerConstants.FRAGMENT_SIZE - 1, x / ServerConstants.FRAGMENT_SIZE};
		return new int[] {y / ServerConstants.FRAGMENT_SIZE - 1, x / ServerConstants.FRAGMENT_SIZE + 1};
	}

	// This method is used to return, given that there is the given
	// String (representing a board object) left to the given (x, y)
	// coordinate, the position of that object in the board.
	public int[] whereLeft(int x, int y, String s)
	{
		if (y % ServerConstants.FRAGMENT_SIZE == ServerConstants.FRAGMENT_SIZE / 2)
			return new int[] {y / ServerConstants.FRAGMENT_SIZE, x / ServerConstants.FRAGMENT_SIZE - 1};
		else if (y % ServerConstants.FRAGMENT_SIZE < ServerConstants.FRAGMENT_SIZE / 2)
		{
			if (("" + total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE - 1]).toLowerCase().equals(s))
				return new int[] {y / ServerConstants.FRAGMENT_SIZE - 1, x / ServerConstants.FRAGMENT_SIZE - 1};
			return new int[] {y / ServerConstants.FRAGMENT_SIZE, x / ServerConstants.FRAGMENT_SIZE - 1};
		}
		if (("" + total[y / ServerConstants.FRAGMENT_SIZE][x / ServerConstants.FRAGMENT_SIZE - 1]).toLowerCase().equals(s))
			return new int[] {y / ServerConstants.FRAGMENT_SIZE, x / ServerConstants.FRAGMENT_SIZE - 1};
		return new int [] {y / ServerConstants.FRAGMENT_SIZE + 1, x / ServerConstants.FRAGMENT_SIZE - 1};
	}
	
	// This method is used to return, given that there is the given
	// String (representing a board object) right to the given (x, y)
	// coordinate, the position of that object in the board.
	public int[] whereRight(int x, int y, String s)
	{
		if (y % ServerConstants.FRAGMENT_SIZE == ServerConstants.FRAGMENT_SIZE / 2)
			return new int[] {y / ServerConstants.FRAGMENT_SIZE, x / ServerConstants.FRAGMENT_SIZE + 1};
		else if (y % ServerConstants.FRAGMENT_SIZE < ServerConstants.FRAGMENT_SIZE / 2)
		{
			if (("" + total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE + 1]).toLowerCase().equals(s))
				return new int[] {y / ServerConstants.FRAGMENT_SIZE - 1, x / ServerConstants.FRAGMENT_SIZE + 1};
			return new int[] {y / ServerConstants.FRAGMENT_SIZE, x / ServerConstants.FRAGMENT_SIZE + 1};
		}
		if (("" + total[y / ServerConstants.FRAGMENT_SIZE][x / ServerConstants.FRAGMENT_SIZE + 1]).toLowerCase().equals(s))
			return new int[] {y / ServerConstants.FRAGMENT_SIZE, x / ServerConstants.FRAGMENT_SIZE + 1};
		return new int[] {y / ServerConstants.FRAGMENT_SIZE + 1, x / ServerConstants.FRAGMENT_SIZE + 1};
	}

	// This method is used to check if there is a tower barrier below
	// the given (x, y) coordinate in the board. It is used in the
	// Server class to handle movement logic.
	public boolean isBelow(int x, int y, String s)
	{
		if (y % ServerConstants.FRAGMENT_SIZE != ServerConstants.FRAGMENT_SIZE / 2)
			return false;
		else if (x % ServerConstants.FRAGMENT_SIZE == ServerConstants.FRAGMENT_SIZE / 2)
			return ("" + total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE]).toLowerCase().equals(s);
		else if (x % ServerConstants.FRAGMENT_SIZE < ServerConstants.FRAGMENT_SIZE / 2)
			return ("" + total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE]
				+ total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE - 1]).toLowerCase().contains(s);
		return ("" + total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE]
			+ total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE + 1]).toLowerCase().contains(s);
	}

	// This method is used to check if there is a tower barrier above
	// the given (x, y) coordinate in the board. It is used in the
	// Server class to handle movement logic.
	public boolean isAbove(int x, int y, String s)
	{
		if (y % ServerConstants.FRAGMENT_SIZE != ServerConstants.FRAGMENT_SIZE / 2)
			return false;
		if (x % ServerConstants.FRAGMENT_SIZE == ServerConstants.FRAGMENT_SIZE / 2)
			return ("" + total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE]).toLowerCase().equals(s);
		else if (x % ServerConstants.FRAGMENT_SIZE < ServerConstants.FRAGMENT_SIZE / 2)
			return ("" + total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE]
				+ total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE - 1]).toLowerCase().contains(s);
		return ("" + total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE]
			+ total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE + 1]).toLowerCase().contains(s);
	}

	// This method is used to check if there is a tower barrier at the
	// left ofthe given (x, y) coordinate in the board. It is used in
	// the Server class to handle movement logic.
	public boolean isLeft(int x, int y, String s)
	{
		if (x % ServerConstants.FRAGMENT_SIZE != ServerConstants.FRAGMENT_SIZE / 2)
			return false;
		if (y % ServerConstants.FRAGMENT_SIZE == ServerConstants.FRAGMENT_SIZE / 2)
			return ("" + total[y / ServerConstants.FRAGMENT_SIZE][x / ServerConstants.FRAGMENT_SIZE - 1]).toLowerCase().equals(s);
		else if (y % ServerConstants.FRAGMENT_SIZE < ServerConstants.FRAGMENT_SIZE / 2)
			return ("" + total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE - 1]
				+ total[y / ServerConstants.FRAGMENT_SIZE][x / ServerConstants.FRAGMENT_SIZE - 1]).toLowerCase().contains(s);
		return ("" + total[y / ServerConstants.FRAGMENT_SIZE][x / ServerConstants.FRAGMENT_SIZE - 1]	
			+ total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE - 1]).toLowerCase().contains(s);
	}
	
	// This method is used to check if there is a tower barrier at the
	// right ofthe given (x, y) coordinate in the board. It is used in
	// the Server class to handle movement logic.
	public boolean isRight(int x, int y, String s)
	{
		if (x % ServerConstants.FRAGMENT_SIZE != ServerConstants.FRAGMENT_SIZE / 2)
			return false;
		if (y % ServerConstants.FRAGMENT_SIZE == ServerConstants.FRAGMENT_SIZE / 2)
			return ("" + total[y / ServerConstants.FRAGMENT_SIZE][x / ServerConstants.FRAGMENT_SIZE + 1]).toLowerCase().equals(s);
		else if (y % ServerConstants.FRAGMENT_SIZE < ServerConstants.FRAGMENT_SIZE / 2)
			return ("" + total[y / ServerConstants.FRAGMENT_SIZE - 1][x / ServerConstants.FRAGMENT_SIZE + 1]
				+ total[y / ServerConstants.FRAGMENT_SIZE][x / ServerConstants.FRAGMENT_SIZE + 1]).toLowerCase().contains(s);
		return ("" + total[y / ServerConstants.FRAGMENT_SIZE][x / ServerConstants.FRAGMENT_SIZE + 1]
			+ total[y / ServerConstants.FRAGMENT_SIZE + 1][x / ServerConstants.FRAGMENT_SIZE + 1]).toLowerCase().contains(s);
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
			{'t', 'o', 'o', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 'o', 'o', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 't', 't', 'o', 'o', 'o', 'h', 'o', 'o', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't'},
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
			{'t', 'o', 'o', 'o', 'o', 'o', 't', 't', 't', 't', 'o',	'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't', 't', 'o', 'o', 't', 't', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'f', 'o', 't'},
			{'t', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 't'},
			{'t', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't', 't'}
		};
	}

	// This method scrambles the board by randomly setting where the
	// shrapnel bombs, hearts, and flags are located.
	public void scrambleBoard()
	{
		int redFlag = (int)(Math.random() * 25 + 1);
		for (int i = 1; i < total.length - 1; i++)
		{
			for (int j = 1; j < total[i].length - 1; j++)
			{
				if (total[i][j] == 't' && Math.random() >= 0.85)
					total[i][j] = 'T';
				else if (total[i][j] == 'o' && Math.random() >= 0.98 && i > 3 && i < total.length - 4)
					total[i][j] = 'h';
				else if (total[i][j] == 'f')
					total[i][j] = 'o';
			}
		}
		total[2][(int)(Math.random() * 25 + 1)] = 'f';
		total[24][(int)(Math.random() * 25 + 1)] = 'f';
	}

	// This method sets the board given the String input
	// by iterating over the input and setting each character.
	public void setBoard(String input)
	{
		for (int i = 0; i < total.length; i++)
		{
			for (int j = 0; j < total[i].length; j++)
				total[i][j] = input.charAt(i * total[i].length + j);
		}
	}

	// This method returns a String representation of the
	// board so it can be parsed in setBoard().
	public String toString()
	{
		String ret = "";
		for (int i = 0; i < total.length; i++)
		{
			for (int j = 0; j < total[i].length; j++)
				ret += total[i][j];
		}
		return ret;
	}

	public boolean inArray(int x, int y, int[][] array)
	{
		for (int i = 0; i < array.length; i++)
		{
			if (array[i][0] == x && array[i][1] == y)
				return true;
		}
		return false;
	}
}