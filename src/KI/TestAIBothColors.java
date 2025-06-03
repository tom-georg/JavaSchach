package KI;
import Logic.Board;
import Logic.Zug;
import Schachfiguren.Schachfigur;

public class TestAIBothColors {
    public static void main(String[] args) {
        System.out.println("Testing AI moves for both colors with performance measurement...\n");
        
        // Create a new board
        Board board = new Board();
        ChessAI ai = new ChessAI(5); // Medium difficulty
        
        System.out.println("Initial board state:");
        printBoardWithCoordinates(board);

        long startTime, endTime;
        long totalWhiteTime = 0;
        long totalBlackTime = 0;

        for (int i = 0; i < 5; i++) {
            System.out.println("\n--- Turn " + (i + 1) + " ---");
            // Let AI make a move as white first
            System.out.println("\nAI (White) is thinking...");
            startTime = System.nanoTime();
            Zug whiteMove = ai.getBestMove(board, Schachfigur.WEISS);
            endTime = System.nanoTime();
            totalWhiteTime += (endTime - startTime);
            
            if (whiteMove != null) {
                System.out.println("AI (White) chose move: " + whiteMove + " (took " + (endTime - startTime) / 1_000_000 + " ms)");
                
                // Store captured piece info if any
                if (board.getFigur(whiteMove.getZielX(), whiteMove.getZielY()) != null) {
                    whiteMove.setZielFigur(board.getFigur(whiteMove.getZielX(), whiteMove.getZielY()));
                }
                
                board.makeMove(whiteMove);
                
                System.out.println("\nBoard after White's move:");
                printBoardWithCoordinates(board);
            } else {
                System.out.println("AI (White) could not find a move.");
                break; // Stop if no move found
            }
            
            // Now let AI make a move as black
            System.out.println("\nAI (Black) is thinking...");
            startTime = System.nanoTime();
            Zug blackMove = ai.getBestMove(board, Schachfigur.SCHWARZ);
            endTime = System.nanoTime();
            totalBlackTime += (endTime - startTime);
            
            if (blackMove != null) {
                System.out.println("AI (Black) chose move: " + blackMove + " (took " + (endTime - startTime) / 1_000_000 + " ms)");
                
                // Store captured piece info if any
                if (board.getFigur(blackMove.getZielX(), blackMove.getZielY()) != null) {
                    blackMove.setZielFigur(board.getFigur(blackMove.getZielX(), blackMove.getZielY()));
                }
                
                board.makeMove(blackMove);
                
                System.out.println("\nBoard after Black's move:");
                printBoardWithCoordinates(board);
                
            } else {
                System.out.println("AI (Black) could not find a move.");
                break; // Stop if no move found
            }
        }

        System.out.println("\n--- Performance Summary ---");
        System.out.println("Total time for 5 White moves: " + totalWhiteTime / 1_000_000 + " ms");
        System.out.println("Average time per White move: " + (totalWhiteTime / 5) / 1_000_000 + " ms");
        System.out.println("Total time for 5 Black moves: " + totalBlackTime / 1_000_000 + " ms");
        System.out.println("Average time per Black move: " + (totalBlackTime / 5) / 1_000_000 + " ms");

        System.out.println("\nMove history:");
        for (Zug move : board.getZugHistorie()) {
            System.out.println("  " + move);
        }
    }
    
    private static void printBoardWithCoordinates(Board board) {
        System.out.println("   a b c d e f g h");
        for (int y = 7; y >= 0; y--) {
            System.out.print((y + 1) + "  ");
            for (int x = 0; x < 8; x++) {
                if (board.getFigur(x, y) != null) {
                    System.out.print(board.getFigur(x, y).getName().charAt(0) + " ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println(" " + (y + 1));
        }
        System.out.println("   a b c d e f g h");
    }
}
