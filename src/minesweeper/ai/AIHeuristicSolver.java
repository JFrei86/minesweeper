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

	public void Initialize(final Frame f)
	{
		t = new Timer(250, new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				makeMove(f);
			}
		});
		t.setCoalesce(false);
		t.start();
	}
	public void setMoveDelay(int delay){
		t.setDelay(delay);
	}
	public int makeMove(Frame f)
	{
		if (!findCertain(f))
		{	
			int prob = findSemiCertain(f);
			if(prob == -1){
				if(!findNoCertain(f)){
					return 1;
				}
				System.out.println("Certainty = 0");
				return 0;
			}
			System.out.println("Certainty = " + (100 - prob));
			return 0;
		}
		System.out.println("Certainty = 100");
		return 0;
	}

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
	private int findSemiCertain(Frame f){
		TreeMap<Integer, ArrayList<Integer>> probabilities = new TreeMap<Integer, ArrayList<Integer>>();
		for (int x = 0; x < f.getBoard().getHeight(); x++){
			for (int y = 0; y < f.getBoard().getWidth(); y++){
				if(!f.getBoard().getBlock(y, x).isClicked() && !f.getBoard().getBlock(y, x).isFlagged() && hasClickedNeighbor(f, x, y)){
					int numClicked = 0;
					int total = 0;
					for (int i = x - 1; i <= x + 1; i++){
						for (int j = y - 1; j <= y + 1; j++){
							if(f.getBoard().isLegal(j, i) && f.getBoard().getBlock(j, i).isClicked()){
								int prob = getProbability(f, i, j);
								if(prob > 0 && prob < 100){
									numClicked++;
									total += prob;
								}
								 
							}
						}
					}
					if(numClicked == 0){
						continue;
					}
					int prob = total / numClicked;
					if (prob > 0 && prob < 100)
					{
						if(probabilities.get(prob) == null){
							probabilities.put(prob, new ArrayList<Integer>());
						}
						probabilities.get(prob).add(x + (y * f.getBoard().getHeight()));
					}
				}
			}
		}
		if(probabilities.size() == 0){
			return -1;
		}
		int numProb = probabilities.firstEntry().getValue().size();
		Random rand = new Random((long) (Math.random() * 1000));
		int index = rand.nextInt(numProb);
		int x = probabilities.firstEntry().getValue().get(index) % f.getBoard().getHeight();
		int y = probabilities.firstEntry().getValue().get(index) / f.getBoard().getHeight();
		f.gameClick(y, x);
		return probabilities.firstEntry().getKey();
	}
	private boolean hasClickedNeighbor(Frame f, int x, int y)
	{
		for (int i = x - 1; i <= x + 1; i++){
			for (int j = y - 1; j <= y + 1; j++){
				if(f.getBoard().isLegal(j, i) && f.getBoard().getBlock(j, i).isClicked()){
					return true;
				}
			}
		}
		return false;
	}

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
	public static void main(String[] args)
	{
		Frame f = new Frame(new Board());
		f.setVisible(true);
		AIHeuristicSolver AI = new AIHeuristicSolver();
		AI.Initialize(f);
	}

	public Timer getTimer()
	{
		return t;
	}

}
