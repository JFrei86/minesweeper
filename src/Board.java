
public class Board {
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
	
	public Board(int x, int y, int numMines){
		generate(x, y, numMines);
		this.mines = numMines;
	}
	
	public Board(){
		this(Y_HARD, X_HARD, MINES_HARD);
	}
	
	public void generate(int y, int x, int numMines){
		board = new Block[y][x];
		this.mines = numMines;
		for(int i = 0; i < numMines;){
			int yMine = (int)(Math.random() * y);
			int xMine = (int)(Math.random() * x);
			if(board[yMine][xMine] == null){
				board[yMine][xMine] = new Block(-1);
				i++;
			}
		}
		for(int j = 0; j < board.length; j++){
			for(int k = 0; k < board[0].length; k++){
				if(board[j][k] == null){
					board[j][k] = new Block(getNeighborMines(j,k));
				}
			}
		}
	}
	public int getNeighborMines(int y, int x){
		int numMines = 0;
		for(int i = y - 1; i <= y + 1; i++){
			for(int j = x - 1; j <= x + 1; j++){
				if(isLegal(i,j) && board[i][j] != null && board[i][j].getNumMines() == -1){
					numMines++;
				}
			}
		}
		return numMines;
	}
	public int getNeighborFlags(int y, int x){
		int numFlags = 0;
		for(int i = y - 1; i <= y + 1; i++){
			for(int j = x - 1; j <= x + 1; j++){
				if(isLegal(i,j) && board[i][j] != null && board[i][j].isFlagged()){
					numFlags++;
				}
			}
		}
		return numFlags;
	}
	public boolean isLegal(int y, int x){return (y < board.length &&
			y >= 0 && x < board[y].length && x >= 0);}
	public Block getBlock(int y, int x){return board[y][x];}
	public int getWidth(){return board.length;}
	public int getHeight(){return board[0].length;}
	public int getMines(){return mines;}
	
	public String toString(){
		String s = "";
		for(int i = 0; i < board.length; i++){
			for(int j = 0; j < board[0].length; j++){
				if(board[i][j].getNumMines() != -1)
					s += " " + board[i][j];
				else
					s += board[i][j];
			}
			s += "\n";
		}
		return s;
	}
	public static void main(String [] args){
		Board b = new Board();
		System.out.println(b);
	}
}
