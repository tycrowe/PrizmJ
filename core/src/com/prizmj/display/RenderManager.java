package com.prizmj.display;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

/**
 * com.prizmj.display.RenderManager in PrizmJ
 */
public class RenderManager {

    private boolean rooms = true;
    private boolean graph = true;

    // Y value that inc/dec according to the Prizm.J Wall height. Any room with a Y value below this value will be rendered.
    // Set to 0 to render all.
    private float renderingDegree = 0;
    private float renderPeak = PrizmJ.WALL_HEIGHT;

    private Blueprint blueprint;

    RenderManager(Blueprint blueprint) {
        this.blueprint = blueprint;
        calculateRenderPeak();
    }

    void switchDimension(Dimension dimension) {
        blueprint.getAllModels().forEach(model -> model.setDimensionView(dimension));
    }

    void render(ModelBatch modelBatch, Environment environment) {
        if(rooms) {
            blueprint.getAllModels().forEach(model -> {
                if(model.getRoom().getY() <= renderingDegree || renderingDegree == -1)
                    model.render(modelBatch, environment);
            });
        } if(graph)
            blueprint.getGeometricNetworkModel().render(modelBatch, environment);
    }

    void toggleRooms() {
        this.rooms = !rooms;
    }

    void toggleGraph() {
        this.graph = !graph;
    }

    void incrementHeightDegree() {
        if(renderingDegree < renderPeak)
            renderingDegree += PrizmJ.WALL_HEIGHT;
    }

    void decrementHeightDegree() {
        if(renderingDegree > 0)
            renderingDegree -= PrizmJ.WALL_HEIGHT;
        else
            renderingDegree = 0;
    }

    private void calculateRenderPeak() {
        // Find the render peak or room with highest y value
        blueprint.getAllModels().forEach(model -> {
            if(model.getRoom().getY() > renderPeak) {
                renderPeak += PrizmJ.WALL_HEIGHT;
            }
        });
        this.renderingDegree = renderPeak;
    }
}
