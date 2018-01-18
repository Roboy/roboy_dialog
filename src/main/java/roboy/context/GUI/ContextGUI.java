package roboy.context.GUI;

import org.apache.commons.lang3.tuple.Pair;
import roboy.context.Context;
import roboy.context.ValueAttribute;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple GUI with the goal of showing the attribute values and histories in the Context.
 * Based on a template from https://www.tutorialspoint.com/swing/swing_jframe.htm
 */
public class ContextGUI {
    private JFrame mainFrame;

    private JPanel controlPanel;
    private TitledBorder valueBorder;
    private JPanel valuePanel;
    private TitledBorder historyBorder;
    private JPanel historyPanel;

    private static int FULL_WIDTH = 400;
    private static int FULL_HEIGHT = 400;

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
        mainFrame.setSize(FULL_WIDTH, FULL_HEIGHT);
        mainFrame.setLayout(new FlowLayout());
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        mainFrame.add(mainPanel);

        // Attribute part initialization.
        valuePanel = new JPanel();
        valuePanel.setLayout(new GridLayout(0,2));
        valueBorder = BorderFactory.createTitledBorder("Context values");
        valueBorder.setTitleJustification(TitledBorder.CENTER);
        valuePanel.setBorder(valueBorder);

        Map<Context.ValueAttributes, JLabel[]> values = new HashMap<>();
        for(Context.ValueAttributes v : Context.ValueAttributes.values()) {
            Object val = v.getLastValue();
            if (val == null) {
                val = "<not initialized>";
            }
            JLabel[] pair = {
                    new JLabel(v.toString()+ ":", JLabel.CENTER),
                    new JLabel(val.toString(),JLabel.CENTER)};
            values.put(v, pair);
            valuePanel.add(pair[0]);
            valuePanel.add(pair[1]);
        }
        mainPanel.add(valuePanel);

        historyPanel = new JPanel();
        historyPanel.setLayout(new GridLayout(0,2));
        historyBorder = BorderFactory.createTitledBorder("Histories");
        historyBorder.setTitleJustification(TitledBorder.CENTER);
        historyPanel.setBorder(historyBorder);

        Map<Context.HistoryAttributes, JList> history = new HashMap<>();
        for(Context.HistoryAttributes v : Context.HistoryAttributes.values()) {
            Object[] vals = v.getNLastValues(Integer.MAX_VALUE).entrySet().toArray();
            if (vals.length == 0) {
                vals = new Object[]{"<not initialized>", "<not initialized>", "<not initialized>", "<not initialized>", "<not initialized>", "<not initialized>",
                        "<not initialized>", "<not initialized>", "<not initialized>", "<not initialized>", "<not initialized>", "<not initialized>",
                        "<not initialized>", "<not initialized>", "<not initialized>", "<not initialized>", "<not initialized>", "<not initialized>",
                        "<not initialized>"};
            }
            JList historyList = new JList(vals);

            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setViewportView(historyList);
            historyPanel.add(new JLabel(v.toString() + ":", JLabel.CENTER));
            historyPanel.add(scrollPane);
        }
        mainPanel.add(historyPanel);

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        mainPanel.add(controlPanel);
        mainPanel.setVisible(true);
    }

    private void showJFrameDemo() {
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
                valueBorder.setTitle("Updated in the future");
                valuePanel.setBorder(valueBorder);
                valuePanel.updateUI();
            }
        );
        controlPanel.add(updateButton);
        mainFrame.setVisible(true);
    }
}
