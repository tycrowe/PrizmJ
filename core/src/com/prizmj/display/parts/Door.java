package com.prizmj.display.parts;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.prizmj.display.Cardinal;
import com.prizmj.display.models.RoomModel;
import com.prizmj.display.parts.abstracts.Room;

import java.util.Vector;

/**
 * com.prizmj.display.buildingparts.Door in PrizmJ
 */
public class Door extends Room{

    private RoomModel connection1;
    private RoomModel connection2;

    private float x;
    private float y;
    private float z;

    // Since our rooms only have 4 walls, we'll allow a positional for better understanding the doors position in relation to the room(s) it occupies
    // This variable also only represents connection1's side.
    // 1-2 South and North respectively
    // 3-4 East and West respectively
    private int side;
    private Cardinal cardinal;

    public Door(int side) {
        //this.cardinal = cardinal;
        this.side = side;
    }

    public Door(Cardinal side) {
        this.side = side.getSide();
    }

    public int getSide() {
        return side;
    }

    public RoomModel getFirstRoom() {
        return connection1;
    }

    public void setInitialRoom(RoomModel connection1) {
        this.connection1 = connection1;
    }

    public RoomModel getSecondRoom() {
        return connection2;
    }

    public void setConnectedRoom(RoomModel connection2) {
        this.connection2 = connection2;
    }

    public Vector3 getPosition() {
        return new Vector3(x, y, z);
    }

    public void updatePosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public Cardinal getCardinal() {
        return cardinal;
    }

    public void setCardinal(Cardinal cardinal) {
        this.cardinal = cardinal;
    }

    @Override
    public String toString() {
        return (getFirstRoom() != null) ? "Room 1: " + getFirstRoom().getRoom().getName() + "\n\tDoor Loc:" + new Vector3(x, y, z).toString() : "Door Loc:" + new Vector3(x, y, z).toString();
    }

}