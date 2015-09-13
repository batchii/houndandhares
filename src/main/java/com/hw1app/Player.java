package com.hw1app;

import java.util.ArrayList;

/**
 * Created by atab7_000 on 9/9/2015.
 */
public class Player {

    private String playerId;

    private String pieceType;

    private ArrayList<Piece> pieces;

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPieceType() {
        return pieceType;
    }

    public void setPieceType(String pieceType) {
        this.pieceType = pieceType;
    }

    public Player(String playerId, String pieceType) {
        this.playerId = playerId;
        this.pieceType = pieceType;
        System.out.println("PieceType " + pieceType);
        this.pieces = new ArrayList<Piece>();
        if (this.pieceType.equals("HOUND")) {
            pieces.add(new Piece("HOUND", 1, 0));
            pieces.add(new Piece("HOUND", 0, 1));
            pieces.add(new Piece("HOUND", 1, 2));
        } else {
            pieces.add(new Piece("HARE", 4, 1));
        }
    }
    public ArrayList<Piece> getPieces(){
        return pieces;
    }


}
