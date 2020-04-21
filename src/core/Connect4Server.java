package core;

import java.io.*;
import java.net.*;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * Connect4Server class uses handles the game logic and controls all online
 * gaming sessions using networking.
 * 
 * @author Ondrea Kee
 * @version 1.0 April 20, 2020
 *
 */
public class Connect4Server extends Application {
	private int sessionNo = 1; // Number a session

	@Override
	public void start(Stage primaryStage) {
		TextArea taLog = new TextArea();
		Scene scene = new Scene(new ScrollPane(taLog), 450, 200);
		primaryStage.setTitle("Connect-4 Server");
		primaryStage.setScene(scene);
		primaryStage.show();

		new Thread(() -> {
			try {
				Platform.setImplicitExit(false);
				ServerSocket serverSocket = new ServerSocket(8004);
				Platform.runLater(() -> taLog.appendText(new Date() + ": Server started at socket 8004\n"));

				while (true) {
					Platform.runLater(() -> taLog
							.appendText(new Date() + ": Waiting for players to join session " + sessionNo + '\n'));

					// Connect to player 1
					Socket player1 = serverSocket.accept();

					Platform.runLater(() -> {
						taLog.appendText(new Date() + ": Player 1 joined session " + sessionNo + '\n');
						taLog.appendText("Player 1's IP address: " + player1.getInetAddress().getHostAddress() + '\n');
					});

					// Notify that the player is Player 1
					new DataOutputStream(player1.getOutputStream()).writeInt(1);

					// Connect to player 2
					Socket player2 = serverSocket.accept();

					Platform.runLater(() -> {
						taLog.appendText(new Date() + ": Player 2 joined session " + sessionNo + '\n');
						taLog.appendText("Player 2's IP address: " + player2.getInetAddress().getHostAddress() + '\n');
					});

					// Notify that the player is Player 2
					new DataOutputStream(player2.getOutputStream()).writeInt(2);

					// Display this session and increment session number
					Platform.runLater(
							() -> taLog.appendText(new Date() + ": Start a thread for session " + sessionNo++ + '\n'));

					// Launch a new thread for this session of two players
					new Thread(new HandleASession(player1, player2)).start();
//          serverSocket.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}).start();
	}

	// Define the thread class for handling a new session for two players
	class HandleASession implements Runnable {
		private Socket player1;
		private Socket player2;

		// Create and initialize cells
		private Connect4 game = new Connect4();

		private DataInputStream player1Input;
		private DataOutputStream player1Output;
		private DataInputStream player2Input;
		private DataOutputStream player2Output;

		/** Construct a thread */
		public HandleASession(Socket player1, Socket player2) {
			this.player1 = player1;
			this.player2 = player2;
		}

		/** Implement the run() method for the thread */
		public void run() {
			try {
				// Create data input and output streams
				DataInputStream player1Input = new DataInputStream(player1.getInputStream());
				DataOutputStream player1Output = new DataOutputStream(player1.getOutputStream());
				DataInputStream player2Input = new DataInputStream(player2.getInputStream());
				DataOutputStream player2Output = new DataOutputStream(player2.getOutputStream());

				// Write anything to notify player 1 to start
				// This is just to let player 1 know to start
				player1Output.writeInt(1);

				// Continuously serve the players and determine and report
				// the game status to the players
				do {
					// Receive a move from player 1
					int col = player1Input.readInt();
					boolean validMove = game.isMoveValid(col);
					while (!validMove) {
						player1Output.writeInt(-1);
						col = player1Input.readInt();
						validMove = game.isMoveValid(col);
					}
					game.placePiece(col);

					// Check if Player 1 wins
					if (game.isWin()) {
						player1Output.writeInt(100);
						player2Output.writeInt(100);
						player2Output.writeInt(col);
						break;
					} else if (game.gameOver()) {
						player1Output.writeInt(0);
						player2Output.writeInt(0);
						player2Output.writeInt(col);
						break;
					} else {
						player1Output.writeInt(10);
					}

					// Notify player 2 to take the turn
					player2Output.writeInt(400);
					player2Output.writeInt(col);

					// Receive a move from Player 2
					col = player2Input.readInt();

					validMove = game.isMoveValid(col);
					while (!validMove) {
						player2Output.writeInt(-1);
						col = player2Input.readInt();
						validMove = game.isMoveValid(col);
					}
					player2Output.writeInt(10);
					game.placePiece(col);

					// Check if Player 2 wins
					if (game.isWin()) {
						player2Output.writeInt(200);
						player1Output.writeInt(200);
						player1Output.writeInt(col);
						break;
					} else if (game.gameOver()) {
						player2Output.writeInt(0);
						player1Output.writeInt(0);
						player1Output.writeInt(col);
						break;
					} else {
						player2Output.writeInt(10);
					}
					player1Output.writeInt(400);
					player1Output.writeInt(col);
				} while (true);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		/** Send the move to other player */
		private void sendMove(DataOutputStream out, int column) throws IOException {
			out.writeChars("Your opponent place their piece in column: " + column);
		}
	}

	/**
	 * The main method is only needed for the IDE with limited JavaFX support. Not
	 * needed for running from the command line.
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
