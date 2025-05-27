import java.util.ArrayList;
import java.util.List;

public class Springer implements Schachfigur {

    private String farbe;
    private int positionX;
    private int positionY;
    private Board board;

    public Springer(String farbe, int x, int y, Board board) {
        this.farbe = farbe;
        this.positionX = x;
        this.positionY = y;
        this.board = board;
    }

    @Override
    public String getName() {
        return "Springer";
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
        return 3;
    }

    @Override
    public Zug[] getMoeglicheZuege() {
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
                } else if (!zielFigur.getFarbe().equals(this.farbe)) {
                    zuege.add(new Zug(this, zielX, zielY, zielFigur));
                }
            }
        }
        return zuege.toArray(new Zug[0]);
    }
}
