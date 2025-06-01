package Schachfiguren;
import java.util.ArrayList;

import Logic.Board;
import Logic.Zug;

public class Laeufer extends Schachfigur {


    public Laeufer(String farbe, int x, int y, Board board) {
        super(farbe, x, y, board);
  
    }

    @Override
    public String getName() {
        return "Laeufer";
    }




    @Override
    public int getWert() {
        return 30;
    }



    @Override
    public ArrayList<Zug> getMoeglicheZuege() {
        int positionX = getPositionX();
        int positionY = getPositionY();
        Board board = super.getBoard();
        boolean isWeiss = isWeiss();
        ArrayList<Zug> zuege = new ArrayList<>();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}}; // Diagonal directions

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
