package roboy.context;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple GUI showing the values and histories in the Context with their content.
 */
public class ContextGUI {
    private JFrame mainFrame;

    // Panel displaying valueAttributes.
    private TitledBorder valueBorder;
    private JPanel valuePanel;
    private Map<AbstractValue, JLabel> valueDisplays;
    private Map<AbstractValueHistory, JScrollPane> historyDisplays;
    private static int MAX_HISTORY_VALUES = 50;

    // Panel displaying historyAttributes.
    private TitledBorder historyBorder;
    private JPanel historyPanel;

    // Update button panel.
    private JPanel controlPanel;

    private static int FULL_WIDTH = 600;
    private static int FULL_HEIGHT = 600;
    private static int ATTR_WIDTH = 590;
    private static int ATTR_HEIGHT = 80;
    private static int HISTORY_HEIGHT = 300;

    private static String NO_VALUE = "<not initialized>";

    private List<AbstractValue> values;
    private List<AbstractValueHistory> histories;

    public static void run(List<AbstractValue> values, List<AbstractValueHistory> histories) {
        ContextGUI gui = new ContextGUI(values, histories);
        gui.startFrame();
    }

    private ContextGUI(List<AbstractValue> values, List<AbstractValueHistory> histories) {
        this.values = values;
        this.histories = histories;
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
        valuePanel.setLayout(new GridLayout(0, 2));
        valuePanel.setPreferredSize(new Dimension(ATTR_WIDTH, ATTR_HEIGHT));
        valueBorder = BorderFactory.createTitledBorder("Context values");
        valueBorder.setTitleJustification(TitledBorder.CENTER);
        valuePanel.setBorder(valueBorder);

        valueDisplays = new HashMap<>();
        for (AbstractValue attribute : values) {
            valuePanel.add(new JLabel(attribute.getClass().getSimpleName() + ":", JLabel.CENTER));
            Object val = attribute.getValue();
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
        historyPanel.setLayout(new GridLayout(0, 2));
        historyPanel.setPreferredSize(new Dimension(ATTR_WIDTH, HISTORY_HEIGHT));
        historyBorder = BorderFactory.createTitledBorder("Histories");
        historyBorder.setTitleJustification(TitledBorder.CENTER);
        historyPanel.setBorder(historyBorder);

        historyDisplays = new HashMap<>();
        for (AbstractValueHistory attribute : histories) {
            historyPanel.add(new JLabel(attribute.getClass().getSimpleName() + ":", JLabel.CENTER));
            Map<Integer, Object> vals = attribute.getLastNValues(MAX_HISTORY_VALUES);
            int elements = attribute.getNumberOfValuesSinceStart();
            DefaultListModel<String> sorted = new DefaultListModel<>();
            if (vals.size() == 0) {
                sorted.add(0, NO_VALUE);
            } else {
                for (Integer i = 0; i < vals.size(); i++) {
                    sorted.add(i, "[" + (elements-i) + "] " + vals.get(vals.size() - i - 1).toString());
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
            public void windowClosing(WindowEvent windowEvent) {
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
        for (AbstractValue attribute : values) {
            Object val = attribute.getValue();
            if (val == null) {
                continue;
            }
            valueDisplays.get(attribute).setText(val.toString());
        }
        valuePanel.updateUI();
    }

    private void updateHistories() {
        for (AbstractValueHistory attribute : histories) {
            Map<Integer, Object> vals = attribute.getLastNValues(MAX_HISTORY_VALUES);
            int elements = attribute.getNumberOfValuesSinceStart();
            if (vals.size() == 0) {
                continue;
            }
            DefaultListModel<String> sorted = new DefaultListModel<>();
            for (Integer i = 0; i < vals.size(); i++) {
                sorted.add(i, "[" + (elements-i-1) + "] " + vals.get(vals.size()-i-1).toString());
            }
            JList historyList = new JList(sorted);
            JScrollPane pane = historyDisplays.get(attribute);
            pane.setViewportView(historyList);
        }
        historyPanel.updateUI();
    }
}
