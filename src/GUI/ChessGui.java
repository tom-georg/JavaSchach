package GUI;
import javax.swing.*;

import Logic.Board;
import Logic.Zug;
import Schachfiguren.*;


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
    private Point selectedFigurScreenPosition; // Store the original screen position for dragging
    private JLabel draggedFigurLabel; // Label for the dragged piece image
    private JLayeredPane layeredPane;
    private JButton undoButton; // Button to undo the last move

    private final int SQUARE_SIZE = 80;
    private final Map<String, ImageIcon> pieceImages = new HashMap<>();

    public ChessGui(Board board) {
        this.board = board;
        setTitle("Schachspiel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(8 * SQUARE_SIZE, 8 * SQUARE_SIZE));

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

        // Add Undo Button
        undoButton = new JButton("Undo Last Move");
        undoButton.addActionListener(e -> {
            board.undoLastMove();
            selectedFigur = null;
            if (draggedFigurLabel != null) {
                layeredPane.remove(draggedFigurLabel);
                draggedFigurLabel = null;
            }
            boardPanel.repaint();
            layeredPane.repaint();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(undoButton);

        add(layeredPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadPieceImages() {
        String basePath = "/pieces/"; // Path relative to the classpath root (e.g., inside src or a resources folder)
        Map<String, String> pieceFileMapping = new HashMap<>();
        // White pieces
        pieceFileMapping.put("BauerWeiss", "BauerWeiss.png");
        pieceFileMapping.put("TurmWeiss", "TurmWeiss.png");
        pieceFileMapping.put("SpringerWeiss", "SpringerWeiss.png");
        pieceFileMapping.put("LaeuferWeiss", "LaeuferWeiss.png");
        pieceFileMapping.put("DameWeiss", "DameWeiss.png");
        pieceFileMapping.put("KoenigWeiss", "KoenigWeiss.png");
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
                    if (figurOnSquare != null /* && isPlayerTurn(figurOnSquare.getFarbe()) */) {
                        selectedFigur = figurOnSquare;
                        selectedFigurScreenPosition = e.getPoint(); // Store initial screen position

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
                        board.makeMove(chosenZug);
                        // If a drag was in progress, remove the label as the piece is now on the board via paintComponent
                        if (draggedFigurLabel != null) {
                            layeredPane.remove(draggedFigurLabel);
                            draggedFigurLabel = null;
                        }
                    }
                    selectedFigur = null; // Deselect after attempting move or clicking elsewhere
                }
                boardPanel.repaint();
                layeredPane.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) || selectedFigur == null || draggedFigurLabel == null) {
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

                boolean moveMade = false;
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
                        board.makeMove(chosenZug);
                        moveMade = true;
                    }
                }
                // If move was not made (e.g. invalid square), piece snaps back implicitly by repaint.
                selectedFigur = null; // Deselect after any drop attempt
                selectedFigurScreenPosition = null;
                boardPanel.repaint();
                layeredPane.repaint();
            }
        };

        MouseMotionAdapter mouseMotionAdapter = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) return;

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
