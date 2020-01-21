package com.prizmj.display.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.prizmj.display.PrizmJ;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DesktopLauncher extends JFrame {

    /*public static void main(String[] args) {
        launch(args);
    }

    private SwingNode prizmJCanvas;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Testing Libgdx");
        createJavaFXCompatibleSwingCanvas();
        StackPane pane = new StackPane(prizmJCanvas);
        primaryStage.setScene(new Scene(pane, 100, 50));
        primaryStage.show();
    }

    private void createJavaFXCompatibleSwingCanvas() {
        SwingUtilities.invokeLater(() -> {
            LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            LwjglCanvas canvas = new LwjglCanvas(new PrizmJ(), config);
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(canvas.getCanvas(), BorderLayout.CENTER);
            prizmJCanvas.setContent(panel);
            System.out.println("hey");
        });
    }*/

    private LwjglAWTCanvas canvas;

    public DesktopLauncher() {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("PrizmJ: Fire Simulator");
        setResizable(false);
        Container container = getContentPane();
        container.setLayout(null);

        PrizmJ prizmJ = new PrizmJ();
        canvas = new LwjglAWTCanvas(prizmJ, config);
        canvas.getCanvas().requestFocus();
        canvas.getCanvas().setBounds(0, 0, 800, 768);
        container.add(canvas.getCanvas());

        // Begin adding console now //
        JTextArea area = new JTextArea();
        JScrollPane scroll = new JScrollPane(area);
        area.setBackground(Color.BLACK);
        area.setForeground(Color.GREEN);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setBorder(new EmptyBorder(10, 10, 10, 10));
        area.setAutoscrolls(true);
        scroll.setBounds(800, 0, 566, 718);
        container.add(scroll);
        PrizmJ.setConsole(area);

        // Begin add command prompt //
        JTextField commandLine = new JTextField();
        commandLine.setBackground(Color.GREEN);
        commandLine.setBounds(800, 718, 560, 20);
        container.add(commandLine);
        PrizmJ.setCommand(commandLine);

        pack();
        setSize(1366, 768);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(DesktopLauncher::new);
    }
}
