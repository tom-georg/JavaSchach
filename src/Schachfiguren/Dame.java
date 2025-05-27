package Schachfiguren;
import java.util.ArrayList;
import java.util.List;

import Logic.Board;
import Logic.Zug;

public class Dame implements Schachfigur {


    private boolean isWeiss; // True if white, false if black
    private int positionX;
    private int positionY;
    private Board board;

    public Dame(String farbe, int x, int y, Board board) {

        this.positionX = x;
        this.positionY = y;
        this.board = board;
        this.isWeiss = farbe.equals("Weiss");
    }

    @Override
    public String getName() {
        return "Dame";
    }

    @Deprecated
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
        return 9;
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
        return zuege.toArray(new Zug[0]);
    }
}
