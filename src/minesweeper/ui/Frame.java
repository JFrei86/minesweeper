package minesweeper.ui;

import minesweeper.data.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ChangeListener;

import minesweeper.ai.*;

/**
 * @author Jesse Freitas Frame inherits from JFrame and contains JMenu and
 *         JMenuItem components for the GUI and the panel in which the board
 *         lays it's JButtons. Private data includes a boolean that indicates if
 *         the player has clicked on the board for the first time, the number of
 *         buttons, the number of flags, the Board object taken by the
 *         constructor, the GridLayout of the board panel, etc.
 * 
 *         Provides Functions which the AI can use to essentially play the game.
 *         The AI has direct access to the board, however, it has been
 *         implemented to only view the parts of the board that have already
 *         been clicked
 */
@SuppressWarnings("serial")
public class Frame extends JFrame
{
	/**
	 * Private Data: timer - keeps track of how long the user has been playing
	 * and updates the time from the time the player first clicks to their
	 * winning or losing state layout - keeps the gameBoard in the rows and
	 * columns represented in the Board gameBoard - the JPanel that contains the
	 * JButtons from the Board's Blocks board - the Board taken by the
	 * Contructor's argument numButtons - the number of JButtons in the panel.
	 * When initialized, this is the width * height numFlags - the number of
	 * flagged squares on the board. When initialized, this is zero.
	 * isFirstClick - the status of the board. Used to determine if the user has
	 * made their first move. Used to ensure that the user cannot lose on the
	 * first click. easy, med, hard, custom, newGame - radio buttons that are
	 * components of the menu at the top of the GUI. minesRemaining - shows the
	 * current number of flags and the distance from winning
	 */
	private JTimerLabel timer = new JTimerLabel();

	private GridLayout layout;
	private JPanel gameBoard;

	private final Board board;
	private int numButtons;
	private int numFlags = 0;
	private boolean isFirstClick = true;

	private JRadioButtonMenuItem easy = new JRadioButtonMenuItem(
			"Easy (9 x 9, 10 Mines)");
	private JRadioButtonMenuItem med = new JRadioButtonMenuItem(
			"Medium (16 x 16, 40 Mines)");
	private JRadioButtonMenuItem hard = new JRadioButtonMenuItem(
			"Hard (16 x 30, 99 Mines)");
	private JMenuItem custom = new JMenuItem("Custom...");
	private JButton newGame = new JButton("Reset");
	private JProgressBar minesRemaining;
	private AIHeuristicSolver AI = new AIHeuristicSolver(this);
	private final AIControlFrame AIcontrol = new AIControlFrame(this);
	
	/**
	 * Constructor: responsible for the initialization of the game. The method
	 * reset() is called upon initialization
	 * 
	 * @param b
	 *            the Board object that is to be used upon initializing the
	 *            game.
	 */
	public Frame(Board b)
	{

		board = b;

		layout = new GridLayout(board.getWidth(), board.getHeight());
		minesRemaining = new JProgressBar(0, b.getMines());
		minesRemaining.setStringPainted(true);
		minesRemaining.setPreferredSize(new Dimension(1, 1));
		gameBoard = new JPanel(layout);
		add(gameBoard);
		
		AI.Initialize(this, new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				AIcontrol.update(AI.makeMove());
			}
		});
		reset();

		JMenuBar bar = new JMenuBar();
		JMenu level = new JMenu("Difficulty");
		MenuListener listener = new MenuListener();		

		bar.add(newGame);
		bar.add(level);
		bar.add(timer);
		bar.add(minesRemaining);
		level.add(easy);
		level.add(med);
		level.add(hard);
		level.add(custom);
		

		newGame.addActionListener(listener);
		easy.addActionListener(listener);
		med.addActionListener(listener);
		hard.addActionListener(listener);
		custom.addActionListener(listener);

		setJMenuBar(bar);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		AIcontrol.setVisible(true);
	}

	public AIHeuristicSolver getAI()
	{
		return AI;
	}

	/**
	 * Resets the game state and the AI to the beginning again.
	 */
	public void reset()
	{
		timer.stop();
		timer.reset();
		numFlags = 0;
		updateProgressBar(numFlags);
		minesRemaining.setMaximum(board.getMines());
		isFirstClick = true;
		numButtons = board.getHeight() * board.getWidth();
		for (int i = 0; i < board.getWidth(); i++)
		{
			for (int j = 0; j < board.getHeight(); j++)
			{
				board.getBlock(i, j).getButton().setEnabled(true);
				board.getBlock(i, j).getButton()
						.addMouseListener(new GameListener(i, j));
				gameBoard.add(board.getBlock(i, j).getButton());
			}
		}
		gameBoard.validate();
		pack();

	}

	/**
	 * Terminates the game with the winning or losing state, as determined by
	 * the flag argument.
	 * 
	 * @param aFlag
	 *            True if the game is in winning state, False if losing state.
	 */
	public void endGame(boolean aFlag)
	{
		timer.stop();
		if (AI.getTimer().isRunning())
			AI.getTimer().stop();
		if (!aFlag)
		{
			for (int i = 0; i < board.getWidth(); i++)
			{
				for (int j = 0; j < board.getHeight(); j++)
				{
					if (board.getBlock(i, j).getNumMines() == -1
							&& !board.getBlock(i, j).isClicked()
							&& !board.getBlock(i, j).isFlagged())
					{
						click(i, j);
					}
					if (board.getBlock(i, j).getNumMines() != -1
							&& board.getBlock(i, j).isFlagged())
					{
						click(i, j, MineImage.NOT_A_MINE);
					}
				}
			}
			for (int i = 0; i < gameBoard.getComponentCount(); i++)
			{
				if (gameBoard.getComponent(i) instanceof JButton)
					gameBoard.getComponent(i).setEnabled(false);
			}
		} else
		{
			numFlags = board.getMines();
			updateProgressBar(numFlags);
			for (int i = 0; i < board.getWidth(); i++)
			{
				for (int j = 0; j < board.getHeight(); j++)
				{
					if (board.getBlock(i, j).getNumMines() == -1
							&& !board.getBlock(i, j).isClicked())
					{
						click(i, j, MineImage.CHECK_MARK);
					}
				}
			}
		}
	}

	/**
	 * Updates the progress bar with the specified value.
	 * 
	 * @param n
	 *            the value to update it to.
	 */
	public void updateProgressBar(int n)
	{
		minesRemaining.setValue(n);
		minesRemaining.setString("" + (board.getMines() - numFlags));
		if (numFlags > minesRemaining.getMaximum())
			minesRemaining.setIndeterminate(true);
		else
			minesRemaining.setIndeterminate(false);
	}

	/**
	 * Clears the graphical components to reinitialize the game
	 */
	private void clearGame()
	{
		while (gameBoard.getComponentCount() != 0)
		{
			gameBoard.remove(0);
		}
		gameBoard.validate();
	}

	/**
	 * Reads in an integer value, Utility for custom games.
	 * 
	 * @param s
	 *            Prompt to be displayed
	 * @param max
	 *            Max value to be returned
	 * @return a number received from input
	 */
	public int getInt(String s, int max)
	{
		boolean isInvalid = true;
		String toParse;
		int i = 0;
		while (isInvalid)
		{
			try
			{
				toParse = JOptionPane.showInputDialog(this, new JLabel(s));
				i = Integer.parseInt(toParse);
				if (i > 1 && i <= max)
					isInvalid = false;

			} catch (Exception e)
			{
			}
		}
		return i;
	}

	/**
	 * Raw game click, handles graphical manipulation
	 * 
	 * @param y
	 * @param x
	 */
	public void click(int y, int x)
	{
		click(y, x, board.getBlock(y, x).getNumMines());
	}

	/**
	 * Raw game click, handles graphical manipulation
	 * 
	 * @param y
	 * @param x
	 * @param image
	 *            the image to display, constant or the number of mines
	 */
	public void click(int y, int x, int image)
	{
		numButtons--;
		int i = gameBoard.getComponentCount();
		gameBoard.remove(board.getBlock(y, x).getButton());
		if (i != gameBoard.getComponentCount())
		{
			MineImage icon = new MineImage(image);
			icon.addMouseListener(new ShiftClickListener(y, x));
			gameBoard.add(icon, (y * board.getHeight() + x));
			gameBoard.validate();
			board.getBlock(y, x).setClicked(true);
		}
	}

	/**
	 * Simulates a recursive click if the clicked area has no mines around it.
	 * 
	 * @param y
	 * @param x
	 */
	public void zeroClick(int y, int x)
	{
		if (board.getBlock(y, x).getNumMines() == 0)
			for (int i = y - 1; i < y + 2; i++)
			{
				for (int j = x - 1; j < x + 2; j++)
				{
					if (board.isLegal(i, j)
							&& board.getBlock(i, j).getNumMines() != -1
							&& !board.getBlock(i, j).isClicked())
					{
						gameClick(i, j);
					}
				}
			}
	}

	/**
	 * If called and the number of mines surrounding is equal to the number of
	 * flags around it, clicks all squares around it.
	 * 
	 * @param y
	 * @param x
	 */
	public void flagClick(int y, int x)
	{
		if (board.getBlock(y, x).getNumMines() == board.getNeighborFlags(y, x))
		{
			for (int i = y - 1; i < y + 2; i++)
			{
				for (int j = x - 1; j < x + 2; j++)
				{
					if (board.isLegal(i, j)
							&& !board.getBlock(i, j).isFlagged()
							&& !board.getBlock(i, j).isClicked())
					{
						gameClick(i, j);
					}
				}
			}
		}
	}

	/**
	 * Only called at the beginning of the game, resets the game until the
	 * clicked box is not a mine.
	 * 
	 * @param y
	 * @param x
	 */
	public void firstClick(int y, int x)
	{
		while (board.getBlock(y, x).getNumMines() == -1)
		{
			newGame(board.getWidth(), board.getHeight(), board.getMines());
		}
		timer.start();
		isFirstClick = false;
	}

	/**
	 * Game simulation click. Handles all click types in the game and calls
	 * helper functions as needed
	 * 
	 * @param y
	 * @param x
	 */
	public void gameClick(int y, int x)
	{
		if (board.getBlock(y, x).isFlagged()
				|| !board.getBlock(y, x).getButton().isEnabled())
			return;
		if (isFirstClick)
		{
			firstClick(y, x);
		}
		click(y, x);
		if (board.getBlock(y, x).getNumMines() == -1)
		{
			gameBoard.getComponents()[y * board.getHeight() + x]
					.setBackground(Color.RED);
			endGame(false);
			return;
		}
		if (numButtons == board.getMines())
		{
			endGame(true);
			return;
		}

		if (board.getBlock(y, x).getNumMines() == 0)
		{
			zeroClick(y, x);
		}
	}

	/**
	 * Generates a new board and resets the game to the beginning.
	 * 
	 * @param y
	 * @param x
	 * @param mines
	 */
	public void newGame(int y, int x, int mines)
	{
		board.generate(y, x, mines);
		layout.setRows(board.getWidth());
		layout.setColumns(board.getHeight());
		gameBoard.validate();
		clearGame();
		reset();

	}

	/**
	 * Toggles a flag on a potential mine
	 * 
	 * @param y
	 * @param x
	 */
	public void toggleFlag(int y, int x)
	{
		if (!board.getBlock(y, x).isFlagged())
		{
			board.getBlock(y, x).setFlagged(true);
			board.getBlock(y, x).getButton()
					.setIcon(new MineImage(MineImage.FLAG));
			updateProgressBar(++numFlags);

		} else
		{
			board.getBlock(y, x).setFlagged(false);
			board.getBlock(y, x).getButton().setIcon(null);
			updateProgressBar(--numFlags);
		}
	}

	/**
	 * Handles Menu Events
	 * 
	 * @author Jesse Freitas 2013
	 * 
	 */
	private class MenuListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == newGame)
			{
				newGame(board.getWidth(), board.getHeight(), board.getMines());
				return;
			}
			if (e.getSource() == easy)
			{
				newGame(Board.Y_EASY, Board.X_EASY, Board.MINES_EASY);
				easy.setSelected(true);
				med.setSelected(false);
				hard.setSelected(false);
			}
			if (e.getSource() == med)
			{
				newGame(Board.Y_MED, Board.X_MED, Board.MINES_MED);
				easy.setSelected(false);
				med.setSelected(true);
				hard.setSelected(false);
			}
			if (e.getSource() == hard)
			{
				newGame(Board.Y_HARD, Board.X_HARD, Board.MINES_HARD);
				easy.setSelected(false);
				med.setSelected(false);
				hard.setSelected(true);
			}
			if (e.getSource() == custom)
			{
				int y = getInt("Rows: Please Enter a Number between 1 and 30",
						30);
				int x = getInt(
						"Columns: Please Enter a Number between 1 and 30", 30);
				int max = y * x / 2;
				newGame(y,
						x,
						getInt("Mines: Please Enter a Number between 1 and "
								+ max, max));
				easy.setSelected(false);
				med.setSelected(false);
				hard.setSelected(false);
			}
		}
	}

	/**
	 * Button Click Listener
	 * 
	 * @author Jesse Freitas 2013
	 * 
	 */
	private class GameListener implements MouseListener
	{
		private int y, x;

		public GameListener(int y, int x)
		{
			this.y = y;
			this.x = x;
		}

		public void mouseClicked(MouseEvent arg0)
		{
		}

		public void mouseEntered(MouseEvent arg0)
		{
		}

		public void mouseExited(MouseEvent arg0)
		{
		}

		public void mousePressed(MouseEvent arg0)
		{
			if (arg0.getButton() == MouseEvent.BUTTON3)
			{
				toggleFlag(y, x);
				return;
			}
			gameClick(y, x);

		}

		public void mouseReleased(MouseEvent arg0)
		{
		}
	}

	public Board getBoard()
	{
		return this.board;
	}

	public class ShiftClickListener implements MouseListener
	{

		private int y, x;

		public ShiftClickListener(int y, int x)
		{
			this.y = y;
			this.x = x;
		}

		public void mouseClicked(MouseEvent e)
		{
		}

		public void mouseEntered(MouseEvent e)
		{
		}

		public void mouseExited(MouseEvent e)
		{
		}

		public void mousePressed(MouseEvent e)
		{
			if (e.isShiftDown())
				flagClick(y, x);
		}

		public void mouseReleased(MouseEvent e)
		{
		}
	}

	public static void main(String[] args)
	{
		Frame f = new Frame(new Board());
		f.setVisible(true);
	}
}
