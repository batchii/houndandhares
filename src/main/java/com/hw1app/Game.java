package com.hw1app;

/**
 * Created by atab7_000 on 9/8/2015.
 */
public class Game {
    private int gameId;

    private Player playerOne;

    private Player playerTwo;

    private String currentState;

    public Game(int gameId, Player playerOne){
        this.gameId = gameId;
        this.playerOne = playerOne;
        this.currentState = "WAITING_FOR_SECOND_PLAYER";
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

    public void setGameId(int gameId) {
        this.gameId = gameId;
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
