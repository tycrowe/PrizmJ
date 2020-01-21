package com.prizmj.display.parts;

import com.badlogic.gdx.graphics.Color;
import com.prizmj.display.parts.abstracts.Room;

/**
 * com.prizmj.display.buildingparts.BasicRoom in PrizmJ
 */
public class Stairwell extends Room {

    private String downstairs;
    private String upstairs;

    /**
     * 2d Representation of Room
     * <p>
     * Example implementation:
     * new Room("BigRoom", 0, 0, 25, 25);
     *
     * @param name - The name of the room
     * @param x - The x transform for the starting position in the 3d world.
     * @param y - ' y transform '
     * @param width - The width of the room. (approx.)
     * @param height - The height of the room. (approx.)
     * @param floorColor - The color the floor of the room.
     */
    public Stairwell(String name, float x, float y, float z, float width, float height, Color floorColor) {
        super(name, x, y, z, width, height, floorColor);
    }

    public String getDownstairs() {
        return downstairs;
    }

    public void setDownstairs(String downstairs) {
        this.downstairs = downstairs;
    }

    public String getUpstairs() {
        return upstairs;
    }

    public void setUpstairs(String upstairs) {
        this.upstairs = upstairs;
    }
}
