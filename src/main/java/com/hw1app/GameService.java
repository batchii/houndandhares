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

    /**
     * Stores the current games
     */
    private HashMap<Integer, Game> gameHashMap;

    private static final Gson gson = new Gson();

    public GameService() {
        gameHashMap = new HashMap<Integer, Game>();
    }

    /**
     *
     * @param pieceType Player one's piece type
     * @return the game that gets initialized
     */
    public Game startNewGame(String pieceType) {
        Player playerOne = new Player("playerOne", pieceType);
        Graph board = Util.makeBoard();
        Game game = new Game(gameId, playerOne, board);
        gameId++;
        gameHashMap.put(game.getGameId(), game);
        return game;
    }


    /**
     *
     * @param id - id of the game
     * @return game board pieces as a jsonArray
     */
    public JsonArray getGameBoard(String id) throws InvalidGameIdException {
        JsonArray gamePiecesList = new JsonArray();
        Game game = gameHashMap.get(Integer.parseInt(id));
        if (game == null) {
            throw new InvalidGameIdException("Not a valid game Id: " + id, null);
        }
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

    /**
     *
     * @param id the id of the game being joined
     * @return { gameId: <id>, playerId: <id>, pieceType: <type> }
     * @throws InvalidGameIdException
     * @throws SecondPlayerAlreadyJoinedException
     */
    public JsonObject joinGame(String id) throws InvalidGameIdException, SecondPlayerAlreadyJoinedException {

        Game game = gameHashMap.get(Integer.parseInt(id));

        JsonObject json = new JsonObject();
        try {
            String pieceType = game.getPlayerOne().getPieceType();
            Player playerTwo;
            if (pieceType.equals("HOUND")) {
                playerTwo = new Player("playerTwo", "HARE");
            } else {
                playerTwo = new Player("playerTwo", "HOUND");
            }
            if (game.getPlayerTwo() != null) {
                throw new SecondPlayerAlreadyJoinedException("A second player already exists", null);
            }
            game.setPlayerTwo(playerTwo);

            game.setCurrentState("TURN_HOUND");


            json.addProperty("gameId", id);
            json.addProperty("playerId", playerTwo.getPlayerId());
            json.addProperty("pieceType", playerTwo.getPieceType());

            return json;
        } catch (NullPointerException ex) {
            throw new InvalidGameIdException("Not a valid game Id: " + id, ex);

        }
    }

    /**
     *
     * @param id game id
     * @return state of the game
     * @throws InvalidGameIdException
     */
    public String getGameState(String id) throws InvalidGameIdException {
        Game game = gameHashMap.get(Integer.parseInt(id));
        if (game == null) {
            throw new InvalidGameIdException("Not a valid game Id: " + id, null);
        }
        return game.getCurrentState();
    }

    /**
     *
     * @param req the contents of the request
     * @return { playerId: <id> }
     * @throws InvalidGameIdException { reason: "INVALID_GAME_ID" }
     * @throws InvalidPlayerIdException  { reason: "INVALID_PLAYER_ID" }
     * @throws IncorrectTurnException  { reason: "INCORRECT_TURN" }
     * @throws IllegalMoveException { reason: "ILLEGAL_MOVE" }
     */
    public JsonObject movePiece(JsonObject req) throws InvalidGameIdException, InvalidPlayerIdException, IncorrectTurnException, IllegalMoveException {
        JsonObject json = new JsonObject();
        Game game = gameHashMap.get(Integer.parseInt(req.get("gameId").getAsString()));
        if (game == null) {
            throw new InvalidGameIdException("Not a valid game Id: " + req.get("gameId").getAsString(), null);
        }

        String playerId = req.get("playerId").getAsString();

        Player player;
        if (!game.getPlayerOne().getPlayerId().equals(playerId)) {
            if (!game.getPlayerTwo().getPlayerId().equals(playerId)) {
                throw new InvalidPlayerIdException("Not a valid player Id: " + playerId, null);
            } else {
                player = game.getPlayerTwo();
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
            JsonObject res = makeMove(player, fromX, fromY, toX, toY, game);
            if (!res.has("reason")) {
                checkState(game);
            }
            return res;
        } else {
            throw new IncorrectTurnException("Incorrect turn", null);
        }

    }

    /**
     *
     * @param game The game to be used
     * @return Return's the player object that is the hound
     */
    private Player getHoundPlayer(Game game) {
        if (game.getPlayerOne().getPieceType().equals("HOUND")) {
            return game.getPlayerOne();
        } else {
            return game.getPlayerTwo();
        }
    }

    /**
     * Checks for win states a changes turns
     * @param game The game we are analyzing
     */
    private void checkState(Game game) {
        //Check for win conditions
        Graph board = game.getBoard();
        ArrayList<Piece> pieces = new ArrayList<Piece>();

        Player hound = getHoundPlayer(game);
        pieces = game.getPieces();


        Player hare;
        if (hound.getPlayerId().contains("One")) {
            hare = game.getPlayerTwo();
        } else {
            hare = game.getPlayerOne();
        }

        //Check for stalling
        HashMap<ArrayList<Piece>, Integer> stalling = game.getStalling();
        if (stalling.containsKey(pieces)) {
            stalling.put(pieces, stalling.get(pieces) + 1);
            if (stalling.get(pieces) == 3) {
                game.setCurrentState("WIN_HARE_BY_STALLING");
                return;
            }
        } else {
            stalling.put(pieces, 1);
        }

        //Hounds win?
        ArrayList<Vertex> adjacents = board.getAdjacentVertices(new Vertex(hare.getPieces().get(0).getX(), hare.getPieces().get(0).getY()));
        if (adjacents.size() == 3) {
            ArrayList<Piece> hounds = hound.getPieces();
            if (adjacents.contains(new Vertex(hounds.get(0).getX(), hounds.get(0).getY()))) {
                if (adjacents.contains(new Vertex(hounds.get(1).getX(), hounds.get(1).getY()))) {
                    if (adjacents.contains(new Vertex(hounds.get(2).getX(), hounds.get(2).getY()))) {
                        game.setCurrentState("WIN_HOUND");
                        return;
                    }
                }
            }
        }
        //Hare win?
        //Pick hound with the least x index
        Piece min = hound.getPieces().get(0);
        for (Piece piece : hound.getPieces()) {
            if (piece.getX() < min.getX()) {
                min = piece;
            }
        }
        if (hare.getPieces().get(0).getX() <= min.getX()) {
            game.setCurrentState("WIN_HARE");
            return;
        }

        //Switch turn
        if (game.getCurrentState().equals("TURN_HOUND")) {
            game.setCurrentState("TURN_HARE");
        } else if (game.getCurrentState().equals("TURN_HARE")) {
            game.setCurrentState("TURN_HOUND");
        }
    }

    /**
     * Makes move if valid
     * @param player player who made the move
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @param game the game being played
     * @return
     * @throws IllegalMoveException
     */
    private JsonObject makeMove(Player player, int fromX, int fromY, int toX, int toY, Game game) throws IllegalMoveException {
        JsonObject res = new JsonObject();
        ArrayList<Piece> pieces = player.getPieces();
        for (Piece piece : pieces) {
            if (piece.getX() == fromX && piece.getY() == fromY) {
                //Player is hound or hare
                if (piece.getType().equals("HOUND")) {
                    if (game.getBoard().getAdjacentVertices(new Vertex(piece.getX(), piece.getY())).contains(new Vertex(toX, toY))) {
                        //Hounds can only move forward or stay in the same x coordinate
                        if (toX >= piece.getX()) {
                            if (!game.getPieces().contains(new Piece(piece.getType(), toX, toY))) { //Check if piece is in this place
                                piece.setX(toX);
                                piece.setY(toY);

                                res.addProperty("playerId", player.getPlayerId());
                                return res;
                            }
                        }
                    }
                } else if (piece.getType().equals("HARE")) {
                    if (game.getBoard().getAdjacentVertices(new Vertex(piece.getX(), piece.getY())).contains(new Vertex(toX, toY))) {
                        if (!game.getPieces().contains(new Piece(piece.getType(), toX, toY))) {
                            res.addProperty("playerId", player.getPlayerId());
                            piece.setX(toX);
                            piece.setY(toY);
                            return res;
                        }
                    }
                }
            }
        }
        throw new IllegalMoveException("Illegal move made", null);

    }


    public static class GameServiceException extends Exception {
        public GameServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class InvalidGameIdException extends Exception {
        public InvalidGameIdException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class SecondPlayerAlreadyJoinedException extends Exception {
        public SecondPlayerAlreadyJoinedException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class InvalidPlayerIdException extends Exception{
        public InvalidPlayerIdException(String message, Throwable cause){
            super(message, cause);
        }
    }
    public static class IncorrectTurnException extends Exception{
        public IncorrectTurnException(String message, Throwable cause){
            super(message, cause);
        }
    }
    public static class IllegalMoveException extends Exception{
        public IllegalMoveException(String message, Throwable cause){
            super(message, cause);
        }
    }
}
