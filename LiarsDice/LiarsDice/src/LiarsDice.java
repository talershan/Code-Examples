package src;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LiarsDice {

    public static void main(String[] args) {
        try {
            // Set default theme to Nimbus if available
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Could not apply Nimbus Look and Feel");
        }

        SwingUtilities.invokeLater(LiarsDice::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Liar's Dice Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 700);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Liar's Dice Simulator", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel for entering values in a row
        JPanel enteringValuesPanel = new JPanel();
        enteringValuesPanel.setLayout(new GridLayout(1, 2));

        // Panel for rolls
        JPanel entireRolls = new JPanel();

        JLabel rollsLabel = new JLabel("Enter number of rolls:");
        rollsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField rollsInput = new JTextField("10000", 10);
        rollsInput.setMaximumSize(new Dimension(200, 30));

        // Panel for dice
        JPanel entireDice = new JPanel();

        JLabel diceLabel = new JLabel("Enter number of dice to roll:");
        diceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField diceInput = new JTextField("5", 10);
        diceInput.setMaximumSize(new Dimension(200, 30));

        JButton simulateButton = new JButton("Simulate");
        simulateButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.setEnabled(false);

        JTextArea resultsArea = new JTextArea(10, 40);
        resultsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultsArea);

        JLabel progressLabel = new JLabel("", JLabel.CENTER);
        progressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        // Panel for all selection areas in a row
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new GridLayout(1, 3));

        // Partial Success Criteria
        JPanel partialPanel = createCriteriaPanel("Partial Success Criteria");

        // Full Success Criteria
        JPanel fullPanel = createCriteriaPanel("Full Success Criteria");

        // Critical Criteria
        JPanel criticalPanel = createCriteriaPanel("Critical Criteria");

        // Add all criteria panels to the selection panel
        selectionPanel.add(partialPanel);
        selectionPanel.add(fullPanel);
        selectionPanel.add(criticalPanel);

        // Add Rolls panels together
        entireRolls.add(rollsLabel);
        entireRolls.add(rollsInput);

        // Add Dice panels together
        entireDice.add(diceLabel);
        entireDice.add(diceInput);

        //Add input panels together
        enteringValuesPanel.add(entireRolls);
        enteringValuesPanel.add(entireDice);

        List<SwingWorker<String, Void>> currentWorker = new ArrayList<>(1);

        simulateButton.addActionListener(e -> {
            try {
                int totalRolls = Integer.parseInt(rollsInput.getText());
                int numDice = Integer.parseInt(diceInput.getText());
                if (numDice <= 0) {
                    JOptionPane.showMessageDialog(frame, "Number of dice must be greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                progressBar.setValue(0);
                progressLabel.setText("Rolling your dice...");
                simulateButton.setEnabled(false);
                cancelButton.setEnabled(true);

                List<String> partialCriteria = getSelectedCriteria(partialPanel);
                List<String> fullCriteria = getSelectedCriteria(fullPanel);
                List<String> critCriteria = getSelectedCriteria(criticalPanel);

                SwingWorker<String, Void> worker = new DiceSimulationWorker(
                    numDice,
                    totalRolls,
                    progressBar,
                    partialCriteria,
                    fullCriteria,
                    critCriteria,
                    resultsArea,
                    simulateButton,
                    cancelButton,
                    progressLabel
                );

                currentWorker.clear();
                currentWorker.add(worker);
                worker.execute();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> {
            if (!currentWorker.isEmpty()) {
                currentWorker.get(0).cancel(true);
                resultsArea.setText("Rolls canceled.");
                progressLabel.setText("Simulation canceled.");
                progressBar.setValue(0);
                simulateButton.setEnabled(true);
                cancelButton.setEnabled(false);
            }
        });

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(enteringValuesPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(selectionPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(simulateButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(scrollPane);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(progressLabel);
        panel.add(progressBar);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(cancelButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static JPanel createCriteriaPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        String[] options = {"Pair", "Two Pair", "Three of a Kind", "Four of a Kind", "Full House", "Straight", "Five of a Kind"};
        for (String option : options) {
            JCheckBox checkBox = new JCheckBox(option);
            panel.add(checkBox);
        }
        return panel;
    }

    private static List<String> getSelectedCriteria(JPanel panel) {
        List<String> selected = new ArrayList<>();
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) comp;
                if (checkBox.isSelected()) {
                    selected.add(checkBox.getText());
                }
            }
        }
        return selected;
    }
    
}
