import Logic.Board;
import Logic.Zug;
import KI.ChessAI;

public class TestAIBothColors {
    public static void main(String[] args) {
        System.out.println("Testing AI moves for both colors...\n");
        
        // Create a new board
        Board board = new Board();
        ChessAI ai = new ChessAI(3); // Medium difficulty
        
        System.out.println("Initial board state:");
        printBoardWithCoordinates(board);
        
        // Let AI make a move as white first
        System.out.println("\nAI (White) is thinking...");
        Zug whiteMove = ai.getBestMove(board, "Weiss");
        
        if (whiteMove != null) {
            System.out.println("AI (White) chose move: " + whiteMove);
            
            // Store captured piece info if any
            if (board.getFigur(whiteMove.getZielX(), whiteMove.getZielY()) != null) {
                whiteMove.setZielFigur(board.getFigur(whiteMove.getZielX(), whiteMove.getZielY()));
            }
            
            board.makeMove(whiteMove);
            
            System.out.println("\nBoard after White's move:");
            printBoardWithCoordinates(board);
        }
        
        // Now let AI make a move as black
        System.out.println("\nAI (Black) is thinking...");
        Zug blackMove = ai.getBestMove(board, "Schwarz");
        
        if (blackMove != null) {
            System.out.println("AI (Black) chose move: " + blackMove);
            
            // Store captured piece info if any
            if (board.getFigur(blackMove.getZielX(), blackMove.getZielY()) != null) {
                blackMove.setZielFigur(board.getFigur(blackMove.getZielX(), blackMove.getZielY()));
            }
            
            board.makeMove(blackMove);
            
            System.out.println("\nBoard after Black's move:");
            printBoardWithCoordinates(board);
            
            System.out.println("\nMove history:");
            for (Zug move : board.getZugHistorie()) {
                System.out.println("  " + move);
            }
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
