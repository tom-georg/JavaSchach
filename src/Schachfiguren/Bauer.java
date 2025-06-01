package Schachfiguren;
import java.util.ArrayList;

import Logic.Board;
import Logic.Zug;

public class Bauer extends Schachfigur {

    


    public Bauer(String farbe, int x, int y, Board board) {
        super(farbe, x, y, board);
       
    }


    @Override
    public String getName() {
        return "Bauer";
    }


    @Override
    public int getWert() {
        return 10;
    }

    @Override
    public ArrayList<Zug> getMoeglicheZuege() {
        Board board = super.getBoard();
        boolean farbe = isWeiss();
        ArrayList<Zug> zuege = new ArrayList<>();
        int zielX, zielY;
        int richtung = isSchwarz() ? 1 : -1; // Schwarz moves +1 in Y, Weiss moves -1 in Y

        int positionX = getPositionX();
        int positionY = getPositionY();
        // Forward 1
        zielX = getPositionX();
        zielY = getPositionY() + richtung;
        if (zielY >= 0 && zielY < 8 && board.getFigur(zielX, zielY) == null) {
            zuege.add(new Zug(this, zielX, zielY));

            // Forward 2 (initial move)
            boolean isInitialPosition = (isSchwarz() && positionY == 1) || (isWeiss() && positionY == 6);
            if (isInitialPosition) {
                zielY = positionY + 2 * richtung;
                if (zielY >= 0 && zielY < 8 && board.getFigur(zielX, zielY) == null && board.getFigur(zielX, positionY + richtung) == null) {
                    zuege.add(new Zug(this, zielX, zielY));
                }
            }
        }

        // Capture moves
        zielY = positionY + richtung;
        if (zielY >= 0 && zielY < 8) {
            // Capture left
            zielX = positionX - 1;
            if (zielX >= 0) {
                Schachfigur zielFigur = board.getFigur(zielX, zielY);
                if (zielFigur != null && !zielFigur.isWeiss() == farbe) {
                    zuege.add(new Zug(this, zielX, zielY, zielFigur));
                }
            }
            // Capture right
            zielX = positionX + 1;
            if (zielX < 8) {
                Schachfigur zielFigur = board.getFigur(zielX, zielY);
                if (zielFigur != null && !zielFigur.isWeiss() == farbe) {
                    zuege.add(new Zug(this, zielX, zielY, zielFigur));
                }
            }
        }
        // TODO: En passant logic would be added here

        return zuege;
    }


}
