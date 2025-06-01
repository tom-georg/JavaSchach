package Schachfiguren;
import java.util.ArrayList;
import java.util.List;

import Logic.Board;
import Logic.Zug;

public class Springer extends Schachfigur {



    public Springer(String farbe, int x, int y, Board board) {
        
        super(farbe, x, y, board);
    }

    @Override
    public String getName() {
        return "Springer";
    }



    @Override
    public int getWert() {
        return 30;
    }



    @Override
    public Zug[] getMoeglicheZuege() {
        int positionX = getPositionX();
        int positionY = getPositionY();
        Board board = super.getBoard();
        boolean isWeiss = isWeiss();
        List<Zug> zuege = new ArrayList<>();
        int[] dx = {-2, -2, -1, -1, 1, 1, 2, 2};
        int[] dy = {-1, 1, -2, 2, -2, 2, -1, 1};

        for (int i = 0; i < 8; i++) {
            int zielX = positionX + dx[i];
            int zielY = positionY + dy[i];

            if (zielX >= 0 && zielX < 8 && zielY >= 0 && zielY < 8) {
                Schachfigur zielFigur = board.getFigur(zielX, zielY);
                if (zielFigur == null) {
                    zuege.add(new Zug(this, zielX, zielY));
                } else if (!zielFigur.isWeiss() == isWeiss) {
                    zuege.add(new Zug(this, zielX, zielY, zielFigur));
                }
            }
        }
        return zuege.toArray(new Zug[0]);
    }
}
