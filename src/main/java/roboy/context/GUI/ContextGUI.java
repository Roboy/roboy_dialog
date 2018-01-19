package roboy.context.GUI;

import roboy.context.Context;
import roboy.context.dataTypes.DataType;

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
    private Map<Context.HistoryAttributes, JScrollPane> historyDisplays;
    private static int MAX_HISTORY_VALUES = 10;

    // Panel displaying historyAttributes.
    private TitledBorder historyBorder;
    private JPanel historyPanel;

    // Update button panel.
    private JPanel controlPanel;

    private static int FULL_WIDTH = 400;
    private static int FULL_HEIGHT = 300;
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

        historyDisplays = new HashMap<>();
        for(Context.HistoryAttributes attribute : Context.HistoryAttributes.values()) {
            historyPanel.add(new JLabel(attribute.toString() + ":", JLabel.CENTER));
            HashMap vals = (HashMap<Integer, DataType>) attribute.getNLastValues(MAX_HISTORY_VALUES);
            DefaultListModel<String> sorted = new DefaultListModel<>();
            if (vals.size() == 0) {
                sorted.add(0, NO_VALUE);
            } else {
                for (Integer i = 0; i < vals.size(); i++) {
                    sorted.add(i, vals.get(vals.size() - i - 1).toString());
                }
            }
            JList historyList = new JList(sorted);
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setViewportView(historyList);
            historyDisplays.put(attribute, scrollPane);
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
                updateHistories();
            }
        );
        controlPanel.add(updateButton);
        mainFrame.setVisible(true);
    }

    private void updateValues() {
        for(Context.ValueAttributes attribute : Context.ValueAttributes.values()) {
            Object val = attribute.getLastValue();
            if (val == null) {
                continue;
            }
            valueDisplays.get(attribute).setText(val.toString());
        }
        valuePanel.updateUI();
    }

    private void updateHistories() {
        for(Context.HistoryAttributes attribute : Context.HistoryAttributes.values()) {
            HashMap vals = (HashMap<Integer, DataType>) attribute.getNLastValues(MAX_HISTORY_VALUES);
            if (vals.size() == 0) {
                continue;
            }
            DefaultListModel<String> sorted = new DefaultListModel<>();
            for(Integer i = 0; i < vals.size(); i++) {
                sorted.add(i, vals.get(vals.size()-i-1).toString());
            }
            JList historyList = new JList(sorted);
            JScrollPane pane = historyDisplays.get(attribute);
            pane.setViewportView(historyList);
        }
        historyPanel.updateUI();
    }
}
