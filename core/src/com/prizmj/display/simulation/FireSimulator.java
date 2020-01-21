package com.prizmj.display.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.prizmj.display.Blueprint;
import com.prizmj.display.PrizmJ;
import com.prizmj.display.models.RoomModel;
import com.prizmj.display.simulation.components.Edge;
import com.prizmj.display.simulation.components.Vertex;
import com.prizmj.display.simulation.dijkstra.Dijkstra;

/**
 * Created by GrimmityGrammity on 11/16/2016.
 *
 * A Fire Simulator is one of the three key components of PrizmJ.
 *
 * A Fire Simulator simulates the spreading of fire within a Building
 *  and performs optimal path analysis on the burning building.
 *  A Fire Simulator requires a Building to burn and a GNM of that building to
 *  perform analysis.
 *
 */
public class FireSimulator {

    private GNM gnm;
    private Blueprint blueprint;
    private PrizmJ prizmJ;

    private boolean simulationRunning;

    private Array<Vertex> latestResults;

    public FireSimulator(PrizmJ prizmJ, Blueprint bprint) {
        gnm = bprint.getGeometricNetworkModel();
        blueprint = bprint;
        this.prizmJ = prizmJ;
        simulationRunning = false;
    }

    public void startFireSimulation(String name) {
        RoomModel room = blueprint.getRoomModelByName(name);
        this.simulationRunning = true;
        PrizmJ.writeToConsole("Starting fire simulation with room: " + name + ".");
        // room.startSmokeSimulation(builder, 0.85f, 0.0146f, 0.5f, gnm);
        // Firefighters arrive and traverse graph
        Timer.schedule(new Timer.Task() {
           @Override
           public void run() {
               gnm.update();
               latestResults = Dijkstra.shortestPaths(gnm.getGraph(), gnm.getGraph().getVertexFromRoom(blueprint.getRoomModelByName("f1_basicroom_1").getRoom()));
           }
        }, 30, 0.6f, 0);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if(isEngulfed() && simulationRunning) {
                    PrizmJ.writeToConsole("Entire building has been engulfed in flames.");
                    simulationRunning = false;
                }
            }
        }, 0, 1);
        room.createSimulation(0.85f, 0.0146f, gnm).startSmokeSimulation(prizmJ.getModelBuilder(), 0.5f);
    }

    public void stepFireSimulation(float step) {
        blueprint.getAllModels().forEach(rm -> rm.stepSmokeSimulation(prizmJ.getModelBuilder(), step));
        Dijkstra.shortestPaths(gnm.getGraph(), gnm.getGraph().getVertexFromRoom(blueprint.getRoomModelByName("f1_basicroom_1").getRoom()));
    }

    public boolean isEngulfed() {
        for(RoomModel model : blueprint.getAllModels()) {
            if(model.getRoom().getSmokeDensity() < 0.85f) return false;
        }
        return true;
    }

    public GNM getGNM() {
        return gnm;
    }

    public boolean isSimulationRunning() {
        return simulationRunning;
    }

    public Array<Vertex> getLatestResults() {
        if(latestResults != null)
            return latestResults;
        else
            return Dijkstra.shortestPaths(gnm.getGraph(), gnm.getGraph().getVertexFromRoom(blueprint.getRoomModelByName("f1_basicroom_1").getRoom()));
    }

    public Array<Vertex> getLatestResultsFromRoom(String roomName) {
        return Dijkstra.shortestPaths(gnm.getGraph(), gnm.getGraph().getVertexFromRoom(blueprint.getRoomModelByName(roomName).getRoom()));
    }

    public void simulateDijkstraResults(Array<Vertex> dr) {
        final int[] x = {0};
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Vertex v = dr.get(x[0]);
                v.changeColor(Color.GREEN);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        v.changeColor(Color.RED);
                    }
                }, 0.5f);
                x[0]++;
            }
        }, 0, 1f, dr.size - 1);
    }

}
