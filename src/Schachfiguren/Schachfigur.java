package Schachfiguren;

import Logic.Board;
import Logic.Zug;
import java.util.ArrayList;

public abstract class Schachfigur {

    public final static boolean WEISS = true;
    public static final boolean SCHWARZ = false;

    private boolean farbe; // True if white, false if black

    private int positionX;
    private int positionY;

    private Board board;

    /**
     * Konstruktor für eine Schachfigur.
     *
     * @param farbe die Farbe der Schachfigur (true für Weiß, false für Schwarz)
     * @param x     die X-Position der Schachfigur
     * @param y     die Y-Position der Schachfigur
     */
    public Schachfigur(boolean farbe, int x, int y, Board board) {
        this.farbe = farbe;
        this.positionX = x;
        this.positionY = y;
        this.board = board;
    }

    public Schachfigur(String farbe, int x, int y, Board board) {
        this.farbe = farbe.equals("Weiss");
        this.positionX = x;
        this.positionY = y;
        this.board = board;
    }

    
    /**
     * Gibt den Namen der Schachfigur zurück.
     *
     * @return der Name der Schachfigur
     */
    public abstract String getName();

 

    public  int getPositionX(){
        return positionX;
    }

    public  int getPositionY(){
        return positionY;
    }

    public void setPosition(int x, int y) {
        this.positionX = x;
        this.positionY = y;
    }

    public Board getBoard() {
        return board;
    }

   
    /**
     * Gibt den Wert der Schachfigur zurück.
     *
     * @return der Wert der Schachfigur
     */
    public abstract int getWert();

    public abstract ArrayList<Zug> getMoeglicheZuege();

    public  boolean isWeiss() {
        return farbe;
    }

    public  boolean isSchwarz() {
        return !farbe;
    }
    public boolean isFarbe(boolean farbe) {
        return this.farbe == farbe;
    }
    public boolean isFarbe(String farbe) {
        return this.farbe == farbe.equals("Weiss");
    }

    public String getFarbeString() {
        return farbe ? "Weiss" : "Schwarz";
    }

    public boolean getFarbe() {
        return farbe;
    }




}