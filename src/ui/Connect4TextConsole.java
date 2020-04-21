package ui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import core.Connect4;
import core.Connect4Client;
import core.Connect4ComputerPlayer;
import core.Connect4Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import ui.Connect4GUI;

/**
 * Connect4TextConsole class builds the UI for a connect 4 game.
 * 
 * @author Ondrea Kee
 * @version 3.0 April 20, 2020
 *
 */
public class Connect4TextConsole {
	/** Constant variable that lists the UI options a player can select */
	private static final Object[] UI_OPTIONS = { "GUI", "Console-Based" };
	private static final Object[] PLAY_OPTIONS = { "Online PvP", "Local Play" };

	public static void main(String[] args) {
		Connect4 game = new Connect4();
		JFrame frame = new JFrame();
		frame.setAlwaysOnTop(true);
		int ui = JOptionPane.showOptionDialog(frame,
				"Welcome to Connect-4!\nPlease select if you would like a GUI or console-based UI design?",
				"Select a UI", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, UI_OPTIONS, null);
		int gameType = JOptionPane.showOptionDialog(frame,
				"To begin a game, please select what type of game you would like to play", "Select a Game Type",
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, PLAY_OPTIONS, null);
		if (ui == 0 && gameType == 0) {
			Thread serverThread = new Thread() {
				public void run() {
					Connect4Server.main(args);
				}
			};

			Thread clientThread = new Thread() {
				public void run() {
					Platform.runLater(() -> {
						Connect4Client guiClient = new Connect4Client();
						guiClient.setUI("gui");
						guiClient.start(new Stage());
					});
				}
			};

			serverThread.start();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			clientThread.start();
		} else if (ui == 0 && gameType == 1) {
			Connect4GUI.main(args);
		} else if (ui == 1 && gameType == 1) {
			game.printBoard();
			System.out.println("Begin Game \n");
			String competitor = "";
			do {
				competitor = JOptionPane.showInputDialog(frame,
						"Enter 'P' if you would like to play against another player. Enter 'C' if you would like to play against the computer");
			} while (!competitor.equalsIgnoreCase("P") && !competitor.equalsIgnoreCase("C"));
			if (competitor.equalsIgnoreCase("C")) {
				System.out.println("Starting game against CPU \nYou are X and the CPU is O. Good Luck! \n");
				Connect4ComputerPlayer cpu = new Connect4ComputerPlayer();
				do {
					if (game.getPlayerTurn() == 'X') {
						int col = Integer.parseInt(JOptionPane.showInputDialog(frame,
								"It is your turn! Which column would you like to place your piece?"));
						boolean validMove = game.isMoveValid(col);
						while (!validMove) {
							col = Integer.parseInt(JOptionPane.showInputDialog(frame,
									"Please enter a different column to place your piece"));
							validMove = game.isMoveValid(col);
						}
						game.placePiece(col);
						System.out.println("Your col selection: " + col);
						game.printBoard();
						System.out.println("");
					} else {
						int col = cpu.getRandomMove();
						boolean validMove = game.isMoveValid(col);
						while (!validMove) {
							col = cpu.getRandomMove();
							validMove = game.isMoveValid(col);
						}
						game.placePiece(col);
						System.out.println("CPU col selection: " + col);
						game.printBoard();
						System.out.println("");
					}
				} while (!game.gameOver() && !game.isWin());
				if (game.isWin()) {
					switch (game.getPlayerTurn()) {
					case 'Y':
						System.out.println("Congrats!! You have won the game!");
						break;
					case 'X':
						System.out.println("Aww man! :( The CPU beat you!");
						break;
					default:
						break;
					}
				} else {
					System.out.println("Well Played! Tied Game!");
				}
			} else {
				do {
					int col = Integer.parseInt(JOptionPane.showInputDialog(frame,
							"Player " + game.getPlayerTurn() + " which column would you like to place your piece?"));
					boolean validMove = game.isMoveValid(col);
					while (!validMove) {
						col = Integer.parseInt(JOptionPane.showInputDialog(frame,
								"Please enter a different column to place your piece"));
						validMove = game.isMoveValid(col);
					}
					game.placePiece(col);
					System.out.println("Col selection: " + col);
					game.printBoard();
					System.out.println("");
				} while (!game.gameOver() && !game.isWin());
				if (game.isWin()) {
					switch (game.getPlayerTurn()) {
					case 'Y':
						System.out.println("Congrats Player X!! You have won the game!");
						break;
					case 'X':
						System.out.println("Congrats Player Y!! You have won the game!");
						break;
					default:
						break;
					}
				} else {
					System.out.println("Well Played! Tied Game!");
				}
			}
		} else if (ui == 1 && gameType == 0) {
			Thread serverThread = new Thread() {
				public void run() {
					Connect4Server.main(args);
				}
			};

			Thread clientThread = new Thread() {
				public void run() {
					Platform.runLater(() -> {
						Connect4Client consoleClient = new Connect4Client();
						consoleClient.setUI("console");
						consoleClient.start(new Stage());
					});
				}
			};

			serverThread.start();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			clientThread.start();
		}
	}
}
