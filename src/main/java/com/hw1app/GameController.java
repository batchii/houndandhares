//-------------------------------------------------------------------------------------------------------------//
// Code based on a tutorial by Shekhar Gulati of SparkJava at
// https://blog.openshift.com/developing-single-page-web-applications-using-java-8-spark-mongodb-and-angularjs/
//-------------------------------------------------------------------------------------------------------------//

package com.hw1app;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static spark.Spark.*;

public class GameController {

    private static final String API_CONTEXT = "/hareandhounds/api";

    private final GameService gameService;

    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    public GameController(GameService gameService) {
        this.gameService = gameService;
        setupEndpoints();
    }

    private void setupEndpoints() {
        //Create a new TODO
        post(API_CONTEXT + "/games", "application/json", (request, response) -> {
            JsonParser p = new JsonParser();
            JsonObject req = p.parse(request.body()).getAsJsonObject();

            Game game = gameService.startNewGame(req.get("pieceType").getAsString());
            response.status(201);
            JsonObject json = new JsonObject();
            json.addProperty("gameId", game.getGameId());
            json.addProperty("playerId", game.getPlayerOne().getPlayerId());
            json.addProperty("pieceType", game.getPlayerOne().getPieceType());
            return json;
        }, new JsonTransformer());

        //Join Game
        put(API_CONTEXT + "/games/:id", "application/json", (request, response) -> {
            try {
                JsonObject json = gameService.joinGame(request.params(":id"));
                response.status(200);
                return json;
            } catch (GameService.InvalidGameIdException ex) {
                response.status(404);
                return Collections.EMPTY_MAP;
            } catch (GameService.SecondPlayerAlreadyJoinedException ex) {
                response.status(410);
                return Collections.EMPTY_MAP;
            }

        }, new JsonTransformer());


        //Fetch State
        get(API_CONTEXT + "/games/:id/state", "application/json", (request, response) -> {
            try {
                String gameState = gameService.getGameState(request.params(":id"));
                JsonObject json = new JsonObject();
                json.addProperty("state", gameState);
                response.status(200);
                return json;
            } catch (GameService.InvalidGameIdException ex) {
                response.status(404);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        //Fetch Board
        get(API_CONTEXT + "/games/:id/board", "application/json", (request, response) -> {
            try {
                JsonArray pieces = gameService.getGameBoard(request.params(":id"));
                response.status(200);
                return pieces;

            } catch (GameService.InvalidGameIdException ex) {
                response.status(404);
                return Collections.EMPTY_MAP;
            }

        }, new JsonTransformer());

        //Move a piece
        post(API_CONTEXT + "/games/:id/turns", "application/json", (request, response) -> {
            JsonParser p = new JsonParser();
            JsonObject req = p.parse(request.body()).getAsJsonObject();

            req.addProperty("gameId", request.params(":id"));

            try {
                JsonObject res = gameService.movePiece(req);
                response.status(200);
                return res;
            } catch (GameService.InvalidGameIdException ex) {
                JsonObject res = new JsonObject();
                response.status(404);
                res.addProperty("reason", "INVALID_GAME_ID");
                return res;
            } catch (GameService.InvalidPlayerIdException ex) {
                JsonObject res = new JsonObject();
                response.status(404);
                res.addProperty("reason", "INVALID_PLAYER_ID");
                return res;
            } catch (GameService.IncorrectTurnException ex) {
                JsonObject res = new JsonObject();
                response.status(422);
                res.addProperty("reason", "INCORRECT_TURN");
                return res;
            } catch (GameService.IllegalMoveException ex) {
                JsonObject res = new JsonObject();
                response.status(422);
                res.addProperty("reason", "ILLEGAL_MOVE");
                return res;
            }


        }, new JsonTransformer());
    }


}
