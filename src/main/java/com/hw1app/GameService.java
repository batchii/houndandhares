package com.hw1app;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.activation.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by atab7_000 on 9/6/2015.
 */
public class GameService {
    private static int gameId = 1;

    private HashMap<Integer, Game> gameHashMap;

    private static final Gson gson = new Gson();

    //This is for the database
    public GameService() {
        gameHashMap = new HashMap<Integer, Game>();
    }

    public Game startNewGame(String pieceType) {
        Player playerOne = new Player("playerOne", pieceType);
        Game game = new Game(gameId, playerOne);
        gameId++;
        gameHashMap.put(game.getGameId(), game);
        return game;
    }

    /**
     * Return game board pieces as a jsonArray
     *
     * @param id - id of the game
     * @return
     */
    public JsonArray getGameBoard(String id) {
        JsonArray gamePiecesList = new JsonArray();
        Game game = gameHashMap.get(Integer.parseInt(id));
        Player playerOne = game.getPlayerOne();
        Player playerTwo = game.getPlayerTwo();
        ArrayList<Piece> pieces = playerOne.getPieces();
        for (Piece piece : pieces) {
            JsonObject playerPiece = new JsonObject();
            playerPiece.addProperty("pieceType", piece.getType());
            playerPiece.addProperty("x", piece.getX());
            playerPiece.addProperty("y", piece.getY());
            gamePiecesList.add(playerPiece);
        }
        if (playerTwo != null) {
            pieces = playerTwo.getPieces();
            for (Piece piece : pieces) {
                JsonObject playerPiece = new JsonObject();
                playerPiece.addProperty("pieceType", piece.getType());
                playerPiece.addProperty("x", piece.getX());
                playerPiece.addProperty("y", piece.getY());
                gamePiecesList.add(playerPiece);
            }
        }

        return gamePiecesList;
    }

    public JsonObject joinGame(String id) {
        Game game = gameHashMap.get(Integer.parseInt(id));
        JsonObject json = new JsonObject();
        String pieceType = game.getPlayerOne().getPieceType();
        Player playerTwo;
        if(pieceType.equals("HOUND")){
            playerTwo = new Player("playerTwo", "HARE");
        } else {
            playerTwo = new Player("playerTwo", "HOUND");
        }
        game.setPlayerTwo(playerTwo);

        game.setCurrentState("TURN_HOUND");


        json.addProperty("gameId", id);
        json.addProperty("playerId", playerTwo.getPlayerId());
        json.addProperty("pieceType", playerTwo.getPieceType());

        return json;

    }

    public String getGameState(String id) {
        return gameHashMap.get(Integer.parseInt(id)).getCurrentState();

    }

    public void movePiece(JsonObject req) {
        String playerId = req.get("playerId").toString();
        int fromX = req.get("fromX").getAsInt();
        int fromY = req.get("fromY").getAsInt();
        int toX = req.get("toX").getAsInt();
        int toY = req.get("toY").getAsInt();


    }


    public static class GameServiceException extends Exception {
        public GameServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }


}
