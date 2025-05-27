package Logic;

import Schachfiguren.*;
public class Zug {
    
    private Schachfigur figur;
    private int startX;
    private int startY;
    private int zielX;
    private int zielY;
    private Schachfigur zielFigur;

    public Zug(Schachfigur figur, int zielX, int zielY) {
        this.figur = figur;
        this.startX = figur.getPositionX();
        this.startY = figur.getPositionY();
        this.zielX = zielX;
        this.zielY = zielY;
    }

    public Zug(Schachfigur figur, int zielX, int zielY, Schachfigur zielFigur) {
        this.figur = figur;
        this.startX = figur.getPositionX();
        this.startY = figur.getPositionY();
        this.zielX = zielX;
        this.zielY = zielY;
        this.zielFigur = zielFigur;
    }
    public Schachfigur getZielFigur() {
        return zielFigur;
    }
    public void setZielFigur(Schachfigur zielFigur) {
        this.zielFigur = zielFigur;
    }

    public Schachfigur getFigur() {
        return figur;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getZielX() {
        return zielX;
    }

    public int getZielY() {
        return zielY;
    }
    @Override
    public String toString() {
        return "Zug{" +
                "figur=" + figur.getName() +
                ", startX=" + startX +
                ", startY=" + startY +
                ", zielX=" + zielX +
                ", zielY=" + zielY +
                (zielFigur != null ? ", zielFigur=" + zielFigur.getName() : "") +
                '}';
    }
}
