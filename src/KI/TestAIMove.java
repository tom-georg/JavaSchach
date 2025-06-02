package KI;
import Logic.Board;
import Logic.Zug;
import Schachfiguren.Schachfigur;

public class TestAIMove {
    public static void main(String[] args) {
        System.out.println("Testing AI move functionality...\n");
        
        // Create a new board
        Board board = new Board();
        ChessAI ai = new ChessAI(3); // Medium difficulty
        
        System.out.println("Initial board state:");
        System.out.println(board.toString());
        
        // Let AI make a move as black
        System.out.println("AI (Black) is thinking...");
        Zug aiMove = ai.getBestMove(board, Schachfigur.SCHWARZ);
        
        if (aiMove != null) {
            System.out.println("AI chose move: " + aiMove);
            
            // Store captured piece info if any
            if (board.getFigur(aiMove.getZielX(), aiMove.getZielY()) != null) {
                aiMove.setZielFigur(board.getFigur(aiMove.getZielX(), aiMove.getZielY()));
            }
            
            board.makeMove(aiMove);
            
            System.out.println("\nBoard after AI move:");
            System.out.println(board.toString());
            
            System.out.println("Move history:");
            System.out.println(board.getZugHistorie().toString());
        } else {
            System.out.println("AI could not find a valid move!");
        }
        
        // Test undo functionality
        System.out.println("\nTesting undo...");
        board.undoLastMove();
        System.out.println("Board after undo:");
        System.out.println(board.toString());
    }
}
