package KI;

import Logic.Board;
import Logic.Zug;

/**
 * Interface for AI players in the chess game.
 */
public interface AIPlayer {
    
    /**
     * Calculates the best move for the given board position.
     * 
     * @param board The current board state
     * @param color The color of the AI player ("Weiss" or "Schwarz")
     * @return The best move found by the AI
     */
    //Zug getBestMove(Board board, String color);

    Zug getBestMove(Board board, boolean color);
    
    /**
     * Gets the difficulty level of the AI.
     * 
     * @return 
     */
    int getDifficulty();
    
    /**
     * Sets the difficulty level of the AI.
     * 
     * @param difficulty The difficulty level (1-5)
     */
    void setDifficulty(int difficulty);
}
