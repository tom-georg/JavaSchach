package KI;
import Logic.Board;
import Logic.Zug;

/**
 * Test class to verify that the AI can handle high difficulty levels
 */
public class TestHighDifficulty {
    public static void main(String[] args) {
        System.out.println("Testing high difficulty levels...\n");
        
        Board board = new Board();
        
        // Test different high difficulty levels
        int[] testDifficulties = {5, 8, 10, 12, 15, 20}; // Including levels beyond 15
        
        for (int difficulty : testDifficulties) {
            System.out.println("Testing difficulty level " + difficulty + "...");
            
            ChessAI ai = new ChessAI(difficulty);
            System.out.println("AI difficulty set to: " + ai.getDifficulty());
            
            // Measure time for AI move
            long startTime = System.currentTimeMillis();
            Zug move = ai.getBestMove(board, "Weiss");
            long endTime = System.currentTimeMillis();
            
            if (move != null) {
                System.out.println("AI found move: " + move);
                System.out.println("Time taken: " + (endTime - startTime) + "ms");
            } else {
                System.out.println("No move found!");
            }
            
            System.out.println("---");
        }
        
        System.out.println("High difficulty test completed!");
    }
}
