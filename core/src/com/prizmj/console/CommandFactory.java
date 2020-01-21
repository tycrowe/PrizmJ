package com.prizmj.console;

import com.prizmj.display.PrizmJ;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.prizmj.display.PrizmJ.cmdLine;

/**
 * com.prizmj.console.Command in PrizmJ
 */
public class CommandFactory {

    private PrizmJ prizmJ;

    private String command;
    private String[] args;

    public CommandFactory(PrizmJ prizmJ) {
        this.prizmJ = prizmJ;
        cmdLine.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE && cmdLine.getCaretPosition() == 2)
                    e.consume();
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // User intends to submit a command!
                    build(cmdLine.getText().substring(2).split(" "));
                    executeCommand();
                }
            }
        });
        cmdLine.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(!cmdLine.getText().startsWith("> "))
                    PrizmJ.clearCommandText();
                cmdLine.setCaretPosition(2);
            }
        });
    }

    public void build(String[] command) {
        setCommand(command[0]);
        setArgs(new String[command.length - 1]);
        if((command.length - 1) != 0) {
            for (int i = 1; i < command.length; i++) {
                args[i - 1] = command[i];
            }
        }
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String getCommand() {
        return command;
    }

    public String[] getArgs() {
        return args;
    }

    public String getArgument(int index) {
        return (index > args.length) ? "" : args[index];
    }

    private void executeCommand() {
        switch (getCommand()) {
            case "help":
                PrizmJ.clearCommandText();
                PrizmJ.writeToConsole("List of commands: [  ] = required (  ) = optional\n");
                PrizmJ.console.append("\thelp: Displays this text.\n");
                PrizmJ.console.append("\tchelp: Displays controls.\n");
                PrizmJ.console.append("\tss [\"room_name\"]: Starts fire simulation in specified room.\n");
                PrizmJ.console.append("\tgetallrooms: Prints the name of all rooms.\n");
                PrizmJ.console.append("\tdijkstra (\"room_name\"): Prints dijkstra information.\n");
                PrizmJ.clearCommandText();
                break;
            case "chelp":
                PrizmJ.writeControlHelpToConsole();
                PrizmJ.clearCommandText();
                break;
            case "ss":
                if(prizmJ.getBlueprint().getRoomModelByName(getArgument(0)) == null) {
                    PrizmJ.writeToConsole("No room found with the name: " + getArgument(0) + ". Try 'getallrooms' to get a list of all the current rooms.");
                    break;
                }
                prizmJ.getFireSimulator().startFireSimulation(getArgument(0));
                PrizmJ.clearCommandText();
                break;
            case "getAllRooms":
            case "gar":
            case "getallrooms":
                prizmJ.getBlueprint().getAllModels().forEach(rm -> PrizmJ.writeToConsole(rm.getRoom().getName()));
                PrizmJ.clearCommandText();
                break;
            case "dijkstra":
            case "dstra":
                if(args.length == 0) {
                    PrizmJ.writeToConsole("For best possible Dijkstra results, run after or during simulation!");
                    PrizmJ.writeToConsole("!- Begin Dijkstra Dump -!");
                    prizmJ.getFireSimulator().getLatestResults().forEach(vertex -> PrizmJ.writeToConsole(String.format("Vertex: \"%s\", Vertex-Weight: (%f)", vertex.getRoom().getName(), vertex.getWeight())));
                    prizmJ.getFireSimulator().simulateDijkstraResults(prizmJ.getFireSimulator().getLatestResults());
                    PrizmJ.writeToConsole("!- End Dijkstra Dump -!");
                } else {
                    PrizmJ.writeToConsole("For best possible Dijkstra results, run after or during simulation!");
                    PrizmJ.writeToConsole("!- Begin Dijkstra Dump -!");
                    prizmJ.getFireSimulator().getLatestResultsFromRoom(getArgument(0)).forEach(vertex -> PrizmJ.writeToConsole(String.format("Vertex: \"%s\", Vertex-Weight: (%f)", vertex.getRoom().getName(), vertex.getWeight())));
                    PrizmJ.writeToConsole("!- End Dijkstra Dump -!");
                }
                PrizmJ.clearCommandText();
                break;
            default:
                PrizmJ.writeToConsole("Unrecognized command: " + toString());
                break;
        }
    }

    @Override
    public String toString() {
        return (command == null || getCommand().length() == 0) ? "Empty Command" : getCommand();
    }
}