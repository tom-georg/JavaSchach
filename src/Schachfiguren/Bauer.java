package Schachfiguren;
import java.util.ArrayList;
import java.util.List;

import Logic.Board;
import Logic.Zug;

public class Bauer implements Schachfigur {

    private String farbe;
    private int positionX;
    private int positionY;
    private Board board;

    public Bauer(String farbe, int x, int y, Board board) {
        this.farbe = farbe;
        this.positionX = x;
        this.positionY = y;
        this.board = board;
    }

    @Override
    public String getName() {
        return "Bauer";
    }

    @Override
    public String getFarbe() {
        return farbe;
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
}
