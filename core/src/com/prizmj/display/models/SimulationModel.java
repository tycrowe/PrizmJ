package com.prizmj.display.models;

import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.prizmj.display.PrizmJ;
import com.prizmj.display.parts.Door;
import com.prizmj.display.parts.Hallway;
import com.prizmj.display.parts.Stairwell;
import com.prizmj.display.parts.abstracts.Room;
import com.prizmj.display.simulation.GNM;

/**
 * com.prizmj.display.models.SimulationModel in PrizmJ
 *
 *  This is the simulation model. It's mainly responsible for stepping through the fire simulation and replicating
 *  of the basic "startSmokeSimulation" and "startFireSimulation".
 *
 */
public class SimulationModel {

    private RoomModel model;

    private boolean showingSmoke = true;
    private boolean simulationRunning = false;

    private float apexPoint = 1.0f;
    private float smokeSpeed = 0.25f;

    private GNM gnm;

    public SimulationModel(RoomModel model, float apexPoint, float smokeSpeed, GNM gnm) {
        this.model = model;
        this.apexPoint = apexPoint;
        this.smokeSpeed = smokeSpeed;
        this.gnm = gnm;
    }

    public SimulationModel(RoomModel model, float apexPoint, GNM gnm) {
        this.model = model;
        this.apexPoint = apexPoint;
        this.gnm = gnm;
    }

    /**
     * Steps through the simulation
     *  -- Replicates simulation but step by step instead.
     */
    public void stepSimulation(ModelBuilder builder, float step) {
        if(getSmokeDensity() < apexPoint) {
            // Smoke density is less than the apex point, so continue simulation.
            model.recreateSmokeCube(builder, getSmokeDensity() + step);
            if(getSmokeDensity() > 0.5f) {
                // For hallways
                if (getRoom() instanceof Hallway) {
                    Array<Door> doors = ((Hallway) getRoom()).getHallwayDoors();
                    // To avoid using the same iterator.
                    for(int x = 0; x < doors.size; x++) {
                        Door door = doors.get(x);
                        if (door.getFirstRoom().getRoom().getSmokeDensity() <= 0) {
                            door.getFirstRoom().createSimulation(gnm).stepSmokeSimulation(builder, 0.25f);
                            PrizmJ.writeToConsole(String.format("Fire has spread from %s to %s!", door.getSecondRoom().getRoom().getName(), door.getFirstRoom().getRoom().getName()));
                        }
                    }
                    // For rooms/stairs
                } else {
                    Array<Door> doors = model.getDoors();
                    for(int x = 0; x < doors.size; x++) {
                        Door door = doors.get(x);
                        if (door.getSecondRoom().getRoom().getSmokeDensity() <= 0) {
                            door.getSecondRoom().createSimulation(gnm).stepSmokeSimulation(builder, 0.25f);
                            PrizmJ.writeToConsole(String.format("Fire has spread to %s!", door.getSecondRoom().getRoom().getName()));
                        } else if (door.getFirstRoom().getRoom().getSmokeDensity() <= 0) {
                            door.getFirstRoom().createSimulation(gnm).stepSmokeSimulation(builder, 0.25f);
                            PrizmJ.writeToConsole(String.format("Fire has spread to %s!", door.getFirstRoom().getRoom().getName()));
                        }
                    }
                }
                // Spread the fire up/down stairs
                if (getRoom() instanceof Stairwell) {
                    if(((Stairwell) getRoom()).getUpstairs() != null) {
                        RoomModel upstairs = model.getBlueprint().getRoomModelByName2(((Stairwell) getRoom()).getUpstairs());
                        if (upstairs.getRoom().getSmokeDensity() <= 0) {
                            upstairs.createSimulation(gnm).stepSmokeSimulation(builder, 0.25f);
                            PrizmJ.writeToConsole(String.format("Fire has spread to %s!", upstairs.getRoom().getName()));
                        }
                    }
                    if(((Stairwell) getRoom()).getDownstairs() != null) {
                        RoomModel downstairs = model.getBlueprint().getRoomModelByName2(((Stairwell) getRoom()).getDownstairs());
                        if (downstairs.getRoom().getSmokeDensity() <= 0) {
                            downstairs.createSimulation(gnm).stepSmokeSimulation(builder, 0.25f);
                            PrizmJ.writeToConsole(String.format("Fire has spread to %s!", downstairs.getRoom().getName()));
                        }
                    }
                }
            }
            gnm.update2();
        }
    }

    /**
     * Runs the smoke simulation
     */
    public void startSmokeSimulation(ModelBuilder builder, float repeatSpeed) {
        if(!simulationRunning) {
            simulationRunning = true;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (getSmokeDensity() < apexPoint){
                        model.recreateSmokeCube(builder, getSmokeDensity() + smokeSpeed);
                        if(getSmokeDensity() > 0.5f) {
                            // For hallways
                            if (getRoom() instanceof Hallway) {
                                ((Hallway) getRoom()).getHallwayDoors().forEach(door -> {
                                    if (door.getFirstRoom().getRoom().getSmokeDensity() <= 0) {
                                        door.getFirstRoom().createSimulation(0.85f, 0.0146f, gnm).startSmokeSimulation(builder, 0.5f);
                                        PrizmJ.writeToConsole(String.format("Fire has spread from the hallway to %s!", door.getFirstRoom().getRoom().getName()));
                                    }
                                });
                                // For rooms/stairs
                            } else {
                                model.getDoors().forEach(door -> {
                                    if (door.getSecondRoom().getRoom().getSmokeDensity() <= 0) {
                                        door.getSecondRoom().createSimulation(0.85f, 0.0146f, gnm).startSmokeSimulation(builder, 0.5f);
                                        PrizmJ.writeToConsole(String.format("Fire has spread to %s!", door.getSecondRoom().getRoom().getName()));
                                    } else if (door.getFirstRoom().getRoom().getSmokeDensity() <= 0) {
                                        door.getFirstRoom().createSimulation(0.85f, 0.0146f, gnm).startSmokeSimulation(builder, 0.5f);
                                        PrizmJ.writeToConsole(String.format("Fire has spread to %s!", door.getFirstRoom().getRoom().getName()));
                                    }
                                });
                            }
                            // Spread the fire up/down stairs
                            if (getRoom() instanceof Stairwell) {
                                if(((Stairwell) getRoom()).getUpstairs() != null) {
                                    RoomModel upstairs = model.getBlueprint().getRoomModelByName(((Stairwell) getRoom()).getUpstairs());
                                    if (upstairs.getRoom().getSmokeDensity() <= 0) {
                                        upstairs.createSimulation(0.85f, 0.0146f, gnm).startSmokeSimulation(builder, 0.5f);
                                        PrizmJ.writeToConsole(String.format("Fire has spread to %s!", upstairs.getRoom().getName()));
                                    }
                                }
                                if(((Stairwell) getRoom()).getDownstairs() != null) {
                                    RoomModel downstairs = model.getBlueprint().getRoomModelByName(((Stairwell) getRoom()).getDownstairs());
                                    if (downstairs.getRoom().getSmokeDensity() <= 0) {
                                        downstairs.createSimulation(0.85f, 0.0146f, gnm).startSmokeSimulation(builder, 0.5f);
                                        PrizmJ.writeToConsole(String.format("Fire has spread to %s!", downstairs.getRoom().getName()));
                                    }
                                }
                            }
                        }
                    } else {
                        simulationRunning = false;
                        cancel();
                    }
                    gnm.update();
                }
            }, 0, repeatSpeed);
        }
    }

    public boolean isShowingSmoke() {
        return showingSmoke;
    }

    public void setShowingSmoke(boolean showingSmoke) {
        this.showingSmoke = showingSmoke;
    }

    public boolean isSimulationRunning() {
        return simulationRunning;
    }

    private float getSmokeDensity() {
        return model.getRoom().getSmokeDensity();
    }

    private Room getRoom() {
        return model.getRoom();
    }
}
