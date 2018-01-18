package roboy.context.GUI;

import roboy.context.Context;
import roboy.context.ValueAttribute;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple GUI with the goal of showing the attribute values and histories in the Context.
 * Based on a template from https://www.tutorialspoint.com/swing/swing_jframe.htm
 */
public class ContextGUI {
    private JFrame mainFrame;

    String[][] historyAttributeData;

    private JLabel headerLabel = new JLabel("test1",JLabel.CENTER);
    private JLabel statusLabel = new JLabel("test2",JLabel.CENTER);
    private JPanel controlPanel;
    private JLabel valueHeader;
    private JPanel valuePanel;
    private JLabel msgLabel = new JLabel("Welcome to TutorialsPoint SWING Tutorial.", JLabel.CENTER);

    public static void main(String[] args) {
        ContextGUI gui = new ContextGUI();
        gui.showJFrameDemo();
    }

    private ContextGUI() {
        prepareGUI();
    }

    private void prepareGUI() {
        // Window initialization.
        mainFrame = new JFrame("Context GUI (alpha)");
        mainFrame.setSize(400, 200);
        mainFrame.setLayout(new BoxLayout(mainFrame.getContentPane(), BoxLayout.Y_AXIS));

        // Attribute part initialization.
        valueHeader = new JLabel("Context values", JLabel.CENTER);
        valueHeader.setBorder(new LineBorder(Color.BLUE));
        mainFrame.add(valueHeader);

        valuePanel = new JPanel();
        valuePanel.setLayout(new GridLayout(0,2));
        Map<Context.ValueAttributes, JLabel[]> values = new HashMap<>();
        for(Context.ValueAttributes v : Context.ValueAttributes.values()) {
            Object val = v.getLastValue();
            if (val == null) {
                val = "<not initialized>";
            }
            JLabel[] pair = {
                    new JLabel(v.toString(), JLabel.CENTER),
                    new JLabel(val.toString(),JLabel.CENTER)};
            values.put(v, pair);
            valuePanel.add(pair[0]);
            valuePanel.add(pair[1]);

        }

        mainFrame.add(valuePanel);

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        statusLabel.setSize(100,100);

        mainFrame.add(headerLabel);
        mainFrame.add(controlPanel);
        mainFrame.add(statusLabel);
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
                statusLabel.setText("A Frame shown to the user.");
                frame.setVisible(true);
            }
        );
        controlPanel.add(okButton);
        mainFrame.setVisible(true);
    }
}
