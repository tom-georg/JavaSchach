package Schachfiguren;
import java.util.ArrayList;

import Logic.Board;
import Logic.Zug;

public class Koenig extends Schachfigur {


   private ArrayList<Zug> zuege = new ArrayList<>(20);

    public Koenig(String farbe, int x, int y, Board board) {
        super(farbe, x, y, board);
 
    }

    @Override
    public String getName() {
        return "Koenig";
    }



    @Override
    public int getWert() {
        return 999; // King's value is often considered infinite or not assigned a point value
    }

  
    @Override
    public ArrayList<Zug> getMoeglicheZuege() {
        int positionX = getPositionX();
        int positionY = getPositionY();
        Board board = super.getBoard();
        boolean farbe = isWeiss();
        zuege.clear(); // Clear previous moves
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < 8; i++) {
            int zielX = positionX + dx[i];
            int zielY = positionY + dy[i];

            if (zielX >= 0 && zielX < 8 && zielY >= 0 && zielY < 8) {
                Schachfigur zielFigur = board.getFigur(zielX, zielY);
                if (zielFigur == null) {
                    zuege.add(new Zug(this, zielX, zielY));
                } else if (!zielFigur.isWeiss() == farbe) {
                    zuege.add(new Zug(this, zielX, zielY, zielFigur));
                }
            }
        }
        // TODO: Implement castling logic
        return zuege;
    }
}
