package roboy.context.GUI;

import roboy.context.Context;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple GUI with the goal of showing the attribute values and histories in the Context.
 */
public class ContextGUI {
    private JFrame mainFrame;

    // Panel displaying valueAttributes.
    private TitledBorder valueBorder;
    private JPanel valuePanel;
    private Map<Context.ValueAttributes, JLabel> valueDisplays;

    // Panel displaying historyAttributes.
    private TitledBorder historyBorder;
    private JPanel historyPanel;

    // Update button panel.
    private JPanel controlPanel;

    private static int FULL_WIDTH = 400;
    private static int FULL_HEIGHT = 400;
    private static int ATTR_WIDTH = 390;
    private static int ATTR_HEIGHT = 50;
    private static int HISTORY_HEIGHT = 100;

    private static String NO_VALUE = "<not initialized>";

    public static void run() {
        ContextGUI gui = new ContextGUI();
        gui.startFrame();
    }

    private ContextGUI() {
        prepareGUI();
    }

    private void prepareGUI() {
        // Window initialization.
        mainFrame = new JFrame("Context GUI");
        mainFrame.setSize(FULL_WIDTH, FULL_HEIGHT);
        mainFrame.setLayout(new FlowLayout());
        JPanel mainPanel = new JPanel();
        //mainPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        mainFrame.add(mainPanel);

        // Attribute part initialization.
        valuePanel = new JPanel();
        valuePanel.setLayout(new GridLayout(0,2));
        valuePanel.setPreferredSize(new Dimension(ATTR_WIDTH, ATTR_HEIGHT));
        valueBorder = BorderFactory.createTitledBorder("Context values");
        valueBorder.setTitleJustification(TitledBorder.CENTER);
        valuePanel.setBorder(valueBorder);

        valueDisplays = new HashMap<>();
        for(Context.ValueAttributes attribute : Context.ValueAttributes.values()) {
            valuePanel.add(new JLabel(attribute.toString()+ ":", JLabel.CENTER));
            Object val = attribute.getLastValue();
            if (val == null) {
                val = NO_VALUE;
            }
            JLabel valueLabel = new JLabel(val.toString(), JLabel.CENTER);
            valueDisplays.put(attribute, valueLabel);
            valuePanel.add(valueLabel);
        }
        mainPanel.add(valuePanel);

        // History part initialization.
        historyPanel = new JPanel();
        historyPanel.setLayout(new GridLayout(0,2));
        historyPanel.setPreferredSize(new Dimension(ATTR_WIDTH, HISTORY_HEIGHT));
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
                mainFrame.dispose();
            }
        });

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        mainPanel.add(controlPanel);
        mainPanel.setVisible(true);
    }

    private void startFrame() {
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
                updateValues();
            }
        );
        controlPanel.add(updateButton);
        mainFrame.setVisible(true);
    }

    private void updateValues() {
        for(Context.ValueAttributes attribute : Context.ValueAttributes.values()) {
            Object val = attribute.getLastValue();
            if (val == null) {
                val = NO_VALUE;
            }
            valueDisplays.get(attribute).setText(val.toString());
        }
        valuePanel.updateUI();
    }
}
