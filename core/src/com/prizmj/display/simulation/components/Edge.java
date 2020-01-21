package com.prizmj.display.simulation.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.UUID;

/**
 * Created by GrimmityGrammity on 11/14/2016.
 */

// TODO: Possible inner class?
public class Edge {

    private UUID id;

    private Vertex start;
    private Vertex end;

    private float length;
    private float walkingSpeed;
    private float traversalTime;

    private Model model;
    private ModelInstance modelInstance;

    public Edge (Vertex start, Vertex end) {
        this.id = UUID.randomUUID();
        this.start = start;
        this.end = end;
        length = computeLength();
        setWalkingSpeed(((start.getWalkingSpeed() + end.getWalkingSpeed()) / 2));
        traversalTime = (length / walkingSpeed);
    }

    public void update() {
        walkingSpeed = ((start.getWalkingSpeed() + end.getWalkingSpeed()) / 2);
        traversalTime = (length / walkingSpeed);
    }

    public void changeColor(Color color) {
        modelInstance.materials.first().set(ColorAttribute.createDiffuse(color));
    }

    private float computeLength() {
        Vector3 x = new Vector3(start.getX(), start.getY(), start.getZ());
        Vector3 y = new Vector3(end.getX(), end.getZ(), end.getZ());
        // len = Math.abs((float)Math.sqrt((x * x) + (y * y)));
        return x.dst(y);
    }

    public UUID getId() { return id; }

    public void setId(UUID id) {
        this.id = id;
    }

    public Vertex getStart() {
        return start;
    }

    public void setStart(Vertex start) {
        this.start = start;
    }

    public Vertex getEnd() {
        return end;
    }

    public void setEnd(Vertex end) {
        this.end = end;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getWalkingSpeed() {
        return walkingSpeed;
    }

    public void setWalkingSpeed(float walkingSpeed) {
        this.walkingSpeed = walkingSpeed;
    }

    public float getTraversalTime() {
        return traversalTime;
    }

    public void setTraversalTime(float traversalTime) {
        this.traversalTime = traversalTime;
    }

    public void render(ModelBatch batch, Environment environment) {
        batch.render(modelInstance, environment);
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
}
