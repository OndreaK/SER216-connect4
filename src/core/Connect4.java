package core;

/**
 * Connect4 class handles all the logic needed to play a game of Connect4.
 * 
 * @author Ondrea Kee
 * @version 2.0 April 6, 2020
 *
 */
public class Connect4 {

	/** Variables to keep track of the number of pieces remaining for each player */
	private int playerXNumPieces, playerYNumPieces;
	/**
	 * Variable that keeps track of whose turn it is. If true, it is player X turn.
	 * If false, it is player Y turn.
	 */
	private boolean playerTurn;
	/** 2-D char array variable that represents the game board */
	public char[][] board;

	/**
	 * Default constructor that initializes the player pieces to 21, the player turn
	 * to player X, and the board to empty
	 */
	public Connect4() {
		playerXNumPieces = 21;
		playerYNumPieces = 21;
		playerTurn = true;
		board = new char[6][7];
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				board[x][y] = ' ';
			}
		}
	}

	/**
	 * @param col - the col the user selects
	 * @return boolean - if the col has empty spots, returns true. else, returns
	 *         false.
	 * @exception IndexOutOfBoundsException - if the col is outside of the board
	 *                                      array, a message is shown to the user to
	 *                                      enter a valid col Method that checks if
	 *                                      a selected column has available empty
	 *                                      spaces
	 */
	public boolean isMoveValid(int col) {
		boolean valid = false;
		try {
			for (int x = board.length - 1; x >= 0; x--) {
				if (board[x][col - 1] == ' ') {
					valid = true;
				}
			}
		} catch (IndexOutOfBoundsException ex) {
			System.out.println("Invalid column selection. Please try again.");
		}
		return valid;
	}

	/**
	 * @param col - the col the user selects
	 * @exception IndexOutOfBoundsException - if the col is outside of the board
	 *                                      array, a message is shown to the user
	 *                                      that the piece cannot be placed Method
	 *                                      to place the current player's piece in
	 *                                      their selected column
	 */
	public void placePiece(int col) {
		try {
			for (int x = board.length - 1; x >= 0; x--) {
				if (board[x][col - 1] == ' ') {
					if (playerTurn) {
						board[x][col - 1] = 'X';
						playerXNumPieces--;
					} else {
						board[x][col - 1] = 'O';
						playerYNumPieces--;
					}
					break;
				}
			}
			playerTurn = !playerTurn;
		} catch (IndexOutOfBoundsException ex) {
			System.out.println("Cannot place piece outside of board dimensions.");
		}
	}

	/**
	 * @return boolean - if true, then both players are out of pieces and game ends
	 *         in a draw. else, the game continues. Method that checks if both
	 *         players still have pieces remaining or not
	 */
	public boolean gameOver() {
		if (playerXNumPieces == 0 && playerYNumPieces == 0) {
			return true;
		}
		return false;

	}

	/**
	 * @return char - current players turn Method that returns the current player
	 */
	public char getPlayerTurn() {
		if (playerTurn) {
			return 'X';
		} else {
			return 'Y';
		}
	}

	/**
	 * Method that prints the current game board in the console window
	 */
	public void printBoard() {
		for (int x = 0; x < board.length; x++) {
			System.out.printf("|");
			for (int y = 0; y < board[x].length; y++) {
				System.out.printf("%c|", board[x][y]);
			}
			System.out.println();
		}
	}

	/**
	 * @return boolean - if true, then the last player to place a piece has won the
	 *         game with 4-in-a-row. else, the game continues. Method that checks if
	 *         a player has won the game by seeing if they have 4 in a row
	 *         horizontally, vertically, or diagonally.
	 */
	public boolean isWin() {
		for (int x = 0; x <= 5; x++) {
			for (int y = 0; y <= 3; y++) {
				if (board[x][y] == board[x][y + 1] && board[x][y] == board[x][y + 2] && board[x][y] == board[x][y + 3]
						&& board[x][y] != ' ') {
					return true;
				}
			}
		}
		for (int x = 0; x <= 2; x++) {
			for (int y = 0; y <= 6; y++) {
				if (board[x][y] == board[x + 1][y] && board[x][y] == board[x + 2][y] && board[x][y] == board[x + 3][y]
						&& board[x][y] != ' ') {
					return true;
				}
			}
		}
		for (int x = 0; x <= 2; x++) {
			for (int y = 0; y <= 3; y++) {
				if (board[x][y] == board[x + 1][y + 1] && board[x][y] == board[x + 2][y + 2]
						&& board[x][y] == board[x + 3][y + 3] && board[x][y] != ' ') {
					return true;
				}
			}
		}
		for (int x = 0; x <= 2; x++) {
			for (int y = 3; y <= 6; y++) {
				if (board[x][y] == board[x + 1][y - 1] && board[x][y] == board[x + 2][y - 2]
						&& board[x][y] == board[x + 3][y - 3] && board[x][y] != ' ') {
					return true;
				}
			}
		}
		return false;
	}
}
