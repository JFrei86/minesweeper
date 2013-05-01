package minesweeper.data;

/**
 * Data Representation of the board
 * 
 * @author Jesse Freitas 2013
 * 
 */
public class Board
{
	public final static int Y_HARD = 16;
	public final static int X_HARD = 30;
	public final static int MINES_HARD = 99;
	public final static int Y_MED = 16;
	public final static int X_MED = 16;
	public final static int MINES_MED = 40;
	public final static int Y_EASY = 9;
	public final static int X_EASY = 9;
	public final static int MINES_EASY = 10;

	private Block[][] board;
	private int mines;

	/**
	 * Constructor
	 * 
	 * @param x
	 * @param y
	 * @param numMines
	 */
	public Board(int x, int y, int numMines)
	{
		generate(x, y, numMines);
		this.mines = numMines;
	}

	/**
	 * Default Constructor
	 */
	public Board()
	{
		this(Y_HARD, X_HARD, MINES_HARD);
	}

	/**
	 * Generates a board with randomly placed mines
	 * 
	 * @param y
	 * @param x
	 * @param numMines
	 */
	public void generate(int y, int x, int numMines)
	{
		board = new Block[y][x];
		this.mines = numMines;
		for (int i = 0; i < numMines;)
		{
			int yMine = (int) (Math.random() * y);
			int xMine = (int) (Math.random() * x);
			if (board[yMine][xMine] == null)
			{
				board[yMine][xMine] = new Block(-1);
				i++;
			}
		}
		for (int j = 0; j < board.length; j++)
		{
			for (int k = 0; k < board[0].length; k++)
			{
				if (board[j][k] == null)
				{
					board[j][k] = new Block(getNeighborMines(j, k));
				}
			}
		}
	}

	/**
	 * Gets the number of mines around a spot on the board
	 * 
	 * @param y
	 * @param x
	 * @return
	 */
	public int getNeighborMines(int y, int x)
	{
		int numMines = 0;
		for (int i = y - 1; i <= y + 1; i++)
		{
			for (int j = x - 1; j <= x + 1; j++)
			{
				if (isLegal(i, j) && board[i][j] != null
						&& board[i][j].getNumMines() == -1)
				{
					numMines++;
				}
			}
		}
		return numMines;
	}

	/**
	 * Gets the number of spots flagged around a specific spot
	 * 
	 * @param y
	 * @param x
	 * @return
	 */
	public int getNeighborFlags(int y, int x)
	{
		int numFlags = 0;
		for (int i = y - 1; i <= y + 1; i++)
		{
			for (int j = x - 1; j <= x + 1; j++)
			{
				if (isLegal(i, j) && board[i][j] != null
						&& board[i][j].isFlagged())
				{
					numFlags++;
				}
			}
		}
		return numFlags;
	}

	/**
	 * Helper function: out of bounds check
	 * 
	 * @param y
	 * @param x
	 * @return
	 */
	public boolean isLegal(int y, int x)
	{
		return (y < board.length && y >= 0 && x < board[y].length && x >= 0);
	}

	// Accessors
	public Block getBlock(int y, int x)
	{
		return board[y][x];
	}

	public int getWidth()
	{
		return board.length;
	}

	public int getHeight()
	{
		return board[0].length;
	}

	public int getMines()
	{
		return mines;
	}

	public String toString()
	{
		String s = "";
		for (int i = 0; i < board.length; i++)
		{
			for (int j = 0; j < board[0].length; j++)
			{
				if (board[i][j].getNumMines() != -1)
					s += " " + board[i][j];
				else
					s += board[i][j];
			}
			s += "\n";
		}
		return s;
	}
	// public static void main(String [] args){
	// Board b = new Board();
	// System.out.println(b);
	// }
}
