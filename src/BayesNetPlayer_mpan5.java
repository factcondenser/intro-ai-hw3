/**
 * Bayesian network Nannon player
 * 
 * @author Mark Cui Pan
 *
 */

import java.util.List;

public class BayesNetPlayer_mpan5 extends NannonPlayer {
	
	// Fields.
	private int 	cellsOnBoard;
	private int 	piecesPerPlayer;
	
	private int		movesInWins 	= 1;
	private int		movesInLosses 	= 1;
	
	private int[][]	countGivenMoveFromToInWins;
	private int[][]	countGivenMoveFromToInLosses; 
	
	private int[][][][]		countGivenBoardEndsInWins;
	private int[][][][]		countGivenBoardEndsInLosses;
	
	private int 	countGivenHitOpponentInWins 					= 1;
    private int 	countGivenBrokeMyPrimeInWins 					= 1;
    private int 	countGivenExtendsPrimeOfMineInWins 				= 1;
    private int 	countGivenCreatesPrimeOfMineInWins 				= 1;
    private int		countGivenBrokeExtendsPrimeInWins				= 1;
    private int		countGivenBrokeCreatesPrimeInWins 				= 1;
    private int		countGivenBrokePrimeHitOpponentInWins			= 1;
    private int		countGivenExtendsPrimeHitOpponentInWins			= 1;
    private int		countGivenCreatesPrimeHitOpponentInWins 		= 1;
    private int		countGivenBrokeExtendsPrimeHitOpponentInWins 	= 1;
    private int		countGivenBrokeCreatesPrimeHitOpponentInWins 	= 1;
    
    private int 	countGivenHitOpponentInLosses 					= 1;
    private int 	countGivenBrokeMyPrimeInLosses 					= 1;
    private int 	countGivenExtendsPrimeOfMineInLosses 			= 1;
    private int 	countGivenCreatesPrimeOfMineInLosses 			= 1;
    private int		countGivenBrokeExtendsPrimeInLosses				= 1;
    private int		countGivenBrokeCreatesPrimeInLosses 			= 1;
    private int		countGivenBrokePrimeHitOpponentInLosses			= 1;
    private int		countGivenExtendsPrimeHitOpponentInLosses		= 1;
    private int		countGivenCreatesPrimeHitOpponentInLosses 		= 1;
    private int		countGivenBrokeExtendsPrimeHitOpponentInLosses 	= 1;
    private int		countGivenBrokeCreatesPrimeHitOpponentInLosses 	= 1;
    
    double[] factors;
	
	public String getPlayerName() { return "Fact_Condenser"; }
	
	// Constructors.
	public BayesNetPlayer_mpan5() {
		initialize();
		
	}
	public BayesNetPlayer_mpan5(NannonGameBoard gameBoard) {
		super(gameBoard);
		initialize();
	}
	
	private void initialize() {
		cellsOnBoard 	= NannonGameBoard.getCellsOnBoard();
		piecesPerPlayer = NannonGameBoard.getPiecesPerPlayer();
		
		countGivenMoveFromToInWins		= new int[cellsOnBoard + 1][cellsOnBoard + 1];
		countGivenMoveFromToInLosses	= new int[cellsOnBoard + 1][cellsOnBoard + 1];
		
		countGivenBoardEndsInWins 		= new int[piecesPerPlayer + 1][piecesPerPlayer + 1][piecesPerPlayer + 1][piecesPerPlayer + 1];
		countGivenBoardEndsInLosses 	= new int[piecesPerPlayer + 1][piecesPerPlayer + 1][piecesPerPlayer + 1][piecesPerPlayer + 1];
		
		for (int i = 0 ; i < 4 ; i++) {
		    for (int j = 0 ; j < 4 ; j++) {
		    	countGivenMoveFromToInWins[i][j]++;
	    		countGivenMoveFromToInLosses[i][j]++;
		    	for (int k = 0 ; k < 4 ; k++) {
		    		for (int l = 0 ; l < 4 ; l++) {
						countGivenBoardEndsInWins[i][j][k][l]++;
						countGivenBoardEndsInLosses[i][j][k][l]++;
					}
				}
			}
		}
	}

	public List<Integer> chooseMove(int[] boardConfiguration, List<List<Integer>> legalMoves) {
		
		// Below is some code you might want to use in your solution.
		//      (a) converts to zero-based counting for the cell locations
		//      (b) converts NannonGameBoard.movingFromHOME and NannonGameBoard.movingToSAFE to NannonGameBoard.cellsOnBoard,
		//          (so you could then make arrays with dimension NannonGameBoard.cellsOnBoard+1)
		//      (c) gets the current and next board configurations.
		
		// Before we start choosing, the best move is the first legal move.
		List<Integer> bestMove = legalMoves.get(0);
		// Any of the odds we calculate for the legal moves will be greater than 0.
		double bestOdds = 0;
		
		if (legalMoves != null) for (List<Integer> move : legalMoves) {

			double[] theseFactors = calculateFactors(boardConfiguration, move);
			double curOdds = theseFactors[0] * theseFactors[1] * theseFactors[2] * theseFactors[3] * theseFactors[4] * theseFactors[5] * theseFactors[6]
					* theseFactors[7] * theseFactors[8] * theseFactors[9] * theseFactors[10] * theseFactors[11] * theseFactors[12] * theseFactors[13];
			if (curOdds > bestOdds) {
				bestOdds = curOdds;
				bestMove = move;
			}
		}
		
		return bestMove;
	}

	public void updateStatistics(boolean             didIwinThisGame, 
		                         List<int[]>         allBoardConfigurationsThisGameForPlayer,
			                     List<Integer>       allCountsOfPossibleMovesForPlayer,
			                     List<List<Integer>> allMovesThisGameForPlayer) {
		
		int numberOfMyMovesThisGame = allBoardConfigurationsThisGameForPlayer.size();	
			
		for (int myMove = 0; myMove < numberOfMyMovesThisGame; myMove++) {
			int[]         currentBoard        = allBoardConfigurationsThisGameForPlayer.get(myMove);
			int           numberPossibleMoves = allCountsOfPossibleMovesForPlayer.get(myMove);
			List<Integer> moveChosen          = allMovesThisGameForPlayer.get(myMove);
			int[]         resultingBoard      = (numberPossibleMoves < 1 ? currentBoard // No move possible, so board is unchanged.
						                                                     : gameBoard.getNextBoardConfiguration(currentBoard, moveChosen));
				
			// You should compute the statistics needed for a Bayes Net for any of these problem formulations:
			//
			//     prob(win | resultingBoard and chosenMove's Effects)               <--- condition on the board produced and also on the important changes from the prev board
			//	   prob(resultingBoard and chosenMove's Effects | win) * prob(win)
			//	   --------------------------------------------------------------
			//	   prob(resultingBoard and chosenMove's Effects)					 <--- fuck the denominator
			//     [prob(resultingBoard | win) * prob(chosenMove's Effects | win)] * prob(win)
			//	   p(A | B) = p(A ^ B) / p(B)
			//	   [p(resultingBoard ^ win) / p(win) * p(effects ^ win) / p(win)] * p(win)
			//	   [p(resultingBoard ^ win) / p(win) * p(hitOpponent ^ win) / p(win) * p(brokeMyPrime ^ win) / p(win) * p(extendsPrimeOfMine ^ win) / p(win) * p(createsPrimeOfMine ^ win) / p(win)] * p(win)
			//	   [countGivenBoardEndsInWins / movesInWins * countGivenMoveFromToInWins / movesInWins * countGivenHitOpponentInWins / movesInWins * countGivenBrokeMyPrimeInWins / movesInWins * countGivenExtendsPrimeOfMineInWins / movesInWins * countGivenCreatesPrimeOfMineInWins / movesInWins] * movesInWins / (movesInWins + movesInLosses)
			
			if (numberPossibleMoves < 1) { continue; } // If NO moves possible, nothing to learn from (it is up to you if you want to learn for cases where there is a FORCED move, ie only one possible move).
	
			// Convert to our internal count-from-zero system.
			// A move is a list of three integers.  Their meanings should be clear from the variable names below.
			int fromCountingFromOne = moveChosen.get(0);  // Convert below to an internal count-from-zero system.
			int   toCountingFromOne = moveChosen.get(1);
			int              effect = moveChosen.get(2);  // See ManageMoveEffects.java for the possible values that can appear here. Also see the four booleans below.

			// Note we use 0 for both 'from' and 'to' because one can never move FROM SAFETY or TO HOME, so we save a memory cell.
			int from = (fromCountingFromOne == NannonGameBoard.movingFromHOME ? 0 : fromCountingFromOne);
			int to   = (toCountingFromOne   == NannonGameBoard.movingToSAFETY ? 0 : toCountingFromOne);
				
			// The 'effect' of move is encoded in these four booleans:
		    boolean        hitOpponent = ManageMoveEffects.isaHit(      effect); // Explained in chooseMove() above.
		    boolean       brokeMyPrime = ManageMoveEffects.breaksPrime( effect);
		    boolean extendsPrimeOfMine = ManageMoveEffects.extendsPrime(effect);
		    boolean createsPrimeOfMine = ManageMoveEffects.createsPrime(effect);
			
			// DO SOMETHING HERE.
		    if (didIwinThisGame) {
		    	if (hitOpponent && !brokeMyPrime && !extendsPrimeOfMine && !createsPrimeOfMine) { countGivenHitOpponentInWins++; }
		    	if (!hitOpponent && brokeMyPrime && !extendsPrimeOfMine && !createsPrimeOfMine) { countGivenBrokeMyPrimeInWins++; }
		    	if (!hitOpponent && !brokeMyPrime && extendsPrimeOfMine && !createsPrimeOfMine) { countGivenExtendsPrimeOfMineInWins++; }
		    	if (!hitOpponent && !brokeMyPrime && !extendsPrimeOfMine && createsPrimeOfMine) { countGivenCreatesPrimeOfMineInWins++; }
		    	if (!hitOpponent && brokeMyPrime && extendsPrimeOfMine && !createsPrimeOfMine) 	{ countGivenBrokeExtendsPrimeInWins++; }
		    	if (!hitOpponent && brokeMyPrime && !extendsPrimeOfMine && createsPrimeOfMine) 	{ countGivenBrokeCreatesPrimeInWins++; }
		    	if (hitOpponent && brokeMyPrime && !extendsPrimeOfMine && !createsPrimeOfMine) 	{ countGivenBrokePrimeHitOpponentInWins++; }
		    	if (hitOpponent && !brokeMyPrime && extendsPrimeOfMine && !createsPrimeOfMine) 	{ countGivenExtendsPrimeHitOpponentInWins++; }
		    	if (hitOpponent && !brokeMyPrime && !extendsPrimeOfMine && createsPrimeOfMine) 	{ countGivenCreatesPrimeHitOpponentInWins++; }
		    	if (hitOpponent && brokeMyPrime && extendsPrimeOfMine && !createsPrimeOfMine) 	{countGivenBrokeExtendsPrimeHitOpponentInWins++; }
		    	if (hitOpponent && brokeMyPrime && !extendsPrimeOfMine && createsPrimeOfMine) 	{countGivenBrokeCreatesPrimeHitOpponentInWins++; }
		        countGivenMoveFromToInWins[from][to]++;
		        countGivenBoardEndsInWins[resultingBoard[1]][resultingBoard[2]][resultingBoard[3]][resultingBoard[4]]++;
		        movesInWins++;
		    } else {
		    	if (hitOpponent && !brokeMyPrime && !extendsPrimeOfMine && !createsPrimeOfMine) { countGivenHitOpponentInLosses++; }
		    	if (!hitOpponent && brokeMyPrime && !extendsPrimeOfMine && !createsPrimeOfMine) { countGivenBrokeMyPrimeInLosses++; }
		    	if (!hitOpponent && !brokeMyPrime && extendsPrimeOfMine && !createsPrimeOfMine) { countGivenExtendsPrimeOfMineInLosses++; }
		    	if (!hitOpponent && !brokeMyPrime && !extendsPrimeOfMine && createsPrimeOfMine) { countGivenCreatesPrimeOfMineInLosses++; }
		    	if (!hitOpponent && brokeMyPrime && extendsPrimeOfMine && !createsPrimeOfMine) 	{ countGivenBrokeExtendsPrimeInLosses++; }
		    	if (!hitOpponent && brokeMyPrime && !extendsPrimeOfMine && createsPrimeOfMine) 	{ countGivenBrokeCreatesPrimeInLosses++; }
		    	if (hitOpponent && brokeMyPrime && !extendsPrimeOfMine && !createsPrimeOfMine) 	{ countGivenBrokePrimeHitOpponentInLosses++; }
		    	if (hitOpponent && !brokeMyPrime && extendsPrimeOfMine && !createsPrimeOfMine) 	{ countGivenExtendsPrimeHitOpponentInLosses++; }
		    	if (hitOpponent && !brokeMyPrime && !extendsPrimeOfMine && createsPrimeOfMine) 	{ countGivenCreatesPrimeHitOpponentInLosses++; }
		    	if (hitOpponent && brokeMyPrime && extendsPrimeOfMine && !createsPrimeOfMine) 	{countGivenBrokeExtendsPrimeHitOpponentInLosses++; }
		    	if (hitOpponent && brokeMyPrime && !extendsPrimeOfMine && createsPrimeOfMine) 	{countGivenBrokeCreatesPrimeHitOpponentInLosses++; }
			    countGivenMoveFromToInLosses[from][to]++;
			    countGivenBoardEndsInLosses[resultingBoard[1]][resultingBoard[2]][resultingBoard[3]][resultingBoard[4]]++;
			    movesInLosses++;
			}     
		}
	}
	
	public void reportLearnedModel() {
		
		double largestFactor = factors[0];
		double smallestFactor = factors[0];
		
		for (int i = 1 ; i < factors.length ; i++) {
			if (factors[i] > largestFactor) { largestFactor = factors[i]; }
			else if (factors[i] < smallestFactor) { smallestFactor = factors[i]; }		
		}
		
		Utils.println("\n-------------------------------------------------");
		Utils.println("\nI (" + getPlayerName() + ") learned that the weights of all the considered factors are as follows:");
		Utils.println("\nboardEndsFactor: " + factors[0]);
		Utils.println("moveFromToFactor: " + factors[1]);
		Utils.println("hitOpponentFactor: " + factors[2]);
		Utils.println("brokeMyPrimeFactor: " + factors[3]);
		Utils.println("extendsPrimeOfMineFactor: " + factors[4]);
		Utils.println("createsPrimeOfMineFactor: " + factors[5]);
		Utils.println("brokeExtendsPrimeFactor: " + factors[6]);
		Utils.println("brokeCreatesPrimeFactor: " + factors[7]);
		Utils.println("brokePrimeHitOpponentFactor: " + factors[8]);
		Utils.println("extendsPrimeHitOpponentFactor: " + factors[9]);
		Utils.println("createsPrimeHitOpponentFactor: " + factors[10]);
		Utils.println("brokeExtendsPrimeHitOpponentFactor: " + factors[11]);
		Utils.println("brokeCreatesPrimeHitOpponentFactor: " + factors[12]);
		Utils.println("movesInWinsFactor: " + factors[13]);
		Utils.println("\n-------------------------------------------------");
	}
	
	private double[] calculateFactors(int[] boardConfiguration, List<Integer> move) {
		
		int fromCountingFromOne    = move.get(0);  // Convert below to an internal count-from-zero system.
		int   toCountingFromOne    = move.get(1);			
		int              effect	   = move.get(2);  // See ManageMoveEffects.java for the possible values that can appear here.	
		
		// Note we use 0 for both 'from' and 'to' because one can never move FROM SAFETY or TO HOME, so we save a memory cell.
		int from = (fromCountingFromOne == NannonGameBoard.movingFromHOME ? 0 : fromCountingFromOne);
		int to   = (toCountingFromOne   == NannonGameBoard.movingToSAFETY ? 0 : toCountingFromOne);
		
		// The 'effect' of move is encoded in these four booleans:
	    boolean        hitOpponent = ManageMoveEffects.isaHit(      effect);  // Did this move 'land' on an opponent (sending it back to HOME)?
	    boolean       brokeMyPrime = ManageMoveEffects.breaksPrime( effect);  // A 'prime' is when two pieces from the same player are adjacent on the board;
	                                                                          // an opponent can NOT land on pieces that are 'prime' - so breaking up a prime of 
	                                                                          // might be a bad idea.
	    boolean extendsPrimeOfMine = ManageMoveEffects.extendsPrime(effect);  // Did this move lengthen (i.e., extend) an existing prime?
	    boolean createsPrimeOfMine = ManageMoveEffects.createsPrime(effect);  // Did this move CREATE a NEW prime? (A move cannot both extend and create a prime.)
	    
		int[] resultingBoard = gameBoard.getNextBoardConfiguration(boardConfiguration, move);  // You might choose NOT to use this - see updateStatistics().
		
		/* Here is what is in a board configuration vector.  There are also accessor functions in NannonGameBoard.java (starts at or around line 60).
		 
		   	boardConfiguration[0] = whoseTurn;        // Ignore, since it is OUR TURN when we play, by definition. (But needed to compute getNextBoardConfiguration.)
    		boardConfiguration[1] = homePieces_playerX; 
    		boardConfiguration[2] = homePieces_playerO;
    		boardConfiguration[3] = safePieces_playerX;
    		boardConfiguration[4] = safePieces_playerO;
    		boardConfiguration[5] = die_playerX;      // I added these early on, but never used them.
    		boardConfiguration[6] = die_playerO;      // Probably can be ignored since get the number of legal moves, which is more meaningful.
   
    		cells 7 to (6 + NannonGameBoard.cellsOnBoard) record what is on the board at each 'cell' (ie, board location).
    					- one of NannonGameBoard.playerX, NannonGameBoard.playerO, or NannonGameBoard.empty.
    		
		 */

		double boardEndsFactor			 			= (countGivenBoardEndsInWins[resultingBoard[1]][resultingBoard[2]][resultingBoard[3]][resultingBoard[4]] / (double) movesInWins) 
															/ (double) (countGivenBoardEndsInLosses[resultingBoard[1]][resultingBoard[2]][resultingBoard[3]][resultingBoard[4]] / (double) movesInLosses);
		double moveFromToFactor						= (countGivenMoveFromToInWins[from][to] / (double) movesInWins) 
															/ (double) (countGivenMoveFromToInLosses[from][to] / (double) movesInLosses);
		double hitOpponentFactor 					= (hitOpponent && !brokeMyPrime && !extendsPrimeOfMine && !createsPrimeOfMine ? (countGivenHitOpponentInWins / (double) movesInWins)
															/ (double) (countGivenHitOpponentInLosses / (double) movesInLosses) : 1);
		double brokeMyPrimeFactor 					= (!hitOpponent && brokeMyPrime && !extendsPrimeOfMine && !createsPrimeOfMine ? (countGivenBrokeMyPrimeInWins / (double) movesInWins)
															/ (double) (countGivenBrokeMyPrimeInLosses / (double) movesInLosses) : 1);
		double extendsPrimeOfMineFactor 			= (!hitOpponent && !brokeMyPrime && extendsPrimeOfMine && !createsPrimeOfMine ? (countGivenExtendsPrimeOfMineInWins / (double) movesInWins)
															/ (double) (countGivenExtendsPrimeOfMineInLosses / (double) movesInLosses) : 1);
		double createsPrimeOfMineFactor 			= (!hitOpponent && !brokeMyPrime && !extendsPrimeOfMine && createsPrimeOfMine ? (countGivenCreatesPrimeOfMineInWins / (double) movesInWins)
															/ (double) (countGivenCreatesPrimeOfMineInLosses / (double) movesInLosses) : 1);
		double brokeExtendsPrimeFactor				= (!hitOpponent && brokeMyPrime && extendsPrimeOfMine && !createsPrimeOfMine ? (countGivenBrokeExtendsPrimeInWins / (double) movesInWins) 
															/ (double) (countGivenBrokeExtendsPrimeInLosses / (double) movesInLosses) : 1);
		double brokeCreatesPrimeFactor				= (!hitOpponent && brokeMyPrime && !extendsPrimeOfMine && createsPrimeOfMine ? (countGivenBrokeCreatesPrimeInWins / (double) movesInWins) 
															/ (double) (countGivenBrokeCreatesPrimeInLosses / (double) movesInLosses) : 1);
		double brokePrimeHitOpponentFactor			= (hitOpponent && brokeMyPrime && !extendsPrimeOfMine && !createsPrimeOfMine ? (countGivenBrokePrimeHitOpponentInWins / (double) movesInWins) 
															/ (double) (countGivenBrokePrimeHitOpponentInLosses / (double) movesInLosses) : 1);
		double extendsPrimeHitOpponentFactor		= (hitOpponent && !brokeMyPrime && extendsPrimeOfMine && !createsPrimeOfMine ? (countGivenExtendsPrimeHitOpponentInWins / (double) movesInWins) 
															/ (double) (countGivenExtendsPrimeHitOpponentInLosses / (double) movesInLosses) : 1);
		double createsPrimeHitOpponentFactor		= (hitOpponent && !brokeMyPrime && !extendsPrimeOfMine && createsPrimeOfMine ? (countGivenCreatesPrimeHitOpponentInWins  / (double) movesInWins) 
															/ (double) (countGivenCreatesPrimeHitOpponentInLosses / (double) movesInLosses) : 1);
		double brokeExtendsPrimeHitOpponentFactor	= (hitOpponent && brokeMyPrime && extendsPrimeOfMine && !createsPrimeOfMine ? (countGivenBrokeExtendsPrimeHitOpponentInWins / (double) movesInWins) 
															/ (double) (countGivenBrokeExtendsPrimeHitOpponentInLosses / (double) movesInLosses) : 1);
		double brokeCreatesPrimeHitOpponentFactor	= (hitOpponent && brokeMyPrime && !extendsPrimeOfMine && createsPrimeOfMine ? (countGivenBrokeCreatesPrimeHitOpponentInWins / (double) movesInWins) 
															/ (double) (countGivenBrokeCreatesPrimeHitOpponentInLosses / (double) movesInLosses) : 1);
		// Technically, both numerator and denominator should be over "(double) (movesInWins + movesInLosses)" here, but these just end up canceling anyway.
		double movesInWinsFactor					= movesInWins / (double) movesInLosses;
		
		double[] theseFactors = {boardEndsFactor, moveFromToFactor, hitOpponentFactor, brokeMyPrimeFactor, extendsPrimeOfMineFactor, createsPrimeOfMineFactor, 
				brokeExtendsPrimeFactor, brokeCreatesPrimeFactor, brokePrimeHitOpponentFactor, extendsPrimeHitOpponentFactor, createsPrimeHitOpponentFactor, 
				brokeExtendsPrimeHitOpponentFactor, brokeCreatesPrimeHitOpponentFactor, movesInWinsFactor};
		
		factors = theseFactors;
		
		return theseFactors;
	}
}
