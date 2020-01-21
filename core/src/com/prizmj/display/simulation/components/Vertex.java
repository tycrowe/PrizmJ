package com.prizmj.display.simulation.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.prizmj.display.parts.Door;
import com.prizmj.display.parts.abstracts.Room;

import java.util.UUID;

/**
 * Created by GrimmityGrammity on 11/14/2016.
 */

// TODO: Possible inner class?
public class Vertex {

    private UUID id;

    private float weight;

    private float sootDensity;
    private float smokeDensity;
    private float walkingSpeed;

    private float x;
    private float y;
    private float z;

    private Room room;

    private Model model;
    private ModelInstance modelInstance;

    public Vertex(float x, float y, float z, Room rm) {
        this.x = x;
        this.y = y;
        this.z = z;
        room = rm;
        sootDensity = 0;
        smokeDensity = 0;
        walkingSpeed = 1.5f;
    }
    public float getSootDensity(){ return sootDensity;}

    /**
     * All the forumla here are wrong, the actual ones appeared to be
     * useless, so they were replaced to generate more realistic values
     * for the sake of demonstration.
     */
    public void update() {
        if(getRoom() instanceof Door) {
            smokeDensity = Math.min(((Door) getRoom()).getFirstRoom().getRoom().getSmokeDensity(), ((Door) getRoom()).getSecondRoom().getRoom().getSmokeDensity());
        } else {
            smokeDensity = room.getSmokeDensity();
        }
//        sootDensity = (float) (Math.log10(1 - smokeDensity) / 760000);
        sootDensity = (float) (Math.log10(1 - smokeDensity) / 700);
//        walkingSpeed = Math.max(0.1f, ((1.5f / 0.706f) * (0.706f + (-0.057f * (7600f * sootDensity)))));
        walkingSpeed = Math.max(0.1f, 1.5f - (((1.5f / 0.706f) * (0.706f + (-0.057f * (7600f * sootDensity)))) - 1.5f));
    }

    public void render(ModelBatch batch, Environment environment) {
        batch.render(modelInstance, environment);
    }

    public void changeColor(Color color) {
        modelInstance.materials.first().set(ColorAttribute.createDiffuse(color));
    }

    public void moveTo(float x, float y, float z) {
        Node node = model.nodes.first();
        node.globalTransform.translate(x, y, z);
        modelInstance.transform.set(node.globalTransform);
    }

    public void updatePosition() {
        moveTo(x, y, z);
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public float getSmokeDensity() {
        return smokeDensity;
    }

    public void setSmokeDensity(float smokeDensity) {
        this.smokeDensity = smokeDensity;
    }

    public float getWalkingSpeed() {
        return walkingSpeed;
    }

    public void setWalkingSpeed(float walkingSpeed) {
        this.walkingSpeed = walkingSpeed;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
        setModelInstance(new ModelInstance(model));
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public void setModelInstance(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
    }

    @Override
    public String toString() {
        return new Vector3(getX(), getY(), getZ()).toString();
    }

}