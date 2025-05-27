import javax.swing.SwingUtilities;

import GUI.ChessGui;
import Logic.Board;


public class App {
    public static void main(String[] args) throws Exception {
                // Ensure GUI operations are on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            Board board = new Board();
            new ChessGui(board);
        });
    }
}
