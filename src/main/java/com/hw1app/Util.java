package com.hw1app;

import java.util.ArrayList;

/**
 * Created by atab7_000 on 9/13/2015.
 */
public class Util {

    public static Graph makeBoard() {
        Graph board = new Graph(false);
        Vertex v;
        v = new Vertex(0, 1);
        ArrayList<Edge<Vertex>> edges = new ArrayList<>();
        edges.add(new Edge<>(new Vertex(1, 0), 0));
        edges.add(new Edge<>(new Vertex(1, 1), 0));
        edges.add(new Edge<>(new Vertex(1, 2), 0));
        board.add(v, edges);

        board.addEdge(new Vertex(1, 0), new Vertex(2, 0), 0);
        board.addEdge(new Vertex(1, 0), new Vertex(2, 1), 0);
        board.addEdge(new Vertex(1, 0), new Vertex(1, 1), 0);

        board.addEdge(new Vertex(1, 1), new Vertex(2, 1), 0);
        board.addEdge(new Vertex(1, 1), new Vertex(1, 2), 0);

        board.addEdge(new Vertex(1, 2), new Vertex(2, 1), 0);
        board.addEdge(new Vertex(1, 2), new Vertex(2, 2), 0);

        board.addEdge(new Vertex(2, 0), new Vertex(2, 1), 0);
        board.addEdge(new Vertex(2, 0), new Vertex(3, 0), 0);

        board.addEdge(new Vertex(2, 1), new Vertex(2, 2), 0);
        board.addEdge(new Vertex(2, 1), new Vertex(3, 0), 0);
        board.addEdge(new Vertex(2, 1), new Vertex(3, 1), 0);
        board.addEdge(new Vertex(2, 1), new Vertex(3, 2), 0);

        board.addEdge(new Vertex(2, 2), new Vertex(3, 2), 0);

        board.addEdge(new Vertex(3, 0), new Vertex(3, 1), 0);
        board.addEdge(new Vertex(3, 0), new Vertex(4, 1), 0);

        board.addEdge(new Vertex(3, 1), new Vertex(4, 1), 0);
        board.addEdge(new Vertex(3, 1), new Vertex(3, 2), 0);

        return board;
    }

}
