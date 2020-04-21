package ui;

import core.Connect4;
import core.Connect4ComputerPlayer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

/**
 * Connect4GUI class uses JavaFx to build a Connect-4 GUI.
 * 
 * @author Ondrea Kee
 * @version 1.0 April 13, 2020
 *
 */
public class Connect4GUI extends Application{
	@Override
	public void start(Stage primaryStage) {
		Connect4 game = new Connect4();
		
		/** Opening dialog to ask user if they would like to play against the CPU or another player */
	    String competitorOptions[] = { "CPU", "Player" }; 
	    ChoiceDialog dialog = new ChoiceDialog(competitorOptions[0], competitorOptions);
	    dialog.setHeaderText("Welcome to Connect-4!\nPlease select whether you would like to play against the computer or against another player.\nAnd remember, good luck and have fun! (:");
	    dialog.showAndWait();
	    Object competitor = dialog.getSelectedItem();
	    
	    /** Builds the JavaFx GUI for a player vs player game */
	    if(competitor.equals("Player")) {
	    	Rectangle board = new Rectangle(5,5,575,475);
		    board.setFill(Color.BLUE);
		    board.setArcHeight(10);
		    board.setArcWidth(10);
		    Group group = new Group();
		    group.getChildren().add(board);
		    int x_cord = 50;
		    for(int x=0; x < 7; x++) {
		    	int y_cord = 50;
		    	for(int y=0; y < 6; y++) {
		    		Circle circle = new Circle(x_cord,y_cord,35,Color.WHITE);
		    		y_cord += 75;
		    		group.getChildren().add(circle);
		    	}
		    	x_cord += 80;
		    }
		    Scene scene = new Scene(group, 600, 500);
		    primaryStage.setTitle("Connect-4: Player vs. Player"); 
		    primaryStage.setScene(scene); 
		    primaryStage.show(); 
		    do {
		    	String colOptions[] = { "1", "2", "3", "4", "5", "6", "7" }; 
			    ChoiceDialog colDialog = new ChoiceDialog(colOptions[0],colOptions);
			    colDialog.setHeaderText("Player "+game.getPlayerTurn()+" which column would you like to place your piece?");
			    colDialog.showAndWait();
			    int col = Integer.valueOf((String)colDialog.getSelectedItem());
			    boolean validMove = game.isMoveValid(col);
				while(!validMove) {
					colDialog.setHeaderText("Please enter a different column to place your piece");
				    colDialog.showAndWait();
				    col = Integer.valueOf((String)colDialog.getSelectedItem());
					validMove = game.isMoveValid(col);
				}
				int colStart = (5*(col-1))+col;
				for(int x = 5; x >= 0; x--) {
					Shape circ = (Shape)group.getChildren().get(colStart+x);
					if(circ.getFill().toString().equalsIgnoreCase("0xffffffff")) {
						if(game.getPlayerTurn() == 'X') {
							circ.setFill(Color.RED);
						}
						else {
							circ.setFill(Color.YELLOW);
						}
						x = 0;
					}
				}
				game.placePiece(col);
			} while(!game.gameOver() && !game.isWin());
			if(game.isWin()) {
				Dialog winDialog = new Dialog();
				winDialog.setHeight(75);
				switch(game.getPlayerTurn()) {
					case 'Y':  
						winDialog.setTitle("Congratulations!!!!!");
						winDialog.setHeaderText("Congrats Player X!! You have won the game!");
						winDialog.showAndWait();
						break;
					case 'X':
						winDialog.setTitle("Congratulations!!!!!");
						winDialog.setHeaderText("Congrats Player Y!! You have won the game!");
						winDialog.showAndWait();
						break;
					default:
						break;
				}
			}
			else {
				Dialog tieDialog = new Dialog();
				tieDialog.setTitle("Congratulations!!!!!");
				tieDialog.setHeaderText("Well Played! Tied Game!");
				tieDialog.showAndWait();
			}
	    }
	    /** Builds the JavaFx GUI for a player vs CPU game */
	    else {
	    	Rectangle board = new Rectangle(5,5,575,475);
		    board.setFill(Color.BLUE);
		    board.setArcHeight(10);
		    board.setArcWidth(10);
		    Group group = new Group();
		    group.getChildren().add(board);
		    int x_cord = 50;
		    for(int x=0; x < 7; x++) {
		    	int y_cord = 50;
		    	for(int y=0; y < 6; y++) {
		    		Circle circle = new Circle(x_cord,y_cord,35,Color.WHITE);
		    		y_cord += 75;
		    		group.getChildren().add(circle);
		    	}
		    	x_cord += 80;
		    }
		    Scene scene = new Scene(group, 600, 500);
		    primaryStage.setTitle("Connect-4: Player vs. CPU"); 
		    primaryStage.setScene(scene); 
		    primaryStage.show(); 
		    Connect4ComputerPlayer cpu = new Connect4ComputerPlayer();
		    do {
				if(game.getPlayerTurn() == 'X') {
					String colOptions[] = { "1", "2", "3", "4", "5", "6", "7" }; 
				    ChoiceDialog colDialog = new ChoiceDialog(colOptions[0],colOptions);
				    colDialog.setHeaderText("It is your turn! Which column would you like to place your piece?");
				    colDialog.showAndWait();
				    int col = Integer.valueOf((String)colDialog.getSelectedItem());
					boolean validMove = game.isMoveValid(col);
					while(!validMove) {
						colDialog.setHeaderText("Please enter a different column to place your piece");
					    colDialog.showAndWait();
					    col = Integer.valueOf((String)colDialog.getSelectedItem());
						validMove = game.isMoveValid(col);
					}
					int colStart = (5*(col-1))+col;
					for(int x = 5; x >= 0; x--) {
						Shape circ = (Shape)group.getChildren().get(colStart+x);
						if(circ.getFill().toString().equalsIgnoreCase("0xffffffff")) {
							circ.setFill(Color.RED);
							x = 0;
						}
					}
					game.placePiece(col);
				}
				else {
					int col = cpu.getRandomMove();
					boolean validMove = game.isMoveValid(col);
					while(!validMove) {
						col = cpu.getRandomMove();
						validMove = game.isMoveValid(col);
					}
					int colStart = (5*(col-1))+col;
					for(int x = 5; x >= 0; x--) {
						Shape circ = (Shape)group.getChildren().get(colStart+x);
						if(circ.getFill().toString().equalsIgnoreCase("0xffffffff")) {
							circ.setFill(Color.YELLOW);
							x = 0;
						}
					}
					game.placePiece(col);
				}
			} while(!game.gameOver() && !game.isWin());
			if(game.isWin()) {
				Dialog winDialog = new Dialog();
				winDialog.setHeight(75);
				switch(game.getPlayerTurn()) {
					case 'Y':  
						winDialog.setTitle("Congratulations!!!!!");
						winDialog.setHeaderText("Congrats!! You have won the game!");
						winDialog.showAndWait();
						break;
					case 'X':
						winDialog.setTitle("Congratulations!!!!!");
						winDialog.setHeaderText("Aww man! :( The CPU beat you!");
						winDialog.showAndWait();
						break;
					default:
						break;
				}
			}
			else {
				Dialog tieDialog = new Dialog();
				tieDialog.setTitle("Congratulations!!!!!");
				tieDialog.setHeaderText("Well Played! Tied Game!");
				tieDialog.showAndWait();
			}
	    }
	}
	
	public static void main(String[] args) { 
		launch(args);
	}
}
