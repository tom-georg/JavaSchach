package GUI;
import javax.swing.*;

import Logic.Board;
import Logic.Zug;
import Schachfiguren.*;
import KI.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;

public class ChessGui extends JFrame {

    private Board board;
    private JPanel boardPanel;
    private Schachfigur selectedFigur;
    private JLabel draggedFigurLabel; // Label for the dragged piece image
    private JLayeredPane layeredPane;
    private JButton undoButton; // Button to undo the last move
    
    // AI-related fields
    private ChessAI chessAI;
    private boolean aiEnabled = false;
    private String aiColor = "Schwarz"; // AI plays as black by default
    private String currentTurn = "Weiss"; // White starts first
    private JButton aiToggleButton;
    private JComboBox<String> difficultyComboBox;
    private JLabel statusLabel;
    private JLabel turnLabel;
    private JButton newGameButton;

    private final int SQUARE_SIZE = 80;
    private final Map<String, ImageIcon> pieceImages = new HashMap<>();

    public ChessGui(Board board) {
        this.board = board;
        this.chessAI = new ChessAI(3); // Default difficulty 3
        
        setTitle("Schachspiel mit KI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(8 * SQUARE_SIZE, 8 * SQUARE_SIZE));

        // ...existing boardPanel code...
        boardPanel = new JPanel(new GridLayout(8, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g; // For better rendering if needed

                for (int row = 0; row < 8; row++) { // GUI rows (board Y from 0 to 7)
                    for (int col = 0; col < 8; col++) { // GUI columns (board X from 0 to 7)
                        // Draw square colors
                        if ((row + col) % 2 == 0) {
                            g2d.setColor(new Color(238, 238, 210)); // Light squares (e.g., "Lichess White")
                        } else {
                            g2d.setColor(new Color(118, 150, 86));  // Dark squares (e.g., "Lichess Green")
                        }
                        g2d.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);

                        // Draw piece
                        Schachfigur figur = ChessGui.this.board.getFigur(col, row); // (boardX, boardY)
                        if (figur != null) {
                            // Don't draw the piece at its original spot if it's being dragged
                            if (!(figur.equals(selectedFigur) && draggedFigurLabel != null)) {
                                ImageIcon icon = getPieceIcon(figur);
                                if (icon != null) {
                                    int xPos = col * SQUARE_SIZE + (SQUARE_SIZE - icon.getIconWidth()) / 2;
                                    int yPos = row * SQUARE_SIZE + (SQUARE_SIZE - icon.getIconHeight()) / 2;
                                    icon.paintIcon(this, g2d, xPos, yPos);
                                }
                            }
                        }
                    }
                }

                // Highlight possible moves if a piece is selected
                if (selectedFigur != null) {
                    Zug[] moeglicheZuege = selectedFigur.getMoeglicheZuege();
                    if (moeglicheZuege != null) {
                        g2d.setColor(new Color(20, 80, 200, 100)); // Semi-transparent blue for highlighting
                        for (Zug zug : moeglicheZuege) {
                            int zugX = zug.getZielX(); // board X
                            int zugY = zug.getZielY(); // board Y
                            // Draw a circle or dot in the center of the target square
                            g2d.fillOval(zugX * SQUARE_SIZE + SQUARE_SIZE / 3,
                                       zugY * SQUARE_SIZE + SQUARE_SIZE / 3,
                                       SQUARE_SIZE / 3, SQUARE_SIZE / 3);
                        }
                    }
                }
            }
        };
        boardPanel.setPreferredSize(new Dimension(8 * SQUARE_SIZE, 8 * SQUARE_SIZE));
        boardPanel.setBounds(0, 0, 8 * SQUARE_SIZE, 8 * SQUARE_SIZE);
        layeredPane.add(boardPanel, JLayeredPane.DEFAULT_LAYER);

        loadPieceImages();
        addMouseListeners();
        createControlPanels();

        add(layeredPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        updateStatusDisplay();
    }

    private void createControlPanels() {
        // Create top panel for AI controls
        JPanel topPanel = new JPanel(new FlowLayout());
        
        // AI Toggle Button
        aiToggleButton = new JButton("Enable AI");
        aiToggleButton.addActionListener(e -> toggleAI());
        
        // Difficulty ComboBox
        String[] difficulties = {
            "Easy (1)", "Easy-Medium (2)", "Medium (3)", "Medium-Hard (4)", "Hard (5)",
            "Very Hard (6)", "Expert (7)", "Master (8)", "Grandmaster (9)", "World Class (10)",
            "Superhuman (11)", "Engine Level 12", "Engine Level 13", "Engine Level 14", "Maximum (15)"
        };
        difficultyComboBox = new JComboBox<>(difficulties);
        difficultyComboBox.setSelectedIndex(2); // Default to medium (3)
        difficultyComboBox.addActionListener(e -> {
            int difficulty = difficultyComboBox.getSelectedIndex() + 1;
            chessAI.setDifficulty(difficulty);
            updateStatusDisplay();
        });
        
        // AI Color selection
        JComboBox<String> aiColorComboBox = new JComboBox<>(new String[]{"AI plays Black", "AI plays White"});
        aiColorComboBox.addActionListener(e -> {
            aiColor = aiColorComboBox.getSelectedIndex() == 0 ? "Schwarz" : "Weiss";
            updateStatusDisplay();
            // If AI color changed and it's AI's turn, make AI move
            if (aiEnabled && currentTurn.equals(aiColor)) {
                SwingUtilities.invokeLater(() -> makeAIMove());
            }
        });
        
        topPanel.add(new JLabel("AI Controls:"));
        topPanel.add(aiToggleButton);
        topPanel.add(new JLabel("Difficulty:"));
        topPanel.add(difficultyComboBox);
        topPanel.add(aiColorComboBox);
        
        // Create bottom panel for game controls and status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // Control buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        // Undo Button
        undoButton = new JButton("Undo Last Move");
        undoButton.addActionListener(e -> {
            board.undoLastMove();
            // Switch turn back
            currentTurn = currentTurn.equals("Weiss") ? "Schwarz" : "Weiss";
            selectedFigur = null;
            if (draggedFigurLabel != null) {
                layeredPane.remove(draggedFigurLabel);
                draggedFigurLabel = null;
            }
            updateStatusDisplay();
            boardPanel.repaint();
            layeredPane.repaint();
        });
        
        // New Game Button
        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> startNewGame());
        
        buttonPanel.add(undoButton);
        buttonPanel.add(newGameButton);
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout());
        turnLabel = new JLabel();
        statusLabel = new JLabel();
        statusPanel.add(turnLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(statusLabel);
        
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void toggleAI() {
        aiEnabled = !aiEnabled;
        aiToggleButton.setText(aiEnabled ? "Disable AI" : "Enable AI");
        updateStatusDisplay();
        
        // If AI is enabled and it's AI's turn, make AI move
        if (aiEnabled && currentTurn.equals(aiColor)) {
            SwingUtilities.invokeLater(() -> makeAIMove());
        }
    }
    
    private void startNewGame() {
        board = new Board(); // Create new board
        currentTurn = "Weiss"; // White starts
        selectedFigur = null;
        if (draggedFigurLabel != null) {
            layeredPane.remove(draggedFigurLabel);
            draggedFigurLabel = null;
        }
        updateStatusDisplay();
        boardPanel.repaint();
        layeredPane.repaint();
        
        // If AI plays white and is enabled, make first move
        if (aiEnabled && aiColor.equals("Weiss")) {
            SwingUtilities.invokeLater(() -> makeAIMove());
        }
    }
    
    private void makeAIMove() {
        if (!aiEnabled || !currentTurn.equals(aiColor)) {
            return;
        }
        
        // Show "AI thinking..." status
        statusLabel.setText("AI is thinking...");
        statusLabel.repaint();
        
        // Use SwingWorker to prevent GUI freezing during AI calculation
        SwingWorker<Zug, Void> aiWorker = new SwingWorker<Zug, Void>() {
            @Override
            protected Zug doInBackground() throws Exception {
                return chessAI.getBestMove(board, aiColor);
            }
            
            @Override
            protected void done() {
                try {
                    Zug aiMove = get();
                    if (aiMove != null) {
                        // Store captured piece info before making the move
                        Schachfigur capturedPiece = board.getFigur(aiMove.getZielX(), aiMove.getZielY());
                        if (capturedPiece != null) {
                            aiMove.setZielFigur(capturedPiece);
                        }
                        
                        board.makeMove(aiMove);
                        currentTurn = currentTurn.equals("Weiss") ? "Schwarz" : "Weiss";
                        updateStatusDisplay();
                        boardPanel.repaint();
                        layeredPane.repaint();
                        
                        // Check for game end conditions
                        checkGameEnd();
                    } else {
                        statusLabel.setText("AI has no legal moves!");
                    }
                } catch (Exception e) {
                    statusLabel.setText("AI error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        
        aiWorker.execute();
    }
    
    private void checkGameEnd() {
        // Simple game end detection - could be improved
        boolean hasLegalMoves = false;
        for (int x = 0; x < 8 && !hasLegalMoves; x++) {
            for (int y = 0; y < 8 && !hasLegalMoves; y++) {
                Schachfigur figur = board.getFigur(x, y);
                if (figur != null && figur.getFarbe().equals(currentTurn)) {
                    if (figur.getMoeglicheZuege().length > 0) {
                        hasLegalMoves = true;
                    }
                }
            }
        }
        
        if (!hasLegalMoves) {
            statusLabel.setText("Game Over! " + currentTurn + " has no legal moves.");
        }
    }
    
    private void updateStatusDisplay() {
        turnLabel.setText("Turn: " + (currentTurn.equals("Weiss") ? "White" : "Black"));
        
        if (aiEnabled) {
            String aiColorName = aiColor.equals("Weiss") ? "White" : "Black";
            statusLabel.setText("AI (" + aiColorName + ") - Difficulty " + chessAI.getDifficulty());
        } else {
            statusLabel.setText("Player vs Player");
        }
    }
    
    private boolean isPlayerTurn() {
        return !aiEnabled || !currentTurn.equals(aiColor);
    }

    // ...existing methods (loadPieceImages, getPieceIcon)...
    private void loadPieceImages() {
        String basePath = "/pieces/"; // Path relative to the classpath root (e.g., inside src or a resources folder)
        Map<String, String> pieceFileMapping = new HashMap<>();
        // White pieces
        pieceFileMapping.put("BauerWeiss", "BauerWeiss.png");
        pieceFileMapping.put("TurmWeiss", "TurmWeiss.png");
        pieceFileMapping.put("SpringerWeiss", "springerWeiss.png");
        pieceFileMapping.put("LaeuferWeiss", "laeuferWeiss.png");
        pieceFileMapping.put("DameWeiss", "dameWeiss.png");
        pieceFileMapping.put("KoenigWeiss", "koenigWeiss.png");
        // Black pieces
        pieceFileMapping.put("BauerSchwarz", "bauerSchwarz.png");
        pieceFileMapping.put("TurmSchwarz", "TurmSchwarz.png");
        pieceFileMapping.put("SpringerSchwarz", "springerSchwarz.png");
        pieceFileMapping.put("LaeuferSchwarz", "laeuferSchwarz.png");
        pieceFileMapping.put("DameSchwarz", "DameSchwarz.png");
        pieceFileMapping.put("KoenigSchwarz", "koenigSchwarz.png");

        for (Map.Entry<String, String> entry : pieceFileMapping.entrySet()) {
            String key = entry.getKey();
            String fileName = entry.getValue();
            URL imageUrl = getClass().getResource(basePath + fileName);
            if (imageUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imageUrl);
                Image img = originalIcon.getImage().getScaledInstance((int)(SQUARE_SIZE * 0.6), (int)(SQUARE_SIZE * 0.6), Image.SCALE_SMOOTH);
                pieceImages.put(key, new ImageIcon(img));
            } else {
                System.err.println("Bild nicht gefunden: " + basePath + fileName + " (für Key: " + key + ")");
                 // Attempt with a slightly different base path if the first fails, common for IDE vs JAR
                URL imageUrlAlt = getClass().getResource("/SchachMuLoe/src"+basePath + fileName);
                if (imageUrlAlt != null) {
                    ImageIcon originalIcon = new ImageIcon(imageUrlAlt);
                    Image img = originalIcon.getImage().getScaledInstance((int)(SQUARE_SIZE * 0.6), (int)(SQUARE_SIZE * 0.6), Image.SCALE_SMOOTH);
                    pieceImages.put(key, new ImageIcon(img));
                    System.out.println("Found with alt path: " + "/SchachMuLoe/src"+basePath + fileName);
                } else {
                     System.err.println("Alternate path also failed for: " + "/SchachMuLoe/src"+basePath + fileName);
                }
            }
        }
    }

    private ImageIcon getPieceIcon(Schachfigur figur) {
        if (figur == null) return null;
        String key = figur.getName() + figur.getFarbe();
        return pieceImages.get(key);
    }

    private void addMouseListeners() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) return; // Ignore right-clicks for piece movement
                
                // Don't allow moves if it's AI's turn
                if (!isPlayerTurn()) {
                    return;
                }

                int clickedGuiX = e.getX();
                int clickedGuiY = e.getY();
                int boardX = clickedGuiX / SQUARE_SIZE;
                int boardY = clickedGuiY / SQUARE_SIZE;

                if (boardX < 0 || boardX >= 8 || boardY < 0 || boardY >= 8) { // Clicked outside board
                    if (selectedFigur != null && draggedFigurLabel != null) { // Cancel drag
                        layeredPane.remove(draggedFigurLabel);
                        draggedFigurLabel = null;
                    }
                    selectedFigur = null;
                    boardPanel.repaint();
                    layeredPane.repaint();
                    return;
                }

                Schachfigur figurOnSquare = board.getFigur(boardX, boardY);

                if (selectedFigur == null) { // No piece currently selected
                    // Only allow selecting pieces of the current player's color
                    if (figurOnSquare != null && figurOnSquare.getFarbe().equals(currentTurn)) {
                        selectedFigur = figurOnSquare;

                        ImageIcon icon = getPieceIcon(selectedFigur);
                        if (icon != null) {
                            draggedFigurLabel = new JLabel(icon);
                            draggedFigurLabel.setSize(icon.getIconWidth(), icon.getIconHeight());
                            // Center icon on cursor
                            draggedFigurLabel.setLocation(clickedGuiX - icon.getIconWidth() / 2, clickedGuiY - icon.getIconHeight() / 2);
                            layeredPane.add(draggedFigurLabel, JLayeredPane.DRAG_LAYER);
                        }
                    }
                } else { // A piece is already selected (selectedFigur != null), try to move it
                    boolean isValidTarget = false;
                    Zug chosenZug = null;
                    Zug[] possibleMoves = selectedFigur.getMoeglicheZuege();
                    if (possibleMoves != null) {
                        for (Zug zug : possibleMoves) {
                            if (zug.getZielX() == boardX && zug.getZielY() == boardY) {
                                isValidTarget = true;
                                chosenZug = zug;
                                break;
                            }
                        }
                    }
                    if (isValidTarget && chosenZug != null) {
                        // Store captured piece info before making the move
                        Schachfigur capturedPiece = board.getFigur(chosenZug.getZielX(), chosenZug.getZielY());
                        if (capturedPiece != null) {
                            chosenZug.setZielFigur(capturedPiece);
                        }
                        
                        board.makeMove(chosenZug);
                        currentTurn = currentTurn.equals("Weiss") ? "Schwarz" : "Weiss";
                        updateStatusDisplay();
                        
                        // If a drag was in progress, remove the label as the piece is now on the board via paintComponent
                        if (draggedFigurLabel != null) {
                            layeredPane.remove(draggedFigurLabel);
                            draggedFigurLabel = null;
                        }
                        
                        // Check for game end
                        checkGameEnd();
                        
                        // If AI is enabled and it's now AI's turn, make AI move
                        if (aiEnabled && currentTurn.equals(aiColor)) {
                            SwingUtilities.invokeLater(() -> makeAIMove());
                        }
                    }
                    selectedFigur = null; // Deselect after attempting move or clicking elsewhere
                }
                boardPanel.repaint();
                layeredPane.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) || selectedFigur == null || draggedFigurLabel == null || !isPlayerTurn()) {
                    // If not a drag release (e.g., click-move handled in mousePressed or no piece selected for drag)
                    // or if it was a right click, just ensure repaint and return.
                    // selectedFigur might have been set to null in mousePressed if it was a click-move.
                    boardPanel.repaint();
                    layeredPane.repaint();
                    return;
                }

                // This is a drag release
                layeredPane.remove(draggedFigurLabel);
                draggedFigurLabel = null;

                int boardX = e.getX() / SQUARE_SIZE;
                int boardY = e.getY() / SQUARE_SIZE;

                if (boardX >= 0 && boardX < 8 && boardY >= 0 && boardY < 8) {
                    Zug[] possibleMoves = selectedFigur.getMoeglicheZuege();
                    Zug chosenZug = null;
                    if (possibleMoves != null) {
                        for (Zug zug : possibleMoves) {
                            if (zug.getZielX() == boardX && zug.getZielY() == boardY) {
                                chosenZug = zug;
                                break;
                            }
                        }
                    }
                    if (chosenZug != null) {
                        // Store captured piece info before making the move
                        Schachfigur capturedPiece = board.getFigur(chosenZug.getZielX(), chosenZug.getZielY());
                        if (capturedPiece != null) {
                            chosenZug.setZielFigur(capturedPiece);
                        }
                        
                        board.makeMove(chosenZug);
                        currentTurn = currentTurn.equals("Weiss") ? "Schwarz" : "Weiss";
                        updateStatusDisplay();
                        
                        // Check for game end
                        checkGameEnd();
                        
                        // If AI is enabled and it's now AI's turn, make AI move
                        if (aiEnabled && currentTurn.equals(aiColor)) {
                            SwingUtilities.invokeLater(() -> makeAIMove());
                        }
                    }
                }
                // If move was not made (e.g. invalid square), piece snaps back implicitly by repaint.
                selectedFigur = null; // Deselect after any drop attempt
                boardPanel.repaint();
                layeredPane.repaint();
            }
        };

        MouseMotionAdapter mouseMotionAdapter = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) || !isPlayerTurn()) return;

                if (selectedFigur != null && draggedFigurLabel != null) {
                    // Update dragged label position to follow the mouse cursor
                    draggedFigurLabel.setLocation(e.getX() - draggedFigurLabel.getWidth() / 2, e.getY() - draggedFigurLabel.getHeight() / 2);
                    layeredPane.repaint(); // Repaint only the layered pane for drag efficiency
                }
            }
        };

        boardPanel.addMouseListener(mouseAdapter);
        boardPanel.addMouseMotionListener(mouseMotionAdapter);
    }

    public static void main(String[] args) {
        // Ensure GUI operations are on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            Board board = new Board();
            new ChessGui(board);
        });
    }
}
