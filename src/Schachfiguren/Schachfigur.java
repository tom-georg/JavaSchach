package Schachfiguren;

import Logic.Zug;

public interface Schachfigur {

    /**
     * Gibt den Namen der Schachfigur zurück.
     *
     * @return der Name der Schachfigur
     */
    String getName();

    /**
     * Gibt die Farbe der Schachfigur zurück.
     *
     * @return die Farbe der Schachfigur
     */
    @Deprecated
    String getFarbe();

    int getPositionX();

    int getPositionY();

    /**
     * Setzt die Position der Schachfigur.
     *
     * @param x die neue X-Position
     * @param y die neue Y-Position
     */
    void setPosition(int x, int y);
    
    /**
     * Gibt den Wert der Schachfigur zurück.
     *
     * @return der Wert der Schachfigur
     */
    int getWert();

    Zug[] getMoeglicheZuege();

    public boolean isWeiss();

    public boolean isSchwarz();




}