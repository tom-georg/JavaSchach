package Logic;
import java.util.ArrayList;
import java.util.List;

import Schachfiguren.*;

public class Board {

    private Schachfigur[][] board;
    private ArrayList<Zug> zugHistorie;

    public Board() {
        board = new Schachfigur[8][8];
        zugHistorie = new ArrayList<>();
        initializeBoard();
    }
    
    // Constructor for creating empty board (used for copying)
    public Board(boolean initialize) {
        board = new Schachfigur[8][8];
        zugHistorie = new ArrayList<>();
        if (initialize) {
            initializeBoard();
        }
    }

    private void initializeBoard() {
        // Initialize pawns
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Bauer("Schwarz", i, 1, this);
            board[6][i] = new Bauer("Weiss", i, 6, this);
        }

        // Initialize rooks
        board[0][0] = new Turm("Schwarz", 0, 0, this);
        board[0][7] = new Turm("Schwarz", 7, 0, this);
        board[7][0] = new Turm("Weiss", 0, 7, this);
        board[7][7] = new Turm("Weiss", 7, 7, this);

        // Initialize knights
        board[0][1] = new Springer("Schwarz", 1, 0, this);
        board[0][6] = new Springer("Schwarz", 6, 0, this);
        board[7][1] = new Springer("Weiss", 1, 7, this);
        board[7][6] = new Springer("Weiss", 6, 7, this);

        // Initialize bishops
        board[0][2] = new Laeufer("Schwarz", 2, 0, this);
        board[0][5] = new Laeufer("Schwarz", 5, 0, this);
        board[7][2] = new Laeufer("Weiss", 2, 7, this);
        board[7][5] = new Laeufer("Weiss", 5, 7, this);

        // Initialize queens and kings
        board[0][3] = new Dame("Schwarz", 3, 0, this);
        board[0][4] = new Koenig("Schwarz", 4, 0, this);
        board[7][3] = new Dame("Weiss", 3, 7, this);
        board[7][4] = new Koenig("Weiss", 4, 7, this);
    }

    public Schachfigur getFigur(int x, int y) {
        if (x < 0 || x >= 8 || y < 0 || y >= 8) {
            return null; // Out of bounds
        }
        return board[y][x];
    }

    public void setPieceAt(int x, int y, Schachfigur piece) {
        if (x >= 0 && x < 8 && y >= 0 && y < 8) {
            board[y][x] = piece;
            if (piece != null) {
                // Ensure the piece itself knows its new position if we are placing it.
                // However, setPosition should ideally be called by the piece or move logic.
                // For now, this helps keep board and piece consistent if setPieceAt is used directly.
                // piece.setPosition(x, y); // This might be redundant if makeMove handles it.
            }
        }
    }

    public Schachfigur removePieceAt(int x, int y) {
        if (x >= 0 && x < 8 && y >= 0 && y < 8) {
            Schachfigur piece = board[y][x];
            board[y][x] = null;
            return piece;
        }
        return null;
    }

    public void makeMove(Zug zug) {
        this.zugHistorie.add(zug); // Store the move before executing

        Schachfigur figur = zug.getFigur();
        int startX = figur.getPositionX();
        int startY = figur.getPositionY();
        int zielX = zug.getZielX();
        int zielY = zug.getZielY();

        // Remove piece from old position
        removePieceAt(startX, startY);
        
        // Schachfigur capturedPiece = getFigur(zielX, zielY); // Check if a piece is captured
        // if (capturedPiece != null) {
        //     // Handle captured piece (e.g., add to a list, not implemented here)
        // }

        // Place piece in new position
        setPieceAt(zielX, zielY, figur);
        
        // Update the piece's internal position
        figur.setPosition(zielX, zielY);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
                if (board[y][x] != null) {
                    sb.append(board[y][x].getName().charAt(0)).append(" ");
                } else {
                    sb.append(". ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public List<Zug> getZugHistorie() {
        return zugHistorie;
    }

    public void undoLastMove() {
        if (!zugHistorie.isEmpty()) {
            Zug lastZug = zugHistorie.remove(zugHistorie.size() - 1);
            Schachfigur figur = lastZug.getFigur();
            int startX = lastZug.getStartX();
            int startY = lastZug.getStartY();
            int zielX = lastZug.getZielX();
            int zielY = lastZug.getZielY();
            Schachfigur capturedFigur = lastZug.getZielFigur(); // Get the captured piece

            // Remove piece from new position
            // board[zielY][zielX] = null; // Simpler than removePieceAt for this logic
            // No, removePieceAt is better as it might have other logic
            removePieceAt(zielX, zielY);
            
            // Restore piece to old position
            setPieceAt(startX, startY, figur);
            
            // Update the piece's internal position
            figur.setPosition(startX, startY);

            // If there was a captured piece, restore it
            if (capturedFigur != null) {
                setPieceAt(zielX, zielY, capturedFigur);
                // Also update the captured piece's internal position if it's tracked,
                // though for a captured piece, its position on board is what matters.
                // capturedFigur.setPosition(zielX, zielY); // This might not be necessary if it's re-added correctly
            }
        }
    }

    /**
     * Creates a copy of this board for AI simulation purposes.
     * @return A new Board instance with the same piece positions
     */
    public Board copy() {
        Board copy = new Board(false); // Create empty board
        
        // Copy all pieces to their exact positions
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Schachfigur originalPiece = getFigur(x, y);
                if (originalPiece != null) {
                    Schachfigur newPiece = createPieceCopy(originalPiece, copy);
                    copy.setPieceAt(x, y, newPiece);
                }
            }
        }
        
        return copy;
    }
    
    /**
     * Creates a copy of a chess piece for the copied board.
     */
    private Schachfigur createPieceCopy(Schachfigur original, Board newBoard) {
        String name = original.getName();
        String color = original.getFarbe();
        int x = original.getPositionX();
        int y = original.getPositionY();
        
        switch (name) {
            case "Bauer": return new Bauer(color, x, y, newBoard);
            case "Turm": return new Turm(color, x, y, newBoard);
            case "Springer": return new Springer(color, x, y, newBoard);
            case "Laeufer": return new Laeufer(color, x, y, newBoard);
            case "Dame": return new Dame(color, x, y, newBoard);
            case "Koenig": return new Koenig(color, x, y, newBoard);
            default: return null;
        }
    }

 
}
