package roboy.context.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A simple GUI with the goal of showing the attribute values and histories in the Context.
 * Based on a template from https://www.tutorialspoint.com/swing/swing_jframe.htm
 */
public class ContextGUI {
    private JFrame mainFrame;

    private JLabel headerLabel = new JLabel("test1",JLabel.CENTER);
    private JLabel statusLabel = new JLabel("test2",JLabel.CENTER);
    private JPanel controlPanel;
    private JLabel msgLabel = new JLabel("Welcome to TutorialsPoint SWING Tutorial.", JLabel.CENTER);

    public static void main(String[] args) {
        ContextGUI gui = new ContextGUI();
        gui.showJFrameDemo();
    }

    private ContextGUI() {
        prepareGUI();
    }

    private void prepareGUI() {
        mainFrame = new JFrame("Context GUI (alpha)");
        mainFrame.setSize(300, 400);
        mainFrame.setLayout(new GridLayout(3,2));

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        //statusLabel.setSize(350,100);

        mainFrame.add(headerLabel);
        mainFrame.add(controlPanel);
//        mainFrame.add(statusLabel);
        mainFrame.setVisible(true);
    }

    private void showJFrameDemo() {
        headerLabel.setText("Container in action: JFrame");
        final JFrame frame = new JFrame();
        frame.setSize(300, 300);
        frame.setLayout(new FlowLayout());
        frame.add(msgLabel);


        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                frame.dispose();
            }
        });
        JButton okButton = new JButton("Open a Frame");
        okButton.addActionListener(e -> {
                headerLabel.setText("Changing text");
//              statusLabel.setText("A Frame shown to the user.");
//              frame.setVisible(true);
            }
        );
        controlPanel.add(okButton);
        mainFrame.setVisible(true);
    }
}
