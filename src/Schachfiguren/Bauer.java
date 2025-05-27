package Schachfiguren;
import java.util.ArrayList;
import java.util.List;

import Logic.Board;
import Logic.Zug;

public class Bauer implements Schachfigur {

    private String farbe;
    private boolean isWeiss; // True if white, false if black
    private int positionX;
    private int positionY;
    private Board board;

    public Bauer(String farbe, int x, int y, Board board) {
        this.farbe = farbe;
        this.positionX = x;
        this.positionY = y;
        this.board = board;
        this.isWeiss = farbe.equals("Weiss");
    }

    @Override
    public String getName() {
        return "Bauer";
    }

    @Override
    public String getFarbe() {
        if (isWeiss) {
            return "Weiss";
        } else {
            return "Schwarz";
        }
    }

    @Override
    public int getPositionX() {
        return positionX;
    }

    @Override
    public int getPositionY() {
        return positionY;
    }

    @Override
    public void setPosition(int x, int y) {
        this.positionX = x;
        this.positionY = y;
    }

    @Override
    public int getWert() {
        return 1;
    }

    @Override
    public Zug[] getMoeglicheZuege() {
        List<Zug> zuege = new ArrayList<>();
        int zielX, zielY;
        int richtung = farbe.equals("Schwarz") ? 1 : -1; // Schwarz moves +1 in Y, Weiss moves -1 in Y

        // Forward 1
        zielX = positionX;
        zielY = positionY + richtung;
        if (zielY >= 0 && zielY < 8 && board.getFigur(zielX, zielY) == null) {
            zuege.add(new Zug(this, zielX, zielY));

            // Forward 2 (initial move)
            boolean isInitialPosition = (farbe.equals("Schwarz") && positionY == 1) || (farbe.equals("Weiss") && positionY == 6);
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
                if (zielFigur != null && !zielFigur.getFarbe().equals(this.farbe)) {
                    zuege.add(new Zug(this, zielX, zielY, zielFigur));
                }
            }
            // Capture right
            zielX = positionX + 1;
            if (zielX < 8) {
                Schachfigur zielFigur = board.getFigur(zielX, zielY);
                if (zielFigur != null && !zielFigur.getFarbe().equals(this.farbe)) {
                    zuege.add(new Zug(this, zielX, zielY, zielFigur));
                }
            }
        }
        // TODO: En passant logic would be added here

        return zuege.toArray(new Zug[0]);
    }

    @Override
    public boolean isWeiss() {
        return isWeiss;
    }

    @Override
    public boolean isSchwarz() {
        return !isWeiss;
    }
}
