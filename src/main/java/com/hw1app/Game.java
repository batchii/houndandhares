package com.hw1app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Created by atab7_000 on 9/8/2015.
 */
public class Game {

    private int gameId;

    private Player playerOne;

    private Player playerTwo;

    private String currentState;

    public HashMap<ArrayList<Piece>, Integer> stalling;

    private Graph<Vertex> board;

    public Graph<Vertex> getBoard() {
        return board;
    }

    public HashMap<ArrayList<Piece>, Integer> getStalling() {
        return stalling;
    }

    public ArrayList<Piece> getPieces(){
        ArrayList<Piece> pieces = new ArrayList<>();
        Player hound, hare;
        if(playerOne.getPieceType().equals("HOUND")){
            hound = playerOne;
            hare = playerTwo;
        } else {
            hound = playerTwo;
            hare = playerOne;
        }
        for(Piece piece : hound.getPieces()){
            pieces.add(piece);
        }
        Collections.sort(pieces, new Comparator<Piece>() {

            public int compare(Piece o1, Piece o2) {
                if (Integer.compare(o1.getX(), o2.getX()) == 0) {
                    return Integer.compare(o1.getY(), o2.getY());
                } else {
                    return Integer.compare(o1.getX(), o2.getX());
                }
            }
        });

        pieces.addAll(hare.getPieces().stream().collect(Collectors.toList()));
        return pieces;
    }

    public Game(int gameId, Player playerOne, Graph<Vertex> board) {
        this.gameId = gameId;
        this.playerOne = playerOne;
        this.currentState = "WAITING_FOR_SECOND_PLAYER";
        this.board = board;
        this.stalling = new HashMap<ArrayList<Piece>, Integer>();
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public int getGameId() {
        return gameId;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(Player playerOne) {
        this.playerOne = playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(Player playerTwo) {
        this.playerTwo = playerTwo;
    }
}
