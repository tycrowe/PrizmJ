package com.prizmj.display.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.prizmj.display.Blueprint;
import com.prizmj.display.PrizmJ;
import com.prizmj.display.PrizmJ;
import com.prizmj.display.parts.Door;
import com.prizmj.display.parts.Hallway;
import com.prizmj.display.parts.Stairwell;
import com.prizmj.display.parts.abstracts.Room;
import com.prizmj.display.simulation.components.Edge;
import com.prizmj.display.simulation.components.Vertex;
import com.prizmj.display.simulation.dijkstra.DirectedGraph;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by GrimmityGrammity on 11/16/2016.
 *
 * A Geometric Network Model(GNM) is one of the three key components of PrizmJ.
 *
 * A GNM is a topological data model that represents the adjacency,
 *  connectivity, and hierarchical relationship between 3D entities in
 *  graph theory.
 */
public class GNM {

    private PrizmJ prizmJ;
    private ModelBuilder modelBuilder;

    private DirectedGraph graph;

    private Blueprint blueprint;

    private int retardVar = create();

    public GNM(PrizmJ prizmJ, Blueprint blueprint) {
        this.prizmJ = prizmJ;
        this.blueprint = blueprint;
        this.modelBuilder = prizmJ.getModelBuilder();
        this.graph = new DirectedGraph();
    }

    // TODO: I'm retarded - Grimace
    private int create() {
        return 1 + 2 + 3;
    }

    /**
     * Compiles the edges and vertex' from the given blueprint.
     *
     * ForEach Room:
     *      If Hallway:
     *          Create vertices at ends of hallway
     *          Attach with edge
     *      Else:
     *          Create a vertex
     *          ForEach Door
     *              Create a vertex
     *              Attach door to room with edge
     *
     * ForEach vertex in the graph:
     *      If Door:
     *          Attach door to secondRoom by edge
     *      If Hallway:
     *          Compute location of new vertex
     *          Clear hallway's edges
     *          Insert new vertex into hallway
     *          Reconstruct edges
     *      Else if Stairs:
     *          Attach upper stairwell with downstairs
     *
     * @param blueprint - The supplied blueprint to map into the DirectedGraph
     */
    public void compile(Blueprint blueprint) {
        // Create vertices for each room and door
        // Connect rooms with their doors
        blueprint.getAllModels().forEach(rm -> {

            // Create initial hallway
            if (rm.getRoom() instanceof Hallway) {
                Vertex A, B;
                // Create North/South vertices
                if (((Hallway) rm.getRoom()).getUpDown()) {
                    A = new Vertex(rm.getRoom().getX(), rm.getRoom().getY() + (PrizmJ.WALL_HEIGHT / 2), rm.getRoom().getZ() + (rm.getRoom().getHeight() / 2), rm.getRoom());
                    B = new Vertex(rm.getRoom().getX(), rm.getRoom().getY() + (PrizmJ.WALL_HEIGHT / 2), rm.getRoom().getZ() - (rm.getRoom().getHeight() / 2), rm.getRoom());
                // Create East/West vertices
                } else {
                    A = new Vertex(rm.getRoom().getX() + (rm.getRoom().getWidth() / 2), rm.getRoom().getY() + (PrizmJ.WALL_HEIGHT / 2), rm.getRoom().getZ(), rm.getRoom());
                    B = new Vertex(rm.getRoom().getX() - (rm.getRoom().getWidth() / 2), rm.getRoom().getY() + (PrizmJ.WALL_HEIGHT / 2), rm.getRoom().getZ(), rm.getRoom());
                }
                // Add vertices to hallway
                ((Hallway) rm.getRoom()).addVertex(A);
                ((Hallway) rm.getRoom()).addVertex(B);
                // Add vertices to graph
                addVertex(A);
                addVertex(B);
                // Create edges
                Edge ab = new Edge(A, B); // North to south
                Edge ba = new Edge(B, A); // South to north
                // Add edges to hallway
                ((Hallway) rm.getRoom()).addEdge(ab);
                ((Hallway) rm.getRoom()).addEdge(ba);
                // Add edges to graph
                addEdge(ab);
                addEdge(ba);

            // Create rooms/stairs/doors
            } else {
                // Create vertex for room
                Vector2 center = getCenter(rm.getRoom());
                Vertex room = new Vertex(center.x, rm.getRoom().getY() + PrizmJ.WALL_HEIGHT / 2, center.y, rm.getRoom());
                addVertex(room);
                // Create vertex for each door if needed, link door with room via edge
                if(rm.getDoors().size > 0) rm.getDoors().forEach(door -> {
                    Vertex vDoor;
                    vDoor = graph.getVertexFromRoom(door);
                    // If the vertex doesn't exist
                    if(vDoor == null){
                        // If the door belongs to this room, create it relative to our location
                        if(door.getFirstRoom().getRoom().getName().compareTo(room.getRoom().getName()) == 0) {
                            vDoor = new Vertex(room.getRoom().getX() + door.getX(), room.getRoom().getY() + PrizmJ.WALL_HEIGHT / 2, room.getRoom().getZ() + door.getZ(), door);
                        // Else the door belongs to another room, create it relative to that room
                        } else {
                            vDoor = new Vertex(door.getFirstRoom().getRoom().getX() + door.getX(), door.getFirstRoom().getRoom().getY() + PrizmJ.WALL_HEIGHT / 2, door.getFirstRoom().getRoom().getZ() + door.getZ(), door);
                        }
                        addVertex(vDoor);
                    }
                    // Link the door and this room
                    addEdge((new Edge(room, vDoor)));
                    addEdge((new Edge(vDoor, room)));
                });
            }
        });


        // Connect all doors with their secondRoom
        graph.getVertices().forEach(vertex -> {
            if (vertex.getRoom() instanceof Door) {
                Room secondRoom = ((Door) vertex.getRoom()).getSecondRoom().getRoom();
                // If room is a hallway
                if(secondRoom instanceof Hallway) {
                    Hallway hallway = (Hallway)((Door) vertex.getRoom()).getSecondRoom().getRoom();
                    Vertex hallVertex;
                    // Create new hall vertex
                    // North/South
                    if (hallway.getUpDown()) {
                        hallVertex = new Vertex(hallway.getX(), vertex.getY(), vertex.getZ(), ((Door) vertex.getRoom()).getSecondRoom().getRoom());
                    // East/West
                    } else {
                        hallVertex = new Vertex(vertex.getX(), vertex.getY(), hallway.getZ(), ((Door) vertex.getRoom()).getSecondRoom().getRoom());
                    }
                    // Remove current edges
                    hallway.getEdges().clear();
                    // Add vertex to hallway/model
                    addVertex(hallVertex);
                    hallway.addVertex(hallVertex);
                    Edge A, B;
                    // Recreate edges in hallway
                    for (int i = 0; i < hallway.getVertices().size - 1; i++) {
                        A = new Edge(hallway.getVertices().get(i), hallway.getVertices().get(i+1));
                        B = new Edge(hallway.getVertices().get(i+1), hallway.getVertices().get(i));
                        addEdge(A);
                        addEdge(B);
                        hallway.addEdge(A);
                        hallway.addEdge(B);
                    }
                    // Connect door with matching hallway vertex
                    addEdge(new Edge(vertex, hallVertex));
                    addEdge(new Edge(hallVertex, vertex));
                }
            } else if (vertex.getRoom() instanceof Stairwell) {
                if(((Stairwell) vertex.getRoom()).getDownstairs() != null){
                    addEdge(new Edge(vertex, graph.getVertexFromRoom(blueprint.getRoomModelByName(((Stairwell) vertex.getRoom()).getDownstairs()).getRoom())));
                    addEdge(new Edge(vertex, graph.getVertexFromRoom(blueprint.getRoomModelByName(((Stairwell) vertex.getRoom()).getDownstairs()).getRoom())));
                }
            }
        });
        update();
    }

    /**
     * Add edge into the graph.
     * Constructs the model and line mesh part.
     * @param edge
     */
    private void addEdge(Edge edge) {
        if (!graph.getEdgesFromVertex(edge.getEnd()).contains(edge, false)) {
            Model line;
            modelBuilder.begin();
            MeshPartBuilder builder = modelBuilder.part(
                    "line",
                    1,
                    VertexAttributes.Usage.Normal | VertexAttributes.Usage.Position,
                    new Material(ColorAttribute.createDiffuse(Color.WHITE)));
            builder.line(edge.getStart().getX(), edge.getStart().getY(), edge.getStart().getZ(),
                    edge.getEnd().getX(), edge.getEnd().getY(), edge.getEnd().getZ());
            line = modelBuilder.end();
            edge.setModel(line);
        }
        graph.addEdge(edge);
    }

    /**
     * Add vertex to the graph.
     * Creates a sphere to represent the vertex.
     * @param vertex
     */
    private void addVertex(Vertex vertex) {
        graph.addVertex(vertex);
        vertex.setModel(modelBuilder.createSphere(
                1, 1, 1,
                16, 16,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                VertexAttributes.Usage.Normal | VertexAttributes.Usage.Position
        ));
        vertex.updatePosition();
    }

    public void update() {
        graph.getGraph().forEach((vertex, edges) -> {
            if(vertex.getRoom().getSmokeDensity() > 0)
                vertex.update();
            edges.forEach(Edge::update);
        });
    }

    public void update2() {
        Map<Vertex, Array<Edge>> map = graph.getGraph();
        Iterator i = map.entrySet().iterator();
        while(i.hasNext()) {
            Map.Entry set = (Map.Entry) i.next();
            Vertex v = (Vertex) set.getKey();
            Array<Edge> e = (Array<Edge>) set.getValue();
            if(v.getRoom().getSmokeDensity() > 0)
                v.update();
            for(int x = 0; x < e.size; x++)
                e.get(x).update();
        }
    }

    public void render(ModelBatch batch, Environment environment) {
        graph.getGraph().forEach((n, a) -> {
            n.render(batch, environment);
            a.forEach(edge -> edge.render(batch, environment));
        });
    }

    public DirectedGraph getGraph() {
        return graph;
    }

    @Deprecated
    private float computeAreaPolygon(Room room) {
        float sum;

        float westX = room.getX() - room.getWidth();
        float eastX = room.getX() + room.getWidth();
        float northZ = room.getZ() - room.getHeight();
        float southZ = room.getZ() + room.getHeight();

        sum = ((((westX * northZ) - (eastX * northZ)) +
                ((eastX * southZ) - (eastX * northZ)) +
                ((eastX * southZ) - (westX * southZ))) * 0.5f);

        return sum;
    }

    /**
     * Compute the centroid of a polygon.
     * Current doesn't work, maybe used in the future.
     * @param room
     * @return
     */
    @Deprecated
    private Vector2 computeCentroids(Room room) {
        float x , y;
        float area = computeAreaPolygon(room);

        float westX = room.getX() - room.getWidth();
        float eastX = room.getX() + room.getWidth();
        float northZ = room.getZ() - room.getHeight();
        float southZ = room.getZ() + room.getHeight();

        x = (((westX + eastX) * ((westX * northZ) - (eastX * northZ))) +
                ((eastX + eastX) * ((eastX * southZ) - (eastX * northZ))) +
                ((eastX + westX) * ((eastX * southZ) - (westX * southZ)))) /
                (6 * area);

        y = (((northZ + northZ) * ((westX * northZ) - (eastX * northZ))) +
                ((northZ + southZ) * ((eastX * southZ) - (eastX * northZ))) +
                ((southZ + southZ) * ((eastX * southZ) - (westX * southZ)))) /
                (6 * area);
        return new Vector2(x, y);
    }

    /**
     * Why exactly do we need to do the math above? This does virtually the exact same thing 10* faster.
     * Unless the node's height in the representation graph NEEDS to be exactly the center of the room, there's no
     * reason why 'y' can't equal (PrizmJ.WALL_HEIGHT / 2) + room.getY() Don't make this stupid if we don't have too
     * @param room - The room to calculate upon.
     * @return - Returns a vector for the coordinates of the center of the room.
     */
    public Vector2 getCenter(Room room) {
        float x1 = room.getX() - room.getWidth();
        float x2 = room.getX() + room.getWidth();
        float z1 = room.getZ() - room.getHeight();
        float z2 = room.getZ() + room.getHeight();
        return new Vector2((x1 + x2) / 2, (z1 + z2) / 2);
    }
}
