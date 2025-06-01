package Schachfiguren;
import java.util.ArrayList;

import Logic.Board;
import Logic.Zug;

public class Dame extends Schachfigur {



    public Dame(String farbe, int x, int y, Board board) {
        super(farbe, x, y, board);

    }

    @Override
    public String getName() {
        return "Dame";
    }



    @Override
    public int getWert() {
        return 90;
    }


    @Override
    public ArrayList<Zug> getMoeglicheZuege() {
        int positionX = getPositionX();
        int positionY = getPositionY();
        Board board = super.getBoard();
        boolean isWeiss = isWeiss();
        ArrayList<Zug> zuege = new ArrayList<>();
        // Combines Rook and Bishop logic
        int[][] directions = {
            {0, 1}, {0, -1}, {1, 0}, {-1, 0}, // Rook directions
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}  // Bishop directions
        };

        for (int[] dir : directions) {
            for (int i = 1; i < 8; i++) {
                int zielX = positionX + dir[0] * i;
                int zielY = positionY + dir[1] * i;

                if (zielX >= 0 && zielX < 8 && zielY >= 0 && zielY < 8) {
                    Schachfigur zielFigur = board.getFigur(zielX, zielY);
                    if (zielFigur == null) {
                        zuege.add(new Zug(this, zielX, zielY));
                    } else {
                        if (!zielFigur.isWeiss() == isWeiss) {
                            zuege.add(new Zug(this, zielX, zielY, zielFigur));
                        }
                        break; // Stop in this direction if a piece is encountered
                    }
                } else {
                    break; // Out of bounds
                }
            }
        }
        return zuege;
    }
}
