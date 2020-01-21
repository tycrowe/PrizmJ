package com.prizmj.display;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.prizmj.console.CommandFactory;
import com.prizmj.display.simulation.FireSimulator;

import javax.swing.*;

/**
 * com.prizmj.display.PrizmJ in PrizmJ
 */
public class PrizmJ extends ApplicationAdapter {
    public static final float WALL_HEIGHT = 2.5f;
    public static final float WALL_THICKNESS = 0.25f;
    public static final float WALL_OFFSET = 0.139f;

    public static final float DOOR_HEIGHT = 1.25f;
    public static final float DOOR_WIDTH = 1f;
    public static final Color DOOR_COLOR = Color.GOLD;

    public static final float STAIRWELL_WIDTH = 4;
    public static final float STAIRWELL_HEIGHT = 3;
    public static final Color STAIRWELL_COLOR = Color.VIOLET;

    public CameraInputController camController;

    private PerspectiveCamera pCamera;
    private float camSpeed = 0.25f;

    private FireSimulator fireSimulator;

    private ModelBuilder modelBuilder;
    private ModelBatch modelBatch;
    private Environment environment;
    private Blueprint print;

    private RenderManager manager;

    private OrthographicCamera debugCam;
    private SpriteBatch batch; // Used mainly for debug purposes //
    private BitmapFont font;
    private boolean DEBUG = true;

    public int currentDimension = 2;
    private int floorplan = 1;

    private Runtime runtime;
    public static JTextArea console;
    public static JTextField cmdLine;
    private CommandFactory commandFeedback;

    @Override
    public void create() {
        this.modelBatch = new ModelBatch();
        this.currentDimension = MathUtils.clamp(currentDimension, 2, 3);

        pCamera = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        pCamera.position.set(10f, 10f, 10f);
        pCamera.lookAt(0, 0, 0);
        pCamera.update();
        this.camController = new CameraInputController(pCamera);
        this.camController.forwardKey = Input.Keys.W;
        this.camController.backwardKey = Input.Keys.S;
        this.camController.rotateLeftKey = 0;
        this.camController.rotateRightKey = 0;
        Gdx.input.setInputProcessor(camController);

        this.environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        modelBuilder = new ModelBuilder();
        try {
            createBuilding();
        } catch (Exception e) {
            e.printStackTrace();
        }
        manager.switchDimension(Dimension.Environment_3D);
        if(DEBUG) {
            debugCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch = new SpriteBatch();
            font = new BitmapFont();
            font.setColor(Color.YELLOW);
        }
        this.runtime = Runtime.getRuntime();
        this.commandFeedback = new CommandFactory(this);
    }

    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(pCamera);
        manager.render(modelBatch, environment);
        modelBatch.end();

        if(DEBUG) {
            batch.begin();
            batch.setProjectionMatrix(debugCam.combined);
            font.draw(batch, String.format("FPS: %d", Gdx.graphics.getFramesPerSecond()), -395, 375);
            {
                font.draw(batch, "Heap Utilization Statistics", -395, 358);
                font.draw(batch, String.format("Free Memory: %dMB", runtime.freeMemory() / (1024 * 1024)), -390, 341);
                font.draw(batch, String.format("Total Memory: %dMB", runtime.totalMemory() / (1024 * 1024)), -390, 324);
                font.draw(batch, String.format("Used Memory: %dMB", (runtime.totalMemory() - runtime.freeMemory()) / (1024*1024)), -390, 307);
            }
            if(fireSimulator.isSimulationRunning())
                font.draw(batch, "Fire Simulation Running", -395, -305);
            batch.end();
            debugCam.update();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            DEBUG = !DEBUG;
            writeToConsole("Debug: " + DEBUG);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
            if (currentDimension == 3) {
                manager.switchDimension(Dimension.Environment_3D);
                currentDimension = 2;
            } else {
                manager.switchDimension(Dimension.Environment_2D);
                currentDimension = 3;
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            try {
                createBuilding();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.F4)) {
            floorplan++;
            try {
                createBuilding();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            manager.toggleRooms();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.F6)) {
            manager.toggleGraph();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.PAGE_UP)) {
            manager.incrementHeightDegree();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.PAGE_DOWN)) {
            manager.decrementHeightDegree();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            if(!fireSimulator.isSimulationRunning()) {
                String name = print.getRandomRoom().getRoom().getName();
                fireSimulator.startFireSimulation(name);
                writeToConsole(String.format("Beginning fire/smoke simulation in %s.", name));
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.DPAD_RIGHT)) {
            fireSimulator.stepFireSimulation(0.05f);
        }
        camController.update();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
    }

    public ModelBuilder getModelBuilder() {
        return modelBuilder;
    }

    public ModelBatch getModelBatch() {
        return modelBatch;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public int getCurrentDimension() {
        return currentDimension;
    }

    public FireSimulator getFireSimulator() {
        return fireSimulator;
    }

    public Blueprint getBlueprint() {
        return print;
    }

    public RenderManager getManager() {
        return manager;
    }

    public void createBuilding() throws Exception {
        if(print != null)
            this.print.dispose();
        this.print = new Blueprint(this);
        try {
            switch (floorplan) {
                case 1:
                    print.createHallway("f1_hallway_1", 0, 0, 0, 5, 20, Color.ORANGE, true).recreateSmokeCube(modelBuilder, 0);
                    print.createAttachingStairwell("f1_hallway_1", "f1_stairwell_1", 0, 0, 8.5f, Cardinal.EAST);
                    print.createAttachingRoom("f1_hallway_1", "f1_basicroom_1", 0, 0, 1.20f, 10, 12, Color.PURPLE, Cardinal.EAST);
                    print.createAttachingRoom("f1_basicroom_1", "f1_basicroom_2", -9.05f - WALL_THICKNESS, 0, 0, 6, 3, Color.CYAN, Cardinal.NORTH);
                    print.createAttachingRoom("f1_hallway_1", "f1_basicroom_3", 0, 0, 7.25f + WALL_THICKNESS, 5, 5, Color.GREEN, Cardinal.WEST);
                    print.createAttachingRoom("f1_hallway_1", "f1_basicroom_4", 0, 0, 1.25f, 5, 8, Color.NAVY, Cardinal.WEST);
                    print.createAttachingRoom("f1_hallway_1", "f1_basicroom_5", 0, 0, -6.25f, 5, 7.25f, Color.RED, Cardinal.WEST);
                    break;
                case 2:
                    print.createHallway("f1_hallway_1", 0, 0, 0, 5, 20, Color.ORANGE, true).recreateSmokeCube(modelBuilder, 0);
                    print.createHallway("f2_hallway_2", 0, PrizmJ.WALL_HEIGHT, 0, 5, 20, Color.ORANGE, true).recreateSmokeCube(modelBuilder, 0);

                    print.createAttachingStairwell("f1_hallway_1", "f1_stairwell_1", 0, 0, 8.5f, Cardinal.EAST);
                    print.createAttachingRoom("f1_hallway_1", "f1_basicroom_1", 0, 0, 1.20f, 10, 12, Color.PURPLE, Cardinal.EAST);
                    print.createAttachingRoom("f1_basicroom_1", "f1_basicroom_2", -9.05f - WALL_THICKNESS, 0, 0, 6, 3, Color.CYAN, Cardinal.NORTH);
                    print.createAttachingRoom("f1_hallway_1", "f1_basicroom_3", 0, 0, 7.25f + WALL_THICKNESS, 5, 5, Color.GREEN, Cardinal.WEST);
                    print.createAttachingRoom("f1_hallway_1", "f1_basicroom_4", 0, 0, 1.25f, 5, 8, Color.NAVY, Cardinal.WEST);
                    print.createAttachingRoom("f1_hallway_1", "f1_basicroom_5", 0, 0, -6.25f, 5, 7.25f, Color.RED, Cardinal.WEST);

                    print.createAttachingStairwell("f1_stairwell_1", "f2_stairwell_2", 0, 0, 0);
                    print.attachRoomByAxis("f2_hallway_2", "f2_stairwell_2", Cardinal.EAST);

                    print.createAttachingRoom("f2_hallway_2", "f2_basicroom_1", 0, PrizmJ.WALL_HEIGHT, 1.20f, 10, 12, Color.PURPLE, Cardinal.EAST);
                    print.createAttachingRoom("f2_basicroom_1", "f2_basicroom_2", -9.05f - WALL_THICKNESS, PrizmJ.WALL_HEIGHT, 0, 6, 3, Color.CYAN, Cardinal.NORTH);
                    print.createAttachingRoom("f2_hallway_2", "f2_basicroom_3", 0, PrizmJ.WALL_HEIGHT, 7.25f + WALL_THICKNESS, 5, 5, Color.GREEN, Cardinal.WEST);
                    print.createAttachingRoom("f2_hallway_2", "f2_basicroom_3", 0, PrizmJ.WALL_HEIGHT, 1.25f, 5, 8, Color.NAVY, Cardinal.WEST);
                    print.createAttachingRoom("f2_hallway_2", "f2_basicroom_4", 0, PrizmJ.WALL_HEIGHT, -6.25f, 5, 7.25f, Color.RED, Cardinal.WEST);
                    break;
                default:
                    print.createHallway("f1_hallway_1", 0, 0, 0, 3, 8, Color.GREEN, true).recreateSmokeCube(modelBuilder, 0);
                    print.createAttachingRoom("f1_hallway_1", "f1_basicroom_1", 0, 0, 0, 10, 8, Color.RED, Cardinal.EAST);
                    floorplan = 0;
                    break;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        print.createGraph();
        print.getRandomRoom().createSimulation(print.getGeometricNetworkModel());
        this.manager = new RenderManager(print);
        this.fireSimulator = new FireSimulator(this, print);
    }

    public static void setConsole(JTextArea area) {
        if(console == null) {
            console = area;
            console.append("Fire Simulation with testing environment: PrizmJ");
            writeControlHelpToConsole();
            writeToConsole("Click anywhere in the canvas or type 'help' into console to begin.");
        } else
            System.out.println("Please don't set a static object!");
    }

    public static void setCommand(JTextField field) {
        if(cmdLine == null) {
            cmdLine = field;
            cmdLine.setText("> ");
        } else
            System.out.println("Please don't set a static object!");
    }

    public static void clearCommandText() {
        cmdLine.setText("> ");
    }

    public static void writeToConsole(String data) {
        console.append("\n> " + data);
    }

    public static void writeControlHelpToConsole() {
        writeToConsole("Camera Controls: \n");
        console.append("\tRotating: Left Click and hold to rotate camera.\n");
        console.append("\tPanning: Right click and hold to pan camera.\n");
        console.append("\tZooming: Scroll wheel.");
        writeToConsole("Keyboard Controls: \n");
        console.append("\tF1: Toggle debug mode.\n");
        console.append("\tF2: Switch Dimension.\n");
        console.append("\tF3: Redraw building. (Resets Simulation)\n");
        console.append("\tF4: Change floor plan.\n");
        console.append("\tF5: Toggle rooms.\n");
        console.append("\tF6: Toggle graph.\n");
        console.append("\tPage Up: Go up one floor.\n");
        console.append("\tPage Down: Go down one floor.");
        writeToConsole("Simulation Controls: \n");
        console.append("\tR: Begin standard simulation.\n");
        console.append("\tRight Arrow/Right D-Pad: Step simulation.");
    }
}