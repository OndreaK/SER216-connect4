package core;

import java.util.Random;

/**
 * Connect4ComputerPlayer class handles all the logic needed to generate moves for a CPU-based player.
 * 
 * @author Ondrea Kee 
 * @version 1.0 April 6, 2020
 *
 */
public class Connect4ComputerPlayer {
	
	/**
	 * Empty default constructor - no needed variable initializations at this time
	 */
	public Connect4ComputerPlayer() {
		
	}

	/**
	 * @return int - returns the randomly generated column number
	 * Method that utilizes the Random class to randomly select a column to place the CPU piece
	 */
	public int getRandomMove() {
		Random rand = new Random();
		return rand.nextInt(6)+1;
	}
}
