package minesweeper.data;

import java.awt.Dimension;
import javax.swing.JButton;


public class Block {
	private int numMines;
	private JButton button;
	private boolean isFlagged;
	private boolean isClicked;
	
	public Block(int x){
		setNumMines(x);
		button = new JButton();
		button.setPreferredSize(new Dimension(30, 30));
		setFlagged(false);
		setClicked(false);
	}
	
	public void setNumMines(int numMines) {this.numMines = numMines;}
	public int getNumMines() {return numMines;}
	public JButton getButton() {return button;}
	public void setFlagged(boolean b) {isFlagged = b;}
	public boolean isFlagged() {return isFlagged;}
	public String toString(){return "" + getNumMines();}
	public void setClicked(boolean isClicked) {this.isClicked = isClicked;}
	public boolean isClicked() {return isClicked;}
}
