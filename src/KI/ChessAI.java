package KI;

import Logic.Board;
import Logic.Zug;
import Schachfiguren.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Main AI implementation using minimax algorithm with alpha-beta pruning.
 */
public class ChessAI implements AIPlayer {
    
    private int difficulty;
    private Random random;
    private static final int MAX_DEPTH = 15; // Maximum search depth
    
    public ChessAI(int difficulty) {
        this.difficulty = Math.max(1, difficulty); // Remove upper limit
        this.random = new Random();
    }
    
    @Override
    public Zug getBestMove(Board board, String color) {
        // Get all possible moves
        List<Zug> allMoves = getAllPossibleMoves(board, color);
        
        if (allMoves.isEmpty()) {
            return null; // No legal moves
        }
        
        // Adjust search depth based on difficulty
        int searchDepth = Math.min(difficulty, MAX_DEPTH);
        
        if (difficulty == 1) {
            // Easy: Random moves with slight preference for captures
            return getRandomMove(allMoves);
        } else if (difficulty == 2) {
            // Easy-Medium: Simple evaluation with shallow search
            return getBestMoveSimple(board, allMoves, color);
        } else {
            // Medium-Hard and beyond: Full minimax with alpha-beta pruning
            // Higher difficulty = deeper search
            return getBestMoveMinimax(board, allMoves, color, searchDepth);
        }
    }
    
    /**
     * Gets a random move, with slight preference for captures.
     */
    private Zug getRandomMove(List<Zug> moves) {
        // Separate captures from normal moves
        List<Zug> captures = new ArrayList<>();
        List<Zug> normalMoves = new ArrayList<>();
        
        for (Zug move : moves) {
            if (move.getZielFigur() != null) {
                captures.add(move);
            } else {
                normalMoves.add(move);
            }
        }
        
        // 70% chance to prefer captures if available
        if (!captures.isEmpty() && random.nextDouble() < 0.7) {
            return captures.get(random.nextInt(captures.size()));
        } else {
            return moves.get(random.nextInt(moves.size()));
        }
    }
    
    /**
     * Simple move evaluation without deep search.
     */
    private Zug getBestMoveSimple(Board board, List<Zug> moves, String color) {
        Zug bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        
        for (Zug move : moves) {
            int score = evaluateMove(board, move, color);
            
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        
        return bestMove;
    }
    
    /**
     * Evaluates a single move without making it on the board.
     */
    private int evaluateMove(Board board, Zug move, String color) {
        int score = 0;
        
        // Capture bonus
        if (move.getZielFigur() != null) {
            score += move.getZielFigur().getWert() * 100;
        }
        
        // Positional bonus for moving to center
        int centerDistance = Math.abs(move.getZielX() - 3) + Math.abs(move.getZielY() - 3);
        score += (6 - centerDistance) * 5;
        
        // Piece development bonus (moving pieces from back rank)
        if (move.getStartY() == (color.equals("Weiss") ? 7 : 0)) {
            score += 20;
        }
        
        return score;
    }
    
    /**
     * Full minimax implementation with alpha-beta pruning.
     */
    private Zug getBestMoveMinimax(Board board, List<Zug> moves, String color, int depth) {
        Zug bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        
        for (Zug move : moves) {
            // Make the move on the actual board
            board.makeMove(move);
            
            // Evaluate using minimax
            int score = minimax(board, depth - 1, alpha, beta, false, color);
            
            // Undo the move to restore board state
            board.undoLastMove();
            
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
            
            alpha = Math.max(alpha, score);
            if (beta <= alpha) {
                break; // Alpha-beta pruning
            }
        }
        
        return bestMove;
    }
    
    /**
     * Minimax algorithm with alpha-beta pruning.
     */
    private int minimax(Board board, int depth, int alpha, int beta, boolean maximizing, String aiColor) {
        // Terminal conditions
        if (depth == 0) {
            return BoardEvaluator.evaluateBoard(board, aiColor);
        }
        
        String currentColor = maximizing ? aiColor : (aiColor.equals("Weiss") ? "Schwarz" : "Weiss");
        List<Zug> moves = getAllPossibleMoves(board, currentColor);
        
        if (moves.isEmpty()) {
            // No legal moves - checkmate or stalemate
            if (BoardEvaluator.isCheckmate(board, currentColor)) {
                return maximizing ? -10000 + depth : 10000 - depth; // Prefer quicker checkmates
            } else {
                return 0; // Stalemate
            }
        }
        
        if (maximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Zug move : moves) {
                board.makeMove(move);
                int eval = minimax(board, depth - 1, alpha, beta, false, aiColor);
                board.undoLastMove();
                
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break; // Beta cutoff
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Zug move : moves) {
                board.makeMove(move);
                int eval = minimax(board, depth - 1, alpha, beta, true, aiColor);
                board.undoLastMove();
                
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break; // Alpha cutoff
                }
            }
            return minEval;
        }
    }
    
    /**
     * Gets all possible moves for a given color.
     */
    private List<Zug> getAllPossibleMoves(Board board, String color) {
        List<Zug> allMoves = new ArrayList<>(250);
        
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Schachfigur figur = board.getFigur(x, y);
                if (figur != null && figur.getFarbe().equals(color)) {
                    Zug[] moves = figur.getMoeglicheZuege();
                    for (Zug move : moves) {
                        allMoves.add(move);
                    }
                }
            }
        }
        
        return allMoves;
    }
    
    
    @Override
    public int getDifficulty() {
        return difficulty;
    }
    
    @Override
    public void setDifficulty(int difficulty) {
        this.difficulty = Math.max(1, difficulty); // Remove upper limit
    }
}
