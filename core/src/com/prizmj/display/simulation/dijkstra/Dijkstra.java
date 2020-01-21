package com.prizmj.display.simulation.dijkstra;
/**************************************************************************
 * File: Dijkstra.java
 * Author: Keith Schwarz (htiek@cs.stanford.edu)
 *
 * An implementation of Dijkstra's single-source shortest path algorithm.
 * The algorithm takes as input a directed graph with non-negative edge
 * costs and a source node, then computes the shortest path from that node
 * to each other node in the graph.
 *
 * The algorithm works by maintaining a priority queue of nodes whose
 * priorities are the lengths of some path from the source node to the
 * node in question.  At each step, the algortihm dequeues a node from
 * this priority queue, records that node as being at the indicated
 * distance from the source, and then updates the priorities of all nodes
 * in the graph by considering all outgoing edges from the recently-
 * dequeued node to those nodes.
 *
 * In the course of this algorithm, the code makes up to |E| calls to
 * decrease-key on the heap (since in the worst case every edge from every
 * node will yield a shorter path to some node than before) and |V| calls
 * to dequeue-min (since each node is removed from the prioritiy queue
 * at most once).  Using a Fibonacci heap, this gives a very good runtime
 * guarantee of O(|E| + |V| lg |V|).
 *
 * This implementation relies on the existence of a FibonacciHeap class, also
 * from the Archive of Interesting Code.  You can find it online at
 *
 *         http://keithschwarz.com/interesting/code/?dir=fibonacci-heap
 */

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.prizmj.display.simulation.components.Edge;
import com.prizmj.display.simulation.components.Vertex;

import java.util.*; // For HashMap

public final class Dijkstra {

    private static boolean running = false;

    /**
     * Toggles Dijkstra running on/off to simulate realistic movement of
     * firefighters.
     */
    public static void toggleRunning() {
        running = !running;
    }

    /**
     * Given a directed, weighted graph G and a source node s, produces the
     * distances from s to each other node in the graph.  If any nodes in
     * the graph are unreachable from s, they will be reported at distance
     * +infinity.
     *
     * @param graph  The graph upon which to run Dijkstra's algorithm.
     * @param source The source node in the graph.
     * @return A map from nodes in the graph to their distances from the source.
     */
    public static Array<Vertex> shortestPaths(DirectedGraph graph, Vertex source) {
        /* Create a Fibonacci heap storing the distances of unvisited nodes
         * from the source node.
         */
        FibonacciHeap pq = new FibonacciHeap();

        /* The Fibonacci heap uses an internal representation that hands back
         * Entry objects for every stored element.  This map associates each
         * node in the graph with its corresponding Entry.
         */
        Map<Vertex, FibonacciHeap.Entry> entries = new HashMap<Vertex, FibonacciHeap.Entry>();

        /* Maintain a map from nodes to their distances.  Whenever we expand a
         * node for the first time, we'll put it in here.
         */
        Array<Vertex> result = new Array<>();

        /* Add each node to the Fibonacci heap at distance +infinity since
         * initially all nodes are unreachable.
         */
        for (Vertex node : graph.getVertices())
            entries.put(node, pq.enqueue(node, Float.POSITIVE_INFINITY));

        /* Update the source so that it's at distance 0.0 from itself; after
         * all, we can get there with a path of length zero!
         */
        pq.decreaseKey(entries.get(source), 0.0f);

        /* Keep processing the queue until no nodes remain. */
        while (!pq.isEmpty()) {

//            if(running) {
                /* Grab the current node.  The algorithm guarantees that we now
                 * have the shortest distance to it.
                 */
            FibonacciHeap.Entry curr = pq.dequeueMin();

            // Change colour of current vertex
            curr.getValue().changeColor(Color.GREEN);

            /* Store this in the result table. */
            result.add(curr.getValue());

                /* Update the priorities of all of its edges. */
            for (Edge arc : graph.getEdgesFromVertex(curr.getValue())) {

                // Change edge colour
                arc.changeColor(Color.GREEN);

//                    if (running) {
                        /* If we already know the shortest path from the source to
                         * this node, don't add the edge.
                         */
                if (result.contains(arc.getEnd(), false)) {
                    arc.changeColor(Color.WHITE);
                    continue;
                }

                        /* Compute the cost of the path from the source to this node,
                         * which is the cost of this node plus the cost of this edge.
                         */
                float pathCost = curr.getPriority() + arc.getTraversalTime();

                        /* If the length of the best-known path from the source to
                         * this node is longer than this potential path cost, update
                         * the cost of the shortest path.
                         */
                FibonacciHeap.Entry dest = entries.get(arc.getEnd());
                if (pathCost < dest.getPriority())
                    pq.decreaseKey(dest, pathCost);

                // Change colour back
                arc.changeColor(Color.WHITE);
//                    }
            }

            // Change colour back
            curr.getValue().changeColor(Color.RED);

        }
//        }

        /* Finally, report the distances we've found. */
        return result;
    }
}


//package com.prizmj.display.simulation.dijkstra;
//
//import com.badlogic.gdx.utils.Array;
//import com.prizmj.display.simulation.GNM;
//import com.prizmj.display.simulation.components.Edge;
//import com.prizmj.display.simulation.components.Vertex;
//
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
///**
// * Created by BBCommand on 11/20/2016.
// */
//public class Dijkstra {
//
//    private GNM gnm;
//    private Array<Edge> edges;
//    private Array<Vertex> vertices;
//
//    private Set<Vertex> settledVertices;
//    private Set<Vertex> unsettledVertices;
//    private Map<Vertex, Vertex> predecessors;
//
//    public Dijkstra(GNM gnm) {
//        this.gnm = gnm;
//        this.edges = gnm.getGraph().getEdges();
//        this.vertices = gnm.getGraph().getVertices();
//    }
//
//    public void execute(Vertex source) {
//        settledVertices = new HashSet<Vertex>();
//        unsettledVertices = new HashSet<Vertex>();
//        unsettledVertices.add(source);
//        while(unsettledVertices.size() > 0) {
//            Vertex vertex = getMinimum(unsettledVertices);
//            settledVertices.add(vertex);
//            unsettledVertices.remove(vertex);
//            findMinimalDistances(vertex);
//        }
//    }
//
//    private void findMinimalDistances(Vertex vertex) {
//        gnm.getGraph().getEdgesFromVertex(vertex).forEach((edge -> {
//            if(edge.getEnd().getWeight() > vertex.getWeight() + edge.getTraversalTime()) {
//                edge.getEnd().setWeight(vertex.getWeight() + edge.getTraversalTime());
//                predecessors.put(edge.getEnd(), vertex);
//                unsettledVertices.add(edge.getEnd());
//            }
//        }));
//    }
//
//}
