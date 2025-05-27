package Schachfiguren;
import java.util.ArrayList;
import java.util.List;

import Logic.Board;
import Logic.Zug;

public class Laeufer implements Schachfigur {


    private boolean isWeiss; // True if white, false if black
    private int positionX;
    private int positionY;
    private Board board;

    public Laeufer(String farbe, int x, int y, Board board) {

        this.isWeiss = farbe.equals("Weiss");
        this.positionX = x;
        this.positionY = y;
        this.board = board;
    }

    @Override
    public String getName() {
        return "Laeufer";
    }

    @Override
    public String getFarbe() {
        return isWeiss ? "Weiss" : "Schwarz";
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
        return 3;
    }

        @Override
    public boolean isWeiss() {
        return isWeiss;
    }

    @Override
    public boolean isSchwarz() {
        return !isWeiss;
    }

    @Override
    public Zug[] getMoeglicheZuege() {
        List<Zug> zuege = new ArrayList<>();
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
        return zuege.toArray(new Zug[0]);
    }
}
