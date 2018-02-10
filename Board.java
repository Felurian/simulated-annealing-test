import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Board {
	public boolean[][] board;
	public double[][] base;
	public int[][] neighbors;
	public double[][] scores;
	final static int SIZE = 31;
	final static int MID = SIZE/2;
	final static int BASE_SCORE = MID+1;
	final static int RANGE = 2;
	final static int HOOD = (RANGE * 2 + 1) * (RANGE * 2 + 1) - 1;
	
	public Board() {
		this(true);
	}
	public Board(boolean shuffle) {
		int s = SIZE;
		board = new boolean[s][s];
		base = new double[s][s];
		neighbors = new int[s][s];
		scores = new double[s][s];
		if(shuffle) {
			this.shuffle();
		}
	}

	@Override
	public String toString() {
		String str = "";
		for (boolean[] r : board) {
			for (boolean b : r) {
				str += (b ? "X" : ".");
			}
			str += "\n";
		}
		return str;
	}
	public Board copy() {
		Board b = new Board(false);
		for(int i=0;i<b.board.length;i++)
			for(int j=0;j<b.board.length;j++)
				b.board[i][j] = this.board[i][j];
		for(int i=0;i<b.base.length;i++)
			for(int j=0;j<b.base.length;j++)
				b.base[i][j] = this.base[i][j];
		for(int i=0;i<b.neighbors.length;i++)
			for(int j=0;j<b.neighbors.length;j++)
				b.neighbors[i][j] = this.neighbors[i][j];
		for(int i=0;i<b.scores.length;i++)
			for(int j=0;j<b.scores.length;j++)
				b.scores[i][j] = this.scores[i][j];
		return b;
	}
	public void shuffle() {
		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board.length; j++)
				board[i][j] = (new Random()).nextBoolean();
		this.initScore();
	}

	public Board mutation() {
		Random rand = new Random();
		Board b = this.copy();
		int y = rand.nextInt(SIZE);
		int x = rand.nextInt(SIZE);
		b.flip(y, x);
		return b;
	}

	private void flip(int i, int j) {
		board[i][j] = !board[i][j];
		base[i][j] = getBase(i, j);
		for (int y = i - RANGE; y <= i + RANGE; y++) {
			for (int x = j - RANGE; x <= j + RANGE; x++) {
				if (y < 0 || x < 0 || y >= SIZE || x >= SIZE)
					continue;
				neighbors[y][x] = getNeighbors(y, x);
			}
		}
		for (int y = i - RANGE; y <= i + RANGE; y++) {
			for (int x = j - RANGE; x <= j + RANGE; x++) {
				if (y < 0 || x < 0 || y >= SIZE || x >= SIZE)
					continue;
				scores[y][x] = getScore(y, x);
			}
		}
	}
	
	public double getScore() {
		double res=0;
		for(double[] r : scores)
			for(double d : r)
				res += d;
		return res;
	}
	private void initScore() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				base[i][j] = getBase(i, j);
				neighbors[i][j] = getNeighbors(i, j);
			}
		}
		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board.length; j++)
				scores[i][j] = getScore(i, j);
	}

	private double getScore(int i, int j) {
		double s = scores[i][j];
		double b = base[i][j];
		double multi = ((double) HOOD - neighbors[i][j]) / HOOD;
		double ns = b * multi;
		return ns;

	}

	private int getNeighbors(int i, int j) {
		int c = 0;
		for (int y = i - RANGE; y <= i + RANGE; y++) {
			for (int x = j - RANGE; x <= j + RANGE; x++) {
				if (y == i && x == j)
					continue;
				if (y < 0 || x < 0 || y >= SIZE || x >= SIZE)
					continue;
				if (board[y][x])
					c++;
			}
		}
		return c;
	}

	private double getBase(int i, int j) {
		if (!board[i][j])
			return 0;
		double dy = i - MID;
		double dx = j - MID;
		double d = Math.abs(dy) + Math.abs(dx);
		if (d >= BASE_SCORE)
			return -1;
		return BASE_SCORE - d;
	}

	public Board anneal() { // simulated annealing!!
		Random rand = new Random();
		Board sol = this;
		double old_cost = sol.getScore();
		double T = 1.0;
		double T_min = 0.001;
		double alpha = 0.99;
		while(T > T_min) {
			for(int i=0;i<6000;i++) {
				Board new_sol = sol.mutation();
				double new_cost = new_sol.getScore();
				double ap = acceptance_probability(old_cost,new_cost,T);
				//System.out.println(old_cost+" -> "+new_cost+" = "+ap);
				if(ap > rand.nextDouble()) {
					//System.out.println("ap was "+ap);
					sol = new_sol;
					old_cost = new_cost;
				}
			}
			T *= alpha;
		}
		return sol;
	}
	
	public static double acceptance_probability(double oldc, double newc, double t) {
		
		return Math.pow(Math.E, (newc-oldc)/t);
	}
	
	public Board opti() {
		Board b = this.mutation();
		for(int i=0;i<20000;i++) {
			double bs = b.getScore();
			Board c = b.mutation();
			double cs = c.getScore();
			if(cs > bs) {
				//System.out.println(bs+" -> "+cs);
				b = c;
			}
		}
		return b;
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		Board b = new Board();
		System.out.println();
		System.out.println("====================");
		System.out.println("  Random Board:");
		System.out.println("====================");
		System.out.print(b);
		System.out.println("Score: " + b.getScore());
		System.out.println("");
		System.out.println("====================");
		System.out.println("  Naive approach:");
		System.out.println("====================");
		Board c = b.opti();
		System.out.print(c);
		System.out.println("Score: " +c.getScore());
		System.out.println("");
		System.out.println("====================");
		System.out.println("  Correct solution:");
		System.out.println("====================");
		Board d = b.anneal();
		System.out.print(d);
		System.out.println("Score: " +d.getScore());
	}
}
