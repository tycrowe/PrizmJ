package com.prizmj.display.simulation.dijkstra;

import com.badlogic.gdx.utils.Array;
import com.prizmj.display.parts.abstracts.Room;
import com.prizmj.display.simulation.components.Edge;
import com.prizmj.display.simulation.components.Vertex;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by BBCommand on 11/17/2016.
 */
public class DirectedGraph {

    // Map of Nodes to outgoing Edges. Each set of edges is stored
    // in an array list.
    private final Map<Vertex, Array<Edge>> graph = new HashMap<>();


    public Array<Vertex> getVertices() {
        Array<Vertex> vertices = new Array<>();
        vertices.addAll(graph.keySet().toArray(new Vertex[graph.size()]));
        return vertices;
    }

    public Array<Edge> getEdgesFromVertex(Vertex vertex) {
        return graph.get(vertex);
    }

    public Vertex getVertexFromRoom(Room room) {
        final Vertex[] ret = {null};
        graph.forEach((vertex, edges) -> {
            if (vertex.getRoom().equals(room)) ret[0] = vertex;
        });
        return ret[0];
    }

    public Array<Edge> getEdges() {
        Array<Edge> edges = new Array<>();
        graph.values().forEach(subArray -> {
            subArray.forEach(edge -> {
                edges.add(edge);
            });
        });
        return edges;
    }

    /**
     * Adds a node to the graph. Does nothing if it is already added.
     *
     * @param vertex - The node to add
     * @return - True if successful, false otherwise
     */
    public boolean addVertex(Vertex vertex) {

        // If the node is in the graph, do nothing
        if (graph.containsKey(vertex))
            return false;

        // Add the node to the graph with an empty set of edges.
        graph.put(vertex, new Array<>());
        return true;
    }

    /**
     * Adds an edge to the graph. Does nothing if edge's start/end nodes
     * are not in the graph.
     *
     * @param edge - The edge to add
     * @return
     */
    public void addEdge(Edge edge) {
        // Ensure both nodes exist in the graph
        if (!graph.containsKey(edge.getStart()) || !graph.containsKey(edge.getEnd()))
            throw new NoSuchElementException("Both nodes must exist in the graph.");

        // Add the edge
        graph.get(edge.getStart()).add(edge);
    }

    /**
     * Removes an edge from the graph. Does nothing if node's start/end nodes
     * are not in the graph.
     *
     * @param edge - The edge to remove
     */
    public void removeEdge(Edge edge) {
        // Ensure both nodes exist in the graph
        if (!graph.containsKey(edge.getStart()) || !graph.containsKey(edge.getEnd()))
            throw new NoSuchElementException("Both nodes must exist in the graph.");

        // Remove the edge
        graph.get(edge.getStart()).removeValue(edge, false);
    }

    /**
     * Removes an edge from the graph. Does nothing if start/end nodes
     * are not in the graph.
     *
     * @param start - Start node of edge to remove
     * @param end   - End node of edge to remove
     */
    public void removeEdge(Vertex start, Vertex end) {
        // Ensure both nodes exist in the graph
        if (!graph.containsKey(start) || !graph.containsKey(end))
            throw new NoSuchElementException("Both nodes must exist in the graph.");

        // For each edge outgoing from the start node
        graph.get(start).forEach((edge) -> {
            // If the edge connects to end node
            if (edge.getEnd().equals(end))
                // Remove the edge
                graph.get(start).removeValue(edge, false);
        });

    }

    public Map<Vertex, Array<Edge>> getGraph() {
        return graph;
    }

}
