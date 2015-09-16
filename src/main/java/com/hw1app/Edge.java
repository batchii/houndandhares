package com.hw1app;

/**
 * Created by Satshabad on Dec 22, 2011, https://github.com/Satshabad/Simple-Graph-Implementation
 *
 * */

public class Edge<V> {

    private V vertex;

    private int weight;

    public Edge(V vert, int w) {
        vertex = vert;
        weight = w;
    }

    public V getVertex() {
        return vertex;
    }

    public void setVertex(V vertex) {
        this.vertex = vertex;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String toString(){

        return "( "+ vertex + ", " + weight + " )";
    }

}
