package core;

import java.io.*;
import java.net.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

/**
 * Connect4Client class listens to the Connect4Server for board configurations
 * and updates the UI for online games.
 * 
 * @author Ondrea Kee
 * @version 1.0 April 20, 2020
 *
 */
public class Connect4Client extends Application {
	private static final Object[] UI_OPTIONS = { "GUI", "Console-Based" };
	private boolean myTurn = false;
	private String myColor = "red";
	private String opponentColor = "yellow";
	private Label lblTitle = new Label();
	private Label lblStatus = new Label();
	private int columnSelected;
	private DataInputStream fromServer;
	private DataOutputStream toServer;
	private boolean continueToPlay = true;
	private String host = "localhost";
	private Group group = new Group();
	private String ui = "gui";
	Connect4 game = new Connect4();

	@Override
	public void start(Stage primaryStage) {
		if (ui.equalsIgnoreCase("gui")) {
			Rectangle board = new Rectangle(5, 5, 575, 475);
			Platform.runLater(() -> {
				board.setFill(Color.BLUE);
				board.setArcHeight(10);
				board.setArcWidth(10);
				group.getChildren().add(board);
				int x_cord = 50;
				for (int x = 0; x < 7; x++) {
					int y_cord = 50;
					for (int y = 0; y < 6; y++) {
						Circle circle = new Circle(x_cord, y_cord, 35, Color.WHITE);
						y_cord += 75;
						group.getChildren().add(circle);
					}
					x_cord += 80;
				}
			});
			BorderPane borderPane = new BorderPane();
			Platform.runLater(() -> {
				borderPane.setTop(lblTitle);
				borderPane.setCenter(group);
				borderPane.setBottom(lblStatus);
				Scene scene = new Scene(borderPane, 700, 600);
				primaryStage.setTitle("Connect-4: Online Player vs. Player");
				primaryStage.setScene(scene);
				primaryStage.show();
			});
		} else if (ui.equalsIgnoreCase("console")) {
			System.out.print("\n\n");
			game.printBoard();
		}
		connectToServer();
	}

	private void connectToServer() {
		try {
			// Create a socket to connect to the server
			Socket socket = new Socket(host, 8004);

			// Create an input stream to receive data from the server
			fromServer = new DataInputStream(socket.getInputStream());

			// Create an output stream to send data to the server
			toServer = new DataOutputStream(socket.getOutputStream());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// Control the game on a separate thread
		new Thread(() -> {
			try {
				// Get notification from the server
				int player = fromServer.readInt();

				// Am I player 1 or 2?
				if (player == 1) {
					myColor = "red";
					opponentColor = "yellow";
					if (ui.equalsIgnoreCase("gui")) {
						Platform.runLater(() -> {
							lblTitle.setText("Your piece color: red");
							lblStatus.setText("Waiting for player 2 to join");
						});
					} else {
						System.out.println("Your piece color: red");
						System.out.println("Waiting for player 2 to join");
					}

					// Receive startup notification from the server
					fromServer.readInt(); // Whatever read is ignored

					// The other player has joined
					if (ui.equalsIgnoreCase("gui")) {
						Platform.runLater(() -> lblStatus.setText(
								"You have been matched with another player. You will start first. Remember, good luck and have fun!! (:"));
					} else if (ui.equalsIgnoreCase("console")) {
						System.out.println(
								"You have been matched with another player. You will start first. Remember, good luck and have fun!! (:");
					}
					// It is my turn
					myTurn = true;
				} else if (player == 2) {
					myColor = "yellow";
					opponentColor = "red";
					if (ui.equalsIgnoreCase("gui")) {
						Platform.runLater(() -> {
							lblTitle.setText("Your piece color: yellow");
							lblStatus.setText(
									"Player 1 will place their piece first. Remember, good luck and have fun!! (:");
						});
					} else if (ui.equalsIgnoreCase("console")) {
						System.out.println("Your piece color: yellow");
						System.out.println(
								"Player 1 will place their piece first. Remember, good luck and have fun!! (:");
					}
				}

				JFrame frame = new JFrame();

				// Continue to play
				while (continueToPlay) {
					if (myTurn) {
						columnSelected = Integer.parseInt(
								JOptionPane.showInputDialog(frame, "Which column would you like to place your piece?"));
						sendMove(); // Send the move to the server
						receiveInfoFromServer(); // Receive info from the server
						myTurn = false;
					} else {
						receiveInfoFromServer();
					}

				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}).start();
	}

	/** Send this player's move to the server */
	private void sendMove() throws IOException {
		toServer.writeInt(columnSelected);
	}

	/**
	 * Receive info from the server
	 * 
	 * @throws InterruptedException
	 */
	private void receiveInfoFromServer() throws IOException {
		// Receive game status
		int status = fromServer.readInt();
		if (status == -1) {

			JFrame frame = new JFrame();
			columnSelected = Integer.parseInt(
					JOptionPane.showInputDialog(frame, "Please enter a different column to place your piece"));

			sendMove(); // Send the move to the server
			receiveInfoFromServer();
		} else if (status == 100) {
			// Player 1 won, stop playing
			continueToPlay = false;
			int colStart = (5 * (columnSelected - 1)) + columnSelected;

			if (myColor.equalsIgnoreCase("red")) {
				if (ui.equalsIgnoreCase("gui")) {
					for (int x = 5; x >= 0; x--) {
						Shape circ = (Shape) group.getChildren().get(colStart + x);
						if (circ.getFill().toString().equalsIgnoreCase("0xffffffff")) {
							if (myColor.equalsIgnoreCase("red")) {
								circ.setFill(Color.RED);
							} else {
								circ.setFill(Color.YELLOW);
							}
							x = 0;
						}
					}
					Platform.runLater(() -> lblStatus.setText("Congrats!! You have won the game! (:"));
				} else if (ui.equalsIgnoreCase("console")) {
					game.placePiece(columnSelected);
					System.out.print("\n\n");
					game.printBoard();
					System.out.println("Congrats!! You have won the game! (:");
				}

			} else if (myColor.equalsIgnoreCase("yellow")) {
				receiveMove();
				if (ui.equalsIgnoreCase("gui")) {
					Platform.runLater(
							() -> lblStatus.setText("Aww man! Better luck next time! Player 1 has won the game!"));
				} else if (ui.equalsIgnoreCase("console")) {
					System.out.println("Aww man! Better luck next time! Player 1 has won the game!");
				}
			}
		} else if (status == 200) {
			// Player 2 won, stop playing
			continueToPlay = false;
			if (myColor == "yellow") {
				if (ui.equalsIgnoreCase("gui")) {
					Platform.runLater(() -> lblStatus.setText("Congrats!! You have won the game! (:"));
				} else if (ui.equalsIgnoreCase("console")) {
					System.out.println("Congrats!! You have won the game! (:");
				}
			} else if (myColor == "red") {
				receiveMove();
				if (ui.equalsIgnoreCase("gui")) {
					Platform.runLater(
							() -> lblStatus.setText("Aww man! Better luck next time! Player 2 has won the game!"));
				} else if (ui.equalsIgnoreCase("console")) {
					System.out.println("Aww man! Better luck next time! Player 2 has won the game!");
				}
			}
		} else if (status == 0) {
			// No winner, game is over
			continueToPlay = false;
			if (ui.equalsIgnoreCase("gui")) {
				Platform.runLater(() -> lblStatus.setText("Well played!! Tied game!"));
			} else if (ui.equalsIgnoreCase("console")) {
				System.out.println("Well played!! Tied game!");
			}
			if (myColor.equalsIgnoreCase("yellow")) {
				receiveMove();
			}
		} else {
			int colStart = (5 * (columnSelected - 1)) + columnSelected;
			if (myTurn) {
				if (ui.equalsIgnoreCase("gui")) {
					for (int x = 5; x >= 0; x--) {
						Shape circ = (Shape) group.getChildren().get(colStart + x);
						if (circ.getFill().toString().equalsIgnoreCase("0xffffffff")) {
							if (myColor.equalsIgnoreCase("red")) {
								circ.setFill(Color.RED);
							} else {
								circ.setFill(Color.YELLOW);
							}
							x = 0;
						}
					}
				} else if (ui.equalsIgnoreCase("console")) {
					game.placePiece(columnSelected);
					System.out.print("\n\n");
					game.printBoard();
				}
			}
			if (status == 400) {
				receiveMove();
				myTurn = true; // It is my turn
			}
		}
	}

	private void receiveMove() throws IOException {
		// Get the other player's move
		int column = fromServer.readInt();
		if (ui.equalsIgnoreCase("gui")) {
			int colStart = (5 * (column - 1)) + column;
			for (int x = 5; x >= 0; x--) {
				Shape circ = (Shape) group.getChildren().get(colStart + x);
				if (circ.getFill().toString().equalsIgnoreCase("0xffffffff")) {
					if (opponentColor.equalsIgnoreCase("red")) {
						circ.setFill(Color.RED);
					} else {
						circ.setFill(Color.YELLOW);
					}
					x = 0;
				}
			}
		} else if (ui.equalsIgnoreCase("console")) {
			game.placePiece(column);
			System.out.print("\n\n");
			game.printBoard();
		}

	}

	public void setUI(String ui) {
		this.ui = ui;
	}

	/**
	 * The main method is only needed for the IDE with limited JavaFX support. Not
	 * needed for running from the command line.
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
