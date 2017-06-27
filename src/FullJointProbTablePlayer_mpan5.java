/**
 * Copyrighted 2013 by Jude Shavlik.  Maybe be freely used for non-profit educational purposes.
 */

///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  PlayNannon.java
// File:             FullJointProbTablePlayer_mpan5.java
// Semester:         CS540 Fall 2014
//
// Author:           Mark Pan mpan5@wisc.edu
// CS Login:         mpan
// Lecturer's Name:  Jude Shavlik
//
//////////////////////////// 80 columns wide ///////////////////////////////////

import java.util.List;

/**
 * A player that calculates an odds ratio using a full joint probability table
 * in order to choose, from among all possible moves, the move most likely to 
 * result in an eventual win.
 *
 * @author Mark Pan
 */
public class FullJointProbTablePlayer_mpan5 extends NannonPlayer {

	// Create two 10-dimensional arrays (one for moves in wins and one for moves in losses) 
	// containing HOME_X, HOME_Y, START_X, START_Y, plus SIX cells (the number of cells on the board).
	// The first four dimensions have 4 possible values: 0 pieces, 1 piece, 2 pieces, or 3 pieces.
	private int[][][][][][][][][][] countGivenMoveInWin 	= new int[4][4][4][4][3][3][3][3][3][3];
	private int[][][][][][][][][][] countGivenMoveInLoss 	= new int[4][4][4][4][3][3][3][3][3][3];
	
	public String getPlayerName() { return "mpan5's FullJointProbTable Player"; }
    
	// Constructors.
	public FullJointProbTablePlayer_mpan5() {
		initialize();
		
	}
	public FullJointProbTablePlayer_mpan5(NannonGameBoard gameBoard) {
		super(gameBoard);
		initialize();
	}
	
	private void initialize() {
		// Put a 1 in every cell of the 10-dimensional arrays to avoid zero probabilities.
		for (int i = 0 ; i < 4 ; i++) {
		    for (int j = 0 ; j < 4 ; j++) {
		    	for (int k = 0 ; k < 4 ; k++) {
		    		for (int l = 0 ; l < 4 ; l++) {
		    			for (int m = 0 ; m < 3 ; m++) {
		    				for (int n = 0 ; n < 3 ; n++) {
		    					for (int o = 0 ; o < 3 ; o++) {
		    						for (int p = 0 ; p < 3 ; p++) {
		    							for (int q = 0 ; q < 3 ; q++) {
		    								for (int r = 0 ; r < 3 ; r++) {
		    									countGivenMoveInWin[i][j][k][l][m][n][o][p][q][r]++;
		    									countGivenMoveInLoss[i][j][k][l][m][n][o][p][q][r]++;
		    								}
		    							}
		    						}
		   						}
		    				}
		    			}
		    		}
		    	}
		    }	
		}
	}
    
	/**
	 * Chooses the move most likely to result in an eventual win
	 * according to an odds ratio calculated from a learned full 
	 * joint probability table.
	 *
	 * @param boardConfiguration An int[] representing the current board configuration
	 * @param legalMoves A list of legal moves that the player can make
	 * @return a List<Integer> representing the chosen move
	 */
	public List<Integer> chooseMove(int[] boardConfiguration, List<List<Integer>> legalMoves) {
		// Before we start choosing, the best move is the first legal move.
		List<Integer> bestMove = legalMoves.get(0);
		// Any of the odds we calculate for the legal moves will be greater than 0.
		double bestOdds = 0;
		
		if (legalMoves != null) for (List<Integer> move : legalMoves) {
			// The board configuration resulting from carrying out a possible 
			// move is what will be used to determine its goodness.
			int[] resultingBoard = gameBoard.getNextBoardConfiguration(boardConfiguration, move);
			
			/* Here is what is in a board configuration vector.
			 	boardConfiguration[0] = whoseTurn;        // Ignore, since it is OUR TURN when we play, by definition.
        		boardConfiguration[1] = homePieces_playerX; 
        		boardConfiguration[2] = homePieces_playerO;
        		boardConfiguration[3] = safePieces_playerX;
        		boardConfiguration[4] = safePieces_playerO;
        		boardConfiguration[5] = die_playerX;
        		boardConfiguration[6] = die_playerO;      // Probably can be ignored since get the number of legal moves, which is more meaningful.
       
        		cells 7 to (6 + NannonGameBoard.cellsOnBoard) record what is on the board at each 'cell' (ie, board location).
        					- one of NannonGameBoard.playerX, NannonGameBoard.playerO, or NannonGameBoard.empty.

				Below is the ODDs of (eventually) winning the game, if entering the 
				board configuration that results from the carrying out the current possible move.
				'win' here is shorthand for "moveInWin" and 'loss' for "moveInLoss"

			    (countGivenMoveInWin[10-dimensional board configuration] / movesInWins) x movesInWins / (movesInWins + movesInLosses)
				--------------------------------------------------------------------------------------------------------------------------
			    (countGivenMoveInLoss[10-dimensional board configuration] / movesInLosses) x movesInLosses / (movesInWins + movesInLosses)
			    
			    This simplifies to:
			    
			    countGivenMoveInWin[10-dimensional board configuration]
			    --------------------------------------------------------
			    countGivenMoveInLoss[10-dimensional board configuration]
			    
			*/
			double curOdds = countGivenMoveInWin[resultingBoard[1]][resultingBoard[2]][resultingBoard[3]][resultingBoard[4]][resultingBoard[7]][resultingBoard[8]][resultingBoard[9]][resultingBoard[10]][resultingBoard[11]][resultingBoard[12]]
					/ (double) countGivenMoveInLoss[resultingBoard[1]][resultingBoard[2]][resultingBoard[3]][resultingBoard[4]][resultingBoard[7]][resultingBoard[8]][resultingBoard[9]][resultingBoard[10]][resultingBoard[11]][resultingBoard[12]];
			if (curOdds > bestOdds) {
				bestOdds = curOdds;
				bestMove = move;
			}
		}
		return bestMove;
	}

	/**
	 * Updates the counts in the full joint probability table
	 * depending on the board configuration and whether that board
	 * configuration was present in a win or a loss.
	 *
	 * @param didIwinThisGame True if win, false otherwise
	 * @param allBoardConfigurationsThisGameForPlayer self-explanatory
	 * @param allCountsOfPossibleMovesForPlayer self-explanatory
	 * @param allMovesThisGameForPlayer self-explanatory
	 * @return void
	 */
	public void updateStatistics(boolean             didIwinThisGame, 
		                         List<int[]>         allBoardConfigurationsThisGameForPlayer,
			                     List<Integer>       allCountsOfPossibleMovesForPlayer,
			                     List<List<Integer>> allMovesThisGameForPlayer) {
		
		int numberOfMyMovesThisGame = allBoardConfigurationsThisGameForPlayer.size();	
		
		for (int myMove = 0; myMove < numberOfMyMovesThisGame; myMove++) {
			int[]         currentBoard        = allBoardConfigurationsThisGameForPlayer.get(myMove);
			int           numberPossibleMoves = allCountsOfPossibleMovesForPlayer.get(myMove);
			
			if (numberPossibleMoves < 1) { continue; } // If NO moves possible, nothing to learn from (it is up to you if you want to learn for cases where there is a FORCED move, ie only one possible move).				
			
			// Map each "world state" to a cell in our ten dimensional array, then increment the count in that cell.
		    // See chooseMove() for an explanation of what is stored in currentBoard and resultingBoard.
		    if (didIwinThisGame) {
		    	countGivenMoveInWin[currentBoard[1]][currentBoard[2]][currentBoard[3]][currentBoard[4]][currentBoard[7]][currentBoard[8]][currentBoard[9]][currentBoard[10]][currentBoard[11]][currentBoard[12]]++;
		    } else { // Do same thing for "InLoss" counters if this game was lost.
		    	countGivenMoveInLoss[currentBoard[1]][currentBoard[2]][currentBoard[3]][currentBoard[4]][currentBoard[7]][currentBoard[8]][currentBoard[9]][currentBoard[10]][currentBoard[11]][currentBoard[12]]++;
		    }		
		}
	}

	/**
	 * Reports the world state in the full joint probability table most likely to
	 * result in an eventual win, as well as the one most likely to result in an
	 * eventual loss.
	 * 
	 * @return void
	 */
	public void reportLearnedModel() {
		
		int[] bestWorldState = new int[10];
		int[] worstWorldState = new int[10];
		// Any of the odds we calculate for the possible world states can't be worse than 0 or better than 1.
		double bestOdds = 0;
		double worstOdds = 1;
		
		for (int i = 0 ; i < 4 ; i++) {
		    for (int j = 0 ; j < 4 ; j++) {
		    	for (int k = 0 ; k < 4 ; k++) {
		    		for (int l = 0 ; l < 4 ; l++) {
		    			for (int m = 0 ; m < 3 ; m++) {
		    				for (int n = 0 ; n < 3 ; n++) {
		    					for (int o = 0 ; o < 3 ; o++) {
		    						for (int p = 0 ; p < 3 ; p++) {
		    							for (int q = 0 ; q < 3 ; q++) {
		    								for (int r = 0 ; r < 3 ; r++) {
		    									double curOdds = countGivenMoveInWin[i][j][k][l][m][n][o][p][q][r]
		    									/ (double) countGivenMoveInLoss[i][j][k][l][m][n][o][p][q][r];
		    									if (curOdds > bestOdds) {
		    										bestOdds = curOdds;
		    										bestWorldState = new int[] {i, j, k, l, m, n, o, p, q, r};
		    									}
		    									if (curOdds < worstOdds) {
		    										worstOdds = curOdds;
		    										worstWorldState = new int[] {i, j, k, l, m, n, o, p, q, r};
		    									}
		    								}
		    							}
		    						}
		   						}
		    				}
		    			}
		    		}
		    	}
		    }	
		}
		
		Utils.println("\n-------------------------------------------------");
		Utils.println("\nI (" + getPlayerName() + ") learned that the world state most likely to result in an eventual win is as follows:");
		Utils.println("\n# of Pieces in HOME for X: " + bestWorldState[0]);
		Utils.println("# of Pieces in HOME for Y: " + bestWorldState[1]);
		Utils.println("# of Pieces in SAFE for X: " + bestWorldState[2]);
		Utils.println("# of Pieces in SAFE for Y: " + bestWorldState[3]);
		Utils.println("Status of Board Cell 1: " + bestWorldState[4]);
		Utils.println("Status of Board Cell 2: " + bestWorldState[5]);
		Utils.println("Status of Board Cell 3: " + bestWorldState[6]);
		Utils.println("Status of Board Cell 4: " + bestWorldState[7]);
		Utils.println("Status of Board Cell 5: " + bestWorldState[8]);
		Utils.println("Status of Board Cell 6: " + bestWorldState[9]);
		Utils.println("\nwhere board cell status '0' means the cell is empty, board cell status '1' means X has a piece there and board cell status '2' means Y has a piece there.");
		Utils.println("\nI (" + getPlayerName() + ") also learned that the world state most likely to result in an eventual loss is as follows:");
		Utils.println("\n# of Pieces in HOME for X: " + worstWorldState[0]);
		Utils.println("# of Pieces in HOME for Y: " + worstWorldState[1]);
		Utils.println("# of Pieces in SAFE for X: " + worstWorldState[2]);
		Utils.println("# of Pieces in SAFE for Y: " + worstWorldState[3]);
		Utils.println("Status of Board Cell 1: " + worstWorldState[4]);
		Utils.println("Status of Board Cell 2: " + worstWorldState[5]);
		Utils.println("Status of Board Cell 3: " + worstWorldState[6]);
		Utils.println("Status of Board Cell 4: " + worstWorldState[7]);
		Utils.println("Status of Board Cell 5: " + worstWorldState[8]);
		Utils.println("Status of Board Cell 6: " + worstWorldState[9]);
		Utils.println("\nwhere, again, board cell status '0' means the cell is empty, board cell status '1' means X has a piece there and board cell status '2' means Y has a piece there.");
		Utils.println("\n-------------------------------------------------");
	}
}
