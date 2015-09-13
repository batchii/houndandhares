//-------------------------------------------------------------------------------------------------------------//
// Code based on a tutorial by Shekhar Gulati of SparkJava at
// https://blog.openshift.com/developing-single-page-web-applications-using-java-8-spark-mongodb-and-angularjs/
//-------------------------------------------------------------------------------------------------------------//

package com.hw1app;

import com.google.gson.Gson;
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
//            try {
            //do stuff
            JsonParser p = new JsonParser();
            JsonObject req = p.parse(request.body()).getAsJsonObject();

            Game game = gameService.startNewGame(req.get("pieceType").getAsString());
            response.status(201);
            JsonObject json = new JsonObject();
            json.addProperty("gameId", game.getGameId());
            json.addProperty("playerId", game.getPlayerOne().getPlayerId());
            json.addProperty("pieceType", game.getPlayerOne().getPieceType());
            return json;
//            }
//            } catch (GameService.GameServiceException ex) {
//                logger.error("Failed to create new entry");
//                response.status(500);
//            }

        }, new JsonTransformer());

        //Join Game
        put(API_CONTEXT + "/games/:id", "application/json", (request, response) -> {
            JsonObject json = gameService.joinGame(request.params(":id"));
            response.status(200);

            return json;


        }, new JsonTransformer());


        //Fetch State
        get(API_CONTEXT + "/games/:id/state", "application/json", (request, response) -> {

            String gameState = gameService.getGameState(request.params(":id"));
            JsonObject json = new JsonObject();
            json.addProperty("state", gameState);
            return json;
        }, new JsonTransformer());

        //Fetch Board
        get(API_CONTEXT + "/games/:id/board", "application/json", (request, response) -> {

            JsonArray pieces = gameService.getGameBoard(request.params(":id"));
            return pieces;
        }, new JsonTransformer());

        //Move a piece
        post(API_CONTEXT + "/games/:id/turns", "application/json", (request, response) -> {
            JsonParser p = new JsonParser();
            JsonObject req = p.parse(request.body()).getAsJsonObject();
            gameService.movePiece(req);

            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

//        //^^ converts a java object to a json object
//
//
//        //Get a specific todo by id
//        get(API_CONTEXT + "/games/:id/state", "application/json", (request, response) -> {
//            try {
//                //Returns a java object
//                return todoService.find(request.params(":id"));
//            } catch (TodoService.TodoServiceException ex) {
//                logger.error(String.format("Failed to find object with id: %s", request.params(":id")));
//                response.status(500);
//                return Collections.EMPTY_MAP;
//            }
//        }, new JsonTransformer());
//        //^^ converts a java object to a json object
//
//        //Get all todos
//        get(API_CONTEXT + "/games", "application/json", (request, response)-> {
//            try {
//                return todoService.findAll() ;
//            } catch (TodoService.TodoServiceException ex) {
//                logger.error("Failed to fetch the list of todos");
//                response.status(500);
//                return Collections.EMPTY_MAP;
//            }
//        }, new JsonTransformer());
//
//
//        //Add a new todo
//        put(API_CONTEXT + "/games/:id", "application/json", (request, response) -> {
//            try {
//                return todoService.update(request.params(":id"), request.body());
//            } catch (TodoService.TodoServiceException ex) {
//                logger.error(String.format("Failed to update todo with id: %s", request.params(":id")));
//                response.status(500);
//                return Collections.EMPTY_MAP;
//            }
//        }, new JsonTransformer());
//
//        //Delete an existing todo
//        delete(API_CONTEXT + "/todos/:id", "application/json", (request, response) -> {
//            try {
//                todoService.delete(request.params(":id"));
//                response.status(200);
//            } catch (TodoService.TodoServiceException ex) {
//                logger.error(String.format("Failed to delete todo with id: %s", request.params(":id")));
//                response.status(500);
//            }
//            return Collections.EMPTY_MAP;
//        }, new JsonTransformer());
//    }
    }


}
