package minesweeper.ai;

import minesweeper.ui.*;
import minesweeper.data.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

import javax.swing.Timer;

public class AIHeuristicSolver
{
	private Timer t;

	/**
	 * Used to start the timer, which calls moves every 500 milliseconds by
	 * default
	 * 
	 * @param f
	 *            the instance of the game to play
	 */
	public void Initialize(final Frame f)
	{
		t = new Timer(500, new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				makeMove(f);
			}
		});
		t.setCoalesce(false); // Prevents the AI from playing with multiple
								// threads at the same time making it easier to
								// see when running
		t.start();
	}

	/**
	 * Set the delay in between moves to slow the AI down
	 * 
	 * @param delay
	 */
	public void setMoveDelay(int delay)
	{
		t.setDelay(delay);
	}

	/**
	 * Makes a move and returns its certainty that the move is not a mine
	 * 
	 * @param f
	 * @return
	 */
	public int makeMove(Frame f)
	{
		if (!findCertain(f))
		{
			int prob = findSemiCertain(f);
			if (prob == -1)
			{
				if (!findNoCertain(f))
				{
					return 1;
				}
				System.out.println("Certainty = 0");
				return 0;
			}
			System.out.println("Certainty = " + (100 - prob));
			return 100 - prob;
		}
		System.out.println("Certainty = 100");
		return 100;
	}

	/**
	 * Make a random move as a last resort if no other better moves have been
	 * found
	 * 
	 * @param f
	 *            the game at the current state
	 * @return whether a move was found
	 */
	private boolean findNoCertain(Frame f)
	{
		Random rand = new Random((long) (Math.random() * 1000));
		int area = f.getBoard().getHeight() * f.getBoard().getWidth();
		int i = 0;
		do
		{
			int x = rand.nextInt(f.getBoard().getHeight());
			int y = rand.nextInt(f.getBoard().getWidth());
			if (!f.getBoard().getBlock(y, x).isClicked()
					&& !f.getBoard().getBlock(y, x).isFlagged())
			{
				f.gameClick(y, x);
				return true;
			}
			i++;
		} while (i < area);
		return false;
	}

	/**
	 * Search the board for a move that it knows with certainty is not a mine
	 * 
	 * @param f
	 *            the game at the most current state
	 * @return whether a move was found
	 */
	private boolean findCertain(Frame f)
	{
		for (int x = 0; x < f.getBoard().getHeight(); x++)
		{
			for (int y = 0; y < f.getBoard().getWidth(); y++)
			{
				int prob = getProbability(f, x, y);
				if (prob == 100)
				{
					boolean stateChanged = false;
					for (int i = x - 1; i <= x + 1; i++)
					{
						for (int j = y - 1; j <= y + 1; j++)
						{
							if (i == x && j == y)
								continue;
							if (f.getBoard().isLegal(j, i)
									&& !f.getBoard().getBlock(j, i).isClicked()
									&& !f.getBoard().getBlock(j, i).isFlagged())
							{
								f.toggleFlag(j, i);
								stateChanged = true;
							}
						}
					}
					if (stateChanged)
						return true;
				}
				if (prob == 0)
				{
					boolean stateChanged = false;
					for (int i = x - 1; i <= x + 1; i++)
					{
						for (int j = y - 1; j <= y + 1; j++)
						{
							if (i == x && j == y)
								continue;
							if (f.getBoard().isLegal(j, i)
									&& !f.getBoard().getBlock(j, i).isClicked()
									&& !f.getBoard().getBlock(j, i).isFlagged())
							{
								f.gameClick(j, i);
								stateChanged = true;
							}
						}
					}
					if (stateChanged)
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Search the board for ajacent blocks that it can narrow down as least
	 * likely to be a mine Algorithm takes into account the number of mines in
	 * the surrounding area and calculates possible probability. If there are
	 * multiple spaces with the same least likely probability a move is chosen
	 * at random from those moves.
	 * 
	 * @param f
	 *            the game at the most current state to make a move
	 * @return the probability p where 0 < p < 100 or -1 if no such move was
	 *         found.
	 */
	private int findSemiCertain(Frame f)
	{
		TreeMap<Integer, ArrayList<Integer>> probabilities = new TreeMap<Integer, ArrayList<Integer>>();
		for (int x = 0; x < f.getBoard().getHeight(); x++)
		{
			for (int y = 0; y < f.getBoard().getWidth(); y++)
			{
				if (!f.getBoard().getBlock(y, x).isClicked()
						&& !f.getBoard().getBlock(y, x).isFlagged()
						&& hasClickedNeighbor(f, x, y))
				{
					int numClicked = 0;
					int total = 0;
					for (int i = x - 1; i <= x + 1; i++)
					{
						for (int j = y - 1; j <= y + 1; j++)
						{
							if (f.getBoard().isLegal(j, i)
									&& f.getBoard().getBlock(j, i).isClicked())
							{
								int prob = getProbability(f, i, j);
								if (prob > 0 && prob < 100)
								{
									numClicked++;
									total += prob;
								}

							}
						}
					}
					if (numClicked == 0)
					{
						continue;
					}
					int prob = total / numClicked;
					if (prob > 0 && prob < 100)
					{
						if (probabilities.get(prob) == null)
						{
							probabilities.put(prob, new ArrayList<Integer>());
						}
						probabilities.get(prob).add(
								x + (y * f.getBoard().getHeight()));
					}
				}
			}
		}
		if (probabilities.size() == 0)
		{
			return -1;
		}
		int numProb = probabilities.firstEntry().getValue().size();
		Random rand = new Random((long) (Math.random() * 1000));
		int index = rand.nextInt(numProb);
		int x = probabilities.firstEntry().getValue().get(index)
				% f.getBoard().getHeight();
		int y = probabilities.firstEntry().getValue().get(index)
				/ f.getBoard().getHeight();
		f.gameClick(y, x);
		return probabilities.firstEntry().getKey();
	}

	/**
	 * Helper function for finding semi certain moves
	 * 
	 * @param f
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean hasClickedNeighbor(Frame f, int x, int y)
	{
		for (int i = x - 1; i <= x + 1; i++)
		{
			for (int j = y - 1; j <= y + 1; j++)
			{
				if (f.getBoard().isLegal(j, i)
						&& f.getBoard().getBlock(j, i).isClicked())
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the probability based on the number of flags, the number of mines
	 * and the available spaces left to find the probability of surrounding
	 * spaces
	 * 
	 * @param f
	 * @param x
	 * @param y
	 * @return the probability out of 100 or -1 if the space has been clicked or
	 *         not legal
	 */
	public int getProbability(Frame f, int x, int y)
	{
		if (!f.getBoard().isLegal(y, x)
				|| f.getBoard().getBlock(y, x).getNumMines() == 0)
		{
			return -1;
		}
		if (!f.getBoard().getBlock(y, x).isClicked())
		{
			return -1;
		}
		int numUnclicked = 0;
		int numFlagged = 0;
		for (int i = x - 1; i <= x + 1; i++)
		{
			for (int j = y - 1; j <= y + 1; j++)
			{
				if (x == i && y == j)
				{
					continue;
				}
				if (f.getBoard().isLegal(j, i)
						&& !f.getBoard().getBlock(j, i).isClicked())
				{
					numUnclicked++;
				}
				if (f.getBoard().isLegal(j, i)
						&& f.getBoard().getBlock(j, i).isFlagged())
				{
					numFlagged++;
				}
			}
		}
		if ((numUnclicked - numFlagged) != 0)
			return (f.getBoard().getBlock(y, x).getNumMines() - numFlagged)
					* 100 / (numUnclicked - numFlagged);
		return 0;
	}

	/**
	 * @param args
	 */
	// public static void main(String[] args)
	// {
	// Frame f = new Frame(new Board());
	// f.setVisible(true);
	// AIHeuristicSolver AI = new AIHeuristicSolver();
	// AI.Initialize(f);
	// }
	/**
	 * Accessor for the timer
	 * 
	 * @return
	 */
	public Timer getTimer()
	{
		return t;
	}

}
