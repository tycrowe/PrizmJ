package com.prizmj.display.parts;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.prizmj.display.parts.abstracts.Room;
import com.prizmj.display.simulation.components.Edge;
import com.prizmj.display.simulation.components.Vertex;

import java.util.Comparator;

/**
 * com.prizmj.display.parts.Hallway in PrizmJ
 */
public class Hallway extends Room {

    // True = North/South
    // False = East/West
    private boolean updown;

    private Array<Vertex> vertices;
    private Array<Edge> edges;
    private Array<Door> hallwayDoors;

    /**
     * 2d Representation of Room
     * <p>
     * Example implementation:
     * new Room("BigRoom", 0, 0, 25, 25);
     *
     * @param name
     * @param x
     * @param y
     * @param z
     * @param width
     * @param height
     * @param floorColor
     */
    public Hallway(String name, float x, float y, float z, float width, float height, Color floorColor, boolean updown) {
        super(name, x, y, z, width, height, floorColor);
        if(width == height) setWidth(width + 1);
        this.updown = updown;
        vertices = new Array<>();
        edges = new Array<>();
        hallwayDoors = new Array<>();
    }

    public void addDoor(Door door) {
        hallwayDoors.add(door);
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public void addVertex(Vertex vertex) {
        vertices.add(vertex);
        // North/South
        if (updown) {
            vertices.sort((v1, v2) -> {
                if (v1.getRoom().getZ() < v2.getRoom().getZ()) {
                    return -1;
                } else if (v1.getRoom().getZ() > v2.getRoom().getZ()) {
                    return 1;
                } else {
                    return 0;
                }
            });
        // East/West
        } else {
            vertices.sort((v1, v2) -> {
                if (v1.getRoom().getX() < v2.getRoom().getX()) {
                    return -1;
                } else if (v1.getRoom().getX() > v2.getRoom().getX()) {
                    return 1;
                } else {
                    return 0;
                }
            });
        }
    }

    public Array<Door> getHallwayDoors() {
        return hallwayDoors;
    }

    public Array<Vertex> getVertices() {
        return vertices;
    }

    public Array<Edge> getEdges() {
        return edges;
    }

    public boolean getUpDown() {
        return updown;
    }
}
