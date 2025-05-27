import KI.ChessAI;
import Logic.Board;
import Logic.Zug;

/**
 * Simple test class to verify AI functionality
 */
public class TestAI {
    public static void main(String[] args) {
        System.out.println("Testing Chess AI...");
        
        // Create a new board and AI
        Board board = new Board();
        ChessAI ai = new ChessAI(3); // Medium difficulty
        
        System.out.println("Initial board position:");
        System.out.println(board.toString());
        
        // Test AI move for white
        System.out.println("\nGetting AI move for White...");
        Zug aiMove = ai.getBestMove(board, "Weiss");
        
        if (aiMove != null) {
            System.out.println("AI suggests move: " + aiMove.toString());
            
            // Test different difficulty levels
            for (int difficulty = 1; difficulty <= 5; difficulty++) {
                ai.setDifficulty(difficulty);
                Zug move = ai.getBestMove(board, "Weiss");
                System.out.println("Difficulty " + difficulty + " suggests: " + 
                    (move != null ? move.toString() : "No move"));
            }
        } else {
            System.out.println("AI returned no move!");
        }
        
        System.out.println("\nAI test completed successfully!");
    }
}
