package com.hw1app;

/**
 * Created by atab7_000 on 9/9/2015.
 */
public class Piece {
    //Hare if true, hound if false
    private String type;

    //location
    private int x, y;

    public Piece(String type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Piece piece = (Piece) o;

        if (getX() != piece.getX()) return false;
        return getY() == piece.getY();

    }

    @Override
    public int hashCode() {
        int result = getX();
        result = 31 * result + getY();
        return result;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
