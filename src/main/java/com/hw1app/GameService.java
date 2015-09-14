package com.hw1app;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.activation.DataSource;
import java.util.*;

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
        Graph board = Util.makeBoard();
        Game game = new Game(gameId, playerOne, board);
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
        if (pieceType.equals("HOUND")) {
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

    public JsonObject movePiece(JsonObject req) {
        JsonObject json = new JsonObject();
        Game game = gameHashMap.get(Integer.parseInt(req.get("gameId").getAsString()));
        if (game == null) {
            json.addProperty("reason", "INVALID_GAME_ID");
            System.out.println("Game was not found");
            return json;
        }

        String playerId = req.get("playerId").getAsString();
        System.out.println("PlayerId: " + playerId);
        Player player;
        if (!game.getPlayerOne().getPlayerId().equals(playerId)) {
            if (!game.getPlayerTwo().getPlayerId().equals(playerId)) {
                json.addProperty("reason", "INVALID_PLAYER_ID");
                System.out.println("problem");
                return json;
            } else {
                player = game.getPlayerTwo();
                System.out.println("set here");
            }
        } else {
            player = game.getPlayerOne();
        }

        String state = game.getCurrentState();
        if ((state.equals("TURN_HOUND") && player.getPieceType().equals("HOUND")) ||
                (state.equals("TURN_HARE") && player.getPieceType().equals("HARE"))) {
            int fromX = req.get("fromX").getAsInt();
            int fromY = req.get("fromY").getAsInt();
            int toX = req.get("toX").getAsInt();
            int toY = req.get("toY").getAsInt();
            JsonObject res =  makeMove(player, fromX, fromY, toX, toY);
            checkState(game);
            return res;
        } else {
            json.addProperty("reason", "INCORRECT_TURN");
            return json;
        }

    }

    private Player getHoundPlayer(Game game){
        if(game.getPlayerOne().getPieceType().equals("HOUND")){
            return game.getPlayerOne();
        } else {
            return game.getPlayerTwo();
        }
    }

    private void checkState(Game game) {
        //Check for win conditions
        Graph board = game.getBoard();
        ArrayList<Piece> pieces = new ArrayList<Piece>();

        Player hound = getHoundPlayer(game);
        for(Piece piece : hound.getPieces()) {
            pieces.add(piece);
        }
        Collections.sort(pieces, new Comparator<Piece>() {

            public int compare(Piece o1, Piece o2) {
                if(Integer.compare(o1.getX(), o2.getX()) == 0){
                    return Integer.compare(o1.getY(), o2.getY());
                } else {
                    return Integer.compare(o1.getX(), o2.getX());
                }
            }
        });
        Player hare;
        if(hound.getPlayerId().contains("One")){
            pieces.add(game.getPlayerTwo().getPieces().get(0));
            hare = game.getPlayerTwo();
            System.out.println("player two is hare");
        } else {
            pieces.add(game.getPlayerOne().getPieces().get(0));
            hare = game.getPlayerOne();
            System.out.println("player one is hare");
        }


        //Hounds win?
        ArrayList<Vertex> adjacents = board.getAdjacentVertices(new Vertex(hare.getPieces().get(0).getX(), hare.getPieces().get(0).getY()));
        if(adjacents.size() == 4){
            for(Piece piece : hound.getPieces()){
                if(adjacents.contains(new Vertex(piece.getX(), piece.getY()))){
                    if(adjacents.contains(new Vertex(piece.getX(), piece.getY()))){
                        if(adjacents.contains(new Vertex(piece.getX(), piece.getY()))){
                            game.setCurrentState("WIN_HOUND");
                        }
                    }
                }
            }
        }
        //Hare win?
        //Pick hound with the least x index
        Piece min = hound.getPieces().get(0);
        for(Piece piece : hound.getPieces()){
            if(piece.getX()<min.getX()){
                min = piece;
            }
        }
        if(hare.getPieces().get(0).getX() <= min.getX()){
            game.setCurrentState("WIN_HARE");
        }




        //Switch turn
        if(game.getCurrentState().equals("TURN_HOUND")){
            game.setCurrentState("TURN_HARE");
        } else if (game.getCurrentState().equals("TURN_HARE")) {
            game.setCurrentState("TURN_HOUND");
        }
        //Stalling?
    }

    private JsonObject makeMove(Player player, int fromX, int fromY, int toX, int toY) {
        JsonObject res = new JsonObject();
        ArrayList<Piece> pieces = player.getPieces();
        for (Piece piece : pieces) {
            if (piece.getX() == fromX && piece.getY() == fromY) {
                System.out.println("this is the right piece");
                if (piece.getType().equals("HOUND")) {
                    if (((toX-piece.getX()) == 0 || (toX-piece.getX()) == 1) && ((toY-piece.getY() <=1) && (toY-piece.getY() >=-1))) { //is vaild move
                        player.setPlayerId(player.getPlayerId() + "1");
                        res.addProperty("playerId", player.getPlayerId());
                        piece.setX(toX);
                        piece.setY(toY);

                        return res;
                    }
                } else if(piece.getType().equals("HARE")){
                    if(((toX-piece.getX()) <=1 && (toX-piece.getX() >=-1) && (toY-piece.getY() <=1 && (toY-piece.getY() >=-1)))){
                        player.setPlayerId(player.getPlayerId() + "1");
                        res.addProperty("playerId", player.getPlayerId());
                        piece.setX(toX);
                        piece.setY(toY);
                        return res;
                    }
                }
            }
        }
        res.addProperty("reason", "ILLEGAL_MOVE");
        return res;
    }


    public static class GameServiceException extends Exception {
        public GameServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }


}
