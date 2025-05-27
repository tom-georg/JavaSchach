package KI;

import Logic.Board;
import Schachfiguren.*;

/**
 * Evaluates chess board positions for the AI.
 */
public class BoardEvaluator {
    
    private static final int CHECKMATE_VALUE = 10000;
    private static final int STALEMATE_VALUE = 0;
    
    /**
     * Evaluates the current board position from the perspective of the given color.
     * Positive values favor the given color, negative values favor the opponent.
     * 
     * @param board The board to evaluate
     * @param color The color to evaluate for ("Weiss" or "Schwarz")
     * @return The evaluation score
     */
    public static int evaluateBoard(Board board, String color) {
        int materialScore = calculateMaterialScore(board, color);
        int positionalScore = calculatePositionalScore(board, color);
        int mobilityScore = calculateMobilityScore(board, color);
        
        return materialScore + positionalScore + mobilityScore;
    }
    
    /**
     * Calculates the material advantage for the given color.
     */
    private static int calculateMaterialScore(Board board, String color) {
        int score = 0;
        
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Schachfigur figur = board.getFigur(x, y);
                if (figur != null) {
                    int pieceValue = figur.getWert();
                    if (figur.getFarbe().equals(color)) {
                        score += pieceValue;
                    } else {
                        score -= pieceValue;
                    }
                }
            }
        }
        
        return score * 100; // Scale material advantage
    }
    
    /**
     * Calculates positional advantages based on piece-square tables.
     */
    private static int calculatePositionalScore(Board board, String color) {
        int score = 0;
        
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Schachfigur figur = board.getFigur(x, y);
                if (figur != null) {
                    int positionalValue = getPositionalValue(figur, x, y);
                    
                    if (figur.getFarbe().equals(color)) {
                        score += positionalValue;
                    } else {
                        score -= positionalValue;
                    }
                }
            }
        }
        
        return score;
    }
    
    /**
     * Gets the positional value of a piece at a given position.
     */
    private static int getPositionalValue(Schachfigur figur, int x, int y) {
        // Adjust coordinates for black pieces (flip the board)
        int adjustedY = figur.getFarbe().equals("Schwarz") ? 7 - y : y;
        
        try {
            switch (figur.getName()) {
                case "Bauer":
                    return getPawnPositionalValue(x, adjustedY);
                case "Springer":
                    return MapWeights.WEIGHTS_KNIGHT[adjustedY][x];
                case "Laeufer":
                    return MapWeights.WEIGHTS_BISHOP[adjustedY][x];
                case "Turm":
                    return MapWeights.WEIGHTS_ROOK[adjustedY][x];
                case "Dame":
                    return MapWeights.WEIGHTS_QUEEN[adjustedY][x];
                case "Koenig":
                    return getKingPositionalValue(x, adjustedY);
                default:
                    return 0;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0; // Safety fallback
        }
    }
    
    /**
     * Gets positional value for pawns with special considerations.
     */
    private static int getPawnPositionalValue(int x, int y) {
        // Basic pawn advancement bonus
        int baseValue = y * 10;
        
        // Center pawns are more valuable
        if (x >= 2 && x <= 5) {
            baseValue += 5;
        }
        
        // Advanced pawns become very valuable
        if (y >= 6) {
            baseValue += 50;
        }
        
        return baseValue;
    }
    
    /**
     * Gets positional value for the king with safety considerations.
     */
    private static int getKingPositionalValue(int x, int y) {
        // Early game: king safety (stay in back rank)
        if (y <= 1) {
            // Prefer corners and sides for castling
            if (x <= 2 || x >= 5) {
                return 30;
            }
            return 10;
        }
        
        // Endgame: king activity becomes important
        // Prefer center squares in endgame
        int distanceFromCenter = Math.abs(x - 3) + Math.abs(y - 3);
        return 50 - (distanceFromCenter * 10);
    }
    
    /**
     * Calculates mobility score (number of possible moves).
     */
    private static int calculateMobilityScore(Board board, String color) {
        int ourMobility = 0;
        int opponentMobility = 0;
        
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Schachfigur figur = board.getFigur(x, y);
                if (figur != null) {
                    int moveCount = figur.getMoeglicheZuege().length;
                    
                    if (figur.getFarbe().equals(color)) {
                        ourMobility += moveCount;
                    } else {
                        opponentMobility += moveCount;
                    }
                }
            }
        }
        
        return (ourMobility - opponentMobility) * 2; // Mobility factor
    }
    
    /**
     * Checks if the game is in checkmate for the given color.
     */
    public static boolean isCheckmate(Board board, String color) {
        // Simple implementation - if no legal moves and king is in check
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Schachfigur figur = board.getFigur(x, y);
                if (figur != null && figur.getFarbe().equals(color)) {
                    if (figur.getMoeglicheZuege().length > 0) {
                        return false; // Has at least one legal move
                    }
                }
            }
        }
        return true; // No legal moves found
    }
    
    /**
     * Checks if the position is a stalemate.
     */
    public static boolean isStalemate(Board board, String color) {
        // If no legal moves but not in check (simplified implementation)
        return isCheckmate(board, color) && !isInCheck(board, color);
    }
    
    /**
     * Simple check detection (can be improved).
     */
    private static boolean isInCheck(Board board, String color) {
        // Find the king
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Schachfigur figur = board.getFigur(x, y);
                if (figur != null && figur.getName().equals("Koenig") && figur.getFarbe().equals(color)) {
                    // Check if any opponent piece can attack this square
                    return isSquareUnderAttack(board, x, y, color);
                }
            }
        }
        return false;
    }
    
    /**
     * Checks if a square is under attack by the opponent.
     */
    private static boolean isSquareUnderAttack(Board board, int x, int y, String defendingColor) {
        String attackingColor = defendingColor.equals("Weiss") ? "Schwarz" : "Weiss";
        
        for (int bx = 0; bx < 8; bx++) {
            for (int by = 0; by < 8; by++) {
                Schachfigur figur = board.getFigur(bx, by);
                if (figur != null && figur.getFarbe().equals(attackingColor)) {
                    // Check if this piece can attack the target square
                    for (Logic.Zug zug : figur.getMoeglicheZuege()) {
                        if (zug.getZielX() == x && zug.getZielY() == y) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
